package com.motilaloswal.services;

public interface AemTokenService {
    String getAccessToken();

    void forceRefresh();

    void refreshToken();
}
