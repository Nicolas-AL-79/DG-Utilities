package com.dgutilities.admin.invsee;

import com.dgutilities.Config;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public class InvseeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("invsee")
                .requires(source -> source.hasPermission(Config.COMMAND_INVSEE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("jogador", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer viewer = context.getSource()
                                    .getPlayerOrException();
                            ServerPlayer target = EntityArgument.getPlayer(context, "jogador");
                            InvseeContainer container = new InvseeContainer(target);
                            viewer.openMenu(
                                    new SimpleMenuProvider(
                                            (id, inventory, player) ->
                                                    new InvseeChestMenu(
                                                            id,
                                                            inventory,
                                                            container
                                                    ),
                                            Component.literal(
                                                    "Inventário de "
                                                            + target.getName().getString()
                                            )
                                    )
                            );
                            return 1;
                        })
                )
        );
    }
}
