package com.dgutilities.player.command;

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
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    if (player != null) {
                        healPlayer(player);
                        return 1;
                    } else {
                        context.getSource().sendFailure(
                                ModMessages.get(context.getSource(),
                                        "command.dg_utilities.heal.self.only_player",
                                        "Only a player can use /heal without specifying a target."
                                )
                        );
                        return 0;
                    }
                })
                .then(Commands.argument("alvos", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");
                            for (ServerPlayer player : players) {
                                healPlayer(player);
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

    private static void healPlayer(ServerPlayer player) {
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);
        player.removeAllEffects();

        player.sendSystemMessage(
                ModMessages.get(
                        player,
                        "command.dg_utilities.heal.self",
                        "You have been healed!"
                )
        );
    }
}
