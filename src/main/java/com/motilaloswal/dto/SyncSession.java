package com.motilaloswal.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SyncSession {
    private final String syncId;
    private final Instant startTime;
    private final List<String> failures = new ArrayList<>();
    private final List<String> skips = new ArrayList<>();

    public SyncSession(String syncId) {
        this.syncId = syncId;
        this.startTime = Instant.now();
    }

    public void addFailure(String detail) { failures.add(detail); }
    public void addSkip(String detail) { skips.add(detail); }
    public String getSyncId() { return syncId; }
    public List<String> getFailures() { return failures; }
    public List<String> getSkips() { return skips; }
    public boolean hasIssues() { return !failures.isEmpty() || !skips.isEmpty(); }
}