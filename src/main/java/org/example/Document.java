package org.example;

import java.util.ArrayList;
import java.util.List;

/*FIXME #4

Para el documento: Doc1.docx, la estructura debería ser:

ROOT
|_(h1, Título #1)
	|_(h2, Inicio de la prueba)
		|_(a, ) <-- Como está vacío, no se debería agregar
		|_(p, Esta prueba se usará para extraer los párrafos)
		|_(p, ) <-- Como está vacío, no se debería agregar
|_(h2, Título #2)

Ajusta Esta clase para que tenga los siguiente atributos

Document (String type, StringBuilder text, List<Document> children, Document parent y Integer level)
 */
public class Document {
    private String type;
    private StringBuilder text;
    private List<Document> children;
    private Document parent;
    private Integer level;

    public Document(String type, Integer level) {
        this.type = type;
        this.level = level;
        this.text = new StringBuilder();
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public Document() {
        this("ROOT", 0);
    }

    public void addChild(Document child) {
        this.children.add(child);
        child.parent = this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StringBuilder getText() {
        return text;
    }

    public void setText(StringBuilder text) {
        this.text = text;
    }

    public void appendText(String additionalText) {
        if (additionalText != null && !additionalText.trim().isEmpty()) {
            if (this.text.length() > 0) {
                this.text.append("\n");
            }
            this.text.append(additionalText.trim());
        }
    }

    public List<Document> getChildren() {
        return children;
    }

    public Document getParent() {
        return parent;
    }

    public void setParent(Document parent) {
        this.parent = parent;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(level);

        sb.append(indent).append("(").append(type).append(", ");
        if (text.length() > 0) {
            sb.append(text);
        }
        sb.append(")\n");

        for (Document child : children) {
            sb.append(child.toString());
        }

        return sb.toString();
    }
}