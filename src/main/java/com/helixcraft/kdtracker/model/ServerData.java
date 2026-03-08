package com.helixcraft.kdtracker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable class representing all data for a single server.
 * Contains the list of events and the best streak value.
 */
public class ServerData {
    private List<Event> events;
    private int bestStreak;
    
    /**
     * Creates a new ServerData with empty events and zero best streak.
     */
    public ServerData() {
        this.events = new ArrayList<>();
        this.bestStreak = 0;
    }
    
    /**
     * Creates a new ServerData with specified events and best streak.
     * 
     * @param events The list of events
     * @param bestStreak The best streak value
     */
    public ServerData(List<Event> events, int bestStreak) {
        this.events = new ArrayList<>(events);
        this.bestStreak = bestStreak;
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    public int getBestStreak() {
        return bestStreak;
    }
    
    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }
    
    /**
     * Adds an event to this server's event list.
     * 
     * @param event The event to add
     */
    public void addEvent(Event event) {
        this.events.add(event);
    }
    
    /**
     * Updates the best streak if the current value is higher.
     * 
     * @param currentStreak The current streak to compare
     */
    public void updateBestStreak(int currentStreak) {
        if (currentStreak > this.bestStreak) {
            this.bestStreak = currentStreak;
        }
    }
    
    /**
     * Gets all-time statistics for this server.
     * 
     * @return ServerStats with all-time data
     */
    public ServerStats getAllTimeStats() {
        return getStatsForFilter(TimeFilter.ALL_TIME);
    }
    
    /**
     * Gets statistics for this server filtered by time.
     * 
     * @param filter The time filter to apply
     * @return ServerStats with filtered data
     */
    public ServerStats getStatsForFilter(TimeFilter filter) {
        long startTimestamp = filter.getStartTimestamp();
        int kills = 0;
        int deaths = 0;
        
        for (Event event : events) {
            if (event.timestamp() >= startTimestamp) {
                if (event.type() == EventType.KILL) {
                    kills++;
                } else if (event.type() == EventType.DEATH) {
                    deaths++;
                }
            }
        }
        
        double kd = deaths == 0 ? (kills > 0 ? kills : 0.0) : (double) kills / deaths;
        return new ServerStats(kills, deaths, kd);
    }
}
