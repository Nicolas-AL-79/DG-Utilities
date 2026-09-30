package com.dgutilities;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DGUtilities.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ----------------------------------------------------
    // COMMANDS
    // ----------------------------------------------------

    public static final ForgeConfigSpec.BooleanValue COMMAND_IGNORE_ENABLED = BUILDER
            .comment("Enables or disables the /ignore command")
            .define("commands.ignore.enabled", true);

    public static final ForgeConfigSpec.BooleanValue COMMAND_AFK_ENABLED = BUILDER
            .comment("Enables or disables the /afk command")
            .define("commands.afk.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_AFK_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /afk. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.afk.permission_level", 0, 0, 4);

    public static final ForgeConfigSpec.BooleanValue AUTO_AFK_ENABLED = BUILDER
            .comment("Enables or disables automatic AFK detection")
            .define("commands.afk.auto_enabled", true);

    public static final ForgeConfigSpec.IntValue AUTO_AFK_TIME_MINUTES = BUILDER
            .comment("Minutes without movement or camera rotation before automatically entering AFK mode")
            .defineInRange("commands.afk.auto_time_minutes", 5, 1, 1440);

    public static final ForgeConfigSpec.BooleanValue COMMAND_TRASH_ENABLED = BUILDER
            .comment("Enables or disables the /trash command")
            .define("commands.trash.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_TRASH_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /trash. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.trash.permission_level", 0, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_ENDERCHEST_ENABLED = BUILDER
            .comment("Enables or disables the /enderchest command")
            .define("commands.enderchest.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_ENDERCHEST_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /enderchest. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.enderchest.permission_level", 1, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_ANNOUNCEMENT_ENABLED = BUILDER
            .comment("Enables or disables the /announcement command")
            .define("commands.announcement.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_ANNOUNCEMENT_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /announcement. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.announcement.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_SCREEN_ANNOUNCEMENT_ENABLED = BUILDER
            .comment("Enables or disables the /screenannounce command")
            .define("commands.screenannounce.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_SCREEN_ANNOUNCEMENT_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /screenannounce. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.screenannounce.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.IntValue SCREEN_ANNOUNCEMENT_FADE_IN = BUILDER
            .comment("Fade-in duration for screen announcements, in ticks")
            .defineInRange("commands.screenannounce.fade_in", 10, 0, 200);

    public static final ForgeConfigSpec.IntValue SCREEN_ANNOUNCEMENT_STAY = BUILDER
            .comment("Time screen announcements remain visible, in ticks")
            .defineInRange("commands.screenannounce.stay", 60, 1, 1200);

    public static final ForgeConfigSpec.IntValue SCREEN_ANNOUNCEMENT_FADE_OUT = BUILDER
            .comment("Fade-out duration for screen announcements, in ticks")
            .defineInRange("commands.screenannounce.fade_out", 10, 0, 200);

    public static final ForgeConfigSpec.BooleanValue COMMAND_FORBID_ENABLED = BUILDER
            .comment("Enables or disables the /item commands")
            .define("commands.forbid.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_FORBID_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /item. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.forbid.permission_level", 3, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_HEAL_ENABLED = BUILDER
            .comment("Enables or disables the /heal command")
            .define("commands.heal.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_HEAL_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /heal. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.heal.permission_level", 1, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_FLY_ENABLED = BUILDER
            .comment("Enables or disables the /fly command")
            .define("commands.fly.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_FLY_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /fly. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.fly.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_GOD_ENABLED = BUILDER
            .comment("Enables or disables the /god command")
            .define("commands.god.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_GOD_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /god. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.god.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_FREEZE_ENABLED = BUILDER
            .comment("Enables or disables the /freeze command")
            .define("commands.freeze.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_FREEZE_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /freeze. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.freeze.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_MUTE_ENABLED = BUILDER
            .comment("Enables or disables the /mute command")
            .define("commands.mute.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_MUTE_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /mute. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.mute.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_INVSEE_ENABLED = BUILDER
            .comment("Enables or disables the /invsee command")
            .define("commands.invsee.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_INVSEE_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /invsee. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.invsee.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_ENDERSEE_ENABLED = BUILDER
            .comment("Enables or disables the /endersee command")
            .define("commands.endersee.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_ENDERSEE_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /endersee. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.endersee.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_DIMENSION_ENABLED = BUILDER
            .comment("Enables or disables dimension access management commands")
            .define("commands.dimension.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_DIMENSION_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to manage dimension access. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.dimension.permission_level", 3, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_LASTPOS_ENABLED = BUILDER
            .comment("Enables or disables the /lastpos command")
            .define("commands.lastpos.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_LASTPOS_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /lastpos. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.lastpos.permission_level", 2, 0, 4);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
