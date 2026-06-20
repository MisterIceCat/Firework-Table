package com.sirsquidly.firework_table.inventory;

import net.minecraft.inventory.InventoryBasic;

public class InventoryFireworkTable extends InventoryBasic
{
    ContainerFireworkTable container;

    public InventoryFireworkTable(ContainerFireworkTable containerIn)
    {
        super("FireworkTable", false, 19);
        container = containerIn;
    }

    @Override
    public int getInventoryStackLimit()
    { return 64; }

    public void consumeIngredients()
    {
        for (int i = 0; i < this.getSizeInventory(); i++)
        {
            this.decrStackSize(i, 1);
        }
    }
}