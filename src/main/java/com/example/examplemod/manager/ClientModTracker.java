package com.example.examplemod.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientModTracker {

    private static final Set<UUID> CLIENTS_WITH_MOD =
            new HashSet<>();

    public static void register(ServerPlayer player) {
        CLIENTS_WITH_MOD.add(player.getUUID());
    }

    public static void unregister(ServerPlayer player) {
        CLIENTS_WITH_MOD.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerLogout(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ClientModTracker.unregister(player);
        }
    }

    public static boolean hasMod(ServerPlayer player) {
        return CLIENTS_WITH_MOD.contains(
                player.getUUID()
        );
    }
}