package com.motilaloswal.services;

import com.motilaloswal.exceptions.AemClientException;

public interface PublicFundService {

    String getFundListing();

    String getIndividualFund(String schemeCode);

    String getFundManager(String schemeCode);
}