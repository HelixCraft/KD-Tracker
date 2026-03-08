# ServerKD Mod — Functional Specification

> **Working Title:** ServerKD
> **Platform:** Minecraft Java Edition — Fabric (Client-side only)

---

## 1. Server Detection & Kill/Death Tracking

### How the mod identifies servers

Every time the player connects to a multiplayer server, the mod reads the **server address** (e.g. `hypixel.net` or `192.168.1.1:25565`) and uses it as a unique key to store and retrieve that server's stats.

- Each server gets its own separate stats entry — no data is ever mixed between servers.
- **Singleplayer worlds are ignored** entirely. The mod only activates on multiplayer servers.

### What gets tracked

| Stat | What counts |
|---|---|
| **Player Kills** | Only kills against other players. Mob kills are ignored completely. |
| **Player Deaths** | All deaths, regardless of cause (killed by player, fall damage, lava, void, etc.). |
| **K/D Ratio** | Automatically calculated: `kills ÷ deaths`. If deaths = 0, displays as `—`. |

### How data is saved

Stats are written to a local JSON file **immediately** after every kill or death, so no data is lost even if the game crashes.

**File location:** `.minecraft/config/serverkd/data.json`

**File format:**
```json
{
  "hypixel.net": {
    "kills": 42,
    "deaths": 17
  },
  "play.example.com": {
    "kills": 5,
    "deaths": 3
  }
}
```

---

## 2. Stats Screen (Overview UI)

### Opening the screen

Press **`K`** (default keybind, fully remappable in Minecraft's vanilla keybind settings) to open the stats screen at any time — whether in-game or on a server.

### Layout: Two Tabs

#### Tab 1 — Current Server

This tab opens **by default** every time the stats screen is opened.

It shows the stats for whichever server the player is currently connected to:

| Field | Example |
|---|---|
| Server Address | `hypixel.net` |
| ⚔️ Kills | `42` |
| 💀 Deaths | `17` |
| 📊 K/D Ratio | `2.47` |

If the player is not currently connected to any server, this tab shows a short message: *"Not connected to a server."*

#### Tab 2 — All Servers

This tab shows a scrollable list of **every server** the mod has ever tracked stats for.

Each entry in the list displays:

| Field | Example |
|---|---|
| Server Address | `play.example.com` |
| ⚔️ Kills | `5` |
| 💀 Deaths | `3` |
| 📊 K/D Ratio | `1.67` |

**Search bar** at the top of Tab 2 allows filtering the list by server address (live filtering as the player types).

### Design

The UI follows the **vanilla Minecraft style** — no external UI libraries or dependencies. It should feel native and consistent with Minecraft's own screens (same fonts, button styles, backgrounds).

---

## 3. Reset Function

### How it works

Every server entry (both in Tab 1 and in each row of Tab 2) has a **Reset** button next to it.

### Reset flow — step by step

**Step 1:** Player clicks the **Reset** button next to a server.

**Step 2:** A confirmation dialog appears:

> ⚠️ *"Are you sure you want to reset the stats for* `hypixel.net`*?*
> *This cannot be undone."*
>
> `[ ✅ Yes, reset ]` &nbsp;&nbsp; `[ ❌ Cancel ]`

**Step 3:**
- If the player clicks **Yes, reset** → kills and deaths for that server are set back to `0`. K/D resets accordingly.
- If the player clicks **Cancel** → nothing changes, the dialog closes.

### Reset All (Tab 2 only)

At the bottom of Tab 2 there is a **Reset All Servers** button that resets the stats of every tracked server at once.

This has its own, slightly stricter confirmation dialog:

> ⚠️ *"Are you sure you want to reset the stats for ALL servers?*
> *This will delete all tracked data and cannot be undone."*
>
> `[ ✅ Yes, reset everything ]` &nbsp;&nbsp; `[ ❌ Cancel ]`

---

## 4. Session Stats

Every time the player connects to a server, the mod starts a fresh **session counter** on top of the existing all-time stats. Both are visible at the same time in Tab 1.

Tab 1 therefore shows two columns side by side:

| Stat | All-Time | This Session |
|---|---|---|
| ⚔️ Kills | `142` | `8` |
| 💀 Deaths | `61` | `3` |
| 📊 K/D Ratio | `2.33` | `2.67` |

Session stats reset automatically every time the player disconnects from the server. They are **not saved to disk** — they only exist for the duration of the current connection.

---

## 5. Kill Streak

The mod tracks the player's current **kill streak** in real time: how many players they have killed in a row without dying.

- The streak counter increments by 1 on every player kill.
- The streak resets to 0 on every death (regardless of cause).
- The **personal best streak** for each server is saved permanently and shown in the stats screen.

Both values are shown in Tab 1:

| Field | Example |
|---|---|
| 🔥 Current Streak | `4` |
| 🏆 Best Streak (this server) | `11` |

The personal best is only updated if the current streak exceeds it. It is included in resets — clicking Reset on a server also clears its best streak record.

---

## 6. Time-Based Stats Filter (Tab 2)

In the All Servers tab, the player can filter all displayed stats by time period using a toggle at the top of the list:

> `[ Today ]` `[ This Week ]` `[ This Month ]` `[ All Time ]`

To make this work, every individual kill and death is stored internally with a **Unix timestamp** alongside the kill/death count. When a filter is selected, the mod recalculates kills, deaths, and K/D on the fly for that time window only.

The default view is **All Time**. The selected filter is remembered while the stats screen is open, but resets to All Time when it is closed and reopened.

**Updated file format** (each event stored with timestamp):
```json
{
  "hypixel.net": {
    "events": [
      { "type": "kill", "timestamp": 1720000000 },
      { "type": "kill", "timestamp": 1720000120 },
      { "type": "death", "timestamp": 1720000300 }
    ],
    "bestStreak": 11
  }
}
```

---

## 7. Stats Export

A dedicated **Export** button is available at the bottom of Tab 2. Clicking it generates a file in the `.minecraft/serverkd/` folder containing the full stats of every tracked server.

The player can choose the format via a small toggle next to the button:

> `[ Export as .txt ]` `[ Export as .csv ]`

**Example `.txt` output:**
```
ServerKD Export — 2024-07-04 15:32
------------------------------------
hypixel.net
  Kills:       142
  Deaths:       61
  K/D Ratio:  2.33
  Best Streak:  11

play.example.com
  Kills:         5
  Deaths:         3
  K/D Ratio:  1.67
  Best Streak:   2
```

**Example `.csv` output:**
```
Server,Kills,Deaths,K/D Ratio,Best Streak
hypixel.net,142,61,2.33,11
play.example.com,5,3,1.67,2
```

After the file is written, a short confirmation message appears on screen: *"Stats exported to .minecraft/serverkd/export_2024-07-04.csv"*

---

## Summary

| Feature | Details |
|---|---|
| Server detection | Automatic, based on server address |
| Singleplayer | Ignored |
| Tracked stats | Player kills, all deaths, K/D ratio |
| Session stats | Separate live counters per connection, shown alongside all-time stats in Tab 1 |
| Kill streak | Live streak counter + best streak per server, resets on death |
| Save location | `.minecraft/config/serverkd/data.json` |
| Save timing | Immediately after every kill/death (with timestamp) |
| Open keybind | `K` (remappable) |
| Tab 1 | Current server — all-time and session stats, live streak |
| Tab 2 | All servers, with search and time-based filter |
| Time filter | Today / This Week / This Month / All Time |
| Reset | Per-server or all-at-once, with confirmation (clears streak records too) |
| Export | `.txt` or `.csv` to `.minecraft/serverkd/` folder |
| UI style | Vanilla Minecraft |
| Platform | Fabric, client-side only |
