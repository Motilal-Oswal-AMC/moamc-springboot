package com.motilaloswal.controller;

import com.motilaloswal.dto.PageContent;
import com.motilaloswal.dto.SearchResponseDTO;
import com.motilaloswal.services.SearchContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/opensearch")
@RequiredArgsConstructor
public class SearchContentController {

    private final SearchContentService searchContentService;

    @GetMapping("/searchContent")
    public ResponseEntity<SearchResponseDTO> searchContent(@RequestParam String query) {
        return ResponseEntity.ok(searchContentService.searchData(query));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String query) {
        return ResponseEntity.ok(searchContentService.getSuggestions(query));
    }
}