package com.dgutilities.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class IgnoreCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ignore")
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "player");
                            PlayerInfo target = findPlayer(name);
                            if (target == null) {
                                sendMessage(Component.literal("Player not found."));
                                return 0;
                            }

                            IgnoreManager.ignore(target.getProfile().getId());

                            sendMessage(Component.translatable("command.dg_utilities.ignore.ignored",
                                    target.getProfile().getName()));

                            return 1;
                        })
                )
        );

        dispatcher.register(Commands.literal("unignore")
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "player");
                            PlayerInfo target = findPlayer(name);
                            if (target == null) {
                                sendMessage(Component.literal("Player not found."));
                                return 0;
                            }

                            IgnoreManager.unignore(target.getProfile().getId());

                            sendMessage(Component.translatable("command.dg_utilities.ignore.unignored",
                                    target.getProfile().getName()));

                            return 1;
                        })
                )
        );
    }

    private static PlayerInfo findPlayer(String name) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            return null;
        }
        for (PlayerInfo player : minecraft.getConnection().getOnlinePlayers()) {
            if (player.getProfile().getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    private static void sendMessage(Component message) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(message);
        }
    }
}
