package com.motilaloswal.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.motilaloswal.services.PublicFundService;
import io.lettuce.core.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/public/v1/funds")
@CrossOrigin(origins = "*")
public class PublicFundController {
    @Autowired
    private PublicFundService publicFundService;

    /**
     * Gets the global fund listing.
     * Logic is delegated to PublicFundService.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping(value = "/listing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFundListing() {
        // All try-catch logic is now handled by @ControllerAdvice
        String listingJson = publicFundService.getFundListing();
        return ResponseEntity.ok(listingJson);
    }

    /**
     * Gets individual fund data from a query parameter.
     * Logic is delegated to PublicFundService.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getIndividualFund(@RequestParam("schcode") String schemeCode) {
        String fundJson = publicFundService.getIndividualFund(schemeCode);
        return ResponseEntity.ok(fundJson);
    }

    /**
     * Gets fund manager details from a query parameter.
     * Logic is delegated to PublicFundService.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping(value = "/manager", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getFundManager(@RequestParam("schcode") String schemeCode) {
        String managerJson = publicFundService.getFundManager(schemeCode);
        return ResponseEntity.ok(managerJson);
    }
}