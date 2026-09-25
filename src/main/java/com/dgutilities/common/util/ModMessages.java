package com.dgutilities.common.util;

import com.dgutilities.common.manager.ClientModTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;

public class ModMessages {
    public static Component get(CommandSourceStack source, String translationKey, String fallback, Object... args) {
        ServerPlayer player = source.getPlayer();
        return player != null
                ? get(player, translationKey, fallback, args)
                : Component.literal(fallback);
    }

    public static Component get(ServerPlayer player, String translationKey, String fallback, Object... args) {
        return ClientModTracker.hasMod(player)
                ? Component.translatable(translationKey, args)
                : Component.literal(fallback);
    }
}