package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class EnderseeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("endersee")
                .requires(source -> source.hasPermission(Config.COMMAND_ENDERSEE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer executor = context.getSource().getPlayer();

                            if (executor != null) {
                                ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                openEnderChest(executor, target);
                                return 1;
                            } else {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.endersee.only_player",
                                                "Only a player can use /endersee."
                                        )
                                );
                                return 0;
                            }
                        })
                )
        );
    }

    private static void openEnderChest(ServerPlayer executor, ServerPlayer target) {
        executor.openMenu(new SimpleMenuProvider((containerId, playerInventory, player) ->
                ChestMenu.threeRows(containerId, playerInventory, target.getEnderChestInventory()),
                Component.translatable("container.dg_utilities.endersee", target.getName())
        ));
    }
}
