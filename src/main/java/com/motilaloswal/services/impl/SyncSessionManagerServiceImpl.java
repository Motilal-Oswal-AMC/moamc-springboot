package com.motilaloswal.services.impl;

import com.motilaloswal.dto.SyncSession;
import com.motilaloswal.services.AlertingService;
import com.motilaloswal.services.SyncSessionManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class SyncSessionManagerServiceImpl implements SyncSessionManagerService {

    private final Map<String, SyncSession> activeSessions = new ConcurrentHashMap<>();
    
    @Autowired
    private AlertingService alertingService;

    @Override
    public String startSession() {
        String syncId = UUID.randomUUID().toString();
        activeSessions.put(syncId, new SyncSession(syncId));
        return syncId;
    }

    @Override
    public void recordFailure(String syncId, String detail) {
        if (activeSessions.containsKey(syncId)) {
            activeSessions.get(syncId).addFailure(detail);
        }
    }

    @Override
    public void recordSkip(String syncId, String detail) {
        if (activeSessions.containsKey(syncId)) {
            activeSessions.get(syncId).addSkip(detail);
        }
    }

    @Override
    public void finishSession(String syncId) {
        SyncSession session = activeSessions.remove(syncId);
        if (session != null && session.hasIssues()) {
            // If there were any failures or skips, send the consolidated report.
            alertingService.sendConsolidatedReport(session);
        }
    }
}