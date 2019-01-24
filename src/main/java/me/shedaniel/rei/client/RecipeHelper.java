package me.shedaniel.rei.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.rei.RoughlyEnoughItemsCore;
import me.shedaniel.rei.RoughlyEnoughItemsPlugin;
import me.shedaniel.rei.api.*;
import me.shedaniel.rei.listeners.RecipeSync;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeHelper implements RecipeSync {
    
    private static Map<ResourceLocation, List<IRecipeDisplay>> recipeCategoryListMap;
    private static List<IRecipeCategory> categories;
    private static RecipeManager recipeManager;
    private static Map<ResourceLocation, SpeedCraftAreaSupplier> speedCraftAreaSupplierMap;
    private static Map<ResourceLocation, List<SpeedCraftFunctional>> speedCraftFunctionalMap;
    
    public RecipeHelper() {
        this.recipeCategoryListMap = Maps.newHashMap();
        this.categories = Lists.newArrayList();
        this.speedCraftAreaSupplierMap = Maps.newHashMap();
        this.speedCraftFunctionalMap = Maps.newHashMap();
    }
    
    public static List<ItemStack> findCraftableByItems(List<ItemStack> inventoryItems) {
        List<ItemStack> craftables = new ArrayList<>();
        for(List<IRecipeDisplay> value : recipeCategoryListMap.values())
            for(IRecipeDisplay recipeDisplay : value) {
                int slotsCraftable = 0;
                List<List<ItemStack>> requiredInput = (List<List<ItemStack>>) recipeDisplay.getRequiredItems();
                for(List<ItemStack> slot : requiredInput) {
                    if (slot.isEmpty()) {
                        slotsCraftable++;
                        continue;
                    }
                    boolean slotDone = false;
                    for(ItemStack possibleType : inventoryItems) {
                        for(ItemStack slotPossible : slot)
                            if (ItemStack.areItemsEqual(slotPossible, possibleType)) {
                                slotsCraftable++;
                                slotDone = true;
                                break;
                            }
                        if (slotDone)
                            break;
                    }
                }
                if (slotsCraftable == recipeDisplay.getRequiredItems().size())
                    craftables.addAll((List<ItemStack>) recipeDisplay.getOutput());
            }
        return craftables.stream().distinct().collect(Collectors.toList());
    }
    
    public static void registerCategory(IRecipeCategory category) {
        categories.add(category);
        recipeCategoryListMap.put(category.getResourceLocation(), Lists.newArrayList());
    }
    
    public static void registerRecipe(ResourceLocation categoryIdentifier, IRecipeDisplay display) {
        if (!recipeCategoryListMap.containsKey(categoryIdentifier))
            return;
        recipeCategoryListMap.get(categoryIdentifier).add(display);
    }
    
    public static Map<IRecipeCategory, List<IRecipeDisplay>> getRecipesFor(ItemStack stack) {
        Map<ResourceLocation, List<IRecipeDisplay>> categoriesMap = new HashMap<>();
        categories.forEach(f -> categoriesMap.put(f.getResourceLocation(), new LinkedList<>()));
        for(List<IRecipeDisplay> value : recipeCategoryListMap.values())
            for(IRecipeDisplay recipeDisplay : value)
                for(ItemStack outputStack : (List<ItemStack>) recipeDisplay.getOutput())
                    if (ItemStack.areItemsEqual(stack, outputStack))
                        categoriesMap.get(recipeDisplay.getRecipeCategory()).add(recipeDisplay);
        categoriesMap.keySet().removeIf(f -> categoriesMap.get(f).isEmpty());
        Map<IRecipeCategory, List<IRecipeDisplay>> recipeCategoryListMap = Maps.newHashMap();
        categories.forEach(category -> {
            if (categoriesMap.containsKey(category.getResourceLocation()))
                recipeCategoryListMap.put(category, categoriesMap.get(category.getResourceLocation()));
        });
        return recipeCategoryListMap;
    }
    
    public static RecipeManager getRecipeManager() {
        return recipeManager;
    }
    
    public static Map<IRecipeCategory, List<IRecipeDisplay>> getUsagesFor(ItemStack stack) {
        Map<ResourceLocation, List<IRecipeDisplay>> categoriesMap = new HashMap<>();
        categories.forEach(f -> categoriesMap.put(f.getResourceLocation(), new LinkedList<>()));
        for(List<IRecipeDisplay> value : recipeCategoryListMap.values())
            for(IRecipeDisplay recipeDisplay : value) {
                boolean found = false;
                for(List<ItemStack> input : (List<List<ItemStack>>) recipeDisplay.getInput()) {
                    for(ItemStack itemStack : input) {
                        if (ItemStack.areItemsEqual(itemStack, stack)) {
                            categoriesMap.get(recipeDisplay.getRecipeCategory()).add(recipeDisplay);
                            found = true;
                            break;
                        }
                    }
                    if (found)
                        break;
                }
            }
        categoriesMap.keySet().removeIf(f -> categoriesMap.get(f).isEmpty());
        Map<IRecipeCategory, List<IRecipeDisplay>> recipeCategoryListMap = Maps.newHashMap();
        categories.forEach(category -> {
            if (categoriesMap.containsKey(category.getResourceLocation()))
                recipeCategoryListMap.put(category, categoriesMap.get(category.getResourceLocation()));
        });
        return recipeCategoryListMap;
    }
    
    public static List<IRecipeCategory> getCategories() {
        return categories;
    }
    
    public static SpeedCraftAreaSupplier getSpeedCraftButtonArea(IRecipeCategory category) {
        if (!speedCraftAreaSupplierMap.containsKey(category.getResourceLocation()))
            return bounds -> {
                return new Rectangle((int) bounds.getMaxX() - 16, (int) bounds.getMaxY() - 16, 10, 10);
            };
        return speedCraftAreaSupplierMap.get(category.getResourceLocation());
    }
    
    public static void registerSpeedCraftButtonArea(ResourceLocation category, SpeedCraftAreaSupplier rectangle) {
        speedCraftAreaSupplierMap.put(category, rectangle);
    }
    
    public static List<SpeedCraftFunctional> getSpeedCraftFunctional(IRecipeCategory category) {
        if (speedCraftFunctionalMap.get(category.getResourceLocation()) == null)
            return Lists.newArrayList();
        return speedCraftFunctionalMap.get(category.getResourceLocation());
    }
    
    public static void registerSpeedCraftFunctional(ResourceLocation category, SpeedCraftFunctional functional) {
        List<SpeedCraftFunctional> list = speedCraftFunctionalMap.containsKey(category) ? new LinkedList<>(speedCraftFunctionalMap.get(category)) : Lists.newLinkedList();
        list.add(functional);
        speedCraftFunctionalMap.put(category, list);
    }
    
    @Override
    public void recipesLoaded(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
        this.recipeCategoryListMap.clear();
        this.categories.clear();
        this.speedCraftAreaSupplierMap.clear();
        this.speedCraftFunctionalMap.clear();
        List<IRecipePlugin> plugins = new LinkedList<>(RoughlyEnoughItemsPlugin.getPlugins());
        plugins.sort((first, second) -> {
            return second.getPriority() - first.getPriority();
        });
        RoughlyEnoughItemsCore.LOGGER.info("Loading %d REI plugins: %s", plugins.size(), String.join(", ", plugins.stream().map(plugin -> {
            ResourceLocation ResourceLocation = RoughlyEnoughItemsPlugin.getPluginResourceLocation(plugin);
            return ResourceLocation == null ? "NULL" : ResourceLocation.toString();
        }).collect(Collectors.toList())));
        Collections.reverse(plugins);
        plugins.forEach(plugin -> {
            plugin.registerPluginCategories();
            plugin.registerRecipes();
            plugin.registerSpeedCraft();
        });
        RoughlyEnoughItemsCore.LOGGER.info("Registered REI Categories: " + String.join(", ", categories.stream().map(category -> {
            return category.getCategoryName();
        }).collect(Collectors.toList())));
    }
    
}