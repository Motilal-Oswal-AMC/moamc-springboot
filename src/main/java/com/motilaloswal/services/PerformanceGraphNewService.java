package com.motilaloswal.services;

import java.util.Map;

public interface PerformanceGraphNewService {
    /**
     * Pass-through call to the external PerformanceGraphNew API.
     * @param requestBody The JSON body to send to the external API.
     * @return The raw JSON string response from the external API.
     */
    String getPerformanceGraph(Map<String, Object> requestBody);
}
