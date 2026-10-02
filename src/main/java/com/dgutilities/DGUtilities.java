package com.dgutilities;

import com.dgutilities.admin.command.*;
import com.dgutilities.admin.invsee.InvseeCommand;
import com.dgutilities.server.command.ClearDropsCommand;
import com.dgutilities.server.command.DimensionCommand;
import com.dgutilities.server.manager.ClearDropsManager;
import com.dgutilities.server.manager.DimensionAccessManager;
import com.dgutilities.server.manager.ForbiddenItemsManager;
import com.dgutilities.admin.manager.PunishmentRegistry;
import com.dgutilities.common.network.ModNetwork;
import com.dgutilities.player.command.*;
import com.dgutilities.server.command.ForbidCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

        ClearDropsManager.reset();

        worldDataFolder = server.getWorldPath(LevelResource.ROOT).resolve(MODID);

        try {
            Files.createDirectories(worldDataFolder);
        } catch (IOException e) {
            throw new RuntimeException("Error to create mod date folder", e);
        }
        // Carrega e salva os arquivos JSON quando o mundo/servidor ligar
        ForbiddenItemsManager.load();
        PunishmentRegistry.load();
        DimensionAccessManager.load();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        ClearDropsManager.reset();
    }

    public static Path getWorldDataFolder() {
        if (worldDataFolder == null) {
            throw new IllegalStateException(
                    "World data folder has not yet been initialized."
            );
        }
        return worldDataFolder;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registrarComandos(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dgDispatcher = new CommandDispatcher<>();
        registerDGCommands(dgDispatcher, event.getBuildContext());
        registerCommandAliases(event.getDispatcher(), dgDispatcher);
    }

    private static void registerDGCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        // Announcement
        if (Config.COMMAND_ANNOUNCEMENT_ENABLED.get() || Config.COMMAND_SCREEN_ANNOUNCEMENT_ENABLED.get()) {
            AnnouncementCommand.register(dispatcher);
        }

        // Forbid / Allow
        if (Config.COMMAND_FORBID_ENABLED.get()) {
            ForbidCommand.register(dispatcher, buildContext);
        }

        // Heal
        if (Config.COMMAND_HEAL_ENABLED.get()) {
            HealCommand.register(dispatcher);
        }

        // Trash
        if (Config.COMMAND_TRASH_ENABLED.get()) {
            TrashCommand.register(dispatcher);
        }

        // Freeze
        if (Config.COMMAND_FREEZE_ENABLED.get()) {
            FreezeCommand.register(dispatcher);
        }

        // Mute
        if (Config.COMMAND_MUTE_ENABLED.get()) {
            MuteCommand.register(dispatcher);
        }

        // AFK
        if (Config.COMMAND_AFK_ENABLED.get()) {
            AFKCommand.register(dispatcher);
        }

        // Invsee
        if (Config.COMMAND_INVSEE_ENABLED.get()) {
            InvseeCommand.register(dispatcher);
        }

        // Freeze and Mute
        if (Config.COMMAND_FREEZE_ENABLED.get() || Config.COMMAND_MUTE_ENABLED.get()) {
            PunishmentCheckCommand.register(dispatcher);
        }

        // Block Dimensions
        if (Config.COMMAND_DIMENSION_ENABLED.get()) {
            DimensionCommand.register(dispatcher, buildContext);
        }

        // Fly
        if (Config.COMMAND_FLY_ENABLED.get()) {
            FlyCommand.register(dispatcher);
        }

        // God
        if (Config.COMMAND_GOD_ENABLED.get()) {
            GodCommand.register(dispatcher);
        }

        // LastPos
        if (Config.COMMAND_LASTPOS_ENABLED.get()) {
            LastPosCommand.register(dispatcher);
        }

        // Endersee
        if (Config.COMMAND_ENDERSEE_ENABLED.get()) {
            EnderseeCommand.register(dispatcher);
        }

        // Enderchest
        if (Config.COMMAND_ENDERCHEST_ENABLED.get()) {
            EnderchestCommand.register(dispatcher);
        }

        // Clear Drop
        if (Config.COMMAND_CLEARDROPS_ENABLED.get()) {
            ClearDropsCommand.register(dispatcher);
        }

        // Tempban
        if (Config.COMMAND_TEMPBAN_ENABLED.get()) {
            TempBanCommand.register(dispatcher);
        }
    }

    private static void registerCommandAliases(CommandDispatcher<CommandSourceStack> serverDispatcher, CommandDispatcher<CommandSourceStack> dgDispatcher) {
        serverDispatcher.register(Commands.literal("dg"));
        CommandNode<CommandSourceStack> dgRoot = serverDispatcher.getRoot().getChild("dg");
        for (CommandNode<CommandSourceStack> command : dgDispatcher.getRoot().getChildren()) {
            serverDispatcher.getRoot().addChild(command);
            dgRoot.addChild(command);
        }
    }
}