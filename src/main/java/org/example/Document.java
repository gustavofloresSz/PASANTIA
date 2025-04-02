package org.example;

/*
El proceso de extracción debe preservar la estructura del documento.
Me explico, si el documento tiene títulos/capítulos, subtítulos, etc. la extracción
debe retornar un documento que contiene otros documentos.

Por ejemplo, un subtítulo que contiene párrafos, tablas o gráficos que a su vez es parte de un
título/capítulo que a su vez es parte de todo el documento.
A nivel de texto sólo interesa llegar a nivel del párrafo; componentes de un párrafo como oraciones,
frases o palabras no importa
 */
public class Document {
    //enum type ROOT, TITLE, SUBTITLE, PARAGRAPH, TABLE, IMAGE;
    //String content;
    //List<Document> children;
}
