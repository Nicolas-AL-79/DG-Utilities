package com.dgutilities.server.command;

import com.dgutilities.Config;
import com.dgutilities.server.manager.AFKManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class AFKCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("afk")
                .requires(source -> source.hasPermission(Config.COMMAND_AFK_PERMISSION_LEVEL.get()))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    AFKManager.startPending(player);
                    return 1;
                })
        );
    }
}
