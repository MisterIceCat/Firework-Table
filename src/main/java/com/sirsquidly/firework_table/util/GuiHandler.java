package com.sirsquidly.firework_table.util;

import com.sirsquidly.firework_table.client.gui.GuiFireworkTable;
import com.sirsquidly.firework_table.inventory.ContainerFireworkTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == 3)
        {
            return new ContainerFireworkTable(player.inventory, world, new BlockPos(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == 3)
        {
            return new GuiFireworkTable(new ContainerFireworkTable(player.inventory, world, new BlockPos(x, y, z)));
        }
        return null;
    }
}