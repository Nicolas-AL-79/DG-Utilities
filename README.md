# DG Utilities

DG Utilities is a Minecraft Forge mod that adds useful commands and tools for server management, administration, moderation, and player convenience.

The project is currently under active development, and more features are planned for future versions.

## Supported Version

- **Minecraft:** 1.20.1
- **Forge:** 47.4.10
- **Java:** 17
- **Current mod version:** 0.7.0

## Features

DG Utilities currently includes:

- General server utility commands
- Administration and moderation tools
- Timed and permanent punishments
- Automatic and manual AFK systems
- Player status indicators in the tab list
- Inventory inspection
- Persistent item restrictions
- Player-specific item restriction bypasses
- Dimension access restrictions
- Player-specific dimension bypasses
- Item-based dimension access keys
- Portal activation and portal travel restrictions
- Client-side ignore functionality
- Configurable command permissions
- Optional client/server behavior

Most server commands can be enabled or disabled individually through the mod configuration file.

Permission levels for administrative commands can also be changed through the configuration.

---

## Commands

### General Server Commands

#### `/afk`

Starts the manual AFK activation process.

```text
/afk
```

The player must remain still for 5 seconds before AFK mode activates.

While AFK:

- The player is protected from damage and knockback.
- Nearby mobs stop targeting the player.
- The player cannot move from the AFK position.
- Moving the camera or pressing SHIFT exits AFK mode.
- `[AFK]` is displayed next to the player's name in the tab list.

DG Utilities can also automatically place inactive players into AFK mode after a configurable amount of time.

Automatic AFK detection checks player position and camera movement at fixed intervals to reduce server overhead.

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

Frozen players are also ignored by mobs and display `[FROZEN]` in the tab list.

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

Muted players display `[MUTED]` in the tab list.

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

DG Utilities includes a persistent item restriction system that allows server administrators to prevent players from obtaining or keeping specific items.

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

## Dimension Access System

DG Utilities includes a persistent dimension access system that can restrict access to dimensions, configure player bypasses, assign item-based access keys, and restrict portal use.

Dimension data is stored per world.

Dimension arguments use Minecraft's registered dimension list, so vanilla and registered modded dimensions are available through command suggestions.

### Dimension Blocking

#### `/dimensionblock <dimension>`

Blocks access to a dimension.

```text
/dimensionblock minecraft:the_nether
/dimensionblock minecraft:the_end
```

When a dimension is blocked, players cannot enter it through normal portals, commands, teleport systems, or other dimension-travel mechanics that trigger Forge's dimension travel event.

A player can still enter if they have a bypass for that dimension or possess its configured dimension key.

---

#### `/dimensionunblock <dimension>`

Removes the dimension restriction.

```text
/dimensionunblock minecraft:the_nether
```

---

#### `/dimensionblock list`

Displays all currently blocked dimensions.

```text
/dimensionblock list
```

This command is public so players can check which dimensions are restricted.

---

### Player Dimension Bypasses

#### `/dimensionallow <player> <dimension>`

Allows a player to bypass the restriction for a specific dimension.

```text
/dimensionallow Player minecraft:the_end
```

---

#### `/dimensionallow list <player>`

Displays all dimension bypasses assigned to a player.

```text
/dimensionallow list Player
```

---

#### `/dimensiondisallow <player> <dimension>`

Removes a player's bypass for a specific dimension.

```text
/dimensiondisallow Player minecraft:the_end
```

---

#### `/dimensiondisallow all <player>`

Removes all dimension bypasses from a player.

```text
/dimensiondisallow all Player
```

---

### Dimension Access Information

#### `/dimensionaccess list`

Displays all players that currently have at least one dimension bypass.

```text
/dimensionaccess list
```

Stored player names allow this information to remain available even when those players are offline.

---

#### `/dimensionaccess list <dimension>`

Displays players with a bypass for a specific dimension.

```text
/dimensionaccess list minecraft:the_end
```

---

### Dimension Keys

A dimension can have an item configured as an access key.

The key is not consumed. The player only needs to carry the required item in their inventory or offhand when attempting to enter a blocked dimension.

#### `/dimensionkey set <dimension> <item>`

Sets the access key for a dimension.

```text
/dimensionkey set minecraft:the_end minecraft:nether_star
```

---

#### `/dimensionkey remove <dimension>`

Removes the configured key.

```text
/dimensionkey remove minecraft:the_end
```

---

#### `/dimensionkey check <dimension>`

Displays the configured key for a dimension.

```text
/dimensionkey check minecraft:the_end
```

This command is public.

---

#### `/dimensionkey list`

Displays every configured dimension key.

```text
/dimensionkey list
```

This command is public so players can see which items are required for restricted dimensions.

---

### Portal Blocking

#### `/portalblock <dimension>`

Disables portal-based access to a dimension.

```text
/portalblock minecraft:the_nether
/portalblock minecraft:the_end
```

Portal blocking is separate from full dimension blocking:

- `dimensionblock` prevents access to the dimension regardless of travel method.
- `portalblock` only prevents portal-based travel to that destination.

For vanilla portals, additional protections are applied:

- **Nether Portal:** newly activated Nether Portals are prevented from forming while Nether portal access is blocked.
- **End Portal:** Eyes of Ender cannot be inserted into End Portal Frames while End portal access is blocked.
- **Existing portals:** travel is canceled when a player attempts to enter a portal-blocked dimension while intersecting a portal block.

Portal travel detection checks the player's bounding box against blocks whose registry ID contains `portal`. This also provides basic compatibility with some modded portal implementations, although full compatibility with every modded portal is not guaranteed.

---

#### `/portalunblock <dimension>`

Removes the portal restriction for a dimension.

```text
/portalunblock minecraft:the_nether
```

---

#### `/portalblock list`

Displays all dimensions whose portal access is currently disabled.

```text
/portalblock list
```

This command is public.

**Default permission level for dimension and portal administration:** `3`

---

## Client Commands

### `/ignore <player>`

Locally ignores messages from another player.

```text
/ignore Player
```

This command is handled entirely on the client side.

Ignored players display a local `[IGNORED]` tag in the tab list. This tag is visible only to the player who ignored them and preserves server-side status tags such as `[AFK]`, `[MUTED]`, and `[FROZEN]`.

---

### `/unignore <player>`

Removes a player from the local ignore list.

```text
/unignore Player
```

Ignored players are stored locally in the client's configuration folder.

---

## Tab List Status Indicators

DG Utilities can display player status directly in the tab list.

Server-side indicators:

- `[AFK]`
- `[MUTED]`
- `[FROZEN]`

Client-side indicator:

- `[IGNORED]`

The `[IGNORED]` indicator is private to the client that ignored the player.

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
| Dimension and portal administration | 3 |

Automatic AFK behavior can also be enabled or disabled and its inactivity timeout can be configured.

Public information commands such as `/dimensionblock list`, `/portalblock list`, `/dimensionkey list`, and `/dimensionkey check <dimension>` do not require administrative permission.

The `/ignore` system is client-side and does not use the server permission system.

---

## Server and Client Compatibility

DG Utilities is designed so that many server-side features can work without requiring every player to have the mod installed.

The mod can be used in different environments:

- **Server with DG Utilities + vanilla client:** server-side commands and administration features can still be available.
- **Client with DG Utilities + vanilla server:** client-only features such as `/ignore` can still be used.
- **DG Utilities on both client and server:** all supported functionality is available.

Some messages use additional localization behavior when both sides have the mod installed.

---

## Data Storage

DG Utilities stores persistent world-specific data inside the current world save under the mod's data folder.

This includes data such as:

- Forbidden items
- Player item bypasses
- Active freeze punishments
- Active mute punishments
- Blocked dimensions
- Portal-blocked dimensions
- Player dimension bypasses
- Dimension access keys

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
