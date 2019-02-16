package me.shedaniel.rei.gui.credits;

import me.shedaniel.rei.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;

public class CreditsGui extends GuiScreen {
    
    private GuiContainer parent;
    private CreditsEntryListWidget entryListWidget;
    
    public CreditsGui(GuiContainer parent) {
        this.parent = parent;
    }
    
    @Override
    public boolean keyPressed(int int_1, int int_2, int int_3) {
        if (int_1 == 256 && this.allowCloseWithEscape()) {
            this.mc.displayGuiScreen(parent);
            GuiHelper.getLastOverlay().onInitialized();
            return true;
        }
        return super.keyPressed(int_1, int_2, int_3);
    }
    
    @Override
    protected void initGui() {
        children.add(entryListWidget = new CreditsEntryListWidget(mc, width, height, 32, height - 32, 12));
        entryListWidget.creditsClearEntries();
        for(String line : I18n.format("text.rei.credit.text").split("\n"))
            entryListWidget.creditsAddEntry(new CreditsEntry(new TextComponentString(line)));
        entryListWidget.creditsAddEntry(new CreditsEntry(new TextComponentString("")));
        addButton(new GuiButton(0, width / 2 - 100, height - 26, I18n.format("gui.done")) {
            @Override
            public void onClick(double double_1, double double_2) {
                CreditsGui.this.mc.displayGuiScreen(parent);
                GuiHelper.getLastOverlay().onInitialized();
            }
        });
    }
    
    @Override
    public void render(int int_1, int int_2, float float_1) {
        this.drawBackground(0);
        this.entryListWidget.drawScreen(int_1, int_2, float_1);
        this.drawCenteredString(this.fontRenderer, I18n.format("text.rei.credits"), this.width / 2, 16, 16777215);
        super.render(int_1, int_2, float_1);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public IGuiEventListener getFocused() {
        return entryListWidget;
    }
    
}
