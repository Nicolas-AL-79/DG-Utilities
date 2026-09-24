package com.dgutilities.admin.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PunishmentManager {
    private static final String NBT_KEY = "dg_utilities";
    private static final String FROZEN_KEY = "frozen";
    private static final String MUTED_KEY = "muted";
    private static final String FREEZE_X_KEY = "freeze_x";
    private static final String FREEZE_Y_KEY = "freeze_y";
    private static final String FREEZE_Z_KEY = "freeze_z";
    private static final String FREEZE_EXPIRES_KEY = "freeze_expires";
    private static final String MUTE_EXPIRES_KEY = "mute_expires";

    /*** Define se o jogador está congelado.*/
    public static void setFrozen(ServerPlayer player, boolean frozen, long durationMillis) {
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);
        data.putBoolean(FROZEN_KEY, frozen);
        if (frozen) {
            data.putDouble(FREEZE_X_KEY, player.getX());
            data.putDouble(FREEZE_Y_KEY, player.getY());
            data.putDouble(FREEZE_Z_KEY, player.getZ());
            long expiration = 0L;

            if (durationMillis > 0) {
                expiration = System.currentTimeMillis() + durationMillis;
            }
            data.putLong(FREEZE_EXPIRES_KEY, expiration);
            PunishmentRegistry.setFreeze(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    expiration
            );
        } else {
            data.remove(FREEZE_X_KEY);
            data.remove(FREEZE_Y_KEY);
            data.remove(FREEZE_Z_KEY);
            player.setTicksFrozen(0);
            data.remove(FREEZE_EXPIRES_KEY);
            PunishmentRegistry.setFreeze(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    null
            );
        }
        player.getPersistentData().put(NBT_KEY, data);
        player.refreshTabListName();
    }

    public static void setFrozen(ServerPlayer player, boolean frozen) {
        setFrozen(player, frozen, 0L);
    }

    /*** Verifica se o jogador está congelado.*/
    public static boolean isFrozen(ServerPlayer player) {
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);
        if (!data.getBoolean(FROZEN_KEY)) return false;
        long expires = data.getLong(FREEZE_EXPIRES_KEY);
        if (expires > 0 && System.currentTimeMillis() >= expires) {
            setFrozen(player, false);
            return false;
        }
        return true;
    }

    public static void enforceFreezePosition(ServerPlayer player) {
        if (!isFrozen(player)) return;
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);

        if (!data.contains(FREEZE_X_KEY)
                || !data.contains(FREEZE_Y_KEY)
                || !data.contains(FREEZE_Z_KEY)) {
            return;
        }

        double x = data.getDouble(FREEZE_X_KEY);
        double y = data.getDouble(FREEZE_Y_KEY);
        double z = data.getDouble(FREEZE_Z_KEY);

        // Elimina qualquer movimento aplicado pelo vanilla ou mods.
        player.setDeltaMovement(0.0, 0.0, 0.0);
        player.fallDistance = 0.0F;

        // Impede estados de voo de continuarem empurrando o jogador.
        player.getAbilities().flying = false;

        player.teleportTo(x, y, z);

        // Remove qualquer velocidade residual.
        player.setDeltaMovement(0.0, 0.0, 0.0);
        player.hurtMarked = true;
    }

    /*** Define se o jogador está mutado.*/
    public static void setMuted(ServerPlayer player, boolean muted, long durationMillis) {
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);
        data.putBoolean(MUTED_KEY, muted);
        if (muted) {
            long expiration = 0L;

            if (durationMillis > 0) {
                expiration = System.currentTimeMillis() + durationMillis;
            }

            data.putLong(MUTE_EXPIRES_KEY, expiration);

            PunishmentRegistry.setMute(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    expiration
            );
        } else {
            data.remove(MUTE_EXPIRES_KEY);

            PunishmentRegistry.setMute(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    null
            );
        }
        player.getPersistentData().put(NBT_KEY, data);
        player.refreshTabListName();
    }

    public static void setMuted(ServerPlayer player, boolean muted) {
        setMuted(player, muted, 0L);
    }

    /*** Verifica se o jogador está mutado.*/
    public static boolean isMuted(ServerPlayer player) {
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);
        if (!data.getBoolean(MUTED_KEY)) return false;
        long expires = data.getLong(MUTE_EXPIRES_KEY);
        if (expires > 0 && System.currentTimeMillis() >= expires) {
            setMuted(player, false);
            return false;
        }
        return true;
    }
}