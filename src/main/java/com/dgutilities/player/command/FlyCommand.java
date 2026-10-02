package com.dgutilities.player.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class FlyCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fly")
                .requires(source -> source.hasPermission(Config.COMMAND_FLY_PERMISSION_LEVEL.get()))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    if (player != null) {
                        if (canFlyNaturally(player)) {
                            context.getSource().sendFailure(
                                    ModMessages.get(context.getSource(),
                                            "command.dg_utilities.fly.natural",
                                            "Your current game mode already allows flight."
                                    )
                            );
                            return 0;
                        }
                        toggleFlight(player);
                        return 1;
                    } else {
                        context.getSource().sendFailure(
                                ModMessages.get(context.getSource(),
                                        "command.dg_utilities.fly.self.only_player",
                                        "Only a player can use /fly without specifying a target."
                                )
                        );
                        return 0;
                    }
                })
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            if (canFlyNaturally(target)) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.fly.target_natural",
                                                target.getName().getString()
                                                        + " is already able to fly because of their game mode.",
                                                target.getName()
                                        )
                                );
                                return 0;
                            }
                            boolean enabled = toggleFlight(target);

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            enabled ? "command.dg_utilities.fly.other.enabled" : "command.dg_utilities.fly.other.disabled",
                                            enabled ? target.getName().getString() + " can now fly." : target.getName().getString() + " can no longer fly.",
                                            target.getName()
                                    ), false
                            );
                            return 1;
                        })
                )
        );
    }

    private static boolean toggleFlight(ServerPlayer player) {
        boolean enabled = !player.getAbilities().mayfly;
        player.getAbilities().mayfly = enabled;
        if (!enabled) {
            player.getAbilities().flying = false;
        }
        player.onUpdateAbilities();
        player.sendSystemMessage(
                ModMessages.get(player,
                        enabled ? "command.dg_utilities.fly.enabled" : "command.dg_utilities.fly.disabled",
                        enabled ? "Flight enabled." : "Flight disabled."
                )
        );
        return enabled;
    }

    private static boolean canFlyNaturally(ServerPlayer player) {
        GameType gameMode = player.gameMode.getGameModeForPlayer();
        return gameMode == GameType.CREATIVE || gameMode == GameType.SPECTATOR;
    }
}
