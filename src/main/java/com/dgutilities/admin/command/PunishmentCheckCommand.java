package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.admin.manager.PunishmentRegistry;
import com.dgutilities.common.util.DurationUtils;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Map;
import java.util.UUID;

public class PunishmentCheckCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /punishments
        dispatcher.register(Commands.literal("punishments")
                .requires(source -> source.hasPermission(
                        Math.max(Config.COMMAND_FREEZE_PERMISSION_LEVEL.get(), Config.COMMAND_MUTE_PERMISSION_LEVEL.get())
                        )
                )
                .executes(context -> {
                    Map<UUID, PunishmentRegistry.PunishmentData> punishments = PunishmentRegistry.getAll();
                    if (punishments.isEmpty()) {
                        context.getSource().sendSuccess(
                                () -> ModMessages.get(context.getSource(),
                                        "command.dg_utilities.punishments.none",
                                        "There are no active punishments."
                                ), false
                        );
                        return 1;
                    }

                    context.getSource().sendSuccess(
                            () -> ModMessages.get(context.getSource(),
                                    "command.dg_utilities.punishments.header",
                                    "Active punishments:"
                            ), false
                    );

                    for (PunishmentRegistry.PunishmentData data : punishments.values()) {
                        String playerName = data.playerName != null ? data.playerName : "Unknown";

                        context.getSource().sendSuccess(
                                () -> ModMessages.get(context.getSource(),
                                        "command.dg_utilities.punishments.player",
                                        playerName + ":",
                                        playerName
                                ), false
                        );

                        if (data.freezeExpiration != null) {
                            String remaining = formatExpiration(context.getSource(), data.freezeExpiration);
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.punishments.freeze",
                                            "- Freeze: " + remaining,
                                            remaining
                                    ), false
                            );
                        }

                        if (data.muteExpiration != null) {
                            String remaining = formatExpiration(context.getSource(), data.muteExpiration);
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.punishments.mute",
                                            "- Mute: " + remaining,
                                            remaining
                                    ), false
                            );
                        }
                    }
                    return punishments.size();
                })
        );

        // /checkfreeze <player>
        dispatcher.register(Commands.literal("checkfreeze")
                .requires(source -> source.hasPermission(Config.COMMAND_FREEZE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            PunishmentRegistry.PunishmentData data = findByName(playerName);
                            if (data == null || data.freezeExpiration == null) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.checkfreeze.none",
                                                playerName
                                                        + " is not frozen.",
                                                playerName
                                        ), false
                                );
                                return 1;
                            }
                            String remaining = formatExpiration(context.getSource(), data.freezeExpiration);

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.checkfreeze.active",
                                            playerName
                                                    + " is frozen. Remaining: "
                                                    + remaining,
                                            playerName,
                                            remaining
                                    ), false
                            );
                            return 1;
                        })
                )
        );

        // /checkmute <player>
        dispatcher.register(Commands.literal("checkmute")
                .requires(source -> source.hasPermission(Config.COMMAND_MUTE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            PunishmentRegistry.PunishmentData data = findByName(playerName);
                            if (data == null || data.muteExpiration == null) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.checkmute.none",
                                                playerName
                                                        + " is not muted.",
                                                playerName
                                        ), false
                                );
                                return 1;
                            }

                            String remaining = formatExpiration(context.getSource(), data.muteExpiration);
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.checkmute.active",
                                            playerName
                                                    + " is muted. Remaining: "
                                                    + remaining,
                                            playerName,
                                            remaining
                                    ), false
                            );
                            return 1;
                        })
                )
        );
    }

    private static PunishmentRegistry.PunishmentData findByName(String playerName) {
        for (PunishmentRegistry.PunishmentData data : PunishmentRegistry.getAll().values()) {
            if (data.playerName != null && data.playerName.equalsIgnoreCase(playerName)) return data;
        }
        return null;
    }

    private static String formatExpiration(CommandSourceStack source, long expiration) {
        if (expiration == 0L) {
            return ModMessages.get(
                    source,
                    "command.dg_utilities.punishments.permanent",
                    "Permanent"
            ).getString();
        }
        return DurationUtils.formatRemaining(expiration);
    }

    private PunishmentCheckCommand() {
    }
}