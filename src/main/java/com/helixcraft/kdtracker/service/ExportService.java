package com.helixcraft.kdtracker.service;

import com.helixcraft.kdtracker.model.ExportFormat;
import com.helixcraft.kdtracker.model.ServerData;
import com.helixcraft.kdtracker.model.ServerStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExportService {
    private static final DateTimeFormatter FILENAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public String exportData(Map<String, ServerData> serverDataMap, ExportFormat format, Path exportDir) throws IOException {
        String timestamp = LocalDateTime.now().format(FILENAME_FORMAT);
        String filename = "export_" + timestamp + format.getExtension();
        Path exportPath = exportDir.resolve(filename);
        
        String content = format == ExportFormat.TXT ? 
            generateTxtExport(serverDataMap) : 
            generateCsvExport(serverDataMap);
        
        Files.createDirectories(exportDir);
        Files.writeString(exportPath, content);
        
        return exportPath.toString();
    }
    
    private String generateTxtExport(Map<String, ServerData> serverDataMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("ServerKD Export — ").append(LocalDateTime.now().format(DISPLAY_FORMAT)).append("\n");
        sb.append("------------------------------------\n");
        
        for (Map.Entry<String, ServerData> entry : serverDataMap.entrySet()) {
            ServerStats stats = entry.getValue().getAllTimeStats();
            sb.append(entry.getKey()).append("\n");
            sb.append("  Kills:       ").append(stats.kills()).append("\n");
            sb.append("  Deaths:      ").append(stats.deaths()).append("\n");
            sb.append("  K/D Ratio:   ").append(formatKD(stats.kd())).append("\n");
            sb.append("  Best Streak: ").append(entry.getValue().getBestStreak()).append("\n");
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private String generateCsvExport(Map<String, ServerData> serverDataMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("Server,Kills,Deaths,K/D Ratio,Best Streak\n");
        
        for (Map.Entry<String, ServerData> entry : serverDataMap.entrySet()) {
            ServerStats stats = entry.getValue().getAllTimeStats();
            sb.append(entry.getKey()).append(",");
            sb.append(stats.kills()).append(",");
            sb.append(stats.deaths()).append(",");
            sb.append(formatKD(stats.kd())).append(",");
            sb.append(entry.getValue().getBestStreak()).append("\n");
        }
        
        return sb.toString();
    }
    
    private String formatKD(double kd) {
        if (Double.isInfinite(kd) || Double.isNaN(kd)) {
            return "—";
        }
        return String.format("%.2f", kd);
    }
}
