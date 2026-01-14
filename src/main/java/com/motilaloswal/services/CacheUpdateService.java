package com.motilaloswal.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface CacheUpdateService {
    void updateIndividualFund(JsonNode payload);

    void updateIndividualFund(JsonNode payload, String syncId);

    void updateFundListing(JsonNode payload);

    void updateFundListing(JsonNode payload, String syncId);

    void updateFundManager(JsonNode payload);

    void updateFundManager(JsonNode payload, String syncId);

    void updatePMSStrategy(JsonNode payload, String syncId);
    void updatePMSListing(JsonNode payload, String syncId);

    void updatePMSManager(JsonNode payload, String syncId);
}
