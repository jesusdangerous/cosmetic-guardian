package com.cosmeticguardian.app.controller;

import com.cosmeticguardian.app.model.CompositionAnalysisRequest;
import com.cosmeticguardian.app.model.CompositionAnalysisResponse;
import com.cosmeticguardian.app.service.CosmeticAnalysisService;
import com.cosmeticguardian.app.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/analysis")
public class CosmeticAnalysisController {
    private static final Logger logger = LoggerFactory.getLogger(CosmeticAnalysisController.class);

    private final CosmeticAnalysisService analysisService;
    private final OcrService ocrService;

    @Autowired
    public CosmeticAnalysisController(CosmeticAnalysisService analysisService, OcrService ocrService) {
        this.analysisService = analysisService;
        this.ocrService = ocrService;
    }

    @PostMapping("/composition")
    public ResponseEntity<CompositionAnalysisResponse> analyzeComposition(
            @RequestBody CompositionAnalysisRequest request) {
        try {
            logger.info("Анализ состава: {}", request.getIngredients());
            CompositionAnalysisResponse response = analysisService.analyzeComposition(request.getIngredients());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при анализе состава", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompositionAnalysisResponse> analyzePhoto(
            @RequestParam("image") MultipartFile imageFile) {
        try {
            if (imageFile.isEmpty()) {
                logger.warn("Получен пустой файл изображения");
                return ResponseEntity.badRequest().build();
            }

            logger.info("Обработка изображения размером {} байт", imageFile.getSize());
            String extractedText = ocrService.extractTextFromImage(imageFile);
            logger.info("Извлеченный текст: {}", extractedText);

            CompositionAnalysisResponse response = analysisService.analyzeComposition(extractedText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при анализе фото", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}