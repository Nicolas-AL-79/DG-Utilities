package com.dgutilities.admin.command;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class AnnouncementCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (Config.COMMAND_ANNOUNCEMENT_ENABLED.get()) {
            registerAnnouncement(dispatcher);
        }
        if (Config.COMMAND_SCREEN_ANNOUNCEMENT_ENABLED.get()) {
            registerScreenAnnouncement(dispatcher);
        }
    }

    private static void registerAnnouncement(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("announcement")
                .requires(source -> source.hasPermission(Config.COMMAND_ANNOUNCEMENT_PERMISSION_LEVEL.get()))
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String message = StringArgumentType.getString(context, "message");
                            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                                player.sendSystemMessage(ModMessages.get(
                                        player,
                                        "command.dg_utilities.announcement",
                                        "[Announcement] " + message,
                                        message
                                ));
                            }
                            return 1;
                        })
                )
        );
    }

    private static void registerScreenAnnouncement(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("screenannounce")
                .requires(source -> source.hasPermission(Config.COMMAND_SCREEN_ANNOUNCEMENT_PERMISSION_LEVEL.get()))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                    String message = StringArgumentType.getString(context, "message");
                                    Component title = Component.literal(message).withStyle(ChatFormatting.GOLD);
                                    for (ServerPlayer player : targets) {
                                        player.connection.send(new ClientboundSetTitlesAnimationPacket(
                                                Config.SCREEN_ANNOUNCEMENT_FADE_IN.get(),
                                                Config.SCREEN_ANNOUNCEMENT_STAY.get(),
                                                Config.SCREEN_ANNOUNCEMENT_FADE_OUT.get()
                                        ));
                                        player.connection.send(new ClientboundSetTitleTextPacket(title));
                                    }
                                    context.getSource().sendSuccess(
                                            () -> ModMessages.get(context.getSource(),
                                                    "command.dg_utilities.screenannounce.success",
                                                    "Screen announcement sent to "
                                                            + targets.size()
                                                            + " player(s).",
                                                    targets.size()
                                            ), false
                                    );
                                    return targets.size();
                                })
                        )
                )
        );
    }
}