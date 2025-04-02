package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Stack;

/*
FIXME #6

Una vez abordados todos todos estos aspectos.

El siguiente objetivo es explorar diseños para escalar el procesamiento

1. Documentos con más de 100, 1000 páginas.
2. 10, 100, 1000, Documentos al mismo tiempo

*/

//prueba2 retorna el texto sin etiquetas, usado en Extractor2
public class StructuredContentHandler extends DefaultHandler {
    private final StringBuilder extractedText = new StringBuilder();
    private final Stack<String> elementStack = new Stack<>();
    private final StringBuilder currentText = new StringBuilder();
    private int hierarchyLevel = 0;
    private Document currentDocument;

    // Elementos que indican niveles jerárquicos en documentos
    private static final String[] TITLE_ELEMENTS = {
            "title", "h1", "h2", "h3", "h4", "h5", "h6", "heading"
    };

    private static final String[] PARAGRAPH_ELEMENTS = {
            "p", "div", "td", "li"
    };

    @Override
    public void startDocument() throws SAXException {
        System.out.println("Inicio del documento");
//        this.currentDocument = new Document(ROOT);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementStack.push(qName.toLowerCase());

        if (isHeadingElement(qName)) {
            flushCurrentText();
            hierarchyLevel = determineHierarchyLevel(qName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isParagraphElement(qName) || isHeadingElement(qName)) {
            flushCurrentText();
        }

        elementStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String content = new String(ch, start, length).trim();
        if (!content.isEmpty()) {
            currentText.append(content).append(" ");
        }
    }

    private void flushCurrentText() {
        String text = currentText.toString().trim();
        if (!text.isEmpty()) {
            String elementType = elementStack.isEmpty() ? "" : elementStack.peek();

            if (isHeadingElement(elementType)) {
                if (extractedText.length() > 0) {
                    extractedText.append("\n");
                }
                extractedText.append("\n").append("  ".repeat(hierarchyLevel - 1))
                        .append(text).append("\n");
            } else if (isParagraphElement(elementType)) {
                extractedText.append("  ".repeat(hierarchyLevel))
                        .append(text).append("\n");
            }
            currentText.setLength(0);
        }
    }

    private boolean isHeadingElement(String element) {
        String elemLower = element.toLowerCase();
        for (String heading : TITLE_ELEMENTS) {
            if (elemLower.equals(heading) || elemLower.contains(heading)) {
                return true;
            }
        }
        return false;
    }

    private boolean isParagraphElement(String element) {
        String elemLower = element.toLowerCase();
        for (String para : PARAGRAPH_ELEMENTS) {
            if (elemLower.equals(para) || elemLower.contains(para)) {
                return true;
            }
        }
        return false;
    }

    private int determineHierarchyLevel(String element) {
        String elemLower = element.toLowerCase();
        if (elemLower.equals("title") || elemLower.equals("h1")) {
            return 1;
        } else if (elemLower.equals("h2")) {
            return 2;
        } else if (elemLower.equals("h3")) {
            return 3;
        } else if (elemLower.equals("h4")) {
            return 4;
        } else if (elemLower.equals("h5")) {
            return 5;
        } else if (elemLower.equals("h6")) {
            return 6;
        } else {
            return 2;
        }
    }

    public String getExtractedText() {
        flushCurrentText();
        return extractedText.toString();
    }
}