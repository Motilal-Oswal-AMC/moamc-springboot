package com.motilaloswal.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtility {

    // Define the IST ZoneId once to avoid recreating it
    private static final ZoneId IST_ZONE_ID = ZoneId.of("Asia/Kolkata");

    // Define a default formatter for common use cases
    private static final DateTimeFormatter DEFAULT_IST_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    /**
     * Converts a given UTC millisecond timestamp to a ZonedDateTime object in IST.
     *
     * @param utcMillis The timestamp in milliseconds from the Unix epoch (UTC).
     * @return A ZonedDateTime object representing the given timestamp in IST.
     */
    public static ZonedDateTime convertUtcMillisToIstZonedDateTime(long utcMillis) {
        Instant instant = Instant.ofEpochMilli(utcMillis);
        return instant.atZone(IST_ZONE_ID);
    }

    /**
     * Gets the current time in IST as a ZonedDateTime object.
     * This directly uses System.currentTimeMillis() internally.
     *
     * @return A ZonedDateTime object representing the current time in IST.
     */
    public static ZonedDateTime getCurrentIstZonedDateTime() {
        return Instant.now().atZone(IST_ZONE_ID);
    }

    /**
     * Formats a ZonedDateTime object (expected to be in IST) into a readable string
     * using the default "yyyy-MM-dd HH:mm:ss z" pattern.
     *
     * @param zonedDateTime The ZonedDateTime object to format.
     * @return A formatted string representation of the date and time in IST.
     */
    public static String formatIstDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DEFAULT_IST_FORMATTER);
    }

    /**
     * Converts a given UTC millisecond timestamp directly into a formatted string
     * representation in IST.
     *
     * @param utcMillis The timestamp in milliseconds from the Unix epoch (UTC).
     * @return A formatted string representation of the date and time in IST.
     */
    public static String formatUtcMillisToIstString(long utcMillis) {
        ZonedDateTime istDateTime = convertUtcMillisToIstZonedDateTime(utcMillis);
        return formatIstDateTime(istDateTime);
    }

    /**
     * Gets the current time in IST as a formatted string.
     * This combines getting the current time and formatting it in one step.
     *
     * @return A formatted string representation of the current time in IST.
     */
    public static String getCurrentIstFormattedString() {
        return formatIstDateTime(getCurrentIstZonedDateTime());
    }

    // You can add more formatters if needed
    public static String formatIstDateTime(ZonedDateTime zonedDateTime, String pattern) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(pattern);
        return zonedDateTime.format(customFormatter);
    }
}
