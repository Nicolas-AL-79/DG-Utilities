package com.dgutilities.server.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class EnderchestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("enderchest")
                .requires(source -> source.hasPermission(Config.COMMAND_ENDERCHEST_PERMISSION_LEVEL.get()))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    if (player != null) {
                        openEnderChest(player);
                        return 1;
                    } else {
                        context.getSource().sendFailure(
                                ModMessages.get(context.getSource(),
                                        "command.dg_utilities.enderchest.only_player",
                                        "Only a player can use /enderchest."
                                )
                        );
                        return 0;
                    }
                })
        );
    }

    private static void openEnderChest(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((containerId, playerInventory, ignored) ->
                ChestMenu.threeRows(containerId, playerInventory, player.getEnderChestInventory()),
                Component.translatable("container.dg_utilities.enderchest")
        ));
    }
}
