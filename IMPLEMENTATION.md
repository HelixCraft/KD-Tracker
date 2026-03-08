# ServerKD Mod - Implementation Summary

## Project Structure

```
src/main/java/com/helixcraft/kdtracker/
├── KDTracker.java                    # Main mod entry point
├── event/
│   └── CombatEventHandler.java       # Handles kill/death events and server connections
├── mixin/
│   ├── ClientPacketListenerMixin.java # Detects server joins
│   ├── MinecraftMixin.java           # Detects server disconnects
│   ├── LocalPlayerMixin.java         # Detects player deaths (respawn)
│   ├── PlayerMixin.java              # Detects player kills
│   └── ClientLevelMixin.java         # Additional entity tracking
├── model/
│   ├── Event.java                    # Kill/death event with timestamp
│   ├── EventType.java                # KILL or DEATH enum
│   ├── ExportFormat.java             # TXT or CSV enum
│   ├── ServerAddress.java            # Server address wrapper
│   ├── ServerData.java               # Per-server data container
│   ├── ServerStats.java              # Computed statistics record
│   └── TimeFilter.java               # Time filtering enum
├── service/
│   ├── DataManager.java              # JSON persistence and data management
│   ├── SessionTracker.java           # In-memory session stats
│   └── ExportService.java            # Export to TXT/CSV
└── ui/
    └── StatsScreen.java              # Main UI with two views
```

## Key Implementation Details

### 1. Server Detection & Tracking

- Uses mixins to detect server join/leave events
- Server address is used as unique key for stats
- Singleplayer worlds are ignored (checked via `isOnMultiplayerServer()`)

### 2. Kill/Death Detection

- **Player Deaths**: Detected via `LocalPlayer.respawn()` mixin
- **Player Kills**: Detected via `Player.die()` mixin when local player is the attacker
- Only player-vs-player kills are counted (mob kills ignored)

### 3. Data Storage

- Location: `.minecraft/config/serverkd/data.json`
- Format: JSON with events array containing timestamps
- Auto-saves immediately after every kill/death
- Uses Gson for serialization

### 4. Session Stats

- Tracked in-memory via `SessionTracker`
- Resets on server disconnect
- Not persisted to disk
- Displayed alongside all-time stats

### 5. Kill Streaks

- Current streak tracked in `SessionTracker`
- Best streak saved per server in `ServerData`
- Resets to 0 on death
- Best streak updated when current exceeds it

### 6. Time-Based Filtering

- Each event stored with Unix timestamp
- Filters: TODAY (24h), THIS_WEEK (7d), THIS_MONTH (30d), ALL_TIME
- Stats recalculated on-the-fly when filter changes
- Filter selection not persisted (resets to ALL_TIME)

### 7. UI Implementation

- Single screen with two views (toggle buttons)
- **Current Server View**: Shows all-time + session stats side-by-side
- **All Servers View**: Scrollable list with search and time filters
- Vanilla Minecraft styling (no external UI libraries)
- Keybind: K (remappable via Minecraft controls)

### 8. Export Functionality

- Supports TXT and CSV formats
- Exports all tracked servers
- Files saved to `.minecraft/config/serverkd/`
- Filename includes timestamp: `export_YYYY-MM-DD_HH-mm-ss.{txt|csv}`

### 9. Reset Functionality

- Per-server reset (not yet implemented in UI)
- Reset all servers (button in All Servers view)
- Confirmation dialogs (simplified implementation)
- Clears events and best streak

## Technical Decisions

### Why Mixins?

- Fabric's recommended approach for event injection
- Direct access to Minecraft's internal methods
- No need for complex event bus systems

### Why Gson?

- Already included in Minecraft
- Simple JSON serialization
- No additional dependencies needed

### Why Immediate Saves?

- Prevents data loss on crashes
- Minimal performance impact (JSON is small)
- User expectation: stats should persist immediately

### Why Timestamp-Based Events?

- Enables time-based filtering
- Future-proof for additional analytics
- Minimal storage overhead (8 bytes per event)

### Why Client-Side Only?

- No server modifications required
- Works on any multiplayer server
- Privacy: data stays local

## Build Configuration

- **Minecraft Version**: 1.21.4
- **Fabric Loader**: 0.18.4+
- **Fabric API**: 0.119.4+1.21.4
- **Java Version**: 21
- **Loom Version**: 1.15-SNAPSHOT
- **Mappings**: Official Mojang mappings

## Testing Recommendations

1. **Server Join/Leave**: Verify session resets and server detection
2. **Kill Tracking**: Test PvP kills are counted correctly
3. **Death Tracking**: Test all death types (PvP, fall, lava, void, etc.)
4. **Streak Tracking**: Verify streak increments and resets
5. **Time Filters**: Test filtering with events at different timestamps
6. **Export**: Verify TXT and CSV output formats
7. **Reset**: Test individual and bulk reset operations
8. **UI**: Test search, filtering, and view switching
9. **Persistence**: Verify data survives game restarts
10. **Keybind**: Test opening screen with K key

## Known Limitations

1. **Kill Detection**: Relies on damage source entity - may not work with all modded weapons
2. **Death Detection**: Uses respawn event - may trigger on bed respawn in some edge cases
3. **UI Scrolling**: Server list doesn't have proper scrolling widget (renders up to screen limit)
4. **Confirmation Dialogs**: Reset confirmations are simplified (no proper modal dialogs)
5. **Per-Server Reset**: Button not yet added to UI (functionality exists in DataManager)

## Future Enhancements

1. Add proper scrollable list widget for All Servers view
2. Implement modal confirmation dialogs
3. Add per-server reset buttons in the list
4. Add graphs/charts for kill/death trends over time
5. Add configurable keybinds for quick stats overlay
6. Add sound effects for kill streaks
7. Add chat notifications for milestones
8. Add leaderboard/comparison features
9. Add import functionality for data migration
10. Add backup/restore functionality
