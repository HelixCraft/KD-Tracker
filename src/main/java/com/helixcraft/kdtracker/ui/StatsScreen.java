package com.helixcraft.kdtracker.ui;

import com.helixcraft.kdtracker.event.CombatEventHandler;
import com.helixcraft.kdtracker.model.ExportFormat;
import com.helixcraft.kdtracker.model.ServerData;
import com.helixcraft.kdtracker.model.ServerStats;
import com.helixcraft.kdtracker.model.TimeFilter;
import com.helixcraft.kdtracker.service.DataManager;
import com.helixcraft.kdtracker.service.ExportService;
import com.helixcraft.kdtracker.service.SessionTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class StatsScreen extends Screen {
    private final DataManager dataManager;
    private final SessionTracker sessionTracker;
    private final CombatEventHandler eventHandler;
    private final Screen parent;
    private final ExportService exportService;
    
    private boolean showAllServers = false;
    private EditBox searchBox;
    private TimeFilter currentFilter = TimeFilter.ALL_TIME;
    private ExportFormat exportFormat = ExportFormat.TXT;
    private String statusMessage = null;
    private int statusMessageTimer = 0;
    
    // Confirmation dialog state
    private boolean showConfirmDialog = false;
    private String confirmDialogServer = null; // null = reset all, otherwise = specific server
    private String confirmDialogText = "";
    private java.util.List<Button> serverResetButtons = new java.util.ArrayList<>();
    
    // GUI Layout Constants
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_PADDING = 20;
    private static final int SECTION_PADDING = 6;
    private static final int SECTION_SPACING = 10;
    
    // Colors
    private static final int COLOR_PANEL_BG = 0xCC000000;
    private static final int COLOR_PANEL_BORDER = 0xFF555555;
    private static final int COLOR_SECTION_BG = 0xAA000000;
    private static final int COLOR_SECTION_BORDER = 0xFF444444;
    private static final int COLOR_SEPARATOR = 0xFF333333;
    private static final int COLOR_HEADER = 0xFFFFAA00;
    private static final int COLOR_LABEL = 0xFFAAAAAA;
    private static final int COLOR_VALUE = 0xFFFFFFFF;
    private static final int COLOR_SERVER_PILL = 0xDD000000;
    private static final int COLOR_TAB_ACTIVE = 0xFF666666;
    private static final int COLOR_TAB_INACTIVE = 0xFF333333;
    private static final int COLOR_TAB_HIGHLIGHT = 0xFFFFAA00;
    
    public StatsScreen(Screen parent, DataManager dataManager, SessionTracker sessionTracker, CombatEventHandler eventHandler) {
        super(Component.literal("KD Tracker Stats"));
        this.parent = parent;
        this.dataManager = dataManager;
        this.sessionTracker = sessionTracker;
        this.eventHandler = eventHandler;
        this.exportService = new ExportService();
    }
    
    private int getPanelLeft() {
        return (this.width - PANEL_WIDTH) / 2;
    }
    
    private int getPanelTop() {
        return 40;
    }
    
    private int getPanelHeight() {
        return this.height - 80;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int panelLeft = getPanelLeft();
        int panelTop = getPanelTop();
        int panelHeight = getPanelHeight();
        
        // Clear server reset buttons list
        serverResetButtons.clear();
        
        // Tab toggle buttons - styled as tabs
        int tabY = panelTop - 25;
        this.addRenderableWidget(Button.builder(
            Component.literal("Current Server"),
            button -> {
                showAllServers = false;
                this.rebuildWidgets();
            }
        ).bounds(panelLeft, tabY, 100, 20).build());
        
        this.addRenderableWidget(Button.builder(
            Component.literal("All Servers"),
            button -> {
                showAllServers = true;
                this.rebuildWidgets();
            }
        ).bounds(panelLeft + 105, tabY, 100, 20).build());
        
        // Search box for all servers view
        if (showAllServers) {
            searchBox = new EditBox(this.font, panelLeft + PANEL_PADDING, panelTop + 80, PANEL_WIDTH - PANEL_PADDING * 2, 20, Component.literal("Search"));
            searchBox.setHint(Component.literal("Search servers..."));
            this.addRenderableWidget(searchBox);
            
            // Time filter buttons
            int filterX = panelLeft + PANEL_PADDING;
            int filterY = panelTop + 110;
            for (TimeFilter filter : TimeFilter.values()) {
                this.addRenderableWidget(Button.builder(
                    Component.literal(getFilterLabel(filter)),
                    button -> {
                        currentFilter = filter;
                        this.rebuildWidgets();
                    }
                ).bounds(filterX, filterY, 70, 20).build());
                filterX += 75;
            }
            
            // Add per-server reset buttons
            addServerResetButtons(panelLeft, panelTop, panelHeight);
            
            // Bottom buttons - properly centered within panel
            int buttonWidth = (PANEL_WIDTH - PANEL_PADDING * 2 - 10) / 2;
            int bottomY = panelTop + panelHeight - 45;
            
            // Export button
            this.addRenderableWidget(Button.builder(
                Component.literal("Export " + exportFormat.name()),
                button -> exportData()
            ).bounds(panelLeft + PANEL_PADDING, bottomY, buttonWidth, 20).build());
            
            // Reset all button
            this.addRenderableWidget(Button.builder(
                Component.literal("Reset All"),
                button -> showResetAllConfirmation()
            ).bounds(panelLeft + PANEL_PADDING + buttonWidth + 10, bottomY, buttonWidth, 20).build());
            
            // Close button - centered
            int closeButtonWidth = PANEL_WIDTH - PANEL_PADDING * 2;
            this.addRenderableWidget(Button.builder(
                Component.literal("Close"),
                button -> this.onClose()
            ).bounds(panelLeft + PANEL_PADDING, panelTop + panelHeight - 20, closeButtonWidth, 20).build());
        } else {
            // Close button for Current Server tab - centered within panel
            int closeButtonWidth = PANEL_WIDTH - PANEL_PADDING * 2;
            this.addRenderableWidget(Button.builder(
                Component.literal("Close"),
                button -> this.onClose()
            ).bounds(panelLeft + PANEL_PADDING, panelTop + panelHeight - 20, closeButtonWidth, 20).build());
        }
    }
    
    /**
     * Adds reset buttons for each server in the All Servers view
     */
    private void addServerResetButtons(int panelLeft, int panelTop, int panelHeight) {
        Map<String, ServerData> allData = dataManager.getAllServerData();
        String searchText = searchBox != null ? searchBox.getValue().toLowerCase() : "";
        
        int contentX = panelLeft + PANEL_PADDING;
        int currentY = panelTop + 145;
        int listHeight = panelHeight - 165;
        int maxY = panelTop + listHeight;
        
        for (Map.Entry<String, ServerData> entry : allData.entrySet()) {
            String serverAddress = entry.getKey();
            
            if (!searchText.isEmpty() && !serverAddress.toLowerCase().contains(searchText)) {
                continue;
            }
            
            if (currentY + 40 > maxY) break;
            
            // Add reset button for this server
            Button resetButton = Button.builder(
                Component.literal("Reset"),
                button -> showResetServerConfirmation(serverAddress)
            ).bounds(contentX + PANEL_WIDTH - PANEL_PADDING * 2 - 55, currentY + 7, 50, 20).build();
            
            this.addRenderableWidget(resetButton);
            serverResetButtons.add(resetButton);
            
            currentY += 40;
        }
    }
    
    /**
     * Note: renderBlurredBackground() override removed for MC 1.21-1.21.2 compatibility.
     * BlurMixin handles blur cancellation for these versions.
     */
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // ✅ STEP 1: Render background blur FIRST
        // This blurs ONLY the game world behind the screen
        // The framebuffer is otherwise empty at this point
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        
        int panelLeft = getPanelLeft();
        int panelTop = getPanelTop();
        int panelHeight = getPanelHeight();
        
        // ✅ STEP 2: Draw ALL custom content AFTER the blur
        // Everything drawn here will be SHARP and unblurred
        
        // Draw main background panel with border
        drawPanel(graphics, panelLeft, panelTop, PANEL_WIDTH, panelHeight);
        
        // Draw tab highlights
        drawTabHighlights(graphics, panelLeft, panelTop);
        
        // Render content based on current view
        if (showAllServers) {
            renderAllServersView(graphics, panelLeft, panelTop, panelHeight);
        } else {
            renderCurrentServerView(graphics, panelLeft, panelTop);
        }
        
        // ✅ STEP 3: Render widgets (buttons, search box) LAST
        // super.render() only renders registered renderables in 1.21.4
        // It does NOT call renderBackground() again
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Render confirmation dialog on top of everything
        if (showConfirmDialog) {
            renderConfirmationDialog(graphics, mouseX, mouseY);
        }
        
        // Render status message
        if (statusMessage != null && statusMessageTimer > 0) {
            graphics.drawCenteredString(this.font, Component.literal(statusMessage),
                this.width / 2, this.height - 80, 0x00FF00);
            statusMessageTimer--;
        }
    }
    
    /**
     * Draws a panel with background and border
     */
    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        // Background
        graphics.fill(x, y, x + width, y + height, COLOR_PANEL_BG);
        
        // Border
        graphics.fill(x, y, x + width, y + 1, COLOR_PANEL_BORDER); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, COLOR_PANEL_BORDER); // Bottom
        graphics.fill(x, y, x + 1, y + height, COLOR_PANEL_BORDER); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, COLOR_PANEL_BORDER); // Right
    }
    
    /**
     * Draws a section box with background and border
     */
    private void drawSectionBox(GuiGraphics graphics, int x, int y, int width, int height) {
        // Background
        graphics.fill(x, y, x + width, y + height, COLOR_SECTION_BG);
        
        // Border
        graphics.fill(x, y, x + width, y + 1, COLOR_SECTION_BORDER); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, COLOR_SECTION_BORDER); // Bottom
        graphics.fill(x, y, x + 1, y + height, COLOR_SECTION_BORDER); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, COLOR_SECTION_BORDER); // Right
    }
    
    /**
     * Draws a horizontal separator line
     */
    private void drawSeparator(GuiGraphics graphics, int x, int y, int width) {
        graphics.fill(x, y, x + width, y + 1, COLOR_SEPARATOR);
    }
    
    /**
     * Draws tab highlights to show active/inactive state
     */
    private void drawTabHighlights(GuiGraphics graphics, int panelLeft, int panelTop) {
        int tabY = panelTop - 25;
        
        // Draw highlight under active tab
        if (!showAllServers) {
            graphics.fill(panelLeft, tabY + 20, panelLeft + 100, tabY + 22, COLOR_TAB_HIGHLIGHT);
        } else {
            graphics.fill(panelLeft + 105, tabY + 20, panelLeft + 205, tabY + 22, COLOR_TAB_HIGHLIGHT);
        }
    }
    
    private void renderCurrentServerView(GuiGraphics graphics, int panelLeft, int panelTop) {
        String currentServer = eventHandler.getCurrentServer();
        
        if (currentServer == null) {
            graphics.drawCenteredString(this.font,
                Component.literal("Not connected to a server."),
                this.width / 2, panelTop + 100, COLOR_LABEL);
            return;
        }
        
        ServerData data = dataManager.getServerData(currentServer);
        ServerStats allTimeStats = data.getAllTimeStats();
        
        int contentX = panelLeft + PANEL_PADDING;
        int contentWidth = PANEL_WIDTH - PANEL_PADDING * 2;
        int currentY = panelTop + PANEL_PADDING;
        
        // Server address pill
        int pillWidth = this.font.width(currentServer) + 20;
        int pillX = (this.width - pillWidth) / 2;
        drawSectionBox(graphics, pillX, currentY, pillWidth, 20);
        graphics.drawCenteredString(this.font, currentServer, this.width / 2, currentY + 6, COLOR_VALUE);
        currentY += 35;
        
        // All-Time Stats Section
        int sectionHeight = 80;
        drawSectionBox(graphics, contentX, currentY, contentWidth, sectionHeight);
        
        // Section header
        graphics.drawString(this.font, "All-Time", contentX + SECTION_PADDING, currentY + SECTION_PADDING, COLOR_HEADER);
        int statsY = currentY + SECTION_PADDING + 15;
        
        // Kills
        graphics.drawString(this.font, "⚔ Kills", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String killsValue = String.valueOf(allTimeStats.kills());
        graphics.drawString(this.font, killsValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(killsValue), 
            statsY, COLOR_VALUE);
        statsY += 12;
        drawSeparator(graphics, contentX + SECTION_PADDING, statsY, contentWidth - SECTION_PADDING * 2);
        statsY += 3;
        
        // Deaths
        graphics.drawString(this.font, "💀 Deaths", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String deathsValue = String.valueOf(allTimeStats.deaths());
        graphics.drawString(this.font, deathsValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(deathsValue), 
            statsY, COLOR_VALUE);
        statsY += 12;
        drawSeparator(graphics, contentX + SECTION_PADDING, statsY, contentWidth - SECTION_PADDING * 2);
        statsY += 3;
        
        // K/D Ratio
        graphics.drawString(this.font, "📊 K/D", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String kdValue = formatKD(allTimeStats.kd());
        graphics.drawString(this.font, kdValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(kdValue), 
            statsY, COLOR_VALUE);
        
        currentY += sectionHeight + SECTION_SPACING;
        
        // This Session Stats Section
        drawSectionBox(graphics, contentX, currentY, contentWidth, sectionHeight);
        
        // Section header
        graphics.drawString(this.font, "This Session", contentX + SECTION_PADDING, currentY + SECTION_PADDING, COLOR_HEADER);
        statsY = currentY + SECTION_PADDING + 15;
        
        // Kills
        graphics.drawString(this.font, "⚔ Kills", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String sessionKillsValue = String.valueOf(sessionTracker.getSessionKills());
        graphics.drawString(this.font, sessionKillsValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(sessionKillsValue), 
            statsY, COLOR_VALUE);
        statsY += 12;
        drawSeparator(graphics, contentX + SECTION_PADDING, statsY, contentWidth - SECTION_PADDING * 2);
        statsY += 3;
        
        // Deaths
        graphics.drawString(this.font, "💀 Deaths", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String sessionDeathsValue = String.valueOf(sessionTracker.getSessionDeaths());
        graphics.drawString(this.font, sessionDeathsValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(sessionDeathsValue), 
            statsY, COLOR_VALUE);
        statsY += 12;
        drawSeparator(graphics, contentX + SECTION_PADDING, statsY, contentWidth - SECTION_PADDING * 2);
        statsY += 3;
        
        // K/D Ratio
        graphics.drawString(this.font, "📊 K/D", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String sessionKdValue = formatKD(sessionTracker.getSessionKD());
        graphics.drawString(this.font, sessionKdValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(sessionKdValue), 
            statsY, COLOR_VALUE);
        
        currentY += sectionHeight + SECTION_SPACING;
        
        // Streak Stats Section
        int streakHeight = 50;
        drawSectionBox(graphics, contentX, currentY, contentWidth, streakHeight);
        
        // Section header
        graphics.drawString(this.font, "Streak", contentX + SECTION_PADDING, currentY + SECTION_PADDING, COLOR_HEADER);
        statsY = currentY + SECTION_PADDING + 15;
        
        // Current Streak
        graphics.drawString(this.font, "🔥 Current", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String currentStreakValue = String.valueOf(sessionTracker.getCurrentStreak());
        graphics.drawString(this.font, currentStreakValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(currentStreakValue), 
            statsY, COLOR_VALUE);
        statsY += 12;
        drawSeparator(graphics, contentX + SECTION_PADDING, statsY, contentWidth - SECTION_PADDING * 2);
        statsY += 3;
        
        // Best Streak
        graphics.drawString(this.font, "🏆 Best", contentX + SECTION_PADDING, statsY, COLOR_LABEL);
        String bestStreakValue = String.valueOf(data.getBestStreak());
        graphics.drawString(this.font, bestStreakValue, 
            contentX + contentWidth - SECTION_PADDING - this.font.width(bestStreakValue), 
            statsY, COLOR_VALUE);
    }
    
    private void renderAllServersView(GuiGraphics graphics, int panelLeft, int panelTop, int panelHeight) {
        Map<String, ServerData> allData = dataManager.getAllServerData();
        String searchText = searchBox != null ? searchBox.getValue().toLowerCase() : "";
        
        int contentX = panelLeft + PANEL_PADDING;
        int contentWidth = PANEL_WIDTH - PANEL_PADDING * 2;
        int currentY = panelTop + 145;
        
        // Filter label
        graphics.drawString(this.font, "Filter: " + getFilterLabel(currentFilter), 
            contentX, panelTop + PANEL_PADDING, COLOR_HEADER);
        
        // Server list area
        int listStartY = currentY;
        int listHeight = panelHeight - 165;
        int maxY = panelTop + listHeight;
        
        boolean hasServers = false;
        
        for (Map.Entry<String, ServerData> entry : allData.entrySet()) {
            String serverAddress = entry.getKey();
            
            if (!searchText.isEmpty() && !serverAddress.toLowerCase().contains(searchText)) {
                continue;
            }
            
            hasServers = true;
            
            if (currentY + 40 > maxY) break;
            
            ServerData data = entry.getValue();
            ServerStats stats = data.getStatsForFilter(currentFilter);
            
            // BUG FIX 2: Draw server entry box with space for reset button
            drawSectionBox(graphics, contentX, currentY, contentWidth - 60, 35);
            
            // Server address
            graphics.drawString(this.font, serverAddress, 
                contentX + SECTION_PADDING, currentY + SECTION_PADDING, COLOR_VALUE);
            
            // Stats on second line
            String statsText = String.format("⚔%d  💀%d  📊%s  🏆%d",
                stats.kills(), stats.deaths(), formatKD(stats.kd()), data.getBestStreak());
            graphics.drawString(this.font, statsText, 
                contentX + SECTION_PADDING, currentY + SECTION_PADDING + 12, COLOR_LABEL);
            
            currentY += 40;
        }
        
        if (!hasServers) {
            graphics.drawCenteredString(this.font,
                Component.literal(searchText.isEmpty() ? "No servers tracked yet." : "No servers match your search."),
                this.width / 2, listStartY + 20, COLOR_LABEL);
        }
    }
    
    private String getFilterLabel(TimeFilter filter) {
        return switch (filter) {
            case TODAY -> "Today";
            case THIS_WEEK -> "Week";
            case THIS_MONTH -> "Month";
            case ALL_TIME -> "All Time";
        };
    }
    
    /**
     * BUG FIX 3: Show confirmation dialog for resetting a specific server
     */
    private void showResetServerConfirmation(String serverAddress) {
        this.confirmDialogServer = serverAddress;
        this.confirmDialogText = "Reset stats for " + serverAddress + "? This cannot be undone.";
        this.showConfirmDialog = true;
    }
    
    /**
     * BUG FIX 3: Show confirmation dialog for resetting all servers
     */
    private void showResetAllConfirmation() {
        this.confirmDialogServer = null;
        this.confirmDialogText = "Reset stats for ALL servers? This cannot be undone.";
        this.showConfirmDialog = true;
    }
    
    /**
     * BUG FIX 3: Render confirmation dialog overlay
     */
    private void renderConfirmationDialog(GuiGraphics graphics, int mouseX, int mouseY) {
        // Semi-transparent overlay
        graphics.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Dialog box dimensions
        int dialogWidth = 300;
        int dialogHeight = 100;
        int dialogX = (this.width - dialogWidth) / 2;
        int dialogY = (this.height - dialogHeight) / 2;
        
        // Draw dialog background
        graphics.fill(dialogX, dialogY, dialogX + dialogWidth, dialogY + dialogHeight, COLOR_PANEL_BG);
        
        // Draw dialog border
        graphics.fill(dialogX, dialogY, dialogX + dialogWidth, dialogY + 1, COLOR_PANEL_BORDER);
        graphics.fill(dialogX, dialogY + dialogHeight - 1, dialogX + dialogWidth, dialogY + dialogHeight, COLOR_PANEL_BORDER);
        graphics.fill(dialogX, dialogY, dialogX + 1, dialogY + dialogHeight, COLOR_PANEL_BORDER);
        graphics.fill(dialogX + dialogWidth - 1, dialogY, dialogX + dialogWidth, dialogY + dialogHeight, COLOR_PANEL_BORDER);
        
        // Draw text - word wrap if needed
        int textY = dialogY + 15;
        int maxWidth = dialogWidth - 20;
        
        // Simple word wrapping
        String[] words = confirmDialogText.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            if (this.font.width(testLine) > maxWidth) {
                graphics.drawCenteredString(this.font, line.toString(), this.width / 2, textY, COLOR_VALUE);
                textY += 10;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) {
            graphics.drawCenteredString(this.font, line.toString(), this.width / 2, textY, COLOR_VALUE);
        }
        
        // Draw buttons
        int buttonWidth = 120;
        int buttonY = dialogY + dialogHeight - 30;
        int yesButtonX = dialogX + (dialogWidth / 2) - buttonWidth - 5;
        int cancelButtonX = dialogX + (dialogWidth / 2) + 5;
        
        // Yes button
        boolean yesHovered = mouseX >= yesButtonX && mouseX <= yesButtonX + buttonWidth && 
                             mouseY >= buttonY && mouseY <= buttonY + 20;
        graphics.fill(yesButtonX, buttonY, yesButtonX + buttonWidth, buttonY + 20, 
                     yesHovered ? 0xFF666666 : 0xFF444444);
        graphics.drawCenteredString(this.font, 
            confirmDialogServer == null ? "Yes, Reset All" : "Yes, Reset", 
            yesButtonX + buttonWidth / 2, buttonY + 6, COLOR_VALUE);
        
        // Cancel button
        boolean cancelHovered = mouseX >= cancelButtonX && mouseX <= cancelButtonX + buttonWidth && 
                                mouseY >= buttonY && mouseY <= buttonY + 20;
        graphics.fill(cancelButtonX, buttonY, cancelButtonX + buttonWidth, buttonY + 20, 
                     cancelHovered ? 0xFF666666 : 0xFF444444);
        graphics.drawCenteredString(this.font, "Cancel", 
            cancelButtonX + buttonWidth / 2, buttonY + 6, COLOR_VALUE);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showConfirmDialog) {
            // Handle confirmation dialog clicks
            int dialogWidth = 300;
            int dialogHeight = 100;
            int dialogX = (this.width - dialogWidth) / 2;
            int dialogY = (this.height - dialogHeight) / 2;
            
            int buttonWidth = 120;
            int buttonY = dialogY + dialogHeight - 30;
            int yesButtonX = dialogX + (dialogWidth / 2) - buttonWidth - 5;
            int cancelButtonX = dialogX + (dialogWidth / 2) + 5;
            
            // Check Yes button
            if (mouseX >= yesButtonX && mouseX <= yesButtonX + buttonWidth && 
                mouseY >= buttonY && mouseY <= buttonY + 20) {
                if (confirmDialogServer == null) {
                    // Reset all
                    resetAll();
                } else {
                    // Reset specific server
                    resetServer(confirmDialogServer);
                }
                showConfirmDialog = false;
                this.rebuildWidgets();
                return true;
            }
            
            // Check Cancel button
            if (mouseX >= cancelButtonX && mouseX <= cancelButtonX + buttonWidth && 
                mouseY >= buttonY && mouseY <= buttonY + 20) {
                showConfirmDialog = false;
                return true;
            }
            
            return true; // Consume all clicks when dialog is open
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private void resetServer(String serverAddress) {
        dataManager.resetServer(serverAddress);
        statusMessage = "Stats for " + serverAddress + " have been reset.";
        statusMessageTimer = 100;
    }
    
    private void resetAll() {
        dataManager.resetAll();
        statusMessage = "All server data has been reset.";
        statusMessageTimer = 100;
    }
    
    private void exportData() {
        try {
            String path = exportService.exportData(
                dataManager.getAllServerData(),
                exportFormat,
                dataManager.getExportDir()
            );
            statusMessage = "Exported to " + path;
            statusMessageTimer = 100;
            
            exportFormat = exportFormat == ExportFormat.TXT ? ExportFormat.CSV : ExportFormat.TXT;
            this.rebuildWidgets(); // Rebuild to update button label
        } catch (Exception e) {
            statusMessage = "Export failed: " + e.getMessage();
            statusMessageTimer = 100;
        }
    }
    
    private String formatKD(double kd) {
        if (kd == 0.0) {
            return "—";
        }
        return String.format("%.2f", kd);
    }
    
    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
