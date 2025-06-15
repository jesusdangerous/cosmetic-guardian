package com.cosmeticguardian.app.service;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class OcrService {

    public String extractTextFromImage(MultipartFile imageFile) throws Exception {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("rus+eng");

        File tempFile = File.createTempFile("ocr-", ".tmp");
        imageFile.transferTo(tempFile);

        try {
            return tesseract.doOCR(tempFile);
        } finally {
            tempFile.delete();
        }
    }
}