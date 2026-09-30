package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.admin.manager.ClearDropsManager;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ClearDropsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cleardrops")
                .requires(source -> source.hasPermission(Config.COMMAND_CLEARDROPS_PERMISSION_LEVEL.get()))
                .executes(context -> {
                    int removed = ClearDropsManager.clearDroppedItems(context.getSource().getServer());
                    if (removed > 0) {
                        context.getSource().sendSuccess(
                                () -> ModMessages.get(context.getSource(),
                                        "command.dg_utilities.cleardrops.success",
                                        "Cleared "
                                                + removed
                                                + " dropped item entities.",
                                        removed
                                ), false
                        );
                    } else {
                        context.getSource().sendSuccess(
                                () -> ModMessages.get(context.getSource(),
                                        "command.dg_utilities.cleardrops.empty",
                                        "No dropped item entities were found."
                                ), false
                        );
                    }
                    return removed;
                })
        );
    }
}
