# ServerKD Tracker

A client-side Fabric mod for Minecraft 1.21.4 that tracks your kills, deaths, and K/D ratio on multiplayer servers.

## Features

### Core Tracking

- **Player Kills**: Tracks only kills against other players (mob kills are ignored)
- **Player Deaths**: Tracks all deaths regardless of cause
- **K/D Ratio**: Automatically calculated and displayed
- **Per-Server Stats**: Each server gets its own separate stats entry

### Session Stats

- Live session counters that reset when you disconnect
- View both all-time and current session stats side-by-side
- Session stats are not saved to disk

### Kill Streaks

- Real-time kill streak counter
- Personal best streak saved per server
- Streak resets on death

### Time-Based Filtering

- Filter stats by time period: Today, This Week, This Month, or All Time
- Each kill and death is stored with a timestamp for accurate filtering

### Data Management

- **Auto-Save**: Stats are saved immediately after every kill/death
- **Export**: Export all stats to TXT or CSV format
- **Reset**: Reset individual server stats or all stats at once
- **Data Location**: `.minecraft/config/serverkd/data.json`

## Usage

### Opening the Stats Screen

Press **K** (default keybind, remappable in Minecraft's controls) to open the stats screen.

### Stats Screen Views

#### Current Server Tab

Shows stats for the server you're currently connected to:

- Server address
- All-time kills, deaths, and K/D ratio
- Session kills, deaths, and K/D ratio
- Current kill streak
- Best kill streak for this server

If not connected to a server, displays "Not connected to a server."

#### All Servers Tab

Shows a list of all servers you've tracked stats on:

- Search bar to filter servers by address
- Time filter buttons (Today/Week/Month/All Time)
- Server list with kills, deaths, K/D, and best streak
- Export button (toggle between TXT and CSV)
- Reset All button

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release of ServerKD Tracker
4. Place the JAR file in your `.minecraft/mods` folder
5. Launch Minecraft 1.21.4 with the Fabric profile

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Requirements

- Minecraft 1.21.4
- Fabric Loader 0.18.4 or higher
- Fabric API
- Java 21 or higher

## Data Storage

Stats are stored in `.minecraft/config/serverkd/data.json` in the following format:

```json
{
  "hypixel.net": {
    "events": [
      { "type": "KILL", "timestamp": 1720000000 },
      { "type": "DEATH", "timestamp": 1720000300 }
    ],
    "bestStreak": 11
  }
}
```

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).
