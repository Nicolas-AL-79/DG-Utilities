package com.dgutilities.common.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class MobTargetUtils {

    public static void clearNearbyMobTargets(ServerPlayer player, double radius) {
        List<Mob> mobs = player.level().getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(radius)
        );

        for (Mob mob : mobs) {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
        }
    }
}