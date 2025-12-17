package com.motilaloswal.controller;

import com.motilaloswal.services.PerformanceGraphNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/v1/performancegraphnew")
public class PerformanceGraphNewController {

    @Autowired
    private PerformanceGraphNewService performanceGraphNewService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<String> getPerformanceGraph(@RequestBody Map<String, Object> requestBody) {
        String response = performanceGraphNewService.getPerformanceGraph(requestBody);
        return ResponseEntity.ok(response);
    }
}
