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

    public static final ForgeConfigSpec.BooleanValue COMMAND_ANNOUNCEMENT_ENABLED = BUILDER
            .comment("Enables or disables the /announcement command")
            .define("commands.announcement.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_ANNOUNCEMENT_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /announcement. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.announcement.permission_level", 2, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_FORBID_ENABLED = BUILDER
            .comment("Enables or disables the /forbid and /allow commands")
            .define("commands.forbid.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_FORBID_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /forbid and /allow. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.forbid.permission_level", 3, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_HEAL_ENABLED = BUILDER
            .comment("Enables or disables the /heal command")
            .define("commands.heal.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_HEAL_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /heal. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.heal.permission_level", 1, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_TRASH_ENABLED = BUILDER
            .comment("Enables or disables the /trash command")
            .define("commands.trash.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_TRASH_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /trash. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.trash.permission_level", 0, 0, 4);

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

    public static final ForgeConfigSpec.BooleanValue COMMAND_IGNORE_ENABLED = BUILDER
            .comment("Enables or disables the /ignore command")
            .define("commands.ignore.enabled", true);

    public static final ForgeConfigSpec.BooleanValue COMMAND_AFK_ENABLED = BUILDER
            .comment("Enables or disables the /afk command")
            .define("commands.afk.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_AFK_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /afk. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.afk.permission_level", 0, 0, 4);

    public static final ForgeConfigSpec.BooleanValue COMMAND_INVSEE_ENABLED = BUILDER
            .comment("Enables or disables the /invsee command")
            .define("commands.invsee.enabled", true);

    public static final ForgeConfigSpec.IntValue COMMAND_INVSEE_PERMISSION_LEVEL = BUILDER
            .comment("Permission level required to use /invsee. 0 = any player, 1-4 = operator level")
            .defineInRange("commands.invsee.permission_level", 2, 0, 4);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
