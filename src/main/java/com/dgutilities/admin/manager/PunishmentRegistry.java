package com.dgutilities.admin.manager;

import com.dgutilities.DGUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunishmentRegistry {
    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Map<UUID, PunishmentData> PUNISHMENTS =
            new HashMap<>();

    private static File getFile() {
        return DGUtilities
                .getWorldDataFolder()
                .resolve("punishments.json")
                .toFile();
    }

    public static void setFreeze(UUID playerUUID, String playerName, Long expiration) {
        PunishmentData data = PUNISHMENTS.computeIfAbsent(
                playerUUID, uuid -> new PunishmentData(playerName)
        );

        data.playerName = playerName;
        data.freezeExpiration = expiration;

        cleanupPlayer(playerUUID);
        save();
    }

    public static void setMute(
            UUID playerUUID,
            String playerName,
            Long expiration
    ) {
        PunishmentData data =
                PUNISHMENTS.computeIfAbsent(
                        playerUUID,
                        uuid -> new PunishmentData(playerName)
                );

        data.playerName = playerName;
        data.muteExpiration = expiration;

        cleanupPlayer(playerUUID);
        save();
    }

    public static PunishmentData get(UUID playerUUID) {
        return PUNISHMENTS.get(playerUUID);
    }

    public static Map<UUID, PunishmentData> getAll() {
        cleanupExpired();
        return new HashMap<>(PUNISHMENTS);
    }

    private static void cleanupPlayer(UUID playerUUID) {
        PunishmentData data =
                PUNISHMENTS.get(playerUUID);

        if (data == null) {
            return;
        }

        if (data.freezeExpiration == null
                && data.muteExpiration == null) {

            PUNISHMENTS.remove(playerUUID);
        }
    }

    public static void cleanupExpired() {
        long now = System.currentTimeMillis();

        PUNISHMENTS.entrySet().removeIf(entry -> {
            PunishmentData data = entry.getValue();

            if (data.freezeExpiration != null
                    && data.freezeExpiration > 0
                    && now >= data.freezeExpiration) {
                data.freezeExpiration = null;
            }

            if (data.muteExpiration != null
                    && data.muteExpiration > 0
                    && now >= data.muteExpiration) {
                data.muteExpiration = null;
            }

            return data.freezeExpiration == null && data.muteExpiration == null;
        });
        save();
    }

    public static void save() {
        File file = getFile();

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(PUNISHMENTS, writer);
        } catch (Exception e) {
            System.out.println("Error to save punishments.json: " + e.getMessage());
        }
    }

    public static void load() {
        PUNISHMENTS.clear();
        File file = getFile();
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<
                            Map<UUID, PunishmentData>>() {}.getType();

            Map<UUID, PunishmentData> loaded = GSON.fromJson(reader, type);

            if (loaded != null) PUNISHMENTS.putAll(loaded);
            cleanupExpired();

        } catch (Exception e) {
            System.out.println("Error to load punishments.json: " + e.getMessage());
        }
    }

    public static class PunishmentData {

        public String playerName;
        public Long freezeExpiration;
        public Long muteExpiration;

        public PunishmentData(String playerName) {
            this.playerName = playerName;
        }
    }
}