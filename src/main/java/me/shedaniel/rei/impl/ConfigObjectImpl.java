/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.impl;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.rei.api.ConfigObject;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.gui.config.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
@Config(name = "roughlyenoughitems/config")
@Environment(EnvType.CLIENT)
public class ConfigObjectImpl implements ConfigObject, ConfigData {
    
    @ConfigEntry.Category("basics") @ConfigEntry.Gui.TransitiveObject @DontApplyFieldName
    public Basics basics = new Basics();
    @ConfigEntry.Category("appearance") @ConfigEntry.Gui.TransitiveObject @DontApplyFieldName
    private Appearance appearance = new Appearance();
    @ConfigEntry.Category("functionality") @ConfigEntry.Gui.TransitiveObject @DontApplyFieldName
    private Functionality functionality = new Functionality();
    @ConfigEntry.Category("advanced") @ConfigEntry.Gui.TransitiveObject @DontApplyFieldName
    private Advanced advanced = new Advanced();
    
    @Override
    public boolean isOverlayVisible() {
        return basics.overlayVisible;
    }
    
    @Override
    public void setOverlayVisible(boolean overlayVisible) {
        basics.overlayVisible = overlayVisible;
    }
    
    @Override
    public boolean isCheating() {
        return basics.cheating;
    }
    
    @Override
    public void setCheating(boolean cheating) {
        basics.cheating = cheating;
    }
    
    @Override
    public EntryPanelOrdering getItemListOrdering() {
        return advanced.layout.entryPanelOrdering.getOrdering();
    }
    
    @Override
    public boolean isItemListAscending() {
        return advanced.layout.entryPanelOrdering.isAscending();
    }
    
    @Override
    public boolean isUsingDarkTheme() {
        return appearance.theme == AppearanceTheme.DARK;
    }
    
    @Override
    public boolean isGrabbingItems() {
        return basics.cheatingStyle == ItemCheatingStyle.GRAB;
    }
    
    @Override
    public boolean isToastDisplayedOnCopyIdentifier() {
        return advanced.accessibility.toastDisplayedOnCopyIdentifier;
    }
    
    @Override
    public boolean doesRenderEntryEnchantmentGlint() {
        return advanced.miscellaneous.renderEntryEnchantmentGlint;
    }
    
    @Override
    public boolean isEntryListWidgetScrolled() {
        return appearance.scrollingEntryListWidget;
    }
    
    @Override
    public boolean shouldAppendModNames() {
        return advanced.tooltips.appendModNames;
    }
    
    @Override
    public RecipeScreenType getRecipeScreenType() {
        return appearance.recipeScreenType;
    }
    
    @Override
    public void setRecipeScreenType(RecipeScreenType recipeScreenType) {
        appearance.recipeScreenType = recipeScreenType;
    }
    
    @Override
    public boolean isLoadingDefaultPlugin() {
        return advanced.miscellaneous.loadDefaultPlugin;
    }
    
    @Override
    public SearchFieldLocation getSearchFieldLocation() {
        return appearance.layout.searchFieldLocation;
    }
    
    @Override
    public boolean isLeftHandSidePanel() {
        return advanced.accessibility.displayPanelLocation == DisplayPanelLocation.LEFT;
    }
    
    @Override
    public boolean isCraftableFilterEnabled() {
        return appearance.layout.enableCraftableOnlyButton;
    }
    
    @Override
    public String getGamemodeCommand() {
        return advanced.commands.gamemodeCommand;
    }
    
    @Override
    public String getGiveCommand() {
        return advanced.commands.giveCommand;
    }
    
    @Override
    public String getWeatherCommand() {
        return advanced.commands.weatherCommand;
    }
    
    @Override
    public int getMaxRecipePerPage() {
        return advanced.layout.maxRecipesPerPage;
    }
    
    @Override
    public boolean doesShowUtilsButtons() {
        return appearance.layout.showUtilsButtons;
    }
    
    @Override
    public boolean doesDisableRecipeBook() {
        return functionality.disableRecipeBook;
    }
    
    @Override
    public boolean doesFixTabCloseContainer() {
        return functionality.disableRecipeBook;
    }
    
    @Override
    public boolean areClickableRecipeArrowsEnabled() {
        return advanced.miscellaneous.clickableRecipeArrows;
    }
    
    @Override
    public RecipeBorderType getRecipeBorderType() {
        return appearance.recipeBorder;
    }
    
    @Override
    public boolean doesVillagerScreenHavePermanentScrollBar() {
        return advanced.accessibility.villagerScreenPermanentScrollBar;
    }
    
    @Override
    public boolean doesRegisterRecipesInAnotherThread() {
        return advanced.miscellaneous.registerRecipesInAnotherThread;
    }
    
    @Override
    public boolean doesSnapToRows() {
        return advanced.accessibility.snapToRows;
    }
    
    @Override
    public boolean isFavoritesEnabled() {
        return basics.favoritesEnabled;
    }
    
    @Override
    public boolean doDisplayFavoritesTooltip() {
        return isFavoritesEnabled() && advanced.tooltips.displayFavoritesTooltip;
    }
    
    @Override
    public boolean doesFastEntryRendering() {
        return advanced.miscellaneous.newFastEntryRendering;
    }
    
    @Override
    public boolean doDebugRenderTimeRequired() {
        return advanced.layout.debugRenderTimeRequired;
    }
    
    @Override
    public boolean doSearchFavorites() {
        return advanced.search.searchFavorites;
    }
    
    @Override
    public ModifierKeyCode getFavoriteKeyCode() {
        return basics.keyBindings.favoriteKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.favoriteKeybind;
    }
    
    @Override
    public ModifierKeyCode getRecipeKeybind() {
        return basics.keyBindings.recipeKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.recipeKeybind;
    }
    
    @Override
    public ModifierKeyCode getUsageKeybind() {
        return basics.keyBindings.usageKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.usageKeybind;
    }
    
    @Override
    public ModifierKeyCode getHideKeybind() {
        return basics.keyBindings.hideKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.hideKeybind;
    }
    
    @Override
    public ModifierKeyCode getPreviousPageKeybind() {
        return basics.keyBindings.previousPageKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.previousPageKeybind;
    }
    
    @Override
    public ModifierKeyCode getNextPageKeybind() {
        return basics.keyBindings.nextPageKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.nextPageKeybind;
    }
    
    @Override
    public ModifierKeyCode getFocusSearchFieldKeybind() {
        return basics.keyBindings.focusSearchFieldKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.focusSearchFieldKeybind;
    }
    
    @Override
    public ModifierKeyCode getCopyRecipeIdentifierKeybind() {
        return basics.keyBindings.copyRecipeIdentifierKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.copyRecipeIdentifierKeybind;
    }
    
    @Override
    public ModifierKeyCode getExportImageKeybind() {
        return basics.keyBindings.exportImageKeybind == null ? ModifierKeyCode.unknown() : basics.keyBindings.exportImageKeybind;
    }
    
    @Override
    public double getEntrySize() {
        return advanced.accessibility.entrySize;
    }
    
    @Override
    public boolean isUsingCompactTabs() {
        return advanced.accessibility.useCompactTabs;
    }
    
    @Override
    public boolean isLowerConfigButton() {
        return appearance.layout.configButtonLocation == ConfigButtonPosition.LOWER;
    }
    
    @Override
    public List<EntryStack> getFavorites() {
        return basics.favorites;
    }
    
    @Override
    public List<EntryStack> getFilteredStacks() {
        return advanced.filtering.filteredStacks;
    }
    
    @Override
    @ApiStatus.Experimental
    public boolean shouldAsyncSearch() {
        return advanced.search.asyncSearch;
    }
    
    @Override
    @ApiStatus.Experimental
    public int getNumberAsyncSearch() {
        return advanced.search.numberAsyncSearch;
    }
    
    @Override
    @ApiStatus.Experimental
    public boolean doDebugSearchTimeRequired() {
        return advanced.search.debugSearchTimeRequired;
    }
    
    @Override
    @ApiStatus.Experimental
    public boolean isSubsetsEnabled() {
        return functionality.isSubsetsEnabled;
    }
    
    @Override
    public boolean shouldResizeDynamically() {
        return advanced.accessibility.resizeDynamically;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface DontApplyFieldName {}
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface UseSpecialRecipeTypeScreen {}
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface UseFilteringScreen {}
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface UsePercentage {
        double min();
        
        double max();
    }
    
    public static class Basics {
        @ConfigEntry.Gui.Excluded public List<EntryStack> favorites = new ArrayList<>();
        @Comment("Declares whether cheating mode is on.") private boolean cheating = false;
        private boolean favoritesEnabled = true;
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        private KeyBindings keyBindings = new KeyBindings();
        @Comment("Declares whether REI is visible.") @ConfigEntry.Gui.Excluded private boolean overlayVisible = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private ItemCheatingStyle cheatingStyle = ItemCheatingStyle.GRAB;
    }
    
    public static class KeyBindings {
        private ModifierKeyCode recipeKeybind = ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(82), Modifier.none());
        private ModifierKeyCode usageKeybind = ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(85), Modifier.none());
        private ModifierKeyCode hideKeybind = ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(79), Modifier.of(false, true, false));
        private ModifierKeyCode previousPageKeybind = ModifierKeyCode.unknown();
        private ModifierKeyCode nextPageKeybind = ModifierKeyCode.unknown();
        private ModifierKeyCode focusSearchFieldKeybind = ModifierKeyCode.unknown();
        private ModifierKeyCode copyRecipeIdentifierKeybind = ModifierKeyCode.unknown();
        private ModifierKeyCode favoriteKeybind = ModifierKeyCode.of(InputUtil.Type.KEYSYM.createFromCode(65), Modifier.none());
        private ModifierKeyCode exportImageKeybind = ModifierKeyCode.unknown();
    }
    
    public static class Appearance {
        @UseSpecialRecipeTypeScreen private RecipeScreenType recipeScreenType = RecipeScreenType.UNSET;
        @Comment("Declares the appearance of REI windows.") @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private AppearanceTheme theme = AppearanceTheme.LIGHT;
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        private Layout layout = new Layout();
        @Comment("Declares the appearance of recipe's border.") @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        private RecipeBorderType recipeBorder = RecipeBorderType.DEFAULT;
        @Comment("Declares whether entry panel is scrolled.") private boolean scrollingEntryListWidget = false;
        
        public static class Layout {
            @Comment("Declares the position of the search field.") @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            private SearchFieldLocation searchFieldLocation = SearchFieldLocation.CENTER;
            @Comment("Declares the position of the config button.") @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            private ConfigButtonPosition configButtonLocation = ConfigButtonPosition.LOWER;
            @Comment("Declares whether the craftable filter button is enabled.") private boolean enableCraftableOnlyButton = false;
            @Comment("Declares whether the utils buttons are shown.") private boolean showUtilsButtons = false;
        }
    }
    
    public static class Functionality {
        @Comment("Declares whether REI should remove the recipe book.") private boolean disableRecipeBook = false;
        @Comment("Declares whether subsets is enabled.") private boolean isSubsetsEnabled = false;
    }
    
    public static class Advanced {
        @ConfigEntry.Gui.CollapsibleObject
        private Tooltips tooltips = new Tooltips();
        @ConfigEntry.Gui.CollapsibleObject
        private Layout layout = new Layout();
        @ConfigEntry.Gui.CollapsibleObject
        private Accessibility accessibility = new Accessibility();
        @ConfigEntry.Gui.CollapsibleObject
        private Search search = new Search();
        @ConfigEntry.Gui.CollapsibleObject
        private Commands commands = new Commands();
        @ConfigEntry.Gui.CollapsibleObject
        private Miscellaneous miscellaneous = new Miscellaneous();
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        private Filtering filtering = new Filtering();
        
        public static class Tooltips {
            @Comment("Declares whether REI should append mod names to entries.") private boolean appendModNames = true;
            @Comment("Declares whether favorites tooltip should be displayed.") private boolean displayFavoritesTooltip = false;
        }
        
        public static class Layout {
            @Comment("The ordering of the items on the entry panel.")
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            private EntryPanelOrderingConfig entryPanelOrdering = EntryPanelOrderingConfig.REGISTRY_ASCENDING;
            @Comment("Declares the maximum amount of recipes displayed in a page if possible.") @ConfigEntry.BoundedDiscrete(min = 2, max = 99)
            private int maxRecipesPerPage = 15;
            @Comment("Declares whether entry rendering time should be debugged.") private boolean debugRenderTimeRequired = false;
        }
        
        public static class Accessibility {
            @UsePercentage(min = 0.25, max = 4.0) private double entrySize = 1.0;
            @Comment("Declares the position of the entry panel.") @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            private DisplayPanelLocation displayPanelLocation = DisplayPanelLocation.RIGHT;
            @Comment("Declares whether scrolled entry panel should snap to rows.") private boolean snapToRows = false;
            @Comment("Declares how the scrollbar in villager screen should act.") private boolean villagerScreenPermanentScrollBar = false;
            private boolean toastDisplayedOnCopyIdentifier = true;
            @Comment("Declares whether REI should use compact tabs for categories.") private boolean useCompactTabs = true;
            @Comment("Declares whether REI should resize its recipe window dynamically") private boolean resizeDynamically = false;
        }
        
        public static class Search {
            @Comment("Declares whether favorites will be searched.") private boolean searchFavorites = true;
            @Comment("Declares whether search time should be debugged.") private boolean debugSearchTimeRequired = false;
            @Comment("Declares whether REI should search async.") private boolean asyncSearch = true;
            @Comment("Declares how many entries should be grouped one async search.") @ConfigEntry.BoundedDiscrete(min = 25, max = 400)
            private int numberAsyncSearch = 50;
        }
        
        public static class Commands {
            @Comment("Declares the command used to change gamemode.") private String gamemodeCommand = "/gamemode {gamemode}";
            @Comment("Declares the command used in servers to cheat items.") private String giveCommand = "/give {player_name} {item_identifier}{nbt} {count}";
            @Comment("Declares the command used to change weather.") private String weatherCommand = "/weather {weather}";
        }
        
        public static class Miscellaneous {
            @Comment("Declares whether arrows in containers should be clickable.") private boolean clickableRecipeArrows = true;
            @Comment("To disable REI's default plugin.\nDon't change this unless you understand what you are doing!") private boolean loadDefaultPlugin = true;
            private boolean registerRecipesInAnotherThread = true;
            @Comment("Whether REI should render entry's enchantment glint") private boolean renderEntryEnchantmentGlint = true;
            private boolean newFastEntryRendering = true;
        }
        
        public static class Filtering {
            @UseFilteringScreen private List<EntryStack> filteredStacks = new ArrayList<>();
        }
    }
}
