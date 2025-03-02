package org.example;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tika.exception.TikaException;

public class DocumentExtractor {
    public static void main(String[] args) {
        String filePath = "src/resources/archivos/documento.pdf";

        try (FileInputStream input = new FileInputStream(new File(filePath))) {
            // Usar CustomContentHandler para preservar la estructura jerárquica
            CustomContentHandler customHandler = new CustomContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            // Configuración del PDFParser
            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setSortByPosition(true); // Ordenar texto por posición
            pdfConfig.setExtractInlineImages(false); // No extraer imágenes
            context.set(PDFParserConfig.class, pdfConfig);

            // Determinar el tipo de archivo y usar el parser correspondiente
            if (filePath.endsWith(".pdf")) {
                PDFParser pdfParser = new PDFParser();
                pdfParser.parse(input, customHandler, metadata, context);
            } else if (filePath.endsWith(".docx")) {
                OOXMLParser ooxmlParser = new OOXMLParser();
                ooxmlParser.parse(input, customHandler, metadata, context);
            } else {
                throw new IllegalArgumentException("Formato de archivo no soportado");
            }

            // Obtener el texto estructurado
            String structuredText = customHandler.getExtractedText();
            System.out.println("Texto estructurado extraído:\n" + structuredText);

        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }
    }
}
