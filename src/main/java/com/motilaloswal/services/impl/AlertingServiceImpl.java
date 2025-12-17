package com.motilaloswal.services.impl;

import com.motilaloswal.dto.SyncSession;
import com.motilaloswal.services.AlertingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertingServiceImpl implements AlertingService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${alerting.mail.to}")
    private String alertRecipient;

    @Value("${alerting.mail.from}")
    private String alertFrom;

    /**
     * Sends a formatted email alert for a Redis failure.
     * This method is marked as @Async to run in a background thread,
     * so it doesn't slow down the main application logic.
     */
    @Async
    @Override
    public void sendRedisFailureAlert(String failedKey, String operation, Exception exception) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(alertFrom);
            message.setTo(alertRecipient);
            message.setSubject("CRITICAL: Redis Cache Update Failure");

            String body = String.format(
                "A critical error occurred while trying to update the Redis cache.\n\n" +
                "Timestamp: %s\n" +
                "Operation: %s\n" +
                "Redis Key: %s\n" +
                "Error Type: %s\n" +
                "Error Message: %s\n\n" +
                "Please check the Spring Boot application logs for the full stack trace.",
                java.time.ZonedDateTime.now(),
                operation,
                failedKey,
                exception.getClass().getName(),
                exception.getMessage()
            );

            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // Log an error if the alert email itself fails to send
            log.error("FATAL: Could not send alert email. " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendSkippedUpdateWarning(String operation, String reason, String payload) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(alertFrom);
            message.setTo(alertRecipient);
            message.setSubject("[WARN] Redis Cache Update Skipped"); // Different subject

            String body = String.format(
                    "A Redis cache update was skipped due to a business rule.\n\n" +
                            "Timestamp: %s\n" +
                            "Operation: %s\n" +
                            "Reason for Skip: %s\n\n" +
                            "--- Received Payload ---\n%s",
                    java.time.ZonedDateTime.now(),
                    operation,
                    reason,
                    payload
            );

            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("FATAL: Could not send SKIPPED warning email. " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendConsolidatedReport(SyncSession session) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(alertFrom);
            message.setTo(alertRecipient);
            message.setSubject("Spring Boot: Redis Sync Issues Report for Session " + session.getSyncId());

            StringBuilder body = new StringBuilder();
            body.append("The following issues were reported during the Redis sync session:\n\n");

            if (!session.getFailures().isEmpty()) {
                body.append("--- FAILURES ---\n");
                session.getFailures().forEach(f -> body.append("- ").append(f).append("\n"));
                body.append("\n");
            }

            if (!session.getSkips().isEmpty()) {
                body.append("--- SKIPS ---\n");
                session.getSkips().forEach(s -> body.append("- ").append(s).append("\n"));
            }

            message.setText(body.toString());
            mailSender.send(message);
        } catch (Exception e) {
            log.error("FATAL: Could not send consolidated alert email.", e);
        }
    }
}