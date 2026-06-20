package com.sirsquidly.firework_table.config;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * 	This is to break part arrays in the config for use in other areas of the code.
 *
 *  I break it up in this class so that I don't have to break the config arrays every time I want to use them.
 */
public class ConfigParser
{
	/** Nightmare spawn biomes list. */
	public static List<ItemStack> veilingTreatItems = Lists.newArrayList();
	public static List<Integer> veilingTreatHappiness = Lists.newArrayList();

	/** Goes through the many Arrays in the config, to translate them into lists to be used elsewhere. */
	public static void breakupConfigArrays()
	{

	}

	public static ItemStack getItemStackFromString(String string)
	{
		String[] ripString = string.split(":");

		if (ripString.length < 2) return ItemStack.EMPTY;

		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(ripString[0], ripString[1]));
		if(item == null) return ItemStack.EMPTY;

		int meta = 0;
		if (ripString.length > 2) meta = Integer.parseInt(ripString[2]);

		return new ItemStack(item, 1, meta);
	}

	public static List<IBlockState> getBlockStatesFromString(String string)
	{
		List<IBlockState> states = new ArrayList<>();
		String[] ripString = string.split(":");

		if (ripString.length < 2)
		{ return states; }

		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(ripString[0], ripString[1]));
		Integer meta;

		if(block == null || block == Blocks.AIR)
		{ return states; }
		if(ripString.length > 2)
		{
			if (ripString[2].equals("*")) meta = -1;
			else meta = Integer.parseInt(ripString[2]);

			if(meta == -1) states.addAll(block.getBlockState().getValidStates());
			else states.add(block.getStateFromMeta(meta));
		}
		else states.add(block.getDefaultState());

		return states;
	}
}