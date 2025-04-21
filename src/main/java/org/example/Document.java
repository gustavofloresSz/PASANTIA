package org.example;

import java.util.ArrayList;
import java.util.List;

/*
El proceso de extracción debe preservar la estructura del documento.
Me explico, si el documento tiene títulos/capítulos, subtítulos, etc. la extracción
debe retornar un documento que contiene otros documentos.

Por ejemplo, un subtítulo que contiene párrafos, tablas o gráficos que a su vez es parte de un
título/capítulo que a su vez es parte de todo el documento.
A nivel de texto sólo interesa llegar a nivel del párrafo; componentes de un párrafo como oraciones,
frases o palabras no importa
 */

/*FIXME #4

Para el documento: Doc1.docx, la estructura debería ser:

ROOT
|_(h1, Bienvenido hola mundo)
	|_(h2, Inicio de la prueba)
		|_(a, ) <-- Como está vacío, no se debería agregar
		|_(p, Esta prueba se usara para extraer los párrafos)
		|_(p, ) <-- Como está vacío, no se debería agregar
|_(h2, Bienvenido hola mundo parte 2)

Ajusta Esta clase para que tenga los siguiente atributos

Document (type, text, children, parent y level)
 */
public class Document {
    private String title;
    private String subtitle;
    private String content;
    private List<Document> children;
    private int level;

    public Document(String title, int level) {
        this.title = title;
        this.level = level;
        this.subtitle = "";
        this.content = "";
        this.children = new ArrayList<>();
    }

    public Document() {
        this("ROOT", 0);
    }

    public void addChild(Document child) {
        this.children.add(child);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void appendContent(String text) {
        if (this.content.isEmpty()) {
            this.content = text;
        } else {
            this.content += "\n" + text;
        }
    }

    public List<Document> getChildren() {
        return children;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (level == 0 || level == 1) {
            sb.append("  ".repeat(level)).append("Title: ").append(title).append("\n");
        } else {
            sb.append("  ".repeat(level)).append("Subtitle: ").append(title).append("\n");
        }

        if (!content.isEmpty()) {
            sb.append("  ".repeat(level)).append("Content: ").append(content).append("\n");
        }

        for (Document child : children) {
            sb.append(child.toString());
        }

        return sb.toString();
    }
}
