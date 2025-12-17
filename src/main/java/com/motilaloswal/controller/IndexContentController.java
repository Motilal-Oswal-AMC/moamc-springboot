package com.motilaloswal.controller;

import com.motilaloswal.dto.PageContent;
import com.motilaloswal.services.IndexContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/v1/opensearch")
@RequiredArgsConstructor
public class IndexContentController {

    private final IndexContentService indexContentService;

    // Handle ADDED and CHANGED (Upsert)
    @PostMapping("/indexContent")
    public ResponseEntity<String> indexContent(@RequestBody PageContent content) {
        String status = indexContentService.syncData(content);
        return ResponseEntity.ok("Sync Successful: " + status);
    }

    // Handle REMOVED (Delete)
    // URL will look like: DELETE /api/internal/v1/opensearch/indexContent?id=/content/path
    @DeleteMapping("/indexContent")
    public ResponseEntity<String> deleteContent(@RequestParam("id") String id) {
        String status = indexContentService.deleteData(id);
        return ResponseEntity.ok("Delete Successful: " + status);
    }
}