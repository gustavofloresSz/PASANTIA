package org.example;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class DocumentExtractor {

    /**
     * Extrae la estructura de un documento
     * @param filePath Ruta al archivo
     * @return Documento estructurado
     * @throws IOException Si hay un error al leer el archivo
     * @throws TikaException Si hay un error al procesar el documento
     * @throws SAXException Si hay un error en el parsing XML
     */
    public Document extract(String filePath) throws IOException, TikaException, SAXException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("El archivo no existe: " + filePath);
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            StructuredContentHandler structuredHandler = new StructuredContentHandler();
            // usamos BodyContentHandler para filtrar elementos no deseados
            BodyContentHandler bodyHandler = new BodyContentHandler(structuredHandler);

            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            // determinar el parser adecuado basado en el tipo de archivo
            Parser parser = determinarParser(inputStream, metadata, context);

            parser.parse(inputStream, bodyHandler, metadata, context); //
            return structuredHandler.getDocument();
        }
    }

    private static Parser determinarParser(InputStream inputStream, Metadata metadata, ParseContext context) throws IOException {
        // Asegurarse de que el inputStream soporte mark/reset
        if (inputStream.markSupported()) {
            inputStream.mark(1024); // Marcar el stream para permitir reset
            Detector detector = new DefaultDetector();
            MediaType mediaType = detector.detect(inputStream, metadata);
            inputStream.reset(); // resetear el stream para el parsing real

            if (mediaType.equals(MediaType.application("pdf"))) {
                PDFParser pdfParser = new PDFParser();
                configurarPDF(pdfParser, context);
                return pdfParser;
            } else if (mediaType.equals(MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml.document")) ||
                    mediaType.equals(MediaType.application("msword"))) {
                return new OOXMLParser();
            } else {
                throw new IllegalArgumentException("El formato de archivo no es soportado: " + mediaType);
            }
        }

        return new AutoDetectParser();
    }

    private static void configurarPDF(PDFParser parser, ParseContext context) {
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setSortByPosition(true);
        pdfConfig.setExtractInlineImages(false);
        context.set(PDFParserConfig.class, pdfConfig);
    }
}