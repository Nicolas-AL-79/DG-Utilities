package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class HealCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heal")
                .requires(source -> source.hasPermission(Config.COMMAND_HEAL_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");

                            for (ServerPlayer player : players) {
                                player.setHealth(player.getMaxHealth());
                                player.getFoodData().setFoodLevel(20);
                                player.getFoodData().setSaturation(20.0f);
                                player.removeAllEffects();
                            }

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.heal.success",
                                            players.size() + " player(s) have been healed.",
                                            players.size()
                                    ), false
                            );

                            return players.size();
                        })
                )
        );
    }
}
