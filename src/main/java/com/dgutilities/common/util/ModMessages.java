package com.dgutilities.common.util;

import com.dgutilities.common.manager.ClientModTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;

public class ModMessages {

    public static Component get(
            CommandSourceStack source,
            String translationKey,
            String fallback,
            Object... args
    ) {
        ServerPlayer player = source.getPlayer();

        if (player != null) {
            return get(
                    player,
                    translationKey,
                    fallback,
                    args
            );
        }


        return Component.literal(fallback);
    }

    public static Component get(
            ServerPlayer player,
            String translationKey,
            String fallback,
            Object... args
    ) {

        if (ClientModTracker.hasMod(player)) {
            return Component.translatable(
                    translationKey,
                    args
            );
        }

        return Component.literal(
                fallback
        );
    }
}