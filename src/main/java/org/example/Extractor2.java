package org.example;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Extractor2 {
    public static void main(String[] args) {
        String filePath = "src/resources/archivos/documento.pdf";

        try (FileInputStream input = new FileInputStream(new File(filePath))) {
            StructuredContentHandler structuredHandler = new StructuredContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setSortByPosition(true); // ordenamos el texto por posición
            pdfConfig.setExtractInlineImages(false);
            context.set(PDFParserConfig.class, pdfConfig);

            if (filePath.endsWith(".pdf")) {
                PDFParser pdfParser = new PDFParser();
                pdfParser.parse(input, structuredHandler, metadata, context);
            } else if (filePath.endsWith(".docx")) {
                OOXMLParser ooxmlParser = new OOXMLParser();
                ooxmlParser.parse(input, structuredHandler, metadata, context);
            } else {
                throw new IllegalArgumentException("Formato de archivo no soportado");
            }

            String structuredText = structuredHandler.getExtractedText();
            System.out.println("Texto estructurado extraído:");
            System.out.println(structuredText);

        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }
    }
}
