package com.dgutilities.player.manager;

import com.dgutilities.common.util.ModMessages;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class GodManager {
    private static final String GOD_KEY = "DGUtilitiesGodMode";
    public static boolean toggle(ServerPlayer player) {
        boolean enabled = !isGod(player);
        setGod(player, enabled);
        if (enabled) {
            player.getActiveEffects()
                    .stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> !effect.isBeneficial())
                    .toList()
                    .forEach(player::removeEffect);
        }
        player.sendSystemMessage(
                ModMessages.get(player,
                        enabled ? "command.dg_utilities.god.enabled" : "command.dg_utilities.god.disabled",
                        enabled ? "God mode enabled." : "God mode disabled."
                )
        );
        return enabled;
    }

    public static boolean isGod(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag playerData = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
        return playerData.getBoolean(GOD_KEY);
    }

    private static void setGod(ServerPlayer player, boolean enabled) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag playerData = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
        playerData.putBoolean(GOD_KEY, enabled);
        persistentData.put(Player.PERSISTED_NBT_TAG, playerData);
    }
}
