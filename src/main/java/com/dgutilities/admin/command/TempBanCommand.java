package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TempBanCommand {

    private static final Pattern DURATION_PATTERN = Pattern.compile("^([1-9]\\d*)([smhdw])$");

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tempban")
                .requires(source -> source.hasPermission(Config.COMMAND_TEMPBAN_PERMISSION_LEVEL.get()))
                // /tempban list
                .then(Commands.literal("list")
                        .executes(context ->
                                listTempBans(context.getSource())
                        )
                )

                // /tempban pardon <player>
                .then(Commands.literal("pardon")
                        .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                .executes(context ->
                                        pardonTempBan(context.getSource(),
                                                GameProfileArgument.getGameProfiles(
                                                        context,
                                                        "player"
                                                )
                                        )
                                )
                        )
                )

                // /tempban <player> <duration> [reason]
                .then(Commands.argument("players", GameProfileArgument.gameProfile())
                        .then(Commands.argument("duration", StringArgumentType.word())
                                .executes(context ->
                                        tempBan(context.getSource(),
                                                GameProfileArgument.getGameProfiles(context, "players"),
                                                StringArgumentType.getString(context, "duration"),
                                                "No reason specified."
                                        )
                                )
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(context ->
                                                tempBan(context.getSource(),
                                                        GameProfileArgument.getGameProfiles(context, "players"),
                                                        StringArgumentType.getString(context, "duration"),
                                                        StringArgumentType.getString(context, "reason")
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int tempBan(CommandSourceStack source, Collection<GameProfile> profiles, String durationText, String reason) {
        Duration duration = parseDuration(durationText);
        if (duration == null) {
            source.sendFailure(
                    ModMessages.get(source,
                            "command.dg_utilities.tempban.invalid_duration",
                            "Invalid duration. Use formats such as 30m, 2h, 7d or 1w."
                    )
            );
            return 0;
        }

        Instant now = Instant.now();
        Instant expiration = now.plus(duration);
        Date createdDate = Date.from(now);
        Date expirationDate = Date.from(expiration);

        UserBanList bans = source.getServer().getPlayerList().getBans();
        String sourceName = source.getTextName();

        for (GameProfile profile : profiles) {
            UserBanListEntry entry = new UserBanListEntry(profile, createdDate, sourceName, expirationDate, reason);
            bans.add(entry);
            ServerPlayer onlinePlayer = source.getServer().getPlayerList().getPlayer(profile.getId());
            if (onlinePlayer != null) {
                onlinePlayer.connection.disconnect(
                        ModMessages.get(onlinePlayer,
                                "command.dg_utilities.tempban.disconnect",
                                "You have been temporarily banned.\n"
                                        + "Reason: " + reason + "\n"
                                        + "Expires: " + DATE_FORMAT.format(expiration),
                                reason, DATE_FORMAT.format(expiration)
                        )
                );
            }

            source.sendSuccess(
                    () -> ModMessages.get(source,
                            "command.dg_utilities.tempban.success",
                            profile.getName()
                                    + " has been temporarily banned for "
                                    + durationText + ". Reason: " + reason,
                            profile.getName(), durationText, reason
                    ), true
            );
        }
        return profiles.size();
    }

    private static Duration parseDuration(String input) {
        Matcher matcher = DURATION_PATTERN.matcher(input.toLowerCase());
        Duration duration = null;
        if (matcher.matches()) {
            try {
                long value = Long.parseLong(matcher.group(1));

                duration = switch (matcher.group(2)) {
                    case "s" -> Duration.ofSeconds(value);
                    case "m" -> Duration.ofMinutes(value);
                    case "h" -> Duration.ofHours(value);
                    case "d" -> Duration.ofDays(value);
                    case "w" -> Duration.ofDays(Math.multiplyExact(value, 7));
                    default -> null;
                };
            } catch (ArithmeticException ignored) {
            }
        }
        return duration;
    }

    private static int pardonTempBan(CommandSourceStack source, Collection<GameProfile> profiles) {
        UserBanList bans = source.getServer().getPlayerList().getBans();
        int pardoned = 0;
        for (GameProfile profile : profiles) {
            if (bans.isBanned(profile)) {
                bans.remove(profile);
                pardoned++;
                source.sendSuccess(
                        () -> ModMessages.get(source,
                                "command.dg_utilities.tempban.pardon.success",
                                profile.getName() + " is no longer banned.",
                                profile.getName()
                        ), true
                );
            } else {
                source.sendFailure(
                        ModMessages.get(source,
                                "command.dg_utilities.tempban.pardon.not_banned",
                                profile.getName() + " is not banned.",
                                profile.getName()
                        )
                );
            }
        }
        return pardoned;
    }

    private static int listTempBans(CommandSourceStack source) {
        UserBanList bans = source.getServer().getPlayerList().getBans();
        Collection<UserBanListEntry> entries = bans.getEntries();
        if (entries.isEmpty()) {
            source.sendSuccess(
                    () -> ModMessages.get(source,
                            "command.dg_utilities.tempban.list.empty",
                            "There are no banned players."
                    ), false
            );
            return 0;
        }
        source.sendSuccess(
                () -> ModMessages.get(source,
                        "command.dg_utilities.tempban.list.header",
                        "Banned players: " + entries.size(),
                        entries.size()
                ), false
        );
        for (UserBanListEntry entry : entries) {
            String playerName = entry.getDisplayName().getString();
            String expiration = entry.getExpires() != null ? DATE_FORMAT.format(entry.getExpires().toInstant()) : "Never";
            source.sendSuccess(
                    () -> ModMessages.get(source,
                            "command.dg_utilities.tempban.list.entry",
                            "- "
                                    + playerName
                                    + " | Expires: " + expiration
                                    + " | Reason: " + entry.getReason(),
                            playerName,
                            expiration,
                            entry.getReason()
                    ), false
            );
        }
        return entries.size();
    }
}
