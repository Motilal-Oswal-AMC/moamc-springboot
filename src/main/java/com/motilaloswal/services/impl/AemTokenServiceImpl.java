package com.motilaloswal.services.impl;

import com.motilaloswal.services.AemTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class AemTokenServiceImpl implements AemTokenService{
    private static final Logger LOG = LoggerFactory.getLogger(AemTokenService.class);

    @Value("${adobe.ims.clientId}")
    private String clientId;

    @Value("${adobe.ims.clientSecret}")
    private String clientSecret;

    @Value("${adobe.ims.scope}")
    private String scope;

    @Value("${adobe.ims.token.url}")
    private String imsTokenUrl;

    private final RestTemplate restTemplate;
    private String accessToken;
    private Instant expiryTime;

    public AemTokenServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiryTime)) {
            LOG.info("Access token expired or missing. Fetching new token from IMS...");
            refreshToken();
        }
        return accessToken;
    }

    @Override
    public synchronized void forceRefresh() {
        LOG.info("Force refreshing AEM IMS access token...");
        refreshToken();
    }

    @Override
    public void refreshToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                      "&client_secret=" + clientSecret +
                      "&grant_type=client_credentials" +
                      "&scope=" + scope;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                imsTokenUrl, HttpMethod.POST, request, Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new RuntimeException("Failed to get access token from IMS");
        }

        this.accessToken = (String) responseBody.get("access_token");
        int expiresIn = ((Number) responseBody.get("expires_in")).intValue(); // usually in seconds
        this.expiryTime = Instant.now().plusSeconds(expiresIn - 60); // renew 1 minute early

        LOG.info("IMS token refreshed successfully. Expires in {} seconds", expiresIn);
    }
}
