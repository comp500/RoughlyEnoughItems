package me.shedaniel.rei.gui.widget;

import com.google.common.collect.Lists;
import me.shedaniel.rei.client.ClientHelper;
import me.shedaniel.rei.client.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSlotWidget extends Gui implements HighlightableWidget {
    
    private static final ResourceLocation RECIPE_GUI = new ResourceLocation("roughlyenoughitems", "textures/gui/recipecontainer.png");
    private List<ItemStack> itemList = new LinkedList<>();
    private boolean drawBackground, showToolTips, clickToMoreRecipes, drawHighlightedBackground;
    private int x, y;
    
    public ItemSlotWidget(int x, int y, ItemStack itemStack, boolean drawBackground, boolean showToolTips) {
        this(x, y, Arrays.asList(itemStack), drawBackground, showToolTips);
    }
    
    public ItemSlotWidget(int x, int y, List<ItemStack> itemList, boolean drawBackground, boolean showToolTips) {
        this.itemList = itemList;
        this.drawBackground = drawBackground;
        this.showToolTips = showToolTips;
        this.x = x;
        this.y = y;
        this.clickToMoreRecipes = false;
        this.drawHighlightedBackground = true;
    }
    
    public ItemSlotWidget(int x, int y, List<ItemStack> itemList, boolean drawBackground, boolean showToolTips, boolean clickToMoreRecipes) {
        this(x, y, itemList, drawBackground, showToolTips);
        this.clickToMoreRecipes = clickToMoreRecipes;
    }
    
    public void setDrawHighlightedBackground(boolean drawHighlightedBackground) {
        this.drawHighlightedBackground = drawHighlightedBackground;
    }
    
    public boolean isDrawBackground() {
        return drawBackground;
    }
    
    @Override
    public List<IWidget> getListeners() {
        return new ArrayList<>();
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        final ItemStack itemStack = getCurrentStack();
        if (drawBackground) {
            Minecraft.getInstance().getTextureManager().bindTexture(RECIPE_GUI);
            drawTexturedModalRect(this.x - 1, this.y - 1, 0, 222, 18, 18);
        }
        if (drawHighlightedBackground && isHighlighted(mouseX, mouseY)) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.colorMask(true, true, true, false);
            drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
        if (!itemStack.isEmpty()) {
            RenderHelper.enableGUIStandardItemLighting();
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.zLevel = 200.0F;
            itemRenderer.renderItemAndEffectIntoGUI(itemStack, x, y);
            itemRenderer.renderItemOverlayIntoGUI(Minecraft.getInstance().fontRenderer, itemStack, x, y, getItemCountOverlay(itemStack));
            itemRenderer.zLevel = 0.0F;
        }
        if (!itemStack.isEmpty() && isHighlighted(mouseX, mouseY) && showToolTips)
            drawToolTip(itemStack);
    }
    
    protected void drawToolTip(ItemStack itemStack) {
        List<String> toolTip = getTooltip(itemStack);
        GuiHelper.getLastOverlay().addTooltip(new QueuedTooltip(ClientHelper.getMouseLocation(), toolTip));
    }
    
    protected List<String> getTooltip(ItemStack itemStack) {
        final String modString = "§9§o" + ClientHelper.getModFromItemStack(itemStack);
        Minecraft mc = Minecraft.getInstance();
        List<String> toolTip = Lists.newArrayList();
        try {
            toolTip = Minecraft.getInstance().currentScreen.getItemToolTip(itemStack).stream().filter(s -> !s.equals(modString)).collect(Collectors.toList());
        } catch (Exception e) {
            toolTip.add(itemStack.getDisplayName().getFormattedText());
        }
        toolTip.addAll(getExtraToolTips(itemStack));
        toolTip.add(modString);
        return toolTip;
    }
    
    protected List<String> getExtraToolTips(ItemStack stack) {
        return Lists.newArrayList();
    }
    
    protected String getItemCountOverlay(ItemStack currentStack) {
        return "";
    }
    
    public ItemStack getCurrentStack() {
        if (itemList.size() == 0)
            return new ItemStack(Items.AIR);
        return itemList.get(MathHelper.floor((System.currentTimeMillis() / 500 % (double) itemList.size()) / 1f));
    }
    
    public void setItemList(List<ItemStack> itemList) {
        this.itemList = itemList;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.x - 1, this.y - 1, 18, 18);
    }
    
    @Override
    public boolean onMouseClick(int button, double mouseX, double mouseY) {
        if (!clickToMoreRecipes)
            return false;
        if (getBounds().contains(mouseX, mouseY)) {
            if (button == 0)
                return ClientHelper.executeRecipeKeyBind(GuiHelper.getLastOverlay(), getCurrentStack().copy());
            else if (button == 1)
                return ClientHelper.executeUsageKeyBind(GuiHelper.getLastOverlay(), getCurrentStack().copy());
        }
        return false;
    }
    
}
