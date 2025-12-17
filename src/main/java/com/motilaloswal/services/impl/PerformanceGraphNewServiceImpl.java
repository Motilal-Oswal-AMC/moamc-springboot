package com.motilaloswal.services.impl;

import com.motilaloswal.services.PerformanceGraphNewService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PerformanceGraphNewServiceImpl implements PerformanceGraphNewService {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceGraphNewServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${motilaloswal.api.url.performanceGraphNew}")
    private String externalApiUrl;

    @Override
    public String getPerformanceGraph(Map<String, Object> requestBody) {
        LOG.info("Calling external PerformanceGraphNew API: {}", externalApiUrl);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            LOG.info("Request Headers: {}", headers);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            LOG.info("Request Body: {}", requestBody);
            ResponseEntity<String> response = restTemplate.postForEntity(externalApiUrl, requestEntity, String.class);
            LOG.info("Received response from external API with status code: {}", response.getStatusCode());
            LOG.info("Response Body: {}", response.getBody());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            LOG.error("External API returned error: {}", e.getStatusCode());
            throw e; // Let controller advice handle it or wrap it
        } catch (RestClientException e) {
            LOG.error("Failed to connect to external API: {}", e.getMessage());
            throw new RuntimeException("External API unavailable", e);
        }
    }
}
