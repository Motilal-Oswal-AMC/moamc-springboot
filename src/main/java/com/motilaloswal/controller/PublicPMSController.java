package com.motilaloswal.controller;

import com.motilaloswal.services.PublicFundService;
import com.motilaloswal.services.PublicPMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/pms")
@CrossOrigin(origins = "*")
public class PublicPMSController {
    @Autowired
    private PublicPMSService pmsService;

    @GetMapping(value = "/listing", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPMSListing() {
        String listingJson = pmsService.getPMSListing();
        return ResponseEntity.ok(listingJson);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPMSStrategy(@RequestParam("schcode") String schemeCode) {
        String strategyJson = pmsService.getPMSStrategy(schemeCode);
        return ResponseEntity.ok(strategyJson);
    }
}