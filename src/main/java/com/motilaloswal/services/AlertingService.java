package com.motilaloswal.services;

import com.motilaloswal.dto.SyncSession;
import org.springframework.scheduling.annotation.Async;

public interface AlertingService {
    @Async
    void sendRedisFailureAlert(String failedKey, String operation, Exception exception);

    @Async
    void sendSkippedUpdateWarning(String operation, String reason, String payload);

    @Async
    void sendConsolidatedReport(SyncSession session);
}
