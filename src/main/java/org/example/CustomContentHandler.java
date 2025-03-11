package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//prueba1 retorna con etiquetas (<p>,<div>,etc), usado en DocumentExtractor
public class CustomContentHandler extends DefaultHandler {
    private StringBuilder extractedText = new StringBuilder();
    private int indentation = 0;

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
