package com.motilaloswal.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.motilaloswal.exceptions.AemClientException;
import com.motilaloswal.exceptions.AemUnavailableException;
import com.motilaloswal.services.AemTokenService;
import com.motilaloswal.services.PublicFundService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PublicFundServiceImpl implements PublicFundService {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PublicFundServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AemTokenService aemTokenService;

    // AEM URLs
    @Value("${aem.api.url.baseurl}")
    private String aemBaseURL;
    @Value("${aem.api.url.individualFundURL}")
    private String aemIndividualFundURL;
    @Value("${aem.api.url.filteredCFFundsURL}")
    private String aemFilteredCFFundsURL;
    @Value("${aem.api.url.individualFundManagerURL}")
    private String aemIndividualFundManagerURL;
    @Value("${aem.api.url.pmsListingURL}")
    private String AEM_PMS_LISTING_URL;
    @Value("${aem.api.url.pmsStrategyURL}")
    private String AEM_PMS_STRATEGY_URL;

    // Cache Expirations
    private static final long CACHE_TTL_1_HOUR = 1;
    private static final long CACHE_TTL_NEGATIVE = 1;

    @Override
    public String getFundListing() {
        final String cacheKey = "fundFilteredLists";
        String fullAemFilteredCFFundsUrl = aemBaseURL + aemFilteredCFFundsURL;

        // 1. Try cache first
        String listingJson = getFromCache(cacheKey);
        // if (listingJson != null) {
        //     LOG.info("Cache HIT for fundFilteredLists.");
        //     return listingJson;
        // }
        if (listingJson != null) {
            LOG.info("Cache HIT for fundFilteredLists.");
            try {
                ObjectMapper mapper = new ObjectMapper();
                // 1. Parse string into a JsonNode (assuming it's an array of objects)
                JsonNode rootNode = mapper.readTree(listingJson);

                // Check if it's an object first, then get the specific field
                if (rootNode.isObject()) {
                    JsonNode actualList = rootNode.get("cfDataObjs");
                    if (actualList != null && actualList.isArray()) {
                        // Run your sorting logic on 'actualList'
                            List<JsonNode> nfoFunds = new ArrayList<>();
                            List<JsonNode> regularFunds = new ArrayList<>();

                            // 2. Separate funds based on the "nfo" tag
                            for (JsonNode fund : actualList) {
                                boolean isNfo = false;
                                JsonNode taggingSection = fund.get("fundsTaggingSection");

                                if (taggingSection != null && taggingSection.isArray()) {
                                    for (JsonNode tag : taggingSection) {
                                        if (tag.asText().contains(":nfo")) {
                                            isNfo = true;
                                            break;
                                        }
                                    }
                                }

                                if (isNfo) {
                                    nfoFunds.add(fund);
                                } else {
                                    regularFunds.add(fund);
                                }
                            }

                            // 3. Reconstruct the list with NFOs at the top
                            ArrayNode sortedArray = mapper.createArrayNode();
                            sortedArray.addAll(nfoFunds);
                            sortedArray.addAll(regularFunds);

                            // 4. Convert back to String
                            listingJson = mapper.writeValueAsString(sortedArray);
                    }
                }

            } catch (Exception e) {
                LOG.error("Error processing JSON sorting", e);
                // Fallback to original listingJson if sorting fails
            }
            return listingJson;
        }

        // 2. Cache MISS -> Fallback to AEM
        LOG.warn("Cache MISS for fundFilteredLists. Calling AEM endpoint: {}", fullAemFilteredCFFundsUrl);

        try {
            String aemData = callAemGet(fullAemFilteredCFFundsUrl);
            cacheData(cacheKey, aemData, CACHE_TTL_1_HOUR);
            return aemData;
        } catch (HttpClientErrorException e) {
            LOG.warn("AEM returned an error ({}) for fund listing.", e.getStatusCode());
            throw new AemClientException("AEM returned an error", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            LOG.error("Failed to connect to AEM fallback at {}: {}", fullAemFilteredCFFundsUrl, e.getMessage());
            throw new AemUnavailableException("Could not connect to the data source.", e);
        }
    }

    @Override
    public String getIndividualFund(String schemeCode) {
        final String cacheKey = "fund:" + schemeCode;
        String fullAemUrl = UriComponentsBuilder.fromHttpUrl(aemBaseURL + aemIndividualFundURL)
                .queryParam("schcode", schemeCode)
                .toUriString();

        // 1. Try cache first
        String fundJson = getFromCache(cacheKey);
        if (fundJson != null) {
            LOG.info("Cache HIT for schcode: {}", schemeCode);
            return fundJson;
        }

        // 2. Cache MISS -> Fallback to AEM
        LOG.warn("Cache MISS for schcode: {}. Calling AEM endpoint: {}", schemeCode, fullAemUrl);
        try {
            String aemData = callAemGet(fullAemUrl);
            if (isIndividualFundResponseSuccessful(aemData)) {
                cacheData(cacheKey, aemData, 0);
            } else {
                LOG.info("Known failure response for schcode {}. Not caching.", schemeCode);
            }
            return aemData;

        } catch (HttpClientErrorException e) {
            LOG.warn("AEM returned an HTTP error ({}) for schcode: {}", e.getStatusCode(), schemeCode);
            throw new AemClientException("AEM returned an HTTP error", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            LOG.error("Failed to connect to AEM fallback at {}: {}", fullAemUrl, e.getMessage());
            throw new AemUnavailableException("Could not connect to the data source.", e);
        }
    }

    @Override
    public String getFundManager(String schemeCode) {
        final String cacheKey = "fundManager:" + schemeCode;
        String fullAemUrl = aemBaseURL + aemIndividualFundManagerURL;

        // 1. Try cache first
        String managerJson = getFromCache(cacheKey);
        if (managerJson != null) {
            LOG.info("Cache HIT for fundManager: {}", schemeCode);
            return managerJson;
        }

        // 2. Cache MISS -> Fallback to AEM
        LOG.warn("Cache MISS for fundManager: {}. Calling AEM endpoint: {}", schemeCode, fullAemUrl);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(aemTokenService.getAccessToken());

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("api_name", "GetFundMangerBySchemeId");
            requestBody.put("schcode", schemeCode);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> aemResponse = restTemplate.postForEntity(fullAemUrl, requestEntity, String.class);
            String aemData = aemResponse.getBody();

            if (isResponseSuccessful(aemData)) {
                cacheData(cacheKey, aemData, 0);
            } else {
                LOG.info("Known failure for fundManager {}. Not caching.", schemeCode);
            }
            return aemData;

       /* } catch (HttpClientErrorException.Unauthorized e) {
            LOG.warn("Token expired for fundManager call. Refreshing and retrying...");
            aemTokenService.forceRefresh();
            return getFundManager(schemeCode);
*/
        } catch (HttpClientErrorException e) {
            LOG.warn("AEM returned an HTTP error ({}) for fundManager schcode: {}", e.getStatusCode(), schemeCode);
            throw new AemClientException("AEM returned an HTTP error", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            LOG.error("Failed to connect to AEM fallback at {}: {}", fullAemUrl, e.getMessage());
            throw new AemUnavailableException("Could not connect to the data source.", e);
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

    private boolean isIndividualFundResponseSuccessful(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) return false;
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            if (root.has("error") && root.get("error").isJsonPrimitive()) {
                return !"not_found".equals(root.get("error").getAsString());
            }
            return true;
        } catch (JsonSyntaxException | IllegalStateException e) {
            LOG.warn("Could not parse AEM response to check for 'error' flag: {}", e.getMessage());
            return true;
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
}
