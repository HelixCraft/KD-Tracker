package com.helixcraft.kdtracker.model;

/**
 * Immutable record representing a single kill or death event.
 * Contains the event type and Unix timestamp.
 */
public record Event(EventType type, long timestamp) {
    
    public static Event now(EventType type) {
        return new Event(type, System.currentTimeMillis() / 1000);
    }
    
    public static Event kill() {
        return now(EventType.KILL);
    }
    
    public static Event death() {
        return now(EventType.DEATH);
    }
}
