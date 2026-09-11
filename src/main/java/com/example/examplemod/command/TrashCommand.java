package com.example.examplemod.command;

import com.example.examplemod.Config;
import com.example.examplemod.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class TrashCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trash")
                .requires(source -> source.hasPermission(Config.COMMAND_TRASH_PERMISSION_LEVEL.get()))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    SimpleContainer container = new SimpleContainer(27);

                    player.openMenu(new SimpleMenuProvider(
                            (id, inventory, playerEntity) -> ChestMenu.threeRows(id, inventory, container),
                            ModMessages.get(
                                    player,
                                    "command.mod_de_teste.trash.title",
                                    "Trash"
                            )
                    ));

                    return 1;
                })
        );
    }
}
