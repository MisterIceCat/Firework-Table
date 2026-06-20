package com.sirsquidly.firework_table;

import com.sirsquidly.firework_table.common.CommonProxy;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = fireworkTable.MOD_ID, name = fireworkTable.NAME, version = fireworkTable.VERSION, dependencies = fireworkTable.DEPENDENCIES)
public class fireworkTable {
    public static final String MOD_ID = "firework_table";
    public static final String NAME = "Firework Table";
    public static final String CONFIG_NAME = "firework_table";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "";
    public static final String CLIENT_PROXY_CLASS = "com.sirsquidly.firework_table.client.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.sirsquidly.firework_table.common.CommonProxy";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance
    public static fireworkTable instance;

    @SidedProxy(clientSide = fireworkTable.CLIENT_PROXY_CLASS, serverSide = fireworkTable.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    { proxy.preInitRegisteries(event); }

    @EventHandler
    public void init(FMLInitializationEvent event)
    { proxy.initRegisteries(event); }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {  proxy.postInitRegisteries(event);  }

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {}
}
