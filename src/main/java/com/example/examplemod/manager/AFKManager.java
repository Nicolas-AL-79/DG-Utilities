package com.example.examplemod.manager;

import com.example.examplemod.util.MobTargetUtils;
import com.example.examplemod.util.ModMessages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKManager {

    public static class AFKData {
        public long startTime;
        public Vec3 startPos;
        public float startYRot;
        public float startXRot;

        public AFKData(long time, Vec3 pos, float yRot, float xRot) {
            this.startTime = time;
            this.startPos = pos;
            this.startYRot = yRot;
            this.startXRot = xRot;
        }
    }

    public static final Map<UUID, AFKData> PENDING_AFK = new HashMap<>();
    public static final Map<UUID, AFKData> AFK_PLAYERS = new HashMap<>();

    public static void startPending(ServerPlayer player) {
        if (AFK_PLAYERS.containsKey(player.getUUID())) {
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.mod_de_teste.afk.already",
                            "You are already AFK!"
                    )
            );
            return;
        }
        PENDING_AFK.put(player.getUUID(), new AFKData(System.currentTimeMillis(), player.position(), player.getYRot(), player.getXRot()));
        player.sendSystemMessage(
                ModMessages.get(
                        player,
                        "command.mod_de_teste.afk.pending",
                        "Starting AFK mode... Stand still for 5 seconds."
                )
        );
    }

    public static boolean isAFK(UUID uuid) {
        return AFK_PLAYERS.containsKey(uuid);
    }

    public static void cancelAFK(ServerPlayer player) {
        if (AFK_PLAYERS.remove(player.getUUID()) != null) {
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.mod_de_teste.afk.cancel_move",
                            "AFK mode deactivated due to movement."
                    )
            );
        }
        if (PENDING_AFK.remove(player.getUUID()) != null) {
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.mod_de_teste.afk.cancel_pending",
                            "AFK countdown cancelled due to movement."
                    )
            );
        }
    }

    public static void checkMovement(ServerPlayer player) {
        UUID uuid = player.getUUID();
        Vec3 currentPos = player.position();
        float currentYRot = player.getYRot();
        float currentXRot = player.getXRot();
        boolean isSneaking = player.isCrouching();

        if (PENDING_AFK.containsKey(uuid)) {
            AFKData data = PENDING_AFK.get(uuid);
            if (isSneaking || Math.abs(currentYRot - data.startYRot) > 1.0f || Math.abs(currentXRot - data.startXRot) > 1.0f || currentPos.distanceToSqr(data.startPos) > 0.05) {
                cancelAFK(player);
            } else if (System.currentTimeMillis() - data.startTime >= 5000) {
                PENDING_AFK.remove(uuid);
                AFK_PLAYERS.put(uuid, new AFKData(System.currentTimeMillis(), currentPos, currentYRot, currentXRot));
                player.sendSystemMessage(
                        ModMessages.get(
                                player,
                                "command.mod_de_teste.afk.active",
                                "AFK mode activated! You are invulnerable, immovable and ignored by monsters. Move your camera or press SHIFT to exit."
                        )
                );

                // Faz todos os monstros ao redor que já estavam te seguindo perderem o alvo
                MobTargetUtils.clearNearbyMobTargets(player, 32.0);
            }
        } else if (AFK_PLAYERS.containsKey(uuid)) {
            AFKData data = AFK_PLAYERS.get(uuid);

            if (isSneaking || Math.abs(currentYRot - data.startYRot) > 1.0f || Math.abs(currentXRot - data.startXRot) > 1.0f) {
                cancelAFK(player);
            } else if (currentPos.distanceToSqr(data.startPos) > 0.05) {
                player.teleportTo(data.startPos.x, data.startPos.y, data.startPos.z);
            }
        }
    }
}
