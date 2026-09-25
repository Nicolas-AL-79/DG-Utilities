package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.admin.manager.ForbiddenItemsManager;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.Set;

public class ForbidCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {

        dispatcher.register(Commands.literal("itemforbid")
                .requires(source -> source.hasPermission(Config.COMMAND_FORBID_PERMISSION_LEVEL.get()))
                .then(Commands.argument("item", ItemArgument.item(buildContext))
                        .executes(context -> {
                            Item item = ItemArgument.getItem(context, "item").getItem();
                            ForbiddenItemsManager.forbidItem(item);

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemforbid.success",
                                            "Item "
                                                    + item.getDescription().getString()
                                                    + " has been forbidden.",
                                            item.getDescription()
                                    ), false
                            );

                            return 1;
                        })
                )
                .then(Commands.literal("list")
                        .executes(context -> {
                            Set<String> items = ForbiddenItemsManager.getForbiddenItems();
                            if (items.isEmpty()) {
                                context.getSource().sendFailure(
                                        ModMessages.get(
                                                context.getSource(),
                                                "command.dg_utilities.itemforbid.list.empty",
                                                "There are no forbidden items."
                                        )
                                );return 0;
                            }
                            String list = String.join(", ", items);
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemforbid.list",
                                            "Forbidden items: " + list,
                                            list
                                    ), false
                            );
                            return items.size();
                        })
                )
                .then(Commands.literal("check").then(Commands.argument("item", ItemArgument.item(buildContext))
                        .executes(context -> {
                            Item item = ItemArgument.getItem(context, "item").getItem();
                            boolean forbidden = ForbiddenItemsManager.isGloballyForbidden(item);
                            if (forbidden) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(
                                                context.getSource(),
                                                "command.dg_utilities.itemforbid.check.forbidden",
                                                "Item "
                                                        + item.getDescription().getString()
                                                        + " is forbidden.",
                                                item.getDescription()
                                        ), false
                                );
                                return 1;
                            }
                            context.getSource().sendFailure(
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemforbid.check.allowed",
                                            "Item "
                                                    + item.getDescription().getString()
                                                    + " is not forbidden.",
                                            item.getDescription()
                                    )
                            );
                            return 0;
                        })
                ))
                .then(Commands.literal("clear")
                        .executes(context -> {
                            boolean changed = ForbiddenItemsManager.clearForbiddenItems();
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(
                                                context.getSource(),
                                                "command.dg_utilities.itemforbid.clear.empty",
                                                "There are no forbidden items to clear."
                                        )
                                );
                                return 0;
                            }
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemforbid.clear.success",
                                            "All forbidden items have been cleared."
                                    ), false
                            );
                            return 1;
                        })
                )
        );

        dispatcher.register(Commands.literal("itemunforbid")
                .requires(source -> source.hasPermission(Config.COMMAND_FORBID_PERMISSION_LEVEL.get()))
                .then(Commands.argument("item", ItemArgument.item(buildContext))
                        .executes(context -> {
                            Item item = ItemArgument.getItem(context, "item").getItem();
                            ForbiddenItemsManager.unforbidItem(item);

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemunforbid.success",
                                            "Item "
                                                    + item.getDescription().getString()
                                                    + " is no longer forbidden.",
                                            item.getDescription()
                                    ), false
                            );

                            return 1;
                        })
                )
        );

        dispatcher.register(Commands.literal("itemallow")
                .requires(source -> source.hasPermission(Config.COMMAND_FORBID_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            boolean changed = ForbiddenItemsManager.allowPlayer(player.getUUID());
                            int result;
                            if (changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.itemallow.player.already",
                                                player.getName().getString()
                                                        + " already has full item bypass.",
                                                player.getName()
                                        )
                                );
                                result = 1;
                            } else {

                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.itemallow.player",
                                                player.getName().getString()
                                                        + " can now bypass all forbidden item restrictions.",
                                                player.getName()
                                        ), false
                                );
                                result = 0;
                            }
                            return result;
                        })
                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    Item item = ItemArgument.getItem(context, "item").getItem();
                                    if (ForbiddenItemsManager.hasFullBypass(player.getUUID())) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.itemallow.item.full_bypass",
                                                        player.getName().getString()
                                                                + " already has full item bypass.",
                                                        player.getName()
                                                )
                                        );
                                        return 0;
                                    }
                                    if (!ForbiddenItemsManager.isGloballyForbidden(item)) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(
                                                        context.getSource(),
                                                        "command.dg_utilities.itemallow.not_forbidden",
                                                        "Item "
                                                                + item.getDescription().getString()
                                                                + " is not forbidden.",
                                                        item.getDescription()
                                                )
                                        );
                                        return 0;
                                    }
                                    if (ForbiddenItemsManager.hasItemBypass(
                                            player.getUUID(),
                                            item
                                    )) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(
                                                        context.getSource(),
                                                        "command.dg_utilities.itemallow.item.already",
                                                        player.getName().getString()
                                                                + " already has permission to bypass "
                                                                + item.getDescription().getString()
                                                                + ".",
                                                        player.getName(),
                                                        item.getDescription()
                                                )
                                        );

                                        return 0;
                                    }
                                    boolean changed = ForbiddenItemsManager.allowPlayerItem(player.getUUID(), item);
                                    if (!changed) {
                                        return 0;
                                    }
                                    context.getSource().sendSuccess(() ->
                                            ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.itemallow.item",
                                                    player.getName().getString()
                                                            + " can now bypass "
                                                            + item.getDescription().getString()
                                                            + ".",
                                                    player.getName(),
                                                    item.getDescription()
                                            ), false
                                    );
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("list").then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            boolean fullBypass = ForbiddenItemsManager.hasFullBypass(player.getUUID());
                            Set<String> items = ForbiddenItemsManager.getPlayerBypassItems(player.getUUID());
                            if (!fullBypass && items.isEmpty()) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.itemallow.list.none",
                                                player.getName().getString()
                                                        + " has no item bypass permissions.",
                                                player.getName()
                                        )
                                );
                                return 0;
                            }
                            String itemList = items.isEmpty() ? "None" : String.join(", ", items);
                            String fullBypassText = fullBypass ? "Yes" : "No";
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.itemallow.list",
                                            player.getName().getString()
                                                    + " - Full bypass: "
                                                    + fullBypassText
                                                    + " - Specific items: "
                                                    + itemList,
                                            player.getName(),
                                            fullBypassText,
                                            itemList
                                    ), false
                            );
                            return 1;
                        })
                ))
                .then(Commands.literal("check").then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            boolean fullBypass = ForbiddenItemsManager.hasFullBypass(player.getUUID());
                            if (fullBypass) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.itemallow.check.full",
                                                player.getName().getString()
                                                        + " has full item bypass.",
                                                player.getName()
                                        ), false
                                );
                                return 1;
                            }
                            context.getSource().sendFailure(
                                    ModMessages.get(context.getSource(),
                                            "command.dg_utilities.itemallow.check.no_full",
                                            player.getName().getString()
                                                    + " does not have full item bypass.",
                                            player.getName()
                                    )
                            );
                            return 0;
                        })
                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    Item item = ItemArgument.getItem(context, "item").getItem();
                                    if (!ForbiddenItemsManager.isGloballyForbidden(item)) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.itemallow.check.item.not_forbidden",
                                                        "Item "
                                                                + item.getDescription().getString()
                                                                + " is not forbidden.",
                                                        item.getDescription()
                                                )
                                        );
                                        return 0;
                                    }

                                    boolean canBypass = ForbiddenItemsManager.canBypassItem(player.getUUID(), item);
                                    if (canBypass) {
                                        context.getSource().sendSuccess(
                                                () -> ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.itemallow.check.item.allowed",
                                                        player.getName().getString()
                                                                + " can bypass the restriction for "
                                                                + item.getDescription().getString()
                                                                + ".",
                                                        player.getName(),
                                                        item.getDescription()
                                                ), false
                                        );
                                        return 1;
                                    }
                                    context.getSource().sendFailure(
                                            ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.itemallow.check.item.denied",
                                                    player.getName().getString()
                                                            + " cannot bypass the restriction for "
                                                            + item.getDescription().getString()
                                                            + ".",
                                                    player.getName(),
                                                    item.getDescription()
                                            )
                                    );
                                    return 0;
                                })
                        )
                ))
        );

        dispatcher.register(Commands.literal("itemdisallow")
                .requires(source -> source.hasPermission(Config.COMMAND_FORBID_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            ForbiddenItemsManager.disallowPlayer(player.getUUID());

                            context.getSource().sendSuccess(() ->
                                    ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemdisallow.player",
                                            player.getName().getString()
                                                    + " is affected by global item restrictions again.",
                                            player.getName()
                                    ), false
                            );

                            return 1;
                        })
                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    Item item = ItemArgument.getItem(context, "item").getItem();
                                    ForbiddenItemsManager.disallowPlayerItem(player.getUUID(), item);

                                    context.getSource().sendSuccess(() ->
                                            ModMessages.get(
                                                    context.getSource(),
                                                    "command.dg_utilities.itemdisallow.item",
                                                    player.getName().getString()
                                                            + " is affected by the restriction for "
                                                            + item.getDescription().getString()
                                                            + " again.",
                                                    player.getName(),
                                                    item.getDescription()
                                            ), false
                                    );
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("all").then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            boolean changed = ForbiddenItemsManager.disallowAll(player.getUUID());
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(
                                                context.getSource(),
                                                "command.dg_utilities.itemdisallow.all.none",
                                                player.getName().getString()
                                                        + " has no item bypass permissions.",
                                                player.getName()
                                        )
                                );
                                return 0;
                            }

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.itemdisallow.all.success",
                                            "All item bypass permissions were removed from "
                                                    + player.getName().getString()
                                                    + ".",
                                            player.getName()
                                    ),
                                    false
                            );
                            return 1;
                        })
                ))
        );
    }
}