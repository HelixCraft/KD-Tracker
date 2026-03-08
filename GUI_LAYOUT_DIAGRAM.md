# ServerKD Mod - GUI Layout Diagram

## Current Server Tab Layout

```
┌────────────────────────────────────────────────────────────┐
│                    [Blurred Game Background]                │
│                                                              │
│  [Current Server]══════  [All Servers]                      │ ← Tab with gold highlight
│  ┌──────────────────────────────────────────────────────┐  │
│  │                                                        │  │ ← Main Panel (400px wide)
│  │                  ┌──────────────────┐                 │  │
│  │                  │  hypixel.net     │                 │  │ ← Server Pill
│  │                  └──────────────────┘                 │  │
│  │                                                        │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ All-Time                                      │    │  │ ← Section Header (Gold)
│  │  │ ─────────────────────────────────────────    │    │  │
│  │  │ ⚔ Kills                              142     │    │  │
│  │  │ ───────────────────────────────────────────  │    │  │ ← Separator
│  │  │ 💀 Deaths                             61     │    │  │
│  │  │ ───────────────────────────────────────────  │    │  │
│  │  │ 📊 K/D                              2.33     │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                        │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ This Session                                  │    │  │
│  │  │ ─────────────────────────────────────────    │    │  │
│  │  │ ⚔ Kills                                8     │    │  │
│  │  │ ───────────────────────────────────────────  │    │  │
│  │  │ 💀 Deaths                              3     │    │  │
│  │  │ ───────────────────────────────────────────  │    │  │
│  │  │ 📊 K/D                              2.67     │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                        │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ Streak                                        │    │  │
│  │  │ ─────────────────────────────────────────    │    │  │
│  │  │ 🔥 Current                             4     │    │  │
│  │  │ ───────────────────────────────────────────  │    │  │
│  │  │ 🏆 Best                               11     │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                        │  │
│  │                                                        │  │
│  │                    [Close]                             │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

## All Servers Tab Layout

```
┌────────────────────────────────────────────────────────────┐
│                    [Blurred Game Background]                │
│                                                              │
│  [Current Server]  [All Servers]══════                      │ ← Tab with gold highlight
│  ┌──────────────────────────────────────────────────────┐  │
│  │ Filter: All Time                                      │  │ ← Filter label
│  │                                                        │  │
│  │  [Search servers...                              ]    │  │ ← Search box
│  │                                                        │  │
│  │  [Today] [Week] [Month] [All Time]                    │  │ ← Filter buttons
│  │                                                        │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ hypixel.net                                   │    │  │ ← Server entry box
│  │  │ ⚔142  💀61  📊2.33  🏆11                     │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                        │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ play.example.com                              │    │  │
│  │  │ ⚔5  💀3  📊1.67  🏆2                          │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                        │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ mc.server.net                                 │    │  │
│  │  │ ⚔23  💀15  📊1.53  🏆5                        │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                        │  │
│  │                                                        │  │
│  │  [Export TXT]              [Reset All]                │  │
│  │                    [Close]                             │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

## Visual Elements Key

### Colors:

- **Panel Background**: Semi-transparent dark (0xCC000000)
- **Panel Border**: Light grey (0xFF555555)
- **Section Background**: Darker transparent (0xAA000000)
- **Section Border**: Medium grey (0xFF444444)
- **Separator Lines**: Dark grey (0xFF333333)
- **Headers**: Gold/Yellow (0xFFFFAA00)
- **Labels**: Light grey (0xFFAAAAAA)
- **Values**: White (0xFFFFFFFF)
- **Tab Highlight**: Gold (0xFFFFAA00)

### Spacing:

- **Panel Padding**: 20px from edges
- **Section Padding**: 6px inside boxes
- **Section Spacing**: 10px between boxes
- **Stat Row Height**: 12px + 3px separator

### Typography:

- All text uses vanilla Minecraft font
- Headers are rendered in gold color for emphasis
- Labels are left-aligned
- Values are right-aligned
- Icons (⚔💀📊🔥🏆) provide visual context

## Before vs After

### Before:

- Plain text on blurred background
- No visual structure
- Hard to read and scan
- Unprofessional appearance
- No clear grouping

### After:

- Professional panel-based layout
- Clear visual hierarchy
- Easy to read and scan
- Matches vanilla Minecraft aesthetic
- Logical grouping with section boxes
- Proper spacing and alignment
- Visual separators between stats
- Styled tabs with active indicators
- Server address in prominent pill
