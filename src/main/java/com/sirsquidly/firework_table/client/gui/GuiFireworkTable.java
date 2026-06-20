package com.sirsquidly.firework_table.client.gui;

import com.sirsquidly.firework_table.config.Config;
import com.sirsquidly.firework_table.inventory.ContainerFireworkTable;
import com.sirsquidly.firework_table.fireworkTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiFireworkTable extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(fireworkTable.MOD_ID, "textures/gui/firework_table.png");
    private static final ResourceLocation ICON_TEXTURES = new ResourceLocation(fireworkTable.MOD_ID, "textures/gui/firework_table_icons.png");
    private static final ResourceLocation PREVIEW_TEXTURES = new ResourceLocation(fireworkTable.MOD_ID, "textures/gui/firework_table_preview.png");

    private final ContainerFireworkTable container;

    /* A list of all Buttons exclusive to the Explosion Tab. Used for disabling them. */
    private List<GuiButton> explosionButtons = new ArrayList<>();

    private GuiTabButton dyeTab;
    private GuiTabButton explosionTab;

    public GuiFireworkTable(ContainerFireworkTable container)
    {
        super(container);
        this.container = container;

        this.ySize = 200;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = I18n.translateToLocalFormatted("container.firework_table.firework_table");
        this.fontRenderer.drawString(s, (int) ((this.xSize * 0.575F) - (s.length() * 3.5F)), 4, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        this.mc.getTextureManager().bindTexture(ICON_TEXTURES);

        if (container.currentTab == ContainerFireworkTable.FireworkTab.DYES)
        {
            this.drawTexturedModalRect(i + 50, j + 12, 0, 0, 76, 88);
        }
        else
        {
            /* 2ed number should be 0. */
            this.drawTexturedModalRect(i + 50, j + 12, 80, 96, 76, 88);

            this.drawTexturedModalRect(i + 111, j + 12, 192, 64, 16, 16);

            /* The big bars! */
            this.drawTexturedModalRect(i + 50, j + 12, 176, 48, 64, 16);

            this.drawTexturedModalRect(i + 50, j + 57, 176, 48, 64, 16);

            /* A test temp Firework Preview. */
            //this.drawTexturedModalRect(i + 130, j + 18, 0, 112, 64, 64);
        }

        /* The Gunpowder Icon. */
        this.drawTexturedModalRect(i + 13, j + 15, 176, 80, 16, 16);
        /* The Firework Star Icon. */
        //this.drawTexturedModalRect(i + 13, j + 38, 192, 80, 16, 16);



        renderLockedSlots();

        if (!container.craftResult.getStackInSlot(0).isEmpty()) renderExplosionPreview();
    }

    /** This adds the Tab Buttons, since they are always required. */
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiTabButton(-1, this.guiLeft + 32, this.guiTop + 67, ContainerFireworkTable.FireworkTab.DYES));
        this.buttonList.add(new GuiTabButton(-2, this.guiLeft + 32, this.guiTop + 84, ContainerFireworkTable.FireworkTab.EXPLOSION));

        addExplosionButton(new GuiExplodeShapeButton(1, this.guiLeft + 51, this.guiTop + 28, ContainerFireworkTable.FireworkShape.SMALL));
        addExplosionButton(new GuiExplodeShapeButton(2, this.guiLeft + 66, this.guiTop + 28, ContainerFireworkTable.FireworkShape.LARGE));
        addExplosionButton(new GuiExplodeShapeButton(3, this.guiLeft + 81, this.guiTop + 28, ContainerFireworkTable.FireworkShape.STAR));
        addExplosionButton(new GuiExplodeShapeButton(4, this.guiLeft + 96, this.guiTop + 28, ContainerFireworkTable.FireworkShape.CREEPER));
        addExplosionButton(new GuiExplodeShapeButton(5, this.guiLeft + 51, this.guiTop + 28 + 15, ContainerFireworkTable.FireworkShape.BURST));

        addExplosionButton(new GuiExplodeEffectButton(101, this.guiLeft + 51, this.guiTop + 73, 0, container));
        addExplosionButton(new GuiExplodeEffectButton(102, this.guiLeft + 51 + 15, this.guiTop + 73, 1, container));

        /* This prevents the Explosion Buttons from being visible for a split second when first opening the container. */
        for (GuiButton button : explosionButtons)
        {
            button.visible = false;
            button.enabled = false;
        }
    }

    private void addExplosionButton(GuiButton button)
    {
        this.buttonList.add(button);
        this.explosionButtons.add(button);
    }


    public void updateScreen()
    {
        super.updateScreen();

        boolean explosionTabActive = container.currentTab == ContainerFireworkTable.FireworkTab.EXPLOSION;
        for (GuiButton button : explosionButtons)
        {
            button.visible = explosionTabActive;
            button.enabled = explosionTabActive;
        }
    }

    /** Uses the basic vanilla message packet to send a number to the container, interpreted within `enchantItem`. */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        /* Effects are based on multiple booleans, so we send very different IDs to inform if true or not. */
        mc.playerController.sendEnchantPacket(this.container.windowId, button.id);
    }

    private void renderLockedSlots()
    {
        if (!Config.enableComplexityLimits || container.currentTab != ContainerFireworkTable.FireworkTab.DYES) return;

        int complexityCheck = (container.currentShape.ordinal() != 0 ? 1 : 0) + (container.flickEnabled ? 1 : 0) + (container.trailEnabled ? 1 : 0);

        this.mc.getTextureManager().bindTexture(ICON_TEXTURES);

        for (int i = 0; i < complexityCheck; i++)
        {
            boolean slotInUse = container.getSlot(9 - i).getHasStack();
            this.drawTexturedModalRect(guiLeft + 106 - (i * 18), guiTop + 32, 220 + (slotInUse ? 18 : 0), 112, 18, 18);
        }
    }


    private void renderExplosionPreview()
    {
        ItemStack stack = container.craftResult.getStackInSlot(0);
        NBTTagCompound outputTagCompound = stack.getTagCompound().getCompoundTag("Explosion");

        int[] colors = outputTagCompound.getIntArray("Colors");
        /* Sort the colors to prevent the user from getting confused! */
        if (Config.guiPreviewColorSorting && colors.length != 0) Arrays.sort(colors);

        int shape = (int) outputTagCompound.getByte("Type");
        boolean twinkle = outputTagCompound.getBoolean("Flicker");
        boolean trail = outputTagCompound.getBoolean("Trail");



        mc.getTextureManager().bindTexture(PREVIEW_TEXTURES);

        for (int i = 0; i < 8; i++)
        {
            int layerColor = colors[i % colors.length];

            float r = ((layerColor >> 16) & 255) / 255f;
            float g = ((layerColor >> 8) & 255) / 255f;
            float b = (layerColor & 255) / 255f;
            GlStateManager.color(r, g, b, 1.0f);

            drawScaledCustomSizeModalRect(guiLeft + 130, guiTop + 18, i * 40, shape * 40, 40, 40,
                    40, 40, 512, 512);
        }

        if (trail)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            for (int i = 0; i < 8; i++)
            {
                int layerColor = colors[i % colors.length];

                float r = ((layerColor >> 16) & 255) / 255f;
                float g = ((layerColor >> 8) & 255) / 255f;
                float b = (layerColor & 255) / 255f;

                GlStateManager.color(r, g, b, 0.4f);
                drawScaledCustomSizeModalRect(guiLeft + 130, guiTop + 17, i * 40, shape * 40, 40, 40,
                        40, 40, 512, 512);

                GlStateManager.color(r, g, b, 0.2f);
                drawScaledCustomSizeModalRect(guiLeft + 130, guiTop + 16, i * 40, shape * 40, 40, 40,
                        40, 40, 512, 512);
            }
            GlStateManager.disableBlend();
        }

        if (twinkle)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawScaledCustomSizeModalRect(guiLeft + 130, guiTop + 18, 360, shape * 40, 40, 40,
                    40, 40, 512, 512);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }





    @SideOnly(Side.CLIENT)
    public class GuiTabButton extends GuiButton
    {
        private final ContainerFireworkTable.FireworkTab tab;
        private boolean selected;

        public GuiTabButton(int id, int x, int y, ContainerFireworkTable.FireworkTab tab)
        {
            super(id, x, y, 18, 16, "");
            this.tab = tab;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(ICON_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int iconOffset = 0;
                int j = 0;

                if (!this.enabled) j = 0;

                else if (isSelected())
                {
                    j = 32;
                    iconOffset = -2;
                }
                else if (this.hovered) j = 16;

                this.drawTexturedModalRect(this.x, this.y, 238, 64 + j, this.width, this.height);


                /* The Tab Icon. */
                switch (this.tab)
                {
                    case DYES:
                        this.drawTexturedModalRect(this.x + 3 + iconOffset, this.y - 1, 224, 32, 16, 16);
                        break;
                    case EXPLOSION:
                        this.drawTexturedModalRect(this.x + 3 + iconOffset, this.y - 1, 240, 32, 16, 16);
                        break;
                }
            }
        }

        public boolean isSelected() { return container.currentTab == tab; }
        public ContainerFireworkTable.FireworkTab getTab() { return tab; }
    }

    @SideOnly(Side.CLIENT)
    public class GuiExplodeShapeButton extends GuiButton
    {
        private final ContainerFireworkTable.FireworkShape shape;
        private boolean selected;

        public GuiExplodeShapeButton(int id, int x, int y, ContainerFireworkTable.FireworkShape shapeIn)
        {
            super(id, x, y, 15, 15, "");
            this.shape = shapeIn;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(ICON_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int iconOffset = 0;
                int j = 0;

                if (!this.enabled) j = 0;
                else if (isSelected())
                {
                    j = 32;
                    iconOffset = -2;
                }
                else if (this.hovered) j = 16;

                this.drawTexturedModalRect(this.x, this.y, 176 + j, 1, this.width, this.height);

                /* The Shape Icon. */
                switch (this.shape)
                {
                    case SMALL:
                        this.drawTexturedModalRect(this.x, this.y, 176, 17, 16, 16);
                        break;
                    case LARGE:
                        this.drawTexturedModalRect(this.x, this.y, 176 + 16, 17, 16, 16);
                        break;
                    case STAR:
                        this.drawTexturedModalRect(this.x, this.y, 176 + 32, 17, 16, 16);
                        break;
                    case CREEPER:
                        this.drawTexturedModalRect(this.x, this.y, 176 + 48, 17, 16, 16);
                        break;
                    case BURST:
                        this.drawTexturedModalRect(this.x, this.y, 176 + 64, 17, 16, 16);
                        break;
                }
            }
        }

        public boolean isSelected() { return container.currentShape == shape; }
    }

    @SideOnly(Side.CLIENT)
    public class GuiExplodeEffectButton extends GuiButton
    {
        private final int effect;
        private final ContainerFireworkTable container;

        public GuiExplodeEffectButton(int id, int x, int y, int effectIn, ContainerFireworkTable containerIn)
        {
            super(id, x, y, 15, 15, "");
            this.effect = effectIn;
            container = containerIn;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(ICON_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                int iconOffset = 0;
                int j = 0;

                if (!this.enabled) j = 0;
                else if (isSelected())
                {
                    j = 32;
                    iconOffset = -2;
                }
                else if (this.hovered) j = 16;

                this.drawTexturedModalRect(this.x, this.y, 176 + j, 1, this.width, this.height);

                /* The Effect Icon. */
                if (this.effect == 1)
                { this.drawTexturedModalRect(this.x, this.y, 176, 33, 16, 16); }
                else
                { this.drawTexturedModalRect(this.x, this.y, 176 + 16, 33, 16, 16); }
            }
        }

        public boolean isSelected()
        {
            switch (effect)
            {
                case 0:
                    return container.flickEnabled;
                case 1:
                    return container.trailEnabled;
                default:
                    return false;
            }
        }
    }
}