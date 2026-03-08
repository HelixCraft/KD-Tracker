package com.helixcraft.kdtracker;

import com.helixcraft.kdtracker.event.CombatEventHandler;
import com.helixcraft.kdtracker.service.DataManager;
import com.helixcraft.kdtracker.service.SessionTracker;
import com.helixcraft.kdtracker.ui.StatsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KDTracker implements ClientModInitializer {
	public static final String MOD_ID = "kd-tracker";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	private static DataManager dataManager;
	private static SessionTracker sessionTracker;
	private static CombatEventHandler eventHandler;
	
	private static KeyMapping openStatsKey;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing KD Tracker mod");
		
		// Initialize services
		dataManager = new DataManager();
		sessionTracker = new SessionTracker();
		eventHandler = new CombatEventHandler(dataManager, sessionTracker);
		
		// Register keybinding
		openStatsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.kd-tracker.open_stats",
			GLFW.GLFW_KEY_K,
			"category.kd-tracker"
		));
		
		// Register tick event for keybinding
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openStatsKey.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new StatsScreen(null, dataManager, sessionTracker, eventHandler));
				}
			}
		});
		
		// Register event handler
		eventHandler.register();
		
		LOGGER.info("KD Tracker mod initialized successfully");
	}
	
	public static DataManager getDataManager() {
		return dataManager;
	}
	
	public static SessionTracker getSessionTracker() {
		return sessionTracker;
	}
	
	public static CombatEventHandler getEventHandler() {
		return eventHandler;
	}
}