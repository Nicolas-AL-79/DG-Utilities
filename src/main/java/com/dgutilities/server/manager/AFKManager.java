package com.dgutilities.server.manager;

import com.dgutilities.common.util.MobTargetUtils;
import com.dgutilities.common.util.ModMessages;
import com.dgutilities.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;

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

    private static final Map<UUID, AutoAFKData> AUTO_AFK_DATA = new HashMap<>();

    private static int autoAFKTickCounter = 0;

    private static class AutoAFKData {
        private Vec3 lastPosition;
        private float lastYRot;
        private float lastXRot;
        private int inactiveMinutes;

        private AutoAFKData(Vec3 lastPosition, float lastYRot, float lastXRot) {
            this.lastPosition = lastPosition;
            this.lastYRot = lastYRot;
            this.lastXRot = lastXRot;
            this.inactiveMinutes = 0;
        }
    }

    public static void registerPlayer(ServerPlayer player) {
        AUTO_AFK_DATA.put(player.getUUID(),
                new AutoAFKData(player.position(), player.getYRot(), player.getXRot())
        );
    }

    public static void removePlayer(ServerPlayer player) {
        UUID uuid = player.getUUID();

        AUTO_AFK_DATA.remove(uuid);
        PENDING_AFK.remove(uuid);
        AFK_PLAYERS.remove(uuid);
    }

    public static void startPending(ServerPlayer player) {
        if (AFK_PLAYERS.containsKey(player.getUUID())) {
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.dg_utilities.afk.already",
                            "You are already AFK!"
                    )
            );
            return;
        }
        PENDING_AFK.put(player.getUUID(), new AFKData(System.currentTimeMillis(), player.position(), player.getYRot(), player.getXRot()));
        player.sendSystemMessage(
                ModMessages.get(
                        player,
                        "command.dg_utilities.afk.pending",
                        "Starting AFK mode... Stand still for 5 seconds."
                )
        );
    }

    private static void activateAFK(ServerPlayer player) {
        UUID uuid = player.getUUID();

        AFK_PLAYERS.put(uuid,
                new AFKData(System.currentTimeMillis(), player.position(), player.getYRot(), player.getXRot())
        );

        player.sendSystemMessage(
                ModMessages.get(
                        player,
                        "command.dg_utilities.afk.active",
                        "AFK mode activated! You are invulnerable, immovable and ignored by monsters. Move your camera or press SHIFT to exit."
                )
        );

        MobTargetUtils.clearNearbyMobTargets(player, 32.0);
        player.refreshTabListName();
    }

    public static boolean isAFK(UUID uuid) {
        return AFK_PLAYERS.containsKey(uuid);
    }

    public static void cancelAFK(ServerPlayer player) {
        if (AFK_PLAYERS.remove(player.getUUID()) != null) {
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.dg_utilities.afk.cancel_move",
                            "AFK mode deactivated due to movement."
                    )
            );
            player.refreshTabListName();
        }
        if (PENDING_AFK.remove(player.getUUID()) != null) {
            player.sendSystemMessage(
                    ModMessages.get(
                            player,
                            "command.dg_utilities.afk.cancel_pending",
                            "AFK countdown cancelled due to movement."
                    )
            );
        }
        AUTO_AFK_DATA.put(player.getUUID(),
                new AutoAFKData(
                        player.position(),
                        player.getYRot(),
                        player.getXRot()
                )
        );
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
                activateAFK(player);
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

    private static void checkAutoAFK(ServerPlayer player) {
        UUID uuid = player.getUUID();

        if (AFK_PLAYERS.containsKey(uuid) || PENDING_AFK.containsKey(uuid)) return;

        AutoAFKData data = AUTO_AFK_DATA.get(uuid);

        if (data == null) {
            registerPlayer(player);
            return;
        }

        Vec3 currentPosition = player.position();
        float currentYRot = player.getYRot();
        float currentXRot = player.getXRot();

        boolean moved = currentPosition.distanceToSqr(data.lastPosition) > 0.05;
        boolean rotated = Math.abs(Mth.wrapDegrees(currentYRot - data.lastYRot)) > 1.0f
                        || Math.abs(currentXRot - data.lastXRot) > 1.0f;

        if (moved || rotated) {
            data.lastPosition = currentPosition;
            data.lastYRot = currentYRot;
            data.lastXRot = currentXRot;
            data.inactiveMinutes = 0;
            return;
        }

        data.inactiveMinutes++;
        int requiredMinutes = Config.AUTO_AFK_TIME_MINUTES.get();

        if (data.inactiveMinutes >= requiredMinutes) {
            activateAFK(player);
            AUTO_AFK_DATA.remove(uuid);
        }
    }

    public static void tickAutoAFK(MinecraftServer server) {
        if (!Config.AUTO_AFK_ENABLED.get()) return;

        autoAFKTickCounter++;
        if (autoAFKTickCounter < 1200) return;

        autoAFKTickCounter = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            checkAutoAFK(player);
        }
    }
}
