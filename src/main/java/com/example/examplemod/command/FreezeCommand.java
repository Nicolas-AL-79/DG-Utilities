package com.example.examplemod.command;

import com.example.examplemod.Config;
import com.example.examplemod.manager.PunishmentManager;
import com.example.examplemod.util.MobTargetUtils;
import com.example.examplemod.util.ModMessages;
import com.example.examplemod.util.DurationUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class FreezeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("freeze")
                .requires(source -> source.hasPermission(Config.COMMAND_FREEZE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");

                            for (ServerPlayer player : players) {
                                PunishmentManager.setFrozen(player, true);
                                MobTargetUtils.clearNearbyMobTargets(player, 32.0);
                                player.sendSystemMessage(ModMessages.get(
                                        player,
                                        "command.mod_de_teste.freeze.frozen",
                                        "You have been frozen."
                                ));
                            }

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.mod_de_teste.freeze.success",
                                            players.size() + " player(s) have been frozen.",
                                            players.size()
                                    ), false
                            );

                            return players.size();
                        })
                        .then(Commands.argument("tempo", StringArgumentType.word())
                                .executes(context -> {
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");
                                    String timeInput = StringArgumentType.getString(context, "tempo");
                                    long duration = DurationUtils.parseDuration(timeInput);

                                    if (duration < 0) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(context.getSource(),
                                                        "command.mod_de_teste.punishment.invalid_duration",
                                                        "Invalid duration. Use s, m, h or d. Example: 30s, 10m, 2h, 3d."
                                                )
                                        );
                                        return 0;
                                    }

                                    for (ServerPlayer player : players) {
                                        PunishmentManager.setFrozen(player, true, duration);
                                        MobTargetUtils.clearNearbyMobTargets(player, 32.0);
                                        player.sendSystemMessage(
                                                ModMessages.get(
                                                        player,
                                                        "command.mod_de_teste.freeze.frozen_timed",
                                                        "You have been frozen for "
                                                                + timeInput
                                                                + ".",
                                                        timeInput
                                                )
                                        );
                                    }

                                    context.getSource().sendSuccess(() ->
                                            ModMessages.get(context.getSource(),
                                                    "command.mod_de_teste.freeze.success_timed",
                                                    players.size()
                                                            + " player(s) have been frozen for "
                                                            + timeInput
                                                            + ".",
                                                    players.size(),
                                                    timeInput
                                            ), false
                                    );
                                    return players.size();
                                })
                        )
                )
        );

        dispatcher.register(Commands.literal("unfreeze")
                .requires(source -> source.hasPermission(Config.COMMAND_FREEZE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");

                            for (ServerPlayer player : players) {
                                PunishmentManager.setFrozen(player, false);
                                player.sendSystemMessage(
                                        ModMessages.get(
                                                player,
                                                "command.mod_de_teste.unfreeze.unfrozen",
                                                "You have been unfrozen!"
                                        )
                                );
                            }

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.mod_de_teste.unfreeze.success",
                                            players.size() + " player(s) have been unfrozen.",
                                            players.size()
                                    ), false
                            );

                            return players.size();
                        })
                )
        );
    }
}
