package com.sirsquidly.firework_table.config;

import com.sirsquidly.firework_table.fireworkTable;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.common.config.Config(modid = fireworkTable.MOD_ID, name = fireworkTable.CONFIG_NAME)
@net.minecraftforge.common.config.Config.LangKey("config.firework_table.title")
@Mod.EventBusSubscriber(modid = fireworkTable.MOD_ID)
public class Config
{
    @net.minecraftforge.common.config.Config.LangKey("config.firework_table.configVersion")
    @net.minecraftforge.common.config.Config.Comment({
            "Config Versions help inform modpack makers/config users if changes have been made to the config between updates. These differ from main versioning, since the config file is static.",
            "Basically, you compare the current default of this value, to the default of when you generated it.",
            "",
            "The versioning follows:",
            "0.0.x - Default values have been slightly adjusted.",
            "0.x.0 - Config options have been added.",
            "x.0.0 - Previous Config Options have been completely overhauled and/or removed. Creating a fresh file is recommended."
    })
    public static String configVersion = "1.0.0";



    @net.minecraftforge.common.config.Config.RequiresMcRestart
    @net.minecraftforge.common.config.Config.LangKey("config.firework_table.block.firework_table.enableComplexityLimits")
    @net.minecraftforge.common.config.Config.Comment({
            "Lowers available Dye Slots based on selected Effects and non-default Shapes.",
            "",
            "This is to match the vanilla limitations of Firework Star Crafting."
    })
    public static boolean enableComplexityLimits = true;

    @net.minecraftforge.common.config.Config.RequiresMcRestart
    @net.minecraftforge.common.config.Config.LangKey("config.firework_table.block.firework_table.guiPreviewColorSorting")
    @net.minecraftforge.common.config.Config.Comment("Sorts colors displayed in the Preview, to prevent confusion.")
    public static boolean guiPreviewColorSorting = true;


    @Mod.EventBusSubscriber(modid = fireworkTable.MOD_ID)
    public static class ConfigSyncHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if(event.getModID().equals(fireworkTable.MOD_ID))
            { ConfigManager.sync(fireworkTable.MOD_ID, net.minecraftforge.common.config.Config.Type.INSTANCE); }
        }
    }
}