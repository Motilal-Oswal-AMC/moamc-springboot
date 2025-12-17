package com.motilaloswal.services;

public interface SyncSessionManagerService {
    String startSession();

    void recordFailure(String syncId, String detail);

    void recordSkip(String syncId, String detail);

    void finishSession(String syncId);
}
