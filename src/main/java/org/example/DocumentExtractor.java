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
        String filePath = "src/resources/archivos/Tax_Guide.pdf";

        try (FileInputStream input = new FileInputStream(new File(filePath))) {
            CustomContentHandler customHandler = new CustomContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

/*
FIXME #2
Estas configuraciones son para el parser de PDF, pero no para el parser de OOXML.

Revisando
https://tika.apache.org/3.1.0/api/org/apache/tika/parser/pdf/PDFParserConfig.html

Se puede configurar el PDFParser directamente, sin hace falta:

Calls to PDFParser, i.e. parser.getPDFParserConfig().setEnableAutoSpace()

Son pocos los casos en los que requerimos ajustar el ParseContext
Ver:

5.5 Context-sensitive parsing (Libro)

Quitar este bloque de código, creo que es buena idea extraer documentos de tipo 'imágen'.
 */
            /* PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setSortByPosition(true); // ordenamos el texto por posición
            pdfConfig.setExtractInlineImages(true); // extraer imágenes
            context.set(PDFParserConfig.class, pdfConfig);
 */
            PDFParserConfig pdfConfig = new PDFParserConfig();
            pdfConfig.setSortByPosition(true); // ordenamos el texto por posición
            pdfConfig.setExtractInlineImages(false); // No extraer imágenes
            context.set(PDFParserConfig.class, pdfConfig);


/*
FIXME #1
Esta forma de detectar el tipo de archivo no es la mejor porque la extensión de un archivo se puede cambiar fácilmente.
Revisar https://tika.apache.org/3.1.0/detection.html#Content_Detection
Mover este bloque a una función estática que acepte un objeto de tipo java.nio.file.Path y retorne el parser apropiado o un error.
*/

/*
FIXME #3
Usar el BodyContentHandler(customHandler) para extraer el contenido del cuerpo del documento. Podemos omitir el encabezado/header.
 */
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
