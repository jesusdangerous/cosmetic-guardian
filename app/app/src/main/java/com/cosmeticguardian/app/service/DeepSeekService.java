package com.cosmeticguardian.app.service;

import com.cosmeticguardian.app.config.DeepSeekConfig;
import com.cosmeticguardian.app.model.DeepSeekRequest;
import com.cosmeticguardian.app.model.DeepSeekResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class DeepSeekService {

    private final RestTemplate restTemplate;
    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public DeepSeekService(RestTemplate restTemplate, DeepSeekConfig deepSeekConfig, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.deepSeekConfig = deepSeekConfig;
        this.objectMapper = objectMapper;
    }

    public String analyzeIngredients(String ingredients) {
        String prompt = "Проанализируй состав косметического средства и предоставь ответ в строго заданном формате JSON.\n" +
                "Ингредиенты: " + ingredients + "\n\n" +
                "Формат ответа должен быть строго следующим:\n" +
                "{\n" +
                "  \"safetyScore\": 85.5,\n" +
                "  \"safetyStatus\": \"safe\",\n" +
                "  \"ingredientsAnalysis\": [\n" +
                "    {\n" +
                "      \"name\": \"Aqua\",\n" +
                "      \"origin\": \"natural\",\n" +
                "      \"safetyScore\": 95.0,\n" +
                "      \"benefits\": [\"Увлажнение\", \"База для косметических средств\"],\n" +
                "      \"risks\": [],\n" +
                "      \"safetyStatus\": \"safe\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"recommendations\": [\"Подходит для ежедневного использования\", \"Не вызывает раздражения\"],\n" +
                "  \"suitableFor\": [\"Все типы кожи\", \"Чувствительная кожа\"]\n" +
                "}";

        DeepSeekRequest request = new DeepSeekRequest();
        request.setMessages(Collections.singletonList(
                new DeepSeekRequest.Message("user", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<DeepSeekResponse> response = restTemplate.postForEntity(
                deepSeekConfig.getApiUrl(),
                entity,
                DeepSeekResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        } else {
            throw new RuntimeException("Failed to get response from DeepSeek API");
        }
    }
}