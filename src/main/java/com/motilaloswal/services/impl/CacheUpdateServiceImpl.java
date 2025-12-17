package com.motilaloswal.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motilaloswal.services.AlertingService;
import com.motilaloswal.services.CacheUpdateService;
import com.motilaloswal.services.SyncSessionManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheUpdateServiceImpl implements CacheUpdateService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AlertingService alertingService;
    @Autowired
    private SyncSessionManagerService sessionManager;

    @Override
    public void updateIndividualFund(JsonNode payload) {
        updateIndividualFund(payload, null);
    }

    @Override
    public void updateIndividualFund(JsonNode payload, String syncId) {
        String schemeCode = payload.path("schcode").asText(null);
        String redisKey = "fund:" + schemeCode;
        if (schemeCode == null) {
            handleError(syncId, "N/A", "IndividualFund Update", new IllegalArgumentException("Payload missing schcode"));
            return;
        }

        try {
            String fundJson = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(redisKey, fundJson);
            log.info("Successfully updated Redis for key: {}", redisKey);
        } catch (DataAccessException e) {
            handleError(syncId, redisKey, "IndividualFund Update", e);
        } catch (Exception e) {
            handleError(syncId, redisKey, "IndividualFund Update", e);
        }
    }

    @Override
    public void updateFundListing(JsonNode payload) {
        updateFundListing(payload, null);
    }

    @Override
    public void updateFundListing(JsonNode payload, String syncId) {
        String redisKey = "fundFilteredLists";
        log.info("Updating cache for FundListing...");
        try {
            String fundListJson = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(redisKey, fundListJson);
            log.info("Successfully updated FundListings in Redis.");
        } catch (DataAccessException e) {
            handleError(syncId, redisKey, "FundListing Update", e);
        } catch (Exception e) {
            handleError(syncId, redisKey, "FundListing Update", e);
        }
    }

    @Override
    public void updateFundManager(JsonNode payload) {
        updateFundManager(payload, null);
    }

    @Override
    public void updateFundManager(JsonNode payload, String syncId) {
        // The key for the manager data is the schemeCode, as per your AEM logic
        String schemeCode = payload.path("data").path("response").path("mangerDetails").get(0).path("managerSchcode").asText(null);
        String redisKey = "fundManager" + ":" + schemeCode;
        if (schemeCode == null) {
            log.error("Failed to update FundManager: payload is missing 'managerSchcode'.");
            return;
        }

        log.info("Updating cache for FundManager related to schcode: {}", schemeCode);
        try {
            String managerJson = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(redisKey, managerJson);
            log.info("Successfully updated Redis for key: fundmanager:{}", schemeCode);
        } catch (DataAccessException e) {
            handleError(syncId, redisKey, "FundManager Update", e);
        } catch (Exception e) {
            handleError(syncId, redisKey, "FundManager Update", e);
        }
    }

    @Override
    public void updatePMSStrategy(JsonNode payload, String syncId) {
        String schemeCode = payload.path("schemeCode").asText(null);
        String redisKey = "pms-strategy" + ":" + schemeCode;
        if (schemeCode == null) {
            handleError(syncId, "N/A", "PMS Strategy Update", new IllegalArgumentException("Payload missing schcode"));
            return;
        }

        log.info("Updating Redis for PMS Strategy: {}", schemeCode);
        try {
            String jsonVal = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(redisKey, jsonVal);
        } catch (Exception e) {
            handleError(syncId, redisKey, "PMS Strategy Update", e);
        }
    }

    @Override
    public void updatePMSListing(JsonNode payload, String syncId) {
        String redisKey = "pms-strategy-list";
        log.info("Updating Redis for PMS Listing");
        try {
            String jsonVal = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForValue().set(redisKey, jsonVal);
        } catch (Exception e) {
            handleError(syncId, redisKey, "PMS Listing Update", e);
        }
    }

    private void handleError(String syncId, String key, String operation, Exception e) {
        log.error("CRITICAL: Error during {} for key: {}", operation, key, e);
        if (syncId != null) {
            // If part of a session, record it for the consolidated report
            sessionManager.recordFailure(syncId, String.format("[%s] %s: %s", operation, key, e.getMessage()));
        }

        // ALWAYS throw exception to ensure AEM receives a 500 Error
        throw new RuntimeException("Redis Sync Failed: " + e.getMessage(), e);
    }
}