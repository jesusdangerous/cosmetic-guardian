package com.cosmeticguardian.app.service;

import com.cosmeticguardian.app.model.CompositionAnalysisResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class CosmeticAnalysisService {

    private final DeepSeekService deepSeekService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CosmeticAnalysisService(DeepSeekService deepSeekService, ObjectMapper objectMapper) {
        this.deepSeekService = deepSeekService;
        this.objectMapper = objectMapper;
    }

    public CompositionAnalysisResponse analyzeComposition(String ingredients) throws JsonProcessingException {
        try {
            String analysisJson = deepSeekService.analyzeIngredients(ingredients);
            return objectMapper.readValue(analysisJson, CompositionAnalysisResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing composition: " + e.getMessage(), e);
        }
    }
}