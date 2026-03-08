package com.helixcraft.kdtracker.service;

public class SessionTracker {
    private int sessionKills = 0;
    private int sessionDeaths = 0;
    private int currentStreak = 0;
    
    public void recordKill() {
        sessionKills++;
        currentStreak++;
    }
    
    public void recordDeath() {
        sessionDeaths++;
        currentStreak = 0;
    }
    
    public void reset() {
        sessionKills = 0;
        sessionDeaths = 0;
        currentStreak = 0;
    }
    
    public int getSessionKills() {
        return sessionKills;
    }
    
    public int getSessionDeaths() {
        return sessionDeaths;
    }
    
    public int getCurrentStreak() {
        return currentStreak;
    }
    
    public double getSessionKD() {
        if (sessionDeaths == 0) {
            return sessionKills > 0 ? sessionKills : 0;
        }
        return (double) sessionKills / sessionDeaths;
    }
}
