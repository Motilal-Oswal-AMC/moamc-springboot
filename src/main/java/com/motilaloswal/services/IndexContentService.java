package com.motilaloswal.services;

import com.motilaloswal.dto.PageContent;

public interface IndexContentService {
    // Existing method for Create/Update
    String syncData(PageContent content);

    // NEW method for Deletion
    String deleteData(String id);
}