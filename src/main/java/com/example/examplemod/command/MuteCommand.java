package com.example.examplemod.command;

import com.example.examplemod.Config;
import com.example.examplemod.manager.PunishmentManager;
import com.example.examplemod.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class MuteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mute")
                .requires(source -> source.hasPermission(Config.COMMAND_MUTE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");

                            for (ServerPlayer player : players) {
                                PunishmentManager.setMuted(player, true);
                                player.sendSystemMessage(
                                        ModMessages.get(
                                                player,
                                                "command.mod_de_teste.mute.muted",
                                                "You have been muted on the server!"
                                        )
                                );
                            }

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.mod_de_teste.mute.success",
                                            players.size() + " player(s) have been muted.",
                                            players.size()
                                    ), false
                            );

                            return players.size();
                        })
                )
        );

        dispatcher.register(Commands.literal("unmute")
                .requires(source -> source.hasPermission(Config.COMMAND_MUTE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");

                            for (ServerPlayer player : players) {
                                PunishmentManager.setMuted(player, false);
                                player.sendSystemMessage(
                                        ModMessages.get(
                                                player,
                                                "command.mod_de_teste.unmute.unmuted",
                                                "You have been unmuted!"
                                        )
                                );
                            }

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.mod_de_teste.unmute.success",
                                            players.size() + " player(s) have been unmuted.",
                                            players.size()
                                    ), false
                            );
                            return players.size();
                        })
                )
        );
    }
}
