package com.dgutilities.client;

import com.dgutilities.DGUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IgnoreManager {
    // Lista de nomes de jogadores ignorados localmente
    public static final Set<String> IGNORED_PLAYERS = new HashSet<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FOLDER = FMLPaths.CONFIGDIR.get().resolve(DGUtilities.MODID).toFile();
    private static final File FILE = new File(FOLDER,"ignored_players.json");

    public static void ignore(UUID uuid) {
        IGNORED_PLAYERS.add(uuid.toString());
        save();
    }

    public static void unignore(UUID uuid) {
        IGNORED_PLAYERS.remove(uuid.toString());
        save();
    }

    public static boolean isIgnored(UUID uuid) {
        return IGNORED_PLAYERS.contains(uuid.toString());
    }

    public static void save() {
        if (!FOLDER.exists() && !FOLDER.mkdirs()) {
            System.out.println("Not possible to create the folder " + FOLDER.getAbsolutePath());
            return;
        }
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(IGNORED_PLAYERS, writer);
        } catch (Exception e) {
            System.out.println("Error to save ignored_players.json: " + e.getMessage());
        }
    }

    public static void load() {
        IGNORED_PLAYERS.clear();

        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                Type type = new TypeToken<Set<String>>() {}.getType();
                Set<String> data = GSON.fromJson(reader, type);

                if (data != null) {
                    IGNORED_PLAYERS.addAll(data);
                }
            } catch (Exception e) {
                System.out.println("Error to load ignored_players.json: " + e.getMessage());
            }
        }
    }
}
