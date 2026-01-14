package com.motilaloswal.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.motilaloswal.exceptions.AemClientException;
import com.motilaloswal.exceptions.AemUnavailableException;
import com.motilaloswal.services.AemTokenService;
import com.motilaloswal.services.PublicFundService;
import com.motilaloswal.services.PublicPMSService;
import io.lettuce.core.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PublicPMSServiceImpl implements PublicPMSService {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PublicPMSServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AemTokenService aemTokenService;

    // AEM URLs
    @Value("${aem.api.url.baseurl}")
    private String aemBaseURL;
    @Value("${aem.api.url.pmsListingURL}")
    private String AEM_PMS_LISTING_URL;
    @Value("${aem.api.url.pmsStrategyURL}")
    private String AEM_PMS_STRATEGY_URL;
    @Value("${aem.api.url.pmsManagerURL}")
    private String AEM_PMS_MANAGER_URL;

    // Cache Expirations
    private static final long CACHE_TTL_1_HOUR = 1;

    @Override
    public String getPMSListing() {
        final String cacheKey = "pms-strategy-list";
        String fullAemUrl = aemBaseURL + AEM_PMS_LISTING_URL;

        // 1. Try cache
        String listingJson = getFromCache(cacheKey);
        if (listingJson != null) {
            LOG.info("Cache HIT for PMS Listing.");
            return listingJson;
        }

        // 2. Cache MISS -> Fallback to AEM
        LOG.warn("Cache MISS for PMS Listing. Calling AEM: {}", fullAemUrl);
        try {
            String aemData = callAemGet(fullAemUrl);
            cacheData(cacheKey, aemData, CACHE_TTL_1_HOUR);
            return aemData;
        } catch (HttpClientErrorException e) {
            LOG.warn("AEM error ({}) for PMS Listing.", e.getStatusCode());
            throw new AemClientException("AEM error", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            LOG.error("Failed to connect to AEM for PMS Listing: {}", e.getMessage());
            throw new AemUnavailableException("Could not connect to data source.", e);
        }
    }

    @Override
    public String getPMSStrategy(String schemeCode) {
        final String cacheKey = "pms-strategy:" + schemeCode;
        String fullAemUrl = UriComponentsBuilder.fromHttpUrl(aemBaseURL + AEM_PMS_STRATEGY_URL)
                .queryParam("schemeCode", schemeCode)
                .toUriString();

        // 1. Try cache
        String strategyJson = getFromCache(cacheKey);
        if (strategyJson != null) {
            LOG.info("Cache HIT for PMS Strategy: {}", schemeCode);
            return strategyJson;
        }

        // 2. Cache MISS -> Fallback to AEM
        LOG.warn("Cache MISS for PMS Strategy: {}. Calling AEM: {}", schemeCode, fullAemUrl);
        try {
            String aemData = callAemGet(fullAemUrl);
            // Basic validation check - could be improved
            if (aemData != null && !aemData.contains("\"error\"")) {
                cacheData(cacheKey, aemData, 0); // No expiry for individual items usually
            }
            return aemData;
        } catch (HttpClientErrorException e) {
            LOG.warn("AEM error ({}) for PMS Strategy: {}", e.getStatusCode(), schemeCode);
            throw new AemClientException("AEM error", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            LOG.error("Failed to connect to AEM for PMS Strategy: {}", e.getMessage());
            throw new AemUnavailableException("Could not connect to data source.", e);
        }
    }

    @Override
    public String getPMSManager(String schemeCode) {
        final String cacheKey = "pmsManager:" + schemeCode;
        String fullAemUrl = aemBaseURL + AEM_PMS_MANAGER_URL;

        // 1. Try cache first
        String managerJson = getFromCache(cacheKey);
        if (managerJson != null) {
            LOG.info("Cache HIT for pmsManager: {}", schemeCode);
            return managerJson;
        }

        // 2. Cache MISS -> Fallback to AEM
        LOG.warn("Cache MISS for pmsManager: {}. Calling AEM endpoint: {}", schemeCode, fullAemUrl);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(aemTokenService.getAccessToken());

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("api_name", "GetPMSMangerBySchemeId");
            requestBody.put("schcode", schemeCode);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> aemResponse = restTemplate.postForEntity(fullAemUrl, requestEntity, String.class);
            String aemData = aemResponse.getBody();

            if (isResponseSuccessful(aemData)) {
                cacheData(cacheKey, aemData, 0);
            } else {
                LOG.info("Known failure for pmsManager {}. Not caching.", schemeCode);
            }
            return aemData;

       /* } catch (HttpClientErrorException.Unauthorized e) {
            LOG.warn("Token expired for fundManager call. Refreshing and retrying...");
            aemTokenService.forceRefresh();
            return getFundManager(schemeCode);
*/
        } catch (HttpClientErrorException e) {
            LOG.warn("AEM returned an HTTP error ({}) for pmsManager schcode: {}", e.getStatusCode(), schemeCode);
            throw new AemClientException("AEM returned an HTTP error", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            LOG.error("Failed to connect to AEM fallback at {}: {}", fullAemUrl, e.getMessage());
            throw new AemUnavailableException("Could not connect to the data source.", e);
        }
    }

    private boolean isResponseSuccessful(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) return false;
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            if (root.has("data") && root.get("data").isJsonObject()) {
                JsonObject data = root.getAsJsonObject("data");
                if (data.has("success") && data.get("success").isJsonPrimitive()) {
                    return data.get("success").getAsBoolean();
                }
            }
            return true;
        } catch (JsonSyntaxException | IllegalStateException e) {
            LOG.warn("Could not parse AEM response to check for 'success' flag: {}", e.getMessage());
            return true;
        }
    }

    // -----------------------
    // Shared helper methods
    // -----------------------

    private String callAemGet(String url) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(aemTokenService.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            LOG.warn("Access token expired. Refreshing token and retrying GET for {}", url);
            throw new AemClientException("AEM returned an HTTP error", e.getStatusCode(), e.getResponseBodyAsString(), e);
           /* aemTokenService.forceRefresh();
            HttpHeaders retryHeaders = new HttpHeaders();
            retryHeaders.setBearerAuth(aemTokenService.getAccessToken());
            HttpEntity<Void> retryEntity = new HttpEntity<>(retryHeaders);
            ResponseEntity<String> retryResponse = restTemplate.exchange(url, HttpMethod.GET, retryEntity, String.class);
            return retryResponse.getBody();*/
        }
    }

    private String getFromCache(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (RedisException e) {
            LOG.error("Redis connection failed while GETTING key {}. Skipping cache.", key);
            return null;
        }
    }

    private void cacheData(String key, String value, long hours) {
        try {
            if (hours > 0) {
                redisTemplate.opsForValue().set(key, value, hours, TimeUnit.HOURS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            LOG.info("Cache SET for key {}", key);
        } catch (RedisException e) {
            LOG.error("Redis connection failed while SETTING key {}. Error: {}", key, e.getMessage());
        }
    }
}
