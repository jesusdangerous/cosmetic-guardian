package com.cosmeticguardian.app.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OcrService {
    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);

    @Value("${tesseract.datapath:src/main/resources/tessdata}")
    private String tessDataPath;

    public String extractTextFromImage(MultipartFile imageFile) throws IOException, TesseractException {
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Неподдерживаемый формат файла");
        }

        Path tempDir = Files.createTempDirectory("ocr-temp");
        File tempFile = new File(tempDir.toFile(), "ocr-image." + getFileExtension(imageFile));

        try {
            imageFile.transferTo(tempFile);

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("rus+eng");
            tesseract.setPageSegMode(6);

            logger.info("Начало OCR обработки файла: {}", tempFile.getAbsolutePath());
            String result = tesseract.doOCR(tempFile);
            logger.info("OCR обработка завершена");

            return result;
        } finally {
            // Удаление временных файлов
            if (tempFile.exists() && !tempFile.delete()) {
                logger.warn("Не удалось удалить временный файл: {}", tempFile.getAbsolutePath());
            }
            if (tempDir.toFile().exists() && !tempDir.toFile().delete()) {
                logger.warn("Не удалось удалить временную директорию: {}", tempDir);
            }
        }
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        return "jpg"; // По умолчанию
    }
}