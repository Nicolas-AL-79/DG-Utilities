package com.dgutilities.admin.manager;

import com.dgutilities.DGUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ForbiddenItemsManager {
    public static final Set<String> FORBIDDEN_ITEMS = new HashSet<>();

    public static final Set<UUID> BYPASS_ALL = new HashSet<>();

    public static final Map<UUID, Set<String>> BYPASS_ITEMS = new HashMap<>();

    // O GSON é a ferramenta do Google (já inclusa no Minecraft) para transformar código em texto JSON
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Arquivo salvo dentro da pasta do mundo
    private static File getFile() {
        return DGUtilities
                .getWorldDataFolder()
                .resolve("forbidden_items.json")
                .toFile();
    }



    public static void forbidItem(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return;
        FORBIDDEN_ITEMS.add(key.toString());
        save();
    }

    public static void unforbidItem(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return;
        FORBIDDEN_ITEMS.remove(key.toString());
        save();
    }

    public static boolean allowPlayer(UUID playerUUID) {
        if (BYPASS_ALL.contains(playerUUID)) return false;
        BYPASS_ALL.add(playerUUID);
        save();
        return true;
    }

    public static void disallowPlayer(UUID playerUUID) {
        if (!BYPASS_ALL.remove(playerUUID)) return;
        save();
    }

    public static boolean disallowAll(UUID playerUUID) {
        boolean changed = BYPASS_ALL.remove(playerUUID);
        if (BYPASS_ITEMS.remove(playerUUID) != null) changed = true;
        if (changed) save();
        return changed;
    }

    public static boolean allowPlayerItem(UUID playerUUID, Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return false;
        String itemName = key.toString();
        if (!FORBIDDEN_ITEMS.contains(itemName)) return false;
        Set<String> items = BYPASS_ITEMS.computeIfAbsent(playerUUID, uuid -> new HashSet<>());
        if (!items.add(itemName)) return false;
        save();
        return true;
    }

    public static void disallowPlayerItem(UUID playerUUID, Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return;
        String itemName = key.toString();
        if (!FORBIDDEN_ITEMS.contains(itemName)) return;
        if (BYPASS_ALL.remove(playerUUID)) {
            Set<String> allowedItems = new HashSet<>(FORBIDDEN_ITEMS);
            allowedItems.remove(itemName);
            if (allowedItems.isEmpty()) {
                BYPASS_ITEMS.remove(playerUUID);
            } else {
                BYPASS_ITEMS.put(playerUUID, allowedItems);
            }
            save();
            return;
        }
        Set<String> allowedItems = BYPASS_ITEMS.get(playerUUID);
        if (allowedItems == null) return;
        if (!allowedItems.remove(itemName)) return;
        if (allowedItems.isEmpty()) BYPASS_ITEMS.remove(playerUUID);
        save();
    }

    public static boolean isForbidden(UUID playerUUID, Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return false;
        String itemName = key.toString();
        if (!FORBIDDEN_ITEMS.contains(itemName)) return false;
        if (BYPASS_ALL.contains(playerUUID)) return false;
        Set<String> allowedItems = BYPASS_ITEMS.get(playerUUID);
        return allowedItems == null || !allowedItems.contains(itemName);
    }

    public static Set<String> getForbiddenItems() {
        return new HashSet<>(FORBIDDEN_ITEMS);
    }

    public static boolean isGloballyForbidden(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return false;
        return FORBIDDEN_ITEMS.contains(key.toString());
    }

    public static boolean clearForbiddenItems() {
        if (FORBIDDEN_ITEMS.isEmpty()) return false;
        FORBIDDEN_ITEMS.clear();

        // As permissões específicas deixam de ter sentido,
        // pois não existe mais nenhum item proibido.
        BYPASS_ITEMS.clear();

        save();
        return true;
    }

    public static Set<String> getPlayerBypassItems(UUID playerUUID) {
        Set<String> items = BYPASS_ITEMS.get(playerUUID);
        if (items == null) return new HashSet<>();
        return new HashSet<>(items);
    }

    public static boolean canBypassItem(UUID playerUUID, Item item) {
        if (BYPASS_ALL.contains(playerUUID)) return true;
        return hasItemBypass(playerUUID, item);
    }

    public static boolean hasFullBypass(UUID playerUUID) {
        return BYPASS_ALL.contains(playerUUID);
    }

    public static boolean hasItemBypass(UUID playerUUID, Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null) return false;
        Set<String> items = BYPASS_ITEMS.get(playerUUID);
        return items != null && items.contains(key.toString());
    }


    // Metodo para salvar os dados no arquivo
    public static void save() {
        File file = getFile();

        Data data = new Data();
        data.forbiddenItems.addAll(FORBIDDEN_ITEMS);
        data.bypassAll.addAll(BYPASS_ALL);
        data.bypassItems.putAll(BYPASS_ITEMS);

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
        } catch (Exception e) {
            System.out.println("Error to save forbidden_items.json: " + e.getMessage());
        }
    }

    // Metodo para ler os dados do arquivo
    public static void load() {
        FORBIDDEN_ITEMS.clear();
        BYPASS_ALL.clear();
        BYPASS_ITEMS.clear();
        File file = getFile();

        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Data data = GSON.fromJson(reader, Data.class);

            if (data == null) return;

            if (data.forbiddenItems != null) {
                FORBIDDEN_ITEMS.addAll(data.forbiddenItems);
            }
            if (data.bypassAll != null) {
                BYPASS_ALL.addAll(data.bypassAll);
            }
            if (data.bypassItems != null) {
                BYPASS_ITEMS.putAll(data.bypassItems);
            }
        } catch (Exception e) {
            System.out.println("Error to load forbidden_items.json: " + e.getMessage());
        }
    }

    private static class Data {
        Set<String> forbiddenItems = new HashSet<>();
        Set<UUID> bypassAll = new HashSet<>();
        Map<UUID, Set<String>> bypassItems = new HashMap<>();
    }
}
