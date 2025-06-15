package com.cosmeticguardian.app.controller;

import com.cosmeticguardian.app.model.CompositionAnalysisRequest;
import com.cosmeticguardian.app.model.CompositionAnalysisResponse;
import com.cosmeticguardian.app.service.CosmeticAnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class CosmeticAnalysisController {

    private final CosmeticAnalysisService analysisService;

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
}