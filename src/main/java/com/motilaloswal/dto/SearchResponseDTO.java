package com.motilaloswal.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class SearchResponseDTO {
    private List<PageContent> funds = new ArrayList<>();
    private List<PageContent> blogs = new ArrayList<>();
    private List<PageContent> tools = new ArrayList<>();
    private List<PageContent> documents = new ArrayList<>();
    private List<PageContent> faqs = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private String redirectUrl;

    public void addFund(PageContent content) { funds.add(content); }
    public void addBlog(PageContent content) { blogs.add(content); }
    public void addTool(PageContent content) { tools.add(content); }
    public void addDocument(PageContent content) { documents.add(content); }
    public void addFaq(PageContent content) { faqs.add(content); }
    public void addSuggestion(String suggestion) { suggestions.add(suggestion); }
}
