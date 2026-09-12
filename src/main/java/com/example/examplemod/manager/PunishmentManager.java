package com.example.examplemod.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PunishmentManager {
    private static final String NBT_KEY = "mod_de_teste";
    private static final String FROZEN_KEY = "frozen";
    private static final String MUTED_KEY = "muted";
    private static final String FREEZE_X_KEY = "freeze_x";
    private static final String FREEZE_Y_KEY = "freeze_y";
    private static final String FREEZE_Z_KEY = "freeze_z";

    /*** Define se o jogador está congelado.*/
    public static void setFrozen(ServerPlayer player, boolean frozen) {
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);
        data.putBoolean(FROZEN_KEY, frozen);
        if (frozen) {
            data.putDouble(FREEZE_X_KEY, player.getX());
            data.putDouble(FREEZE_Y_KEY, player.getY());
            data.putDouble(FREEZE_Z_KEY, player.getZ());
        } else {
            data.remove(FREEZE_X_KEY);
            data.remove(FREEZE_Y_KEY);
            data.remove(FREEZE_Z_KEY);
            player.setTicksFrozen(0);
        }
        player.getPersistentData().put(NBT_KEY, data);
    }

    /*** Verifica se o jogador está congelado.*/
    public static boolean isFrozen(ServerPlayer player) {
        return player.getPersistentData().getCompound(NBT_KEY).getBoolean(FROZEN_KEY);
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
    public static void setMuted(ServerPlayer player, boolean muted) {
        CompoundTag data = player.getPersistentData().getCompound(NBT_KEY);
        data.putBoolean(MUTED_KEY, muted);
        player.getPersistentData().put(NBT_KEY, data);
    }

    /*** Verifica se o jogador está mutado.*/
    public static boolean isMuted(ServerPlayer player) {
        return player.getPersistentData().getCompound(NBT_KEY).getBoolean(MUTED_KEY);
    }
}