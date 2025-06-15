package com.cosmeticguardian.app.model;

import lombok.Data;

import java.util.List;

@Data
public class DeepSeekResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
        private Integer index;
        private String finish_reason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}