package com.helixcraft.kdtracker.model;

import net.minecraft.client.multiplayer.ServerData;

/**
 * Immutable record representing a server address.
 * Used as a unique identifier for multiplayer servers.
 */
public record ServerAddress(String address) {
    
    /**
     * Creates a ServerAddress from Minecraft's ServerData.
     * 
     * @param serverData The server data from Minecraft
     * @return A new ServerAddress instance
     */
    public static ServerAddress fromConnection(ServerData serverData) {
        return new ServerAddress(serverData.ip);
    }
}
