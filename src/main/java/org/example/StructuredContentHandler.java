package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class StructuredContentHandler extends DefaultHandler {
    private static final Set<String> HEADING_TAGS = new HashSet<>(Arrays.asList(
            "title", "h1", "h2", "h3", "h4", "h5", "h6", "heading"));
    private static final Set<String> PARAGRAPH_TAGS = new HashSet<>(Arrays.asList(
            "p", "div", "td", "li"));

    private final StringBuilder currentText = new StringBuilder();
    private final Stack<String> elementStack = new Stack<>();
    private Document rootDocument;
    private Document currentDocument;
    private final Stack<Document> documentStack = new Stack<>();

    @Override
    public void startDocument() throws SAXException {
        rootDocument = new Document();
        currentDocument = rootDocument;
        documentStack.push(rootDocument);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        elementStack.push(qName.toLowerCase());
        // Limpiar texto acumulado entre elementos
        currentText.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String element = qName.toLowerCase();

        // Uso de Set para búsqueda más eficiente (O(1) vs O(n))
        if (HEADING_TAGS.contains(element)) {
            processHeading(element);
        } else if (PARAGRAPH_TAGS.contains(element)) {
            processParagraph();
        }

        currentText.setLength(0);
        elementStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String content = new String(ch, start, length);
        // Solo agregar espacios cuando sea necesario
        if (!content.isEmpty()) {
            if (currentText.length() > 0 && !Character.isWhitespace(currentText.charAt(currentText.length() - 1))
                    && !Character.isWhitespace(content.charAt(0))) {
                currentText.append(' ');
            }
            currentText.append(content);
        }
    }

    private void processHeading(String element) {
        String headingText = currentText.toString().trim();
        if (headingText.isEmpty()) {
            return;
        }

        // Determinar nivel del encabezado de manera más directa
        int level = 1;
        if (element.length() == 2 && element.charAt(0) == 'h') {
            try {
                level = Character.getNumericValue(element.charAt(1));
            } catch (NumberFormatException e) {
                // Mantener nivel 1 por defecto
            }
        }

        // Ajustar documentStack según el nivel
        adjustDocumentStack(level);

        // Crear y agregar el nuevo documento
        Document parent = documentStack.isEmpty() ? rootDocument : documentStack.peek();
        Document newSection = new Document(headingText, level);
        parent.addChild(newSection);

        documentStack.push(newSection);
        currentDocument = newSection;
    }

    private void adjustDocumentStack(int level) {
        while (!documentStack.isEmpty() && documentStack.peek().getLevel() >= level) {
            documentStack.pop();
        }
    }

    private void processParagraph() {
        String paragraphText = currentText.toString().trim();
        if (!paragraphText.isEmpty() && currentDocument != null) {
            currentDocument.appendContent(paragraphText);
        }
    }

    public Document getDocument() {
        return rootDocument;
    }
}