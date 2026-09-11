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

public class FreezeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("freeze")
                .requires(source -> source.hasPermission(Config.COMMAND_FREEZE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");

                            for (ServerPlayer player : players) {
                                PunishmentManager.setFrozen(player, true);
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
