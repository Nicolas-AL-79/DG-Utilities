package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class LastPosCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lastpos")
                .requires(source -> source.hasPermission(Config.COMMAND_LASTPOS_PERMISSION_LEVEL.get()))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
                            GameProfile profile = profiles.iterator().next();
                            return showLastPosition(context.getSource(), profile);
                        })
                )
        );
    }

    private static int showLastPosition(CommandSourceStack source, GameProfile profile) {
        Path playerDataPath = source.getServer()
                .getWorldPath(LevelResource.PLAYER_DATA_DIR)
                .resolve(profile.getId().toString() + ".dat");

        if (!Files.exists(playerDataPath)) {
            source.sendFailure(
                    ModMessages.get(source,
                            "command.dg_utilities.lastpos.no_data",
                            "No saved player data was found for "
                                    + profile.getName()
                                    + ".",
                            profile.getName()
                    )
            );

            return 0;
        }

        try {
            CompoundTag tag = NbtIo.readCompressed(playerDataPath.toFile());
            ListTag pos = tag.getList("Pos", Tag.TAG_DOUBLE);

            double x = pos.getDouble(0);
            double y = pos.getDouble(1);
            double z = pos.getDouble(2);

            String dimension = tag.getString("Dimension");

            source.sendSuccess(
                    () -> ModMessages.get(source,
                            "command.dg_utilities.lastpos.success",
                            profile.getName()
                                    + "'s last position:\n"
                                    + "Dimension: " + dimension + "\n"
                                    + " - X: " + String.format("%.2f", x)
                                    + ", Y: " + String.format("%.2f", y)
                                    + ", Z: " + String.format("%.2f", z),
                            profile.getName(),
                            dimension,
                            String.format("%.2f", x),
                            String.format("%.2f", y),
                            String.format("%.2f", z)
                    ), false
            );
            return 1;
        } catch (IOException e) {
            source.sendFailure(
                    ModMessages.get(source,
                            "command.dg_utilities.lastpos.read_error",
                            "Failed to read player data for "
                                    + profile.getName() + ".",
                            profile.getName()
                    )
            );
            return 0;
        }
    }
}
