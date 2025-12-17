package com.motilaloswal.dto;

import lombok.Data;

@Data
public class PageContent {
    private String id;
    private String title;
    private String description;
    private String pageUrl;
    private String pageType;
    private String category;
    private java.util.List<String> tags;
    private String publishDate; // ISO 8601 format preferred
    private String content; // Full text content for indexing

    private CompletionSuggestionDTO suggest;

    @Data
    public static class CompletionSuggestionDTO {
        private java.util.List<String> input;
        private int weight;
    }
}