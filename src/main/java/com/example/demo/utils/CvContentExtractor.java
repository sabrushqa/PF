package com.example.demo.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CvContentExtractor {

    /**
     * Extrait le contenu textuel d'un fichier (PDF ou Word)
     * @param file Le fichier à analyser
     * @return Le contenu textuel du fichier
     * @throws IOException En cas d'erreur lors de la lecture du fichier
     */
    public String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        if (contentType == null) {
            throw new IOException("Type de fichier non déterminé");
        }

        switch (contentType) {
            case "application/pdf":
                return extractFromPdf(inputStream);
            case "application/msword":
                return extractFromDoc(inputStream);
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return extractFromDocx(inputStream);
            default:
                throw new IOException("Format non supporté: " + contentType);
        }
    }

    /**
     * Extrait le texte d'un fichier PDF
     */
    private String extractFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Extrait le texte d'un fichier Word (.doc)
     */
    private String extractFromDoc(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }

    /**
     * Extrait le texte d'un fichier Word (.docx)
     */
    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }
}