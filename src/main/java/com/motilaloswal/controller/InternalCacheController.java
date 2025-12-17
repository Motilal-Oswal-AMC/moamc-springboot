package com.motilaloswal.controller;

import com.motilaloswal.services.CacheUpdateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.motilaloswal.services.SyncSessionManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/v1/sync")
public class InternalCacheController {

    @Autowired
    private CacheUpdateService cacheUpdateService;

    @Autowired
    private SyncSessionManagerService sessionManager;

    @PostMapping("/session/start")
    public ResponseEntity<String> startSyncSession() {
        String syncId = sessionManager.startSession();
        return ResponseEntity.ok(syncId);
    }

    @PostMapping("/session/finish")
    public ResponseEntity<Void> finishSyncSession(@RequestHeader("X-Sync-ID") String syncId) {
        sessionManager.finishSession(syncId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/individual-fund")
    public ResponseEntity<Void> syncIndividualFund(@RequestBody JsonNode payload) {
        cacheUpdateService.updateIndividualFund(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fund-listing")
    public ResponseEntity<Void> syncFundListing(@RequestBody JsonNode payload) {
        cacheUpdateService.updateFundListing(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fund-manager")
    public ResponseEntity<Void> syncFundManager(@RequestBody JsonNode payload) {
        cacheUpdateService.updateFundManager(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pms-strategy")
    public ResponseEntity<Void> syncPMSStrategy(@RequestBody JsonNode payload, @RequestHeader(value = "X-Sync-ID", required = false) String syncId) {
        cacheUpdateService.updatePMSStrategy(payload, syncId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pms-listing")
    public ResponseEntity<Void> syncPMSListing(@RequestBody JsonNode payload, @RequestHeader(value = "X-Sync-ID", required = false) String syncId) {
        cacheUpdateService.updatePMSListing(payload, syncId);
        return ResponseEntity.ok().build();
    }
}