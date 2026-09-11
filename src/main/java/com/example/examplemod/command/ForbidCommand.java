package com.example.examplemod.command;

import com.example.examplemod.Config;
import com.example.examplemod.manager.ForbiddenItemsManager;
import com.example.examplemod.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.Collection;

public class ForbidCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {

        dispatcher.register(Commands.literal("forbid")
                .requires(source -> source.hasPermission(Config.COMMAND_FORBID_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");
                                    Item item = ItemArgument.getItem(context, "item").getItem();

                                    for (ServerPlayer player : players) {
                                        ForbiddenItemsManager.forbidItem(player.getUUID(), item);
                                    }

                                    context.getSource().sendSuccess(() ->
                                            ModMessages.get(
                                                    context.getSource(),
                                                    "command.mod_de_teste.forbid.success",
                                                    "Item "
                                                            + item.getDescription().getString()
                                                            + " has been forbidden for "
                                                            + players.size()
                                                            + " player(s).",
                                                    item.getDescription(),
                                                    players.size()
                                            ), false
                                    );

                                    return players.size();
                                })
                        )
                )
        );

        dispatcher.register(Commands.literal("allow")
                .requires(source -> source.hasPermission(Config.COMMAND_FORBID_PERMISSION_LEVEL.get()))
                .then(Commands.argument("alvos", EntityArgument.players())
                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "alvos");
                                    Item item = ItemArgument.getItem(context, "item").getItem();

                                    for (ServerPlayer player : players) {
                                        ForbiddenItemsManager.allowItem(player.getUUID(), item);
                                    }

                                    context.getSource().sendSuccess(() ->
                                            ModMessages.get(
                                                    context.getSource(),
                                                    "command.mod_de_teste.allow.success",
                                                    "Item "
                                                            + item.getDescription().getString()
                                                            + " is now allowed for "
                                                            + players.size()
                                                            + " player(s).",
                                                    item.getDescription(),
                                                    players.size()
                                            ), false
                                    );

                                    return players.size();
                                })
                        )
                )
        );
    }
}
