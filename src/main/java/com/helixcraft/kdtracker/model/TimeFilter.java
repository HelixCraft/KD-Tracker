package com.helixcraft.kdtracker.model;

/**
 * Enum representing time filter options for statistics.
 * Each filter defines a time window for filtering events.
 */
public enum TimeFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    ALL_TIME;
    
    /**
     * Gets the start timestamp (Unix time in seconds) for this filter.
     * Events with timestamps >= this value should be included.
     * 
     * @return The start timestamp in Unix seconds
     */
    public long getStartTimestamp() {
        long now = System.currentTimeMillis() / 1000;
        return switch (this) {
            case TODAY -> now - 86400;        // 24 hours
            case THIS_WEEK -> now - 604800;   // 7 days
            case THIS_MONTH -> now - 2592000; // 30 days
            case ALL_TIME -> 0;               // Beginning of time
        };
    }
}
