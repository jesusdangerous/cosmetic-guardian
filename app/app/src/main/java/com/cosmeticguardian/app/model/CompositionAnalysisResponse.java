package com.cosmeticguardian.app.model;


import lombok.Data;
import java.util.List;

@Data
public class CompositionAnalysisResponse {
    private Double safetyScore;
    private String safetyStatus;
    private List<IngredientAnalysis> ingredientsAnalysis;
    private List<String> recommendations;
    private List<String> suitableFor;

    @Data
    public static class IngredientAnalysis {
        private String name;
        private String origin;
        private Double safetyScore;
        private List<String> benefits;
        private List<String> risks;
        private String safetyStatus;
    }
}
