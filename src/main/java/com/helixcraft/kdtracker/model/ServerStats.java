package com.helixcraft.kdtracker.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Immutable record representing computed statistics.
 * Contains kills, deaths, and K/D ratio.
 */
public record ServerStats(int kills, int deaths, double kd) {
}
