package com.sirsquidly.firework_table.common;

import com.sirsquidly.firework_table.config.ConfigParser;
import com.sirsquidly.firework_table.fireworkTable;
import com.sirsquidly.firework_table.util.GuiHandler;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy
{
    public void preInitRegisteries(FMLPreInitializationEvent event) {}

    public void initRegisteries(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(fireworkTable.instance, new GuiHandler());
    }

    public void postInitRegisteries(FMLPostInitializationEvent event)
    { ConfigParser.breakupConfigArrays(); }

    @SideOnly(Side.CLIENT)
    public void registerItemRenderer(Item item, int meta, String id) {}
}