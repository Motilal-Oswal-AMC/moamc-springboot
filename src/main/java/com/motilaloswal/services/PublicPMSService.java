package com.motilaloswal.services;

import com.motilaloswal.exceptions.AemClientException;

public interface PublicPMSService {
    String getPMSListing();

    String getPMSStrategy(String schemeCode);
}