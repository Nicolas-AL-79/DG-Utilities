package com.dgutilities;

import com.dgutilities.admin.command.*;
import com.dgutilities.admin.invsee.InvseeCommand;
import com.dgutilities.admin.manager.DimensionAccessManager;
import com.dgutilities.admin.manager.ForbiddenItemsManager;
import com.dgutilities.admin.manager.PunishmentRegistry;
import com.dgutilities.common.network.ModNetwork;
import com.dgutilities.server.command.AFKCommand;
import com.dgutilities.server.command.TrashCommand;
import com.dgutilities.common.util.ModMessages;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DGUtilities.MODID)
public class DGUtilities
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "dg_utilities";

    public DGUtilities(FMLJavaModLoadingContext context)
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC, MODID + "/dg_utilities-common.toml");

        context.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (remoteVersion, isFromServer) -> true
                )
        );

        ModNetwork.register();
    }

    private static Path worldDataFolder;
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        MinecraftServer server = event.getServer();

        worldDataFolder = server
                .getWorldPath(LevelResource.ROOT)
                .resolve(MODID);

        try {
            Files.createDirectories(worldDataFolder);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error to create mod date folder",
                    e
            );
        }
        // Carrega e salva os arquivos JSON quando o mundo/servidor ligar
        ForbiddenItemsManager.load();
        PunishmentRegistry.load();
        DimensionAccessManager.load();
    }

    public static Path getWorldDataFolder() {
        if (worldDataFolder == null) {
            throw new IllegalStateException(
                    "World data folder has not yet been initialized."
            );
        }
        return worldDataFolder;
    }

    @SubscribeEvent
    public void registrarComandos(RegisterCommandsEvent event) {
        if (Config.COMMAND_ANNOUNCEMENT_ENABLED.get()) {
            // Announcement
            event.getDispatcher().register(Commands.literal("announcement")
                    .requires(source -> source.hasPermission(Config.COMMAND_ANNOUNCEMENT_PERMISSION_LEVEL.get()))
                    .then(Commands.argument("mensagem", StringArgumentType.greedyString())
                            .executes(context -> {
                                String mensagem = StringArgumentType.getString(context, "mensagem");

                                for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                                    player.sendSystemMessage(ModMessages.get(
                                            player,
                                            "command.dg_utilities.announcement",
                                            "[Announcement] " + mensagem,
                                            mensagem
                                    ));
                                }
                                return 1;
                            })
                    )
            );
        }

        // Forbid / Allow
        if (Config.COMMAND_FORBID_ENABLED.get()) {
            ForbidCommand.register(
                    event.getDispatcher(),
                    event.getBuildContext()
            );
        }

        // Heal
        if (Config.COMMAND_HEAL_ENABLED.get()) {
            HealCommand.register(event.getDispatcher());
        }

        // Trash
        if (Config.COMMAND_TRASH_ENABLED.get()) {
            TrashCommand.register(event.getDispatcher());
        }

        // Freeze
        if (Config.COMMAND_FREEZE_ENABLED.get()) {
            FreezeCommand.register(event.getDispatcher());
        }

        // Mute
        if (Config.COMMAND_MUTE_ENABLED.get()) {
            MuteCommand.register(event.getDispatcher());
        }

        // AFK
        if (Config.COMMAND_AFK_ENABLED.get()) {
            AFKCommand.register(event.getDispatcher());
        }

        // Invsee
        if (Config.COMMAND_INVSEE_ENABLED.get()) {
            InvseeCommand.register(event.getDispatcher());
        }

        if (Config.COMMAND_FREEZE_ENABLED.get() || Config.COMMAND_MUTE_ENABLED.get()) {
            PunishmentCheckCommand.register(event.getDispatcher());
        }

        if (Config.COMMAND_DIMENSION_ENABLED.get()) {
            DimensionCommand.register(event.getDispatcher(), event.getBuildContext());
        }
    }
}