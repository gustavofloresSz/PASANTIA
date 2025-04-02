package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
FIXME #4

Revisando
https://tika.apache.org/3.1.0/parser.html:

The parsed content of the document stream is returned to the client application as a sequence of XHTML SAX events.
XHTML is used to express structured content of the document and SAX events enable streamed processing. Note that the
XHTML format is used here only to convey structural information, not to render the documents for browsing!

The XHTML SAX events produced by the parser implementation are sent to a ContentHandler instance given to the parse method

La idea es identificar los elementos referenciados en los eventos para detectar la estructura del documento
El resultado no debe ser un cadena de texto sino una estructura de árbol que represente el documento.

Podemos definir el record/clase 'Document' para representar cada nodo de esta estructura de árbol.
 */
//prueba1 retorna con etiquetas (<p>,<div>,etc), usado en DocumentExtractor
public class CustomContentHandler extends DefaultHandler {


    private StringBuilder extractedText = new StringBuilder();
    private int indentation = 0;

    /*
    FIXME #5
    Se puede usar este evento para crear el documento raíz del árbol.
     */
    @Override
    public void startDocument() throws SAXException {
        //
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        extractedText.append("  ".repeat(indentation)).append("<").append(qName).append(">\n");
        indentation++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        indentation--;
        extractedText.append("  ".repeat(indentation)).append("</").append(qName).append(">\n");
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String content = new String(ch, start, length).trim();
        if (!content.isEmpty()) {
            extractedText.append("  ".repeat(indentation)).append(content).append("\n");
        }
    }

    public String getExtractedText() {
        return extractedText.toString();
    }
}
