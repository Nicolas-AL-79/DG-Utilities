package com.dgutilities.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class IgnoredTabManager {

    private static final Map<UUID, Component> ORIGINAL_NAMES = new HashMap<>();
    private static final Map<UUID, Component> LAST_APPLIED_NAMES = new HashMap<>();

    public static void update() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getConnection() == null) {
            clear();
        } else {
            for (PlayerInfo playerInfo : minecraft.getConnection().getOnlinePlayers()) {
                UUID uuid = playerInfo.getProfile().getId();

                if (IgnoreManager.isIgnored(uuid)) {
                    applyIgnoredTag(playerInfo);
                } else {
                    removeIgnoredTag(playerInfo);
                }
            }
        }
    }

    private static void applyIgnoredTag(PlayerInfo playerInfo) {
        UUID uuid = playerInfo.getProfile().getId();
        Component currentName = playerInfo.getTabListDisplayName();
        Component lastApplied = LAST_APPLIED_NAMES.get(uuid);
        if (lastApplied == null || !Objects.equals(currentName, lastApplied)) {
            ORIGINAL_NAMES.put(
                    uuid,
                    getBaseName(playerInfo)
            );
        }

        Component original = ORIGINAL_NAMES.get(uuid);
        Component ignoredName = Component.literal("[IGNORED] ").append(original);
        playerInfo.setTabListDisplayName(ignoredName);

        LAST_APPLIED_NAMES.put(uuid, ignoredName);
    }

    private static void removeIgnoredTag(PlayerInfo playerInfo) {
        UUID uuid = playerInfo.getProfile().getId();
        Component original = ORIGINAL_NAMES.remove(uuid);
        Component lastApplied = LAST_APPLIED_NAMES.remove(uuid);

        if (original != null && Objects.equals(playerInfo.getTabListDisplayName(), lastApplied)) {
            playerInfo.setTabListDisplayName(original);
        }
    }

    private static Component getBaseName(PlayerInfo playerInfo) {
        Component displayName = playerInfo.getTabListDisplayName();
        if (displayName != null) return displayName.copy();
        return Component.literal(playerInfo.getProfile().getName());
    }

    public static void clear() {
        ORIGINAL_NAMES.clear();
        LAST_APPLIED_NAMES.clear();
    }
}