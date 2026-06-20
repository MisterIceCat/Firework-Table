package com.sirsquidly.firework_table.common.blocks;

import com.sirsquidly.firework_table.fireworkTable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFireworkTable extends Block
{
    public BlockFireworkTable() { super(Material.WOOD); }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            playerIn.openGui(fireworkTable.instance, 3, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return true;
    }
}