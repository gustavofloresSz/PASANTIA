package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class StructuredContentHandler extends DefaultHandler {
    private static final Set<String> HEADING_TAGS = new HashSet<>(Arrays.asList(
            "h1", "h2", "h3", "h4", "h5", "h6"));
    private static final Set<String> PARAGRAPH_TAGS = new HashSet<>(Arrays.asList("p"));

    private final StringBuilder currentText = new StringBuilder();
    private final Deque<String> elementStack = new ArrayDeque<>();
    private Document rootDocument;
    private Document currentDocument;
    private final Deque<Document> documentStack = new ArrayDeque<>();
    private boolean collectingParagraphText = false;

    @Override
    public void startDocument() throws SAXException {
        rootDocument = new Document();
        currentDocument = rootDocument;
        documentStack.push(rootDocument);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String element = qName.toLowerCase();
        elementStack.push(element);

        if (PARAGRAPH_TAGS.contains(element)) {
            collectingParagraphText = true;
            currentText.setLength(0);
        } else if (HEADING_TAGS.contains(element)) {
            collectingParagraphText = false;
            currentText.setLength(0);
        } else if (!HEADING_TAGS.contains(element) && !PARAGRAPH_TAGS.contains(element) && !collectingParagraphText) {
            currentText.setLength(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String element = qName.toLowerCase();

        if (HEADING_TAGS.contains(element)) {
            processHeading(element);
            currentText.setLength(0);
        } else if (PARAGRAPH_TAGS.contains(element)) {
            processParagraph();
            collectingParagraphText = false;
            currentText.setLength(0);
        }

        elementStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String content = new String(ch, start, length);

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
        if (headingText.isEmpty()) return;

        int level = 1;
        if (element.length() == 2 && element.charAt(0) == 'h') {
            try {
                level = Character.getNumericValue(element.charAt(1));
            } catch (NumberFormatException e) {
                // Nivel 1 por defecto
            }
        }

        adjustDocumentStack(level);

        Document parent = documentStack.isEmpty() ? rootDocument : documentStack.peek();
        Document newSection = new Document(element, level);
        newSection.appendText(headingText);
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
            Document paragraphDoc = new Document("p", currentDocument.getLevel() + 1);
            paragraphDoc.appendText(paragraphText);
            currentDocument.addChild(paragraphDoc);
        }
    }

    public Document getDocument() {
        return rootDocument;
    }
}
