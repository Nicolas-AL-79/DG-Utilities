# DG Utilities

DG Utilities is a Minecraft Forge mod that adds useful commands and tools for server management, administration, moderation, and player convenience.

The project is currently under active development, and more features are planned for future versions.

## Features

DG Utilities currently includes:

- General server utility commands
- Administration and moderation tools
- Chat and on-screen server announcements
- Administrative flight and god mode
- Offline player last-position lookup
- Timed and permanent punishments
- Automatic and manual AFK systems
- Player status indicators in the tab list
- Inventory inspection
- Ender Chest access and inspection
- Persistent item restrictions
- Player-specific item restriction bypasses
- Dimension access restrictions
- Player-specific dimension bypasses
- Item-based dimension access keys
- Portal activation and portal travel restrictions
- Manual and automatic dropped-item cleanup
- Temporary player bans with ban-list management
- Client-side ignore functionality
- Configurable command permissions
- Optional client/server behavior
- `/dg` fallback aliases for server-side commands

Most server commands can be enabled or disabled individually through the mod configuration file.

Permission levels for administrative commands can also be changed through the configuration.

---

## Commands

### Command Aliases

Server-side DG Utilities commands can be executed either directly or through the `/dg` command namespace.

For example:

```text
/heal
/dg heal

/tempban Player 30m
/dg tempban Player 30m
```

The `/dg` form is available as a fallback in case a short command name conflicts with a command provided by Minecraft or another mod.

Client-only commands such as `/ignore` and `/unignore` are not registered under `/dg`.

---

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

#### `/enderchest`

Opens your Ender Chest from anywhere.

```text
/enderchest
```

The command can only be executed by a player and provides access to the player's normal Ender Chest inventory without requiring an Ender Chest block.

**Default permission level:** `1`

---

## Administration Commands

### Announcements

#### `/announcement <message>`

Sends an announcement to all players currently connected to the server.

```text
/announcement Server restart in 10 minutes.
```

**Default permission level:** `2`

---

#### `/screenannounce <targets> <message>`

Displays a highlighted announcement in the center of the selected players' screens.

```text
/screenannounce Player Server restart in 5 minutes.
/screenannounce @a Event starting now!
```

Screen announcements use configurable fade-in, display, and fade-out durations.

**Default permission level:** `2`

---

### Heal

#### `/heal`

Fully restores your own health, hunger, and saturation and removes active effects.

The command without a target can only be used by a player.

#### `/heal <targets>`

Fully restores the health, hunger, and saturation of one or more players.

```text
/heal Player
/heal @a
```

**Default permission level:** `1`

---

### Fly

#### `/fly`

Toggles flight for the player executing the command.

#### `/fly <player>`

Toggles flight for another player.

Creative and Spectator players already have flight through their game mode, so DG Utilities does not override their natural flight state.

When flight is disabled by the command, active flying is also stopped.

**Default permission level:** `2`

---

### God Mode

#### `/god`

Toggles god mode for the player executing the command.

#### `/god <player>`

Toggles god mode for another player.

While god mode is active:

- Incoming attacks and damage are blocked.

- Harmful status effects cannot be applied.

- Harmful effects already active when god mode is enabled are removed.

- Beneficial effects continue to work normally.

- The god mode state is stored persistently on the player.

**Default permission level:** `2`

---

### Last Position

#### `/lastpos <player>`

Displays the last position saved for a player, including their dimension and exact coordinates.

The command reads the player's saved Minecraft player data, allowing administrators to inspect the last saved position of offline players as well.

Example output:

```text
Player's last position:
Dimension: minecraft:overworld
X: 123.45, Y: 64.00, Z: -987.65
```

**Default permission level:** `2`

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

### Temporary Bans

#### `/tempban <player> <duration> [reason]`

Temporarily bans one or more player profiles using Minecraft's standard player ban list.

```text
/tempban Player 30m
/tempban Player 2h Griefing
/tempban Player 7d Repeated rule violations
/tempban Player 1w
```

Supported duration units:

- `s` - seconds
- `m` - minutes
- `h` - hours
- `d` - days
- `w` - weeks

If the player is currently online, they are disconnected immediately.

The ban uses an expiration timestamp, so it continues to expire while the player is offline or the server is stopped.

---

#### `/tempban list`

Displays the players currently stored in Minecraft's player ban list, including their expiration date and reason.

```text
/tempban list
```

Because DG Utilities uses Minecraft's standard ban list, permanent bans created outside `/tempban` may also appear here.

---

#### `/tempban pardon <player>`

Removes a player from Minecraft's player ban list.

```text
/tempban pardon Player
```

This also makes ban-list management available in singleplayer worlds, where Minecraft's vanilla `/ban`, `/pardon`, and `/banlist` commands are normally unavailable.

**Default permission level:** `3`

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

#### `/endersee <player>`

Opens another online player's Ender Chest.

```text
/endersee Player
```

The target player's Ender Chest can be viewed and modified through a chest-style interface.

The target player must be online.

**Default permission level:** `2`

---

## Dropped Item Cleanup

#### `/cleardrops`

Immediately removes all currently loaded dropped item entities from every loaded dimension.

```text
/cleardrops
```

The command reports how many dropped item entities were removed.

**Default permission level:** `2`

### Automatic Cleanup

DG Utilities can automatically clear dropped items when the number of loaded item entities reaches a configurable threshold.

By default:

- Automatic cleanup is enabled.
- Cleanup starts when `2000` dropped item entities are loaded.
- A `30` second countdown starts before the cleanup.
- Players receive warning messages before the items are removed.

The automatic cleanup threshold and countdown duration can be changed in the mod configuration.

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

| Feature                             | Permission Level |
|-------------------------------------| ---: |
| `/afk`                              | 0 |
| `/trash`                            | 0 |
| `/heal`                             | 1 |
| `/enderchest`                       | 1 |
| `/announcement`                     | 2 |
| `/screenannounce`                   | 2 |
| `/fly`                              | 2 |
| `/god`                              | 2 |
| `/lastpos`                          | 2 |
| `/freeze`                           | 2 |
| `/mute`                             | 2 |
| `/invsee`                           | 2 |
| `/endersee`                         | 2 |
| `/cleardrops`                       | 2 |
| Item restriction commands           | 3 |
| Dimension and portal administration | 3 |
| `/tempban`                          | 3 |

Automatic AFK behavior can also be enabled or disabled and its inactivity timeout can be configured.

Automatic dropped-item cleanup can be enabled or disabled, and its item threshold and cleanup countdown can also be configured.

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

Some player-specific states, such as god mode, are stored in the player's persistent NBT data.

```/lastpos``` reads Minecraft's existing saved player data rather than creating a separate position database.

Client-only information, such as ignored players, is stored locally in the client's configuration folder.

---

## Languages

DG Utilities currently includes translations for:

- English (`en_us`)
- Portuguese - Brazil (`pt_br`)
- Spanish (`es_es`)

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
