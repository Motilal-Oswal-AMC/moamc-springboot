package com.motilaloswal.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motilaloswal.dto.PageContent;
import com.motilaloswal.dto.SearchResponseDTO;
import com.motilaloswal.services.SearchContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchContentServiceImpl implements SearchContentService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Value("${opensearch.index-name}")
    private String indexName;

    private static final String REDIRECT_KEYWORD = "Account Statement";
    private static final String REPORT_HUB_URL = "https://www.motilaloswalmf.com/reporthub";

    @Override
    public SearchResponseDTO searchData(String searchTerm) {
        SearchResponseDTO responseDTO = new SearchResponseDTO();

        // 1. Handle specific redirect case
        if (REDIRECT_KEYWORD.equalsIgnoreCase(searchTerm.trim())) {
            responseDTO.setRedirectUrl(REPORT_HUB_URL);
            return responseDTO;
        }

        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 2. Build Query
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // Multi-match with fuzziness and boosting
            boolQuery.must(QueryBuilders.multiMatchQuery(searchTerm)
                    .field("title", 2.0f) // Boost title
                    .field("description")
                    .field("content")
                    .field("tags")
                    .fuzziness(Fuzziness.AUTO)
                    .operator(Operator.AND)); // Prefer all terms to match

            sourceBuilder.query(boolQuery);
            sourceBuilder.size(50); // Fetch enough results to categorize

            searchRequest.source(sourceBuilder);

            // 3. Execute Search
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 4. Process Results
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                try {
                    PageContent content = objectMapper.readValue(hit.getSourceAsString(), PageContent.class);
                    categorizeResult(responseDTO, content);
                } catch (Exception e) {
                    log.error("Error parsing search hit", e);
                }
            }

        } catch (Exception e) {
            log.error("Error executing search", e);
        }

        return responseDTO;
    }

  /*  @Override
    public List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // Use Completion Suggester
            org.opensearch.search.suggest.completion.CompletionSuggestionBuilder suggestionBuilder =
                    org.opensearch.search.suggest.SuggestBuilders.completionSuggestion("suggest")
                            .prefix(query)
                            .skipDuplicates(true)
                            .size(5);

            org.opensearch.search.suggest.SuggestBuilder suggestBuilder = new org.opensearch.search.suggest.SuggestBuilder();
            suggestBuilder.addSuggestion("content_suggestion", suggestionBuilder);

            sourceBuilder.suggest(suggestBuilder);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Parse Suggestions
            org.opensearch.search.suggest.Suggest suggest = searchResponse.getSuggest();
            org.opensearch.search.suggest.completion.CompletionSuggestion completionSuggestion =
                    suggest.getSuggestion("content_suggestion");

            if (completionSuggestion != null) {
                java.util.Set<String> seenTitles = new java.util.HashSet<>();

                for (org.opensearch.search.suggest.completion.CompletionSuggestion.Entry entry : completionSuggestion.getEntries()) {
                    for (org.opensearch.search.suggest.completion.CompletionSuggestion.Entry.Option option : entry.getOptions()) {
                        // Extract the full document source to get the Title
                        if (option.getHit() != null && option.getHit().getSourceAsMap() != null) {
                            String fullTitle = (String) option.getHit().getSourceAsMap().get("title");
                            if (fullTitle != null && seenTitles.add(fullTitle)) {
                                suggestions.add(fullTitle);
                            }
                        } else {
                            // Fallback to matched text if hit is missing
                            String text = option.getText().string();
                            if (seenTitles.add(text)) {
                                suggestions.add(text);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error fetching suggestions", e);
        }
        return suggestions;
    }
*/

    @Override
    public List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // CHANGE: Instead of SuggestBuilder, use a QueryBuilder on the 'title' field
            sourceBuilder.query(org.opensearch.index.query.QueryBuilders
                    .matchPhrasePrefixQuery("title", query));

            sourceBuilder.size(5); // Limit results
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // CHANGE: Parse standard SearchHits instead of Suggest objects
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                Map<String, Object> source = hit.getSourceAsMap();
                if (source.containsKey("title")) {
                    suggestions.add((String) source.get("title"));
                }
            }
        } catch (Exception e) {
            log.error("Error fetching suggestions", e);
        }
        return suggestions;
    }

    private void categorizeResult(SearchResponseDTO responseDTO, PageContent content) {
        String category = content.getCategory();

        if (category == null || category.trim().isEmpty()) {
            responseDTO.addOther(content);
            return;
        }

        switch (category.trim().toLowerCase()) {

            case "fund":
            case "funds":
                responseDTO.addFund(content);
                break;

            case "blog":
            case "blogs":
                responseDTO.addBlog(content);
                break;

            case "tool":
            case "tools":
            case "calculator":
            case "calculators":
                responseDTO.addTool(content);
                break;

            case "faq":
            case "faqs":
                responseDTO.addFaq(content);
                break;

            case "document":
            case "documents":
                responseDTO.addDocument(content);
                break;

            default:
                responseDTO.addOther(content);
        }
    }
}
