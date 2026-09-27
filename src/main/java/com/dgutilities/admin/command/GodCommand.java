package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.admin.manager.GodManager;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class GodCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("god")
            .requires(source -> source.hasPermission(Config.COMMAND_GOD_PERMISSION_LEVEL.get()))
            // /god
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayer();
                if (player != null) {
                    GodManager.toggle(player);
                    return 1;
                } else {
                    context.getSource().sendFailure(
                            ModMessages.get(context.getSource(),
                                    "command.dg_utilities.god.self.only_player",
                                    "Only a player can use /god without specifying a target."
                            )
                    );
                    return 0;
                }
            })
            // /god <player>
            .then(Commands.argument("target", EntityArgument.player())
                    .executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            boolean enabled = GodManager.toggle(target);
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            enabled ? "command.dg_utilities.god.other.enabled" : "command.dg_utilities.god.other.disabled",
                                            enabled ? target.getName().getString() + " is now in god mode." : target.getName().getString() + " is no longer in god mode.",
                                            target.getName()
                                    ), false
                            );
                            return 1;
                    })
            )
        );
    }
}