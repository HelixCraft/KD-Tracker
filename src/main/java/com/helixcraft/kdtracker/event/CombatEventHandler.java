package com.helixcraft.kdtracker.event;

import com.helixcraft.kdtracker.KDTracker;
import com.helixcraft.kdtracker.service.DataManager;
import com.helixcraft.kdtracker.service.SessionTracker;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CombatEventHandler {
    private final DataManager dataManager;
    private final SessionTracker sessionTracker;
    private String currentServer = null;
    
    public CombatEventHandler(DataManager dataManager, SessionTracker sessionTracker) {
        this.dataManager = dataManager;
        this.sessionTracker = sessionTracker;
    }
    
    public void register() {
        // Listen for entity death events
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // This will be handled via mixin for better control
        });
    }
    
    public void onPlayerKill() {
        if (currentServer != null) {
            dataManager.recordKill(currentServer);
            sessionTracker.recordKill();
            
            // Update best streak
            int currentStreak = sessionTracker.getCurrentStreak();
            dataManager.getServerData(currentServer).updateBestStreak(currentStreak);
            dataManager.saveData();
            
            KDTracker.LOGGER.info("Player kill recorded on {}", currentServer);
        }
    }
    
    public void onPlayerDeath() {
        if (currentServer != null) {
            dataManager.recordDeath(currentServer);
            sessionTracker.recordDeath();
            KDTracker.LOGGER.info("Player death recorded on {}", currentServer);
        }
    }
    
    public void onServerJoin(String serverAddress) {
        this.currentServer = serverAddress;
        sessionTracker.reset();
        KDTracker.LOGGER.info("Joined server: {}", serverAddress);
    }
    
    public void onServerLeave() {
        if (currentServer != null) {
            KDTracker.LOGGER.info("Left server: {}", currentServer);
            this.currentServer = null;
            sessionTracker.reset();
        }
    }
    
    public String getCurrentServer() {
        return currentServer;
    }
    
    public boolean isOnMultiplayerServer() {
        return currentServer != null;
    }
}
