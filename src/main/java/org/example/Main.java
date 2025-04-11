package org.example;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/resources/archivos/Doc3.docx";

        try {
            DocumentExtractor extractor = new DocumentExtractor();
            Document document = extractor.extract(filePath);
            System.out.println("Documento estructurado extraído:");
            System.out.println(document.toString());

        } catch (Exception e) {
            System.err.println("Error al procesar el documento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}