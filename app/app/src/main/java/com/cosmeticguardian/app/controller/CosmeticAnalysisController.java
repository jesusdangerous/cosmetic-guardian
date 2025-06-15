package com.cosmeticguardian.app.controller;

import com.cosmeticguardian.app.model.CompositionAnalysisRequest;
import com.cosmeticguardian.app.model.CompositionAnalysisResponse;
import com.cosmeticguardian.app.service.CosmeticAnalysisService;
import com.cosmeticguardian.app.service.OcrService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/analysis")
public class CosmeticAnalysisController {

    private final CosmeticAnalysisService analysisService;
    private OcrService ocrService;

    @Autowired
    public CosmeticAnalysisController(CosmeticAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/composition")
    public ResponseEntity<CompositionAnalysisResponse> analyzeComposition(
            @RequestBody CompositionAnalysisRequest request) {
        try {
            System.out.println("Received ingredients: " + request.getIngredients());
            CompositionAnalysisResponse response = analysisService.analyzeComposition(request.getIngredients());
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompositionAnalysisResponse> analyzePhoto(
            @RequestParam("image") MultipartFile imageFile) {
        try {
            // 1. Извлечь текст с фото (OCR)
            String extractedText = ocrService.extractTextFromImage(imageFile);

            // 2. Проанализировать текст с помощью DeepSeek
            CompositionAnalysisResponse response = analysisService.analyzeComposition(extractedText);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}