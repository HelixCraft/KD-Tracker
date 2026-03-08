package com.helixcraft.kdtracker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.helixcraft.kdtracker.KDTracker;
import com.helixcraft.kdtracker.model.Event;
import com.helixcraft.kdtracker.model.ServerData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("serverkd");
    private static final Path DATA_FILE = CONFIG_DIR.resolve("data.json");
    
    private final Map<String, ServerData> serverDataMap = new HashMap<>();
    
    public DataManager() {
        loadData();
    }
    
    private void loadData() {
        try {
            if (Files.exists(DATA_FILE)) {
                String json = Files.readString(DATA_FILE);
                Map<String, ServerData> loaded = GSON.fromJson(json, 
                    new TypeToken<Map<String, ServerData>>(){}.getType());
                if (loaded != null) {
                    serverDataMap.putAll(loaded);
                }
                KDTracker.LOGGER.info("Loaded data for {} servers", serverDataMap.size());
            }
        } catch (IOException e) {
            KDTracker.LOGGER.error("Failed to load data", e);
        }
    }
    
    public void saveData() {
        try {
            Files.createDirectories(CONFIG_DIR);
            String json = GSON.toJson(serverDataMap);
            Files.writeString(DATA_FILE, json);
        } catch (IOException e) {
            KDTracker.LOGGER.error("Failed to save data", e);
        }
    }
    
    public ServerData getServerData(String address) {
        return serverDataMap.computeIfAbsent(address, k -> new ServerData());
    }
    
    public Map<String, ServerData> getAllServerData() {
        return new HashMap<>(serverDataMap);
    }
    
    public void recordKill(String address) {
        ServerData data = getServerData(address);
        data.addEvent(Event.kill());
        saveData();
    }
    
    public void recordDeath(String address) {
        ServerData data = getServerData(address);
        data.addEvent(Event.death());
        saveData();
    }
    
    public void resetServer(String address) {
        serverDataMap.put(address, new ServerData());
        saveData();
    }
    
    public void resetAll() {
        serverDataMap.clear();
        saveData();
    }
    
    public Path getExportDir() {
        return CONFIG_DIR;
    }
}
