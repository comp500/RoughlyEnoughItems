package me.shedaniel.rei.gui;

import me.shedaniel.rei.RoughlyEnoughItemsCore;
import me.shedaniel.rei.client.ClientHelper;
import me.shedaniel.rei.client.GuiHelper;
import me.shedaniel.rei.client.KeyBindHelper;
import me.shedaniel.rei.gui.widget.*;
import me.shedaniel.rei.listeners.IMixinGuiContainer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerGuiOverlay extends GuiScreen {
    
    public static String searchTerm = "";
    private static int page = 0;
    private static ItemListOverlay itemListOverlay;
    private final List<IWidget> widgets;
    private final List<QueuedTooltip> queuedTooltips;
    private Rectangle rectangle;
    private IMixinGuiContainer containerGui;
    private MainWindow window;
    private ButtonWidget buttonLeft, buttonRight;
    private int lastLeft;
    
    public ContainerGuiOverlay(GuiContainer containerGui) {
        this.queuedTooltips = new ArrayList<>();
        this.containerGui = (IMixinGuiContainer) containerGui;
        this.widgets = new ArrayList<>();
    }
    
    public void onInitialized() {
        //Update Variables
        this.widgets.clear();
        this.window = Minecraft.getInstance().mainWindow;
        if (Minecraft.getInstance().currentScreen instanceof GuiContainer)
            this.containerGui = (IMixinGuiContainer) Minecraft.getInstance().currentScreen;
        this.rectangle = calculateBoundary();
        widgets.add(this.itemListOverlay = new ItemListOverlay(containerGui, page));
        this.lastLeft = getLeft();
        
        this.itemListOverlay.updateList(getItemListArea(), page, searchTerm);
        widgets.add(buttonLeft = new ButtonWidget(rectangle.x, rectangle.y + 5, 16, 16, "<") {
            @Override
            public void onPressed(int button, double mouseX, double mouseY) {
                page--;
                if (page < 0)
                    page = getTotalPage();
                itemListOverlay.updateList(getItemListArea(), page, searchTerm);
            }
        });
        widgets.add(buttonRight = new ButtonWidget(rectangle.x + rectangle.width - 18, rectangle.y + 5, 16, 16, ">") {
            @Override
            public void onPressed(int button, double mouseX, double mouseY) {
                page++;
                if (page > getTotalPage())
                    page = 0;
                itemListOverlay.updateList(getItemListArea(), page, searchTerm);
            }
        });
        page = MathHelper.clamp(page, 0, getTotalPage());
        widgets.add(new ButtonWidget(10, 10, 40, 20, "") {
            @Override
            public void draw(int int_1, int int_2, float float_1) {
                this.text = getCheatModeText();
                super.draw(int_1, int_2, float_1);
            }
            
            @Override
            public void onPressed(int button, double mouseX, double mouseY) {
                ClientHelper.setCheating(!ClientHelper.isCheating());
            }
        });
        widgets.add(new ButtonWidget(10, 35, 40, 20, I18n.format("text.rei.config")) {
            @Override
            public void onPressed(int button, double mouseX, double mouseY) {
                ClientHelper.openConfigWindow(containerGui.getContainerGui());
            }
        });
        this.widgets.add(new LabelWidget(rectangle.x + (rectangle.width / 2), rectangle.y + 10, "") {
            @Override
            public void draw(int mouseX, int mouseY, float partialTicks) {
                page = MathHelper.clamp(page, 0, getTotalPage());
                this.text = String.format("%s/%s", page + 1, getTotalPage() + 1);
                super.draw(mouseX, mouseY, partialTicks);
            }
        });
        if (GuiHelper.searchField == null)
            GuiHelper.searchField = new TextFieldWidget(0, 0, 0, 0) {
                @Override
                public boolean mouseClicked(double double_1, double double_2, int int_1) {
                    if (isVisible() && getBounds().contains(double_1, double_2) && int_1 == 1) {
                        setText("");
                        return true;
                    }
                    return super.mouseClicked(double_1, double_2, int_1);
                }
            };
        GuiHelper.searchField.setChangedListener(s -> {
            searchTerm = s;
            itemListOverlay.updateList(page, searchTerm);
        });
        GuiHelper.searchField.setBounds(getTextFieldArea());
        this.widgets.add(GuiHelper.searchField);
        GuiHelper.searchField.setText(searchTerm);
        if (RoughlyEnoughItemsCore.getConfigHelper().showCraftableOnlyButton())
            this.widgets.add(new CraftableToggleButtonWidget(containerGui, getCraftableToggleArea()) {
                @Override
                public void onPressed(int button, double mouseX, double mouseY) {
                    RoughlyEnoughItemsCore.getConfigHelper().toggleCraftableOnly();
                    itemListOverlay.updateList(page, searchTerm);
                }
            });
        
        this.children.addAll(widgets);
    }
    
    private Rectangle getTextFieldArea() {
        int widthRemoved = RoughlyEnoughItemsCore.getConfigHelper().showCraftableOnlyButton() ? 22 : 0;
        if (RoughlyEnoughItemsCore.getConfigHelper().sideSearchField())
            return new Rectangle(rectangle.x + 2, window.getScaledHeight() - 22, rectangle.width - 6 - widthRemoved, 18);
        if (Minecraft.getInstance().currentScreen instanceof RecipeViewingWidget) {
            RecipeViewingWidget widget = (RecipeViewingWidget) Minecraft.getInstance().currentScreen;
            return new Rectangle(widget.getBounds().x, window.getScaledHeight() - 22, widget.getBounds().width - widthRemoved, 18);
        }
        return new Rectangle(containerGui.getContainerLeft(), window.getScaledHeight() - 22, containerGui.getContainerWidth() - widthRemoved, 18);
    }
    
    private Rectangle getCraftableToggleArea() {
        Rectangle searchBoxArea = getTextFieldArea();
        searchBoxArea.setLocation(searchBoxArea.x + searchBoxArea.width + 4, searchBoxArea.y - 1);
        searchBoxArea.setSize(20, 20);
        return searchBoxArea;
    }
    
    private String getCheatModeText() {
        return I18n.format(String.format("%s%s", "text.rei.", ClientHelper.isCheating() ? "cheat" : "nocheat"));
    }
    
    private Rectangle getItemListArea() {
        return new Rectangle(rectangle.x + 2, rectangle.y + 24, rectangle.width - 4, rectangle.height - (RoughlyEnoughItemsCore.getConfigHelper().sideSearchField() ? 27 + 22 : 27));
    }
    
    public Rectangle getRectangle() {
        return rectangle;
    }
    
    public void renderOverlay(int mouseX, int mouseY, float partialTicks) {
        List<ItemStack> currentStacks = ClientHelper.getInventoryItemsTypes();
        if (getLeft() != lastLeft)
            onInitialized();
        else if (RoughlyEnoughItemsCore.getConfigHelper().craftableOnly() && (!hasSameListContent(new LinkedList<>(GuiHelper.inventoryStacks), currentStacks) || (currentStacks.size() != GuiHelper.inventoryStacks.size()))) {
            GuiHelper.inventoryStacks = ClientHelper.getInventoryItemsTypes();
            itemListOverlay.updateList(page, searchTerm);
        }
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        this.render(mouseX, mouseY, partialTicks);
        RenderHelper.disableStandardItemLighting();
        queuedTooltips.forEach(queuedTooltip -> containerGui.getContainerGui().drawHoveringText(queuedTooltip.text, queuedTooltip.mouse.x, queuedTooltip.mouse.y));
        queuedTooltips.clear();
        RenderHelper.disableStandardItemLighting();
    }
    
    private boolean hasSameListContent(List<ItemStack> list1, List<ItemStack> list2) {
        Collections.sort(list1, (itemStack, t1) -> {
            return itemStack.getDisplayName().getFormattedText().compareToIgnoreCase(t1.getDisplayName().getFormattedText());
        });
        Collections.sort(list2, (itemStack, t1) -> {
            return itemStack.getDisplayName().getFormattedText().compareToIgnoreCase(t1.getDisplayName().getFormattedText());
        });
        String lastString = String.join("", list1.stream().map(itemStack -> {
            return itemStack.getDisplayName().getFormattedText();
        }).collect(Collectors.toList())), currentString = String.join("", list2.stream().map(itemStack -> {
            return itemStack.getDisplayName().getFormattedText();
        }).collect(Collectors.toList()));
        return lastString.equals(currentString);
    }
    
    public void setContainerGui(IMixinGuiContainer containerGui) {
        this.containerGui = containerGui;
    }
    
    public void addTooltip(QueuedTooltip queuedTooltip) {
        queuedTooltips.add(queuedTooltip);
    }
    
    @Override
    public void render(int int_1, int int_2, float float_1) {
        if (!GuiHelper.isOverlayVisible())
            return;
        widgets.forEach(widget -> {
            RenderHelper.disableStandardItemLighting();
            widget.draw(int_1, int_2, float_1);
        });
        RenderHelper.disableStandardItemLighting();
        itemListOverlay.draw(int_1, int_2, float_1);
        RenderHelper.disableStandardItemLighting();
        super.render(int_1, int_2, float_1);
    }
    
    private Rectangle calculateBoundary() {
        int startX = containerGui.getContainerLeft() + containerGui.getContainerWidth() + 10;
        int width = window.getScaledWidth() - startX;
        if (Minecraft.getInstance().currentScreen instanceof RecipeViewingWidget) {
            RecipeViewingWidget widget = (RecipeViewingWidget) Minecraft.getInstance().currentScreen;
            startX = widget.getBounds().x + widget.getBounds().width + 10;
            width = window.getScaledWidth() - startX;
        }
        return new Rectangle(startX, 0, width, window.getScaledHeight());
    }
    
    private int getLeft() {
        if (Minecraft.getInstance().currentScreen instanceof RecipeViewingWidget) {
            RecipeViewingWidget widget = (RecipeViewingWidget) Minecraft.getInstance().currentScreen;
            return widget.getBounds().x;
        }
        return containerGui.getContainerLeft();
    }
    
    private int getTotalPage() {
        return MathHelper.ceil(itemListOverlay.getCurrentDisplayed().size() / itemListOverlay.getTotalSlotsPerPage());
    }
    
    @Override
    public boolean mouseScrolled(double amount) {
        if (rectangle.contains(ClientHelper.getMouseLocation())) {
            if (amount > 0 && buttonLeft.enabled)
                buttonLeft.onPressed(0, 0, 0);
            else if (amount < 0 && buttonRight.enabled)
                buttonRight.onPressed(0, 0, 0);
            else
                return false;
            return true;
        }
        for(IWidget widget : widgets)
            if (widget.mouseScrolled(amount))
                return true;
        return false;
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        for(IGuiEventListener listener : children)
            if (listener.keyPressed(int_1, int_2, int_3))
                return true;
        Point point = ClientHelper.getMouseLocation();
        ItemStack itemStack = null;
        for(IWidget widget : itemListOverlay.getListeners())
            if (widget instanceof ItemSlotWidget && ((ItemSlotWidget) widget).isHighlighted(point.x, point.y)) {
                itemStack = ((ItemSlotWidget) widget).getCurrentStack();
                break;
            }
        if (itemStack == null && Minecraft.getInstance().currentScreen instanceof RecipeViewingWidget) {
            RecipeViewingWidget recipeViewingWidget = (RecipeViewingWidget) Minecraft.getInstance().currentScreen;
            for(IGuiEventListener entry : recipeViewingWidget.getChildren())
                if (entry instanceof ItemSlotWidget && ((ItemSlotWidget) entry).isHighlighted(point.x, point.y)) {
                    itemStack = ((ItemSlotWidget) entry).getCurrentStack();
                    break;
                }
        }
        if (itemStack == null && Minecraft.getInstance().currentScreen instanceof GuiContainer)
            if (containerGui.getHoveredSlot() != null)
                itemStack = containerGui.getHoveredSlot().getStack();
        if (itemStack != null && !itemStack.isEmpty()) {
            if (KeyBindHelper.RECIPE.matchesKey(int_1, int_2))
                return ClientHelper.executeRecipeKeyBind(this, itemStack, containerGui);
            else if (KeyBindHelper.USAGE.matchesKey(int_1, int_2))
                return ClientHelper.executeUsageKeyBind(this, itemStack, containerGui);
        }
        if (KeyBindHelper.HIDE.matchesKey(int_1, int_2)) {
            GuiHelper.toggleOverlayVisible();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char char_1, int int_1) {
        for(IGuiEventListener listener : children)
            if (listener.charTyped(char_1, int_1))
                return true;
        return super.charTyped(char_1, int_1);
    }
    
}