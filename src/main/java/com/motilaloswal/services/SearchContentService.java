package com.motilaloswal.services;

import com.motilaloswal.dto.PageContent;
import com.motilaloswal.dto.SearchResponseDTO;

import java.util.List;

public interface SearchContentService {
    SearchResponseDTO searchData(String searchTerm);
    List<String> getSuggestions(String query);
}
