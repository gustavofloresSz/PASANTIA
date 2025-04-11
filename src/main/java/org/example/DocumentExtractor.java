package org.example;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


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

        try (FileInputStream input = new FileInputStream(file)) {
            StructuredContentHandler structuredHandler = new StructuredContentHandler();
            // usamos BodyContentHandler para filtrar elementos no deseados
            BodyContentHandler bodyHandler = new BodyContentHandler(structuredHandler);

            Metadata metadata = new Metadata();
            metadata.set("resourceName", file.getName());

            // Configurar el parser según la extensión del archivo
            Parser parser;
            ParseContext context = new ParseContext();

            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".pdf")) {
                parser = new PDFParser();

                // Configuración específica para PDFs
                PDFParserConfig pdfConfig = new PDFParserConfig();
                pdfConfig.setSortByPosition(true);
                pdfConfig.setExtractInlineImages(false);
                context.set(PDFParserConfig.class, pdfConfig);
            } else if (fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
                parser = new OOXMLParser();
            } else {
                throw new IllegalArgumentException("El formato de archivo no es soportado: " + fileName);
            }

            parser.parse(input, bodyHandler, metadata, context);
            return structuredHandler.getDocument();
        }
    }
}