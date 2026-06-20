package com.sirsquidly.firework_table.inventory;

import com.sirsquidly.firework_table.config.Config;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFireworkTable extends Container
{
    public final InventoryFireworkTable inventory;
    public InventoryBasic craftResult = new InventoryBasic("FireworkTableOutput", false, 1);

    private int lastShape = -1;
    private int lastTab = -1;
    public boolean prevFlickEnabled = false;
    public boolean prevTrailEnabled = false;

    public FireworkTab currentTab = FireworkTab.DYES;
    public FireworkShape currentShape = FireworkShape.SMALL;
    public boolean flickEnabled = false;
    public boolean trailEnabled = false;


    private final World world;
    private final BlockPos pos;
    private final EntityPlayer player;

    public ContainerFireworkTable(InventoryPlayer playerInventory, World worldIn, BlockPos posIn)
    {
        this.world = worldIn;
        this.pos = posIn;
        this.player = playerInventory.player;

        inventory = new InventoryFireworkTable(this);

        addSlotToContainer(new ContainerFireworkTable.SlotGunpower(inventory, 0, 13, 15));
        //addSlotToContainer(new ContainerFireworkTable.SlotFireworkStar(inventory, 1, 13, 38));

        for (int i = 0; i < 8; i++)
        { addSlotToContainer(new ContainerFireworkTable.SlotDye(inventory, 2 + i, 53 + (i % 4) * 18, 15 + (i / 4) * 18)); }

        for (int i = 0; i < 8; i++)
        { addSlotToContainer(new ContainerFireworkTable.SlotDye(inventory, 10 + i, 53 + (i % 4) * 18, 63 + (i / 4) * 18)); }

        addSlotToContainer(new ContainerFireworkTable.SlotOutput(inventory, craftResult, 0, 142, 67));


        /* This section sets up the Player's Inventory. */
        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            { this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 105 + k * 18)); }
        }
        for (int l = 0; l < 9; ++l)
        { this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 163)); }
    }


    public boolean enchantItem(EntityPlayer playerIn, int id)
    {
        System.out.println("Received ID = " + id);

        if (id > 0 && id < 100)
        {
            this.currentShape = FireworkShape.values()[id - 1];
            return true;
        }

        switch (id)
        {
            case 101:
                this.flickEnabled = !this.flickEnabled;
                return true;
            case 102:
                this.trailEnabled = !this.trailEnabled;
                return true;
            case -1:
                this.currentTab = FireworkTab.DYES;
                return true;
            case -2:
                this.currentTab = FireworkTab.EXPLOSION;
                return true;
        }
        return false;
    }

    /** Pretty much used to pass serverside information into `updateProgressBar`, which is read client-side. */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        updateOutput();

        for (IContainerListener listener : listeners)
        {
            if (lastTab != currentTab.ordinal())
            { listener.sendWindowProperty(this, 0, currentTab.ordinal()); }

            if (lastShape != currentShape.ordinal())
            { listener.sendWindowProperty(this, 1, currentShape.ordinal()); }

            if (prevFlickEnabled != flickEnabled)
            { listener.sendWindowProperty(this, 2, flickEnabled ? 1 : 0); }

            if (prevTrailEnabled != trailEnabled)
            { listener.sendWindowProperty(this, 3, trailEnabled ? 1 : 0); }
        }

        lastTab = currentTab.ordinal();
        lastShape = currentShape.ordinal();
        prevFlickEnabled = flickEnabled;
        prevTrailEnabled = trailEnabled;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0) { currentTab = FireworkTab.values()[data]; }
        if (id == 1) { currentShape = FireworkShape.values()[data]; }
        if (id == 2) { flickEnabled = data == 1; }
        if (id == 3) { trailEnabled = data == 1; }
    }

    private void updateOutput()
    {
        /* If a locked slot has an item, cancel any output!*/
        for (int i = 2; i <= 9; i++)
        {
            if (isDyeSlotDisabled(i) && !inventory.getStackInSlot(i).isEmpty())
            {
                craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                return;
            }
        }

        ItemStack gunpowder = inventory.getStackInSlot(0);

        if (gunpowder.isEmpty())
        {
            craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
            return;
        }

        int[] colors = collectDyeColors(2, 9);
        int[] colorsFade = collectDyeColors(10, 17);

        if (colors.length == 0)
        {
            craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
            return;
        }

        ItemStack result = new ItemStack(Items.FIREWORK_CHARGE);
        NBTTagCompound explosion = new NBTTagCompound();

        explosion.setByte("Type", (byte) this.currentShape.ordinal());
        explosion.setIntArray("Colors", colors);
        if (colorsFade.length != 0) explosion.setIntArray("FadeColors", colorsFade);
        explosion.setBoolean("Flicker", this.flickEnabled);
        explosion.setBoolean("Trail", this.trailEnabled);

        NBTTagCompound root = new NBTTagCompound();
        root.setTag("Explosion", explosion);
        result.setTagCompound(root);

        craftResult.setInventorySlotContents(0, result);
    }

    private int[] collectDyeColors(int startSlot, int endSlot)
    {
        IntArrayList list = new IntArrayList();

        for (int i = startSlot; i <= endSlot; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                list.add(Integer.valueOf(ItemDye.DYE_COLORS[net.minecraftforge.oredict.DyeUtils.rawDyeDamageFromStack(stack) & 15]));
            }
        }

        return list.toIntArray();
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.inventory.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.inventory.getSizeInventory(), this.inventorySlots.size(), true))
                { return ItemStack.EMPTY; }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.inventory.getSizeInventory(), false))
            { return ItemStack.EMPTY; }

            if (itemstack1.isEmpty())
            { slot.putStack(ItemStack.EMPTY); }
            else
            { slot.onSlotChanged(); }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public void onContainerClosed(EntityPlayer playerIn)
    {
        if (!this.world.isRemote)
        {
            this.clearContainer(playerIn, this.world, this.inventory);
        }

        super.onContainerClosed(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    { return true; }

    public enum FireworkTab { DYES, EXPLOSION }
    public enum FireworkShape { SMALL, LARGE, STAR, CREEPER, BURST }

    /** Determins the 'complexity' of the current item, as in, how possible it would be to craft. */
    public int getComplexity()
    {
        int complexity = 0;
        if (!Config.enableComplexityLimits) return complexity;
        if (this.currentShape.ordinal() != 0) complexity++;
        if (this.flickEnabled) complexity++;
        if (this.trailEnabled) complexity++;
        return complexity;
    }

    public boolean isDyeSlotDisabled(int slotIndex)
    {
        int complexity = getComplexity();

        int lastDyeSlot = 9;
        int disabledSlotsStart = lastDyeSlot - complexity + 1;

        return slotIndex >= disabledSlotsStart && slotIndex < 10;
    }

    class SlotGunpower extends Slot
    {
        public SlotGunpower(IInventory inventoryIn, int index, int xIn, int yIn)
        { super(inventoryIn, index, xIn, yIn); }

        public boolean isItemValid(ItemStack stack)
        {
            return stack.getItem() == Items.GUNPOWDER;
        }
    }

    class SlotFireworkStar extends Slot
    {
        public SlotFireworkStar(IInventory inventoryIn, int index, int xIn, int yIn)
        { super(inventoryIn, index, xIn, yIn); }

        public boolean isItemValid(ItemStack stack)
        {
            return stack.getItem() == Items.FIREWORK_CHARGE;
        }

        public int getSlotStackLimit()
        {
            return 1;
        }
    }

    class SlotDye extends Slot
    {
        int slotIndex;

        public SlotDye(IInventory inventoryIn, int index, int xIn, int yIn)
        {
            super(inventoryIn, index, xIn, yIn);
            this.slotIndex = index;
        }

        public boolean isItemValid(ItemStack stack)
        {
            if (inventory instanceof InventoryFireworkTable)
            {
                ContainerFireworkTable table = ((InventoryFireworkTable)inventory).container;
                if (table.isDyeSlotDisabled(this.slotIndex)) return false;
            }

            return net.minecraftforge.oredict.DyeUtils.isDye(stack);
        }

        @SideOnly(Side.CLIENT)
        public boolean isEnabled()
        {
            if (inventory instanceof InventoryFireworkTable)
            {
                ContainerFireworkTable table = ((InventoryFireworkTable)inventory).container;
                return table.currentTab == FireworkTab.DYES;
            }

            return true;
        }
    }

    class SlotOutput extends Slot
    {
        private final InventoryFireworkTable fireworkInv;

        public SlotOutput(InventoryFireworkTable fireworkInvIn, IInventory inventoryIn, int index, int xIn, int yIn)
        {
            super(inventoryIn, index, xIn, yIn);
            this.fireworkInv = fireworkInvIn;
        }

        @Override
        public boolean isItemValid(ItemStack stack) { return false; }

        @Override
        public ItemStack onTake(EntityPlayer player, ItemStack stack)
        {
            fireworkInv.consumeIngredients();
            return stack;
        }
    }
}