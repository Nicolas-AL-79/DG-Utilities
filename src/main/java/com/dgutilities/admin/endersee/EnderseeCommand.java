package com.dgutilities.admin.endersee;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class EnderseeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("endersee")
                .requires(source -> source.hasPermission(Config.COMMAND_ENDERSEE_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            ServerPlayer executor = context.getSource().getPlayer();

                            if (executor != null) {
                                Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
                                if (profiles.size() != 1) {
                                    context.getSource().sendFailure(
                                            ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.endersee.single_player",
                                                    "You must specify exactly one player."
                                            )
                                    );
                                    return 0;
                                }
                                GameProfile profile = profiles.iterator().next();
                                return openEnderChest(executor, profile);
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

    private static int openEnderChest(ServerPlayer executor, GameProfile profile) {
        ServerPlayer target = executor.getServer().getPlayerList().getPlayer(profile.getId());
        if (target != null) {
            // ONLINE
            openOnlineEnderChest(executor, target);
            return 1;
        } else {
            // OFFLINE
            return openOfflineEnderChest(executor, profile);
        }
    }

    private static void openOnlineEnderChest(ServerPlayer executor, ServerPlayer target) {
        executor.openMenu(new SimpleMenuProvider((containerId, playerInventory, player) ->
                ChestMenu.threeRows(containerId, playerInventory, target.getEnderChestInventory()),
                Component.translatable("container.dg_utilities.endersee", target.getName())
        ));
    }

    private static int openOfflineEnderChest(ServerPlayer executor, GameProfile profile) {
        Path playerDataPath = executor.getServer()
                .getWorldPath(LevelResource.PLAYER_DATA_DIR)
                .resolve(profile.getId() + ".dat");

        File playerDataFile = playerDataPath.toFile();

        if (!playerDataFile.exists()) {
            executor.sendSystemMessage(
                    ModMessages.get(executor,
                            "command.dg_utilities.endersee.no_data",
                            "No saved player data was found for "
                                    + profile.getName() + ".",
                            profile.getName()
                    )
            );
            return 0;
        }
        try {
            CompoundTag playerData = NbtIo.readCompressed(playerDataFile);
            OfflineEnderChestContainer enderChest = new OfflineEnderChestContainer(playerData, playerDataFile);
            executor.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, player) ->
                            ChestMenu.threeRows(containerId, playerInventory, enderChest),
                            Component.translatable("container.dg_utilities.endersee", profile.getName())
            ));
            return 1;
        } catch (IOException e) {
            executor.sendSystemMessage(
                    ModMessages.get(executor,
                            "command.dg_utilities.endersee.read_error",
                            "Failed to read player data for "
                                    + profile.getName() + ".",
                            profile.getName()
                    )
            );
            return 0;
        }
    }
}
