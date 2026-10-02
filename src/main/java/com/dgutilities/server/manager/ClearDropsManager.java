package com.dgutilities.server.manager;

import com.dgutilities.Config;
import com.dgutilities.common.util.ModMessages;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClearDropsManager {
    private static final Set<ItemEntity> TRACKED_ITEMS = new HashSet<>();
    private static final Set<Integer> WARNED_SECONDS = new HashSet<>();
    private static final Set<Integer> WARNING_SECONDS = Set.of(30, 20, 10, 5, 4, 3, 2, 1);

    private static boolean countdownActive;
    private static int countdownTicks;

    public static void trackItem(ItemEntity item) {
        TRACKED_ITEMS.add(item);
    }

    public static void untrackItem(ItemEntity item) {
        TRACKED_ITEMS.remove(item);
    }

    public static void tick(MinecraftServer server) {
        if (!Config.AUTO_CLEARDROPS_ENABLED.get()) {
            if (countdownActive) cancelCountdown();
            return;
        }

        if (!countdownActive) {
            int threshold = Config.AUTO_CLEARDROPS_THRESHOLD.get();
            if (TRACKED_ITEMS.size() >= threshold) {
                TRACKED_ITEMS.removeIf(ItemEntity::isRemoved);
                if (TRACKED_ITEMS.size() >= threshold) {
                    startCountdown(server);
                }
            }
            return;
        }

        int secondsRemaining = (countdownTicks + 19) / 20;
        if (WARNING_SECONDS.contains(secondsRemaining) && WARNED_SECONDS.add(secondsRemaining)) {
            broadcastWarning(server, secondsRemaining);
        }

        countdownTicks--;
        if (countdownTicks <= 0) {
            int removed = clearDroppedItems(server);
            if (removed > 0) {
                broadcastAutomaticCleanup(server, removed);
            }
        }
    }

    public static int clearDroppedItems(MinecraftServer server) {
        List<ItemEntity> itemsToRemove = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity item && !item.isRemoved()) {
                    itemsToRemove.add(item);
                }
            }
        }

        for (ItemEntity item : itemsToRemove) {
            item.discard();
        }
        TRACKED_ITEMS.clear();
        cancelCountdown();
        return itemsToRemove.size();
    }

    public static void cancelCountdown() {
        countdownActive = false;
        countdownTicks = 0;
        WARNED_SECONDS.clear();
    }

    public static void reset() {
        TRACKED_ITEMS.clear();
        cancelCountdown();
    }

    private static void startCountdown(MinecraftServer server) {
        int seconds = Config.AUTO_CLEARDROPS_COUNTDOWN_SECONDS.get();

        countdownActive = true;
        countdownTicks = seconds * 20;

        WARNED_SECONDS.clear();
        WARNED_SECONDS.add(seconds);

        int itemCount = TRACKED_ITEMS.size();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(
                    ModMessages.get(player,
                            "command.dg_utilities.cleardrops.auto_start",
                            "Detected "
                                    + itemCount
                                    + " dropped item entities. Cleanup will run in "
                                    + seconds + " seconds.",
                            itemCount,
                            seconds
                    )
            );
        }
    }

    private static void broadcastWarning(MinecraftServer server, int seconds) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(
                    ModMessages.get(player,
                            "command.dg_utilities.cleardrops.auto_warning",
                            "Dropped items will be cleared in "
                                    + seconds + " seconds.",
                            seconds
                    )
            );
        }
    }

    private static void broadcastAutomaticCleanup(MinecraftServer server, int removed) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(
                    ModMessages.get(player,
                            "command.dg_utilities.cleardrops.auto_success",
                            "Automatic cleanup removed "
                                    + removed
                                    + " dropped item entities.",
                            removed
                    )
            );
        }
    }
}
