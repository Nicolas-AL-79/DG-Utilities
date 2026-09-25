package com.dgutilities.admin.manager;

import com.dgutilities.DGUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class DimensionAccessManager {
    private static final Set<String> BLOCKED_DIMENSIONS = new HashSet<>();
    private static final Set<String> PORTAL_BLOCKED_DIMENSIONS = new HashSet<>();
    private static final Map<UUID, PlayerAccessData> PLAYER_ACCESS = new HashMap<>();
    private static final Map<String, String> DIMENSION_KEYS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File getFile() {
        return DGUtilities.getWorldDataFolder().resolve("dimension_access.json").toFile();
    }

    // ----------------------------------------------------
    // DIMENSION BLOCK
    // ----------------------------------------------------

    public static boolean blockDimension(ResourceLocation dimension) {
        boolean changed = BLOCKED_DIMENSIONS.add(dimension.toString());
        if (changed) save();
        return changed;
    }

    public static boolean unblockDimension(ResourceLocation dimension) {
        String dimensionId = dimension.toString();
        boolean changed = BLOCKED_DIMENSIONS.remove(dimensionId);
        if (!changed) return false;
        for (PlayerAccessData data : PLAYER_ACCESS.values()) {
            data.dimensions.remove(dimensionId);
        }
        cleanupEmptyPlayerAccess();
        save();
        return true;
    }

    public static boolean isDimensionBlocked(ResourceLocation dimension) {
        return BLOCKED_DIMENSIONS.contains(dimension.toString());
    }

    public static Set<String> getBlockedDimensions() {
        return new HashSet<>(BLOCKED_DIMENSIONS);
    }

    // ----------------------------------------------------
    // PORTAL BLOCK
    // ----------------------------------------------------

    public static boolean blockPortal(ResourceLocation dimension) {
        boolean changed = PORTAL_BLOCKED_DIMENSIONS.add(dimension.toString());
        if (changed) save();
        return changed;
    }

    public static boolean unblockPortal(ResourceLocation dimension) {
        boolean changed = PORTAL_BLOCKED_DIMENSIONS.remove(dimension.toString());
        if (changed) save();
        return changed;
    }

    public static boolean isPortalBlocked(ResourceLocation dimension) {
        return PORTAL_BLOCKED_DIMENSIONS.contains(dimension.toString());
    }

    public static Set<String> getPortalBlockedDimensions() {
        return new HashSet<>(PORTAL_BLOCKED_DIMENSIONS);
    }

    // ----------------------------------------------------
    // PLAYER BYPASS
    // ----------------------------------------------------

    public static boolean allowPlayer(ServerPlayer player, ResourceLocation dimension) {
        String dimensionId = dimension.toString();
        PlayerAccessData data = PLAYER_ACCESS.computeIfAbsent(
                player.getUUID(), uuid -> new PlayerAccessData(player.getGameProfile().getName())
        );

        data.playerName = player.getGameProfile().getName();
        boolean changed = data.dimensions.add(dimensionId);
        if (changed) save();
        return changed;
    }

    public static boolean disallowPlayer(UUID playerUUID, ResourceLocation dimension) {
        PlayerAccessData data = PLAYER_ACCESS.get(playerUUID);
        if (data == null) return false;
        boolean changed = data.dimensions.remove(dimension.toString());

        if (!changed) return false;
        if (data.dimensions.isEmpty()) {
            PLAYER_ACCESS.remove(playerUUID);
        }
        save();
        return true;
    }

    public static boolean disallowAll(UUID playerUUID) {
        boolean changed = PLAYER_ACCESS.remove(playerUUID) != null;
        if (changed) save();
        return changed;
    }

    public static boolean hasBypass(UUID playerUUID, ResourceLocation dimension) {
        PlayerAccessData data = PLAYER_ACCESS.get(playerUUID);
        return data != null && data.dimensions.contains(dimension.toString());
    }

    public static Set<String> getPlayerBypasses(UUID playerUUID) {
        PlayerAccessData data = PLAYER_ACCESS.get(playerUUID);
        if (data == null) return new HashSet<>();
        return new HashSet<>(data.dimensions);
    }

    public static Map<UUID, PlayerAccessData> getAllPlayerAccess() {
        Map<UUID, PlayerAccessData> result = new HashMap<>();
        PLAYER_ACCESS.forEach((uuid, data) -> result.put(uuid, data.copy()));
        return result;
    }

    // ----------------------------------------------------
    // DIMENSION KEYS
    // ----------------------------------------------------

    public static boolean setDimensionKey(ResourceLocation dimension, Item item) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);

        if (itemId == null) return false;
        String previous = DIMENSION_KEYS.put(dimension.toString(), itemId.toString());

        boolean changed = !itemId.toString().equals(previous);
        if (changed) save();
        return changed;
    }

    public static boolean removeDimensionKey(ResourceLocation dimension) {
        boolean changed = DIMENSION_KEYS.remove(dimension.toString()) != null;
        if (changed) save();
        return changed;
    }

    public static String getDimensionKey(ResourceLocation dimension) {
        return DIMENSION_KEYS.get(dimension.toString());
    }

    public static Map<String, String> getDimensionKeys() {
        return new HashMap<>(DIMENSION_KEYS);
    }

    public static boolean hasRequiredKey(ServerPlayer player, ResourceLocation dimension) {
        String itemIdString = DIMENSION_KEYS.get(dimension.toString());
        if (itemIdString == null) return false;
        ResourceLocation itemId = ResourceLocation.tryParse(itemIdString);
        if (itemId == null) return false;
        Item requiredItem = ForgeRegistries.ITEMS.getValue(itemId);
        if (requiredItem == null) return false;

        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(requiredItem)) return true;
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (!stack.isEmpty() && stack.is(requiredItem)) return true;
        }
        return false;
    }

    // ----------------------------------------------------
    // ACCESS CHECK
    // ----------------------------------------------------

    public static boolean canAccess(ServerPlayer player, ResourceLocation dimension) {
        return !isDimensionBlocked(dimension) || hasBypass(player.getUUID(), dimension) || hasRequiredKey(player, dimension);
    }

    // ----------------------------------------------------
    // SAVE / LOAD
    // ----------------------------------------------------

    public static void save() {
        File file = getFile();
        Data data = new Data();
        data.blockedDimensions.addAll(BLOCKED_DIMENSIONS);
        data.portalBlockedDimensions.addAll(PORTAL_BLOCKED_DIMENSIONS);
        PLAYER_ACCESS.forEach((uuid, playerData) ->
                data.playerAccess.put(uuid, playerData.copy())
        );
        data.dimensionKeys.putAll(DIMENSION_KEYS);
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
        } catch (Exception e) {
            System.out.println("Error to save dimension_access.json: " + e.getMessage());
        }
    }

    public static void load() {
        BLOCKED_DIMENSIONS.clear();
        PORTAL_BLOCKED_DIMENSIONS.clear();
        PLAYER_ACCESS.clear();
        DIMENSION_KEYS.clear();

        File file = getFile();

        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data == null) return;
            if (data.blockedDimensions != null) {
                BLOCKED_DIMENSIONS.addAll(data.blockedDimensions);
            }
            if (data.portalBlockedDimensions != null) {
                PORTAL_BLOCKED_DIMENSIONS.addAll(data.portalBlockedDimensions);
            }
            if (data.playerAccess != null) {
                PLAYER_ACCESS.putAll(data.playerAccess);
            }
            if (data.dimensionKeys != null) {
                DIMENSION_KEYS.putAll(data.dimensionKeys);
            }
            cleanupEmptyPlayerAccess();
        } catch (Exception e) {
            System.out.println("Error to load dimension_access.json: " + e.getMessage());
        }
    }

    private static void cleanupEmptyPlayerAccess() {
        PLAYER_ACCESS.entrySet().removeIf(entry ->
                entry.getValue() == null
                        || entry.getValue().dimensions == null
                        || entry.getValue().dimensions.isEmpty()
        );
    }

    // ----------------------------------------------------
    // DATA
    // ----------------------------------------------------

    public static class PlayerAccessData {
        public String playerName;
        public Set<String> dimensions = new HashSet<>();
        public PlayerAccessData(String playerName) {
            this.playerName = playerName;
        }
        public PlayerAccessData copy() {
            PlayerAccessData copy = new PlayerAccessData(playerName);
            if (dimensions != null) {
                copy.dimensions.addAll(dimensions);
            }
            return copy;
        }
    }

    private static class Data {
        Set<String> blockedDimensions = new HashSet<>();
        Set<String> portalBlockedDimensions = new HashSet<>();
        Map<UUID, PlayerAccessData> playerAccess = new HashMap<>();
        Map<String, String> dimensionKeys = new HashMap<>();
    }
}