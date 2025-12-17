package com.motilaloswal.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motilaloswal.dto.PageContent;
import com.motilaloswal.services.IndexContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.delete.DeleteRequest; // Import this
import org.opensearch.action.delete.DeleteResponse; // Import this
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexContentServiceImpl implements IndexContentService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Value("${opensearch.index-name}")
    private String indexName;

    @Override
    public String syncData(PageContent content) {
        try {
            // Upsert Logic (Create or Overwrite)
            IndexRequest request = new IndexRequest(indexName);
            request.id(content.getId()); // ID from AEM (e.g., /content/site/page)
            request.source(objectMapper.writeValueAsString(content), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            log.info("Indexed page: {} | Status: {}", content.getId(), response.status());
            return response.getResult().name();

        } catch (Exception e) {
            log.error("Error indexing data for ID: " + content.getId(), e);
            throw new RuntimeException("Indexing failed");
        }
    }

    @Override
    public String deleteData(String id) {
        try {
            log.info("Attempting to delete document with ID: {}", id);

            DeleteRequest request = new DeleteRequest(indexName, id);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);

            log.info("Deleted page: {} | Status: {}", id, response.status());
            return response.getResult().name(); // Returns DELETED or NOT_FOUND

        } catch (Exception e) {
            log.error("Error deleting data for ID: " + id, e);
            throw new RuntimeException("Deletion failed");
        }
    }
}