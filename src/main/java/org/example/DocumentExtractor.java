package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.xml.sax.SAXException;

public class DocumentExtractor {
    public static void main(String[] args) {
        String filePath = "src/resources/archivos/2023 - Core Java Cheatsheet.pdf";

        try (FileInputStream input = new FileInputStream(new File(filePath))) {
            CustomContentHandler customHandler = new CustomContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setSortByPosition(true); // ordenamos el texto por posición
            pdfConfig.setExtractInlineImages(false); // No extraer imágenes
            context.set(PDFParserConfig.class, pdfConfig);

            // determinar el tipo de archivo
            if (filePath.endsWith(".pdf")) {
                PDFParser pdfParser = new PDFParser();
                pdfParser.parse(input, customHandler, metadata, context);
            } else if (filePath.endsWith(".docx")) {
                OOXMLParser ooxmlParser = new OOXMLParser();
                ooxmlParser.parse(input, customHandler, metadata, context);
            } else {
                throw new IllegalArgumentException("Formato de archivo no soportado");
            }

            String structuredText = customHandler.getExtractedText();
            System.out.println("Texto estructurado extraído:\n" + structuredText);

        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }
    }
}
