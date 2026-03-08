package com.helixcraft.kdtracker.model;

/**
 * Enum representing export file formats.
 */
public enum ExportFormat {
    TXT(".txt"),
    CSV(".csv");
    
    private final String extension;
    
    ExportFormat(String extension) {
        this.extension = extension;
    }
    
    public String getExtension() {
        return extension;
    }
}
