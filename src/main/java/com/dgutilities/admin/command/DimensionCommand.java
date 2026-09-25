package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.admin.manager.DimensionAccessManager;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DimensionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        registerDimensionBlock(dispatcher);
        registerDimensionUnblock(dispatcher);
        registerPortalBlock(dispatcher);
        registerPortalUnblock(dispatcher);
        registerDimensionAllow(dispatcher);
        registerDimensionDisallow(dispatcher);
        registerDimensionAccess(dispatcher);
        registerDimensionKey(dispatcher, buildContext);
    }

    // ----------------------------------------------------
    // DIMENSION BLOCK
    // ----------------------------------------------------

    private static void registerDimensionBlock(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimensionblock")
                .then(Commands.literal("list")
                        .executes(context -> {
                            Set<String> dimensions = DimensionAccessManager.getBlockedDimensions();
                            if (dimensions.isEmpty()) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionblock.list.empty",
                                                "There are no blocked dimensions."
                                        ), false
                                );
                                return 0;
                            }

                            String list = String.join(", ", dimensions);

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.dimensionblock.list",
                                            "Blocked dimensions: " + list,
                                            list
                                    ), false
                            );
                            return dimensions.size();
                        })
                )
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                        .executes(context -> {
                            ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                            ResourceLocation dimension = level.dimension().location();
                            boolean changed = DimensionAccessManager.blockDimension(dimension);
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionblock.already",
                                                "Dimension "
                                                        + dimension
                                                        + " is already blocked.",
                                                dimension.toString()
                                        )
                                );
                                return 0;
                            }
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.dimensionblock.success",
                                            "Dimension "
                                                    + dimension
                                                    + " has been blocked.",
                                            dimension.toString()
                                    ), false
                            );
                            return 1;
                        })
                )
        );
    }

    private static void registerDimensionUnblock(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimensionunblock")
                .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(context -> {
                            ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                            ResourceLocation dimension = level.dimension().location();
                            boolean changed = DimensionAccessManager.unblockDimension(dimension);
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionunblock.not_blocked",
                                                "Dimension "
                                                        + dimension
                                                        + " is not blocked.",
                                                dimension.toString()
                                        )
                                );
                                return 0;
                            }
                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.dimensionunblock.success",
                                            "Dimension "
                                                    + dimension
                                                    + " has been unblocked.",
                                            dimension.toString()
                                    ), false
                            );
                            return 1;
                        })
                )
        );
    }

    // ----------------------------------------------------
    // PORTAL BLOCK
    // ----------------------------------------------------

    private static void registerPortalBlock(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("portalblock")
                .then(Commands.literal("list")
                        .executes(context -> {
                            Set<String> dimensions = DimensionAccessManager.getPortalBlockedDimensions();
                            if (dimensions.isEmpty()) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.portalblock.list.empty",
                                                "There are no disabled portals."
                                        ), false
                                );
                                return 0;
                            }

                            String list = String.join(", ", dimensions);

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.portalblock.list",
                                            "Disabled portals: " + list,
                                            list
                                    ), false
                            );
                            return dimensions.size();
                        })
                )

                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                        .executes(context -> {
                            ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                            ResourceLocation dimension = level.dimension().location();
                            boolean changed = DimensionAccessManager.blockPortal(dimension);
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.portalblock.already",
                                                "Portal for "
                                                        + dimension
                                                        + " is already disabled.",
                                                dimension.toString()
                                        )
                                );
                                return 0;
                            }

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.portalblock.success",
                                            "Portal for "
                                                    + dimension
                                                    + " has been disabled.",
                                            dimension.toString()
                                    ), false
                            );
                            return 1;
                        })
                )
        );
    }

    private static void registerPortalUnblock(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("portalunblock")
                .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(context -> {
                            ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                            ResourceLocation dimension = level.dimension().location();
                            boolean changed = DimensionAccessManager.unblockPortal(dimension);
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.portalunblock.not_blocked",
                                                "Portal for "
                                                        + dimension
                                                        + " is not disabled.",
                                                dimension.toString()
                                        )
                                );
                                return 0;
                            }

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.portalunblock.success",
                                            "Portal for "
                                                    + dimension
                                                    + " has been enabled.",
                                            dimension.toString()
                                    ), false
                            );
                            return 1;
                        })
                )
        );
    }

    // ----------------------------------------------------
    // PLAYER BYPASS
    // ----------------------------------------------------

    private static void registerDimensionAllow(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimensionallow")
                .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                                    ResourceLocation dimension = level.dimension().location();
                                    boolean changed = DimensionAccessManager.allowPlayer(player, dimension);
                                    if (!changed) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.dimensionallow.already",
                                                        player.getName().getString()
                                                                + " already has access to "
                                                                + dimension
                                                                + ".",
                                                        player.getName(),
                                                        dimension.toString()
                                                )
                                        );
                                        return 0;
                                    }

                                    context.getSource().sendSuccess(
                                            () -> ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.dimensionallow.success",
                                                    player.getName().getString()
                                                            + " can now access "
                                                            + dimension
                                                            + ".",
                                                    player.getName(),
                                                    dimension.toString()
                                            ), false
                                    );
                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("list").then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            Set<String> dimensions = DimensionAccessManager.getPlayerBypasses(player.getUUID());
                            if (dimensions.isEmpty()) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionallow.list.empty",
                                                player.getName().getString()
                                                        + " has no dimension bypasses.",
                                                player.getName()
                                        ), false
                                );
                                return 0;
                            }
                            String list = String.join(", ", dimensions);

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(
                                            context.getSource(),
                                            "command.dg_utilities.dimensionallow.list",
                                            player.getName().getString()
                                                    + " can access: "
                                                    + list,
                                            player.getName(),
                                            list
                                    ), false
                            );
                            return dimensions.size();
                        })
                ))
        );
    }

    private static void registerDimensionDisallow(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimensiondisallow")
                .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                                    ResourceLocation dimension = level.dimension().location();
                                    boolean changed = DimensionAccessManager.disallowPlayer(player.getUUID(), dimension);
                                    if (!changed) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.dimensiondisallow.not_allowed",
                                                        player.getName().getString()
                                                                + " has no bypass for "
                                                                + dimension
                                                                + ".",
                                                        player.getName(),
                                                        dimension.toString()
                                                )
                                        );
                                        return 0;
                                    }

                                    context.getSource().sendSuccess(
                                            () -> ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.dimensiondisallow.success",
                                                    "Access to "
                                                            + dimension
                                                            + " was removed from "
                                                            + player.getName().getString()
                                                            + ".",
                                                    dimension.toString(),
                                                    player.getName()
                                            ),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )

                .then(Commands.literal("all").then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            boolean changed = DimensionAccessManager.disallowAll(player.getUUID());
                            if (!changed) {
                                context.getSource().sendFailure(
                                        ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensiondisallow.all.empty",
                                                player.getName().getString()
                                                        + " has no dimension bypasses.",
                                                player.getName()
                                        )
                                );
                                return 0;
                            }

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.dimensiondisallow.all.success",
                                            "All dimension bypasses were removed from "
                                                    + player.getName().getString()
                                                    + ".",
                                            player.getName()
                                    ), false
                            );
                            return 1;
                        })
                )));
    }

    // ----------------------------------------------------
    // GLOBAL ACCESS LIST
    // ----------------------------------------------------

    private static void registerDimensionAccess(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dimensionaccess")
                .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                .then(Commands.literal("list")
                        .executes(context -> {
                            Map<UUID, DimensionAccessManager.PlayerAccessData> access = DimensionAccessManager.getAllPlayerAccess();
                            if (access.isEmpty()) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionaccess.empty",
                                                "No players have dimension bypasses."
                                        ), false
                                );
                                return 0;
                            }

                                            context.getSource().sendSuccess(
                                                    () -> ModMessages.get(
                                                            context.getSource(),
                                                            "command.dg_utilities.dimensionaccess.header",
                                                            "Players with dimension access:"
                                                    ),
                                                    false
                                            );
                            for (DimensionAccessManager.PlayerAccessData data : access.values()) {
                                String playerName = data.playerName != null ? data.playerName : "Unknown";
                                String dimensions = String.join(", ", data.dimensions);
                                context.getSource().sendSuccess(() -> componentMessage(playerName + ": " + dimensions), false);
                            }
                            return access.size();
                        })
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> {
                                    ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                                    ResourceLocation dimension = level.dimension().location();
                                    Map<UUID, DimensionAccessManager.PlayerAccessData> access = DimensionAccessManager.getAllPlayerAccess();
                                    int count = 0;
                                    context.getSource().sendSuccess(
                                            () -> ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.dimensionaccess.dimension.header",
                                                    "Players with access to "
                                                            + dimension
                                                            + ":",
                                                    dimension.toString()
                                            ), false
                                    );

                                    for (DimensionAccessManager.PlayerAccessData data : access.values()) {
                                        if (!data.dimensions.contains(dimension.toString())) {
                                            continue;
                                        }
                                        String playerName = data.playerName != null ? data.playerName : "Unknown";
                                        context.getSource().sendSuccess(() -> componentMessage("- " + playerName), false);
                                        count++;
                                    }

                                    if (count == 0) {
                                        context.getSource().sendSuccess(
                                                () -> ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.dimensionaccess.dimension.empty",
                                                        "No players have a bypass for "
                                                                + dimension
                                                                + ".",
                                                        dimension.toString()
                                                ), false
                                        );
                                    }
                                    return count;
                                })
                        )
                )
        );
    }

    // ----------------------------------------------------
    // DIMENSION KEY
    // ----------------------------------------------------

    private static void registerDimensionKey(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("dimensionkey")
                .then(Commands.literal("list")
                        .executes(context -> {
                            Map<String, String> keys = DimensionAccessManager.getDimensionKeys();
                            if (keys.isEmpty()) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionkey.list.empty",
                                                "No dimension keys are configured."
                                        ), false
                                );
                                return 0;
                            }

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.dimensionkey.list.header",
                                            "Dimension keys:"
                                    ), false
                            );

                            keys.forEach((dimension, item) ->
                                    context.getSource().sendSuccess(
                                            () -> componentMessage("- " + dimension + " -> " + item), false
                                    )
                            );
                            return keys.size();
                        })
                )

                .then(Commands.literal("check").then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(context -> {
                            ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                            ResourceLocation dimension = level.dimension().location();
                            String key = DimensionAccessManager.getDimensionKey(dimension);

                            if (key == null) {
                                context.getSource().sendSuccess(
                                        () -> ModMessages.get(context.getSource(),
                                                "command.dg_utilities.dimensionkey.check.none",
                                                "No key is required for "
                                                        + dimension + ".",
                                                dimension.toString()
                                        ), false
                                );
                                return 0;
                            }

                            context.getSource().sendSuccess(
                                    () -> ModMessages.get(context.getSource(),
                                            "command.dg_utilities.dimensionkey.check",
                                            "Key for "
                                                    + dimension
                                                    + ": "
                                                    + key,
                                            dimension.toString(),
                                            key
                                    ), false
                            );
                            return 1;
                        })
                ))

                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .then(Commands.argument("item", ItemArgument.item(buildContext))
                                        .executes(context -> {
                                            ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                                            ResourceLocation dimension = level.dimension().location();
                                            Item item = ItemArgument.getItem(context, "item").getItem();
                                            boolean changed = DimensionAccessManager.setDimensionKey(dimension, item);

                                            if (!changed) {
                                                context.getSource().sendFailure(
                                                        ModMessages.get(context.getSource(),
                                                                "command.dg_utilities.dimensionkey.set.same",
                                                                "That item is already the key for "
                                                                        + dimension
                                                                        + ".",
                                                                dimension.toString()
                                                        )
                                                );
                                                return 0;
                                            }

                                            context.getSource().sendSuccess(
                                                    () -> ModMessages.get(context.getSource(),
                                                            "command.dg_utilities.dimensionkey.set.success",
                                                            item.getDescription().getString()
                                                                    + " is now the key for "
                                                                    + dimension
                                                                    + ".",
                                                            item.getDescription(),
                                                            dimension.toString()
                                                    ), false
                                            );
                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("remove")
                        .requires(source -> source.hasPermission(Config.COMMAND_DIMENSION_PERMISSION_LEVEL.get()))
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> {
                                    ServerLevel level = DimensionArgument.getDimension(context, "dimension");
                                    ResourceLocation dimension = level.dimension().location();
                                    boolean changed = DimensionAccessManager.removeDimensionKey(dimension);
                                    if (!changed) {
                                        context.getSource().sendFailure(
                                                ModMessages.get(context.getSource(),
                                                        "command.dg_utilities.dimensionkey.remove.none",
                                                        "Dimension "
                                                                + dimension
                                                                + " has no configured key.",
                                                        dimension.toString()
                                                )
                                        );
                                        return 0;
                                    }

                                    context.getSource().sendSuccess(
                                            () -> ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.dimensionkey.remove.success",
                                                    "The key for "
                                                            + dimension
                                                            + " has been removed.",
                                                    dimension.toString()
                                            ), false
                                    );
                                    return 1;
                                })
                        )
                )
        );
    }

    /*
     * Pequeno helper para mensagens que não precisam
     * de tradução por enquanto.
     */
    private static net.minecraft.network.chat.Component componentMessage(String text) {
        return net.minecraft.network.chat.Component.literal(text);
    }
}