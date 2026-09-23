# DG Utilities

DG Utilities is a Minecraft Forge mod that adds useful commands and tools for server management, administration, and player convenience.

The project is currently under active development, and more features are planned for future versions.

## Supported Version

- **Minecraft:** 1.20.1
- **Forge:** 47.4.10
- **Java:** 17
- **Current mod version:** 0.5.1

## Features

DG Utilities currently includes:

- General server utility commands
- Administration and moderation tools
- Timed and permanent punishments
- Inventory inspection
- Persistent item restrictions
- Player-specific item restriction bypasses
- Client-side ignore functionality
- Configurable command permissions
- Optional client/server behavior

Most server commands can be enabled or disabled individually through the mod configuration file.

Permission levels for administrative commands can also be changed through the configuration.

---

## Commands

### General Server Commands

#### `/afk`

Marks the player as AFK.

```text
/afk
```

While AFK, nearby mobs stop targeting the player.

**Default permission level:** `0`

---

#### `/trash`

Opens a temporary 27-slot inventory that can be used to discard unwanted items.

```text
/trash
```

Items left inside the trash inventory are discarded when the inventory is closed.

**Default permission level:** `0`

---

#### `/announcement <message>`

Sends an announcement to all players currently connected to the server.

```text
/announcement Server restart in 10 minutes.
```

**Default permission level:** `2`

---

## Administration Commands

### Heal

#### `/heal <targets>`

Fully restores the health, hunger, and saturation of one or more players.

```text
/heal Player
/heal @a
```

**Default permission level:** `1`

---

### Freeze

#### `/freeze <targets>`

Freezes one or more players indefinitely.

```text
/freeze Player
```

A frozen player cannot move or interact normally until the punishment is removed.

#### `/freeze <targets> <duration>`

Freezes one or more players for a specific amount of time.

```text
/freeze Player 30s
/freeze Player 10m
/freeze Player 2h
/freeze Player 3d
```

Supported duration units:

- `s` - seconds
- `m` - minutes
- `h` - hours
- `d` - days

Timed punishments use expiration timestamps, so the timer continues even while the player is offline or the server is stopped.

#### `/unfreeze <targets>`

Removes the freeze punishment.

```text
/unfreeze Player
```

**Default permission level:** `2`

---

### Mute

#### `/mute <targets>`

Mutes one or more players indefinitely.

```text
/mute Player
```

#### `/mute <targets> <duration>`

Mutes one or more players for a specific amount of time.

```text
/mute Player 30s
/mute Player 10m
/mute Player 2h
/mute Player 3d
```

Supported duration units:

- `s` - seconds
- `m` - minutes
- `h` - hours
- `d` - days

#### `/unmute <targets>`

Removes the mute punishment.

```text
/unmute Player
```

**Default permission level:** `2`

---

### Punishment Information

#### `/punishments`

Displays all currently active freeze and mute punishments.

The command also includes punishments belonging to offline players.

```text
/punishments
```

---

#### `/checkfreeze <player>`

Checks whether a player is currently frozen and displays the remaining punishment time.

```text
/checkfreeze Player
```

---

#### `/checkmute <player>`

Checks whether a player is currently muted and displays the remaining punishment time.

```text
/checkmute Player
```

Permanent punishments are displayed as permanent.

---

### Inventory Inspection

#### `/invsee <player>`

Opens another player's inventory.

```text
/invsee Player
```

The target player's inventory can be viewed and modified through a chest-style interface.

**Default permission level:** `2`

---

## Item Restriction System

DG Utilities includes a persistent item restriction system that allows server administrators to prevent players from using or obtaining specific items.

Restrictions are stored per world.

### Global Restrictions

#### `/itemforbid <item>`

Globally forbids an item.

```text
/itemforbid minecraft:diamond_sword
```

---

#### `/itemunforbid <item>`

Removes an item from the global forbidden item list.

```text
/itemunforbid minecraft:diamond_sword
```

---

#### `/itemforbid list`

Displays all globally forbidden items.

```text
/itemforbid list
```

---

#### `/itemforbid check <item>`

Checks whether an item is globally forbidden.

```text
/itemforbid check minecraft:diamond_sword
```

---

#### `/itemforbid clear`

Removes every item from the global forbidden item list.

```text
/itemforbid clear
```

---

### Player Item Bypasses

#### `/itemallow <player>`

Allows a player to bypass all forbidden item restrictions.

```text
/itemallow Player
```

---

#### `/itemallow <player> <item>`

Allows a player to bypass the restriction for one specific forbidden item.

```text
/itemallow Player minecraft:diamond_sword
```

---

#### `/itemallow list <player>`

Displays the item restriction bypasses assigned to a player.

```text
/itemallow list Player
```

---

#### `/itemallow check <player>`

Checks whether a player has a global item restriction bypass.

```text
/itemallow check Player
```

---

#### `/itemallow check <player> <item>`

Checks whether a player can bypass the restriction for a specific item.

```text
/itemallow check Player minecraft:diamond_sword
```

---

### Removing Item Bypasses

#### `/itemdisallow <player>`

Removes the player's global forbidden-item bypass.

Specific item bypasses are preserved.

```text
/itemdisallow Player
```

---

#### `/itemdisallow <player> <item>`

Removes access to a specific forbidden item.

If the player currently has a global bypass, the global bypass is removed and converted into item-specific bypasses for the other currently forbidden items.

```text
/itemdisallow Player minecraft:diamond_sword
```

---

#### `/itemdisallow all <player>`

Removes all global and item-specific bypasses from the player.

```text
/itemdisallow all Player
```

**Default permission level for the item restriction system:** `3`

---

## Client Commands

### `/ignore <player>`

Locally ignores messages from another player.

```text
/ignore Player
```

This command is handled on the client side.

---

### `/unignore <player>`

Removes a player from the local ignore list.

```text
/unignore Player
```

Ignored players are stored locally in the client's configuration folder.

---

## Configuration

DG Utilities generates a Forge configuration file that allows server owners to enable or disable commands and change their required permission levels.

Permission levels follow Minecraft's standard operator permission system:

```text
0 = Any player
1 = Operator level 1
2 = Operator level 2
3 = Operator level 3
4 = Operator level 4
```

Default permission levels:

| Feature | Permission Level |
| --- | ---: |
| `/afk` | 0 |
| `/trash` | 0 |
| `/heal` | 1 |
| `/announcement` | 2 |
| `/freeze` | 2 |
| `/mute` | 2 |
| `/invsee` | 2 |
| Item restriction commands | 3 |

The `/ignore` system is client-side and does not use the server permission system.

---

## Server and Client Compatibility

DG Utilities is designed so that many server-side features can work without requiring every player to have the mod installed.

The mod can be used in different environments:

- **Server with DG Utilities + vanilla client:** server-side commands and administration features can still be available.
- **Client with DG Utilities + vanilla server:** client-only features such as `/ignore` can still be used.
- **DG Utilities on both client and server:** all supported functionality is available.

Some messages may use additional localization behavior when both sides have the mod installed.

---

## Data Storage

DG Utilities stores persistent world-specific data inside the current world save.

This includes data such as:

- Forbidden items
- Player item bypasses
- Active freeze punishments
- Active mute punishments

Client-only information, such as ignored players, is stored locally in the client's configuration folder.

---

## Languages

DG Utilities currently includes translations for:

- English (`en_us`)
- Portuguese - Brazil (`pt_br`)
- Spanish (`es_es`)

---

## Installation

### Client

1. Install Minecraft Forge for Minecraft 1.20.1.
2. Download the DG Utilities `.jar`.
3. Place the `.jar` inside the Minecraft `mods` folder.
4. Start Minecraft using the Forge profile.

### Dedicated Server

1. Install Forge 47.4.10 for Minecraft 1.20.1.
2. Place the DG Utilities `.jar` inside the server's `mods` folder.
3. Start the server.
4. Configure command permissions and enabled features as needed.

---

## Building from Source

Requirements:

- Java 17
- Git
- Minecraft Forge development environment

Clone the repository and run:

### Windows

```text
gradlew.bat build
```

### Linux / macOS

```text
./gradlew build
```

The generated `.jar` will be available in:

```text
build/libs/
```

---

## Development Status

DG Utilities is currently under active development.

The project may eventually be separated into multiple modules under the **DG Utilities** name, such as server, administration, and client-focused packages.

More commands and quality-of-life features are planned for future versions.

---

## License

DG Utilities is distributed under an **All Rights Reserved** license.

See the `LICENSE` file for more information.

---

## Author

Created by **SalocinDG**.
