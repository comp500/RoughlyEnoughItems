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

package me.shedaniel.rei.api;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Container;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface AutoTransferHandler {
    
    /**
     * @return the priority of this handler, higher priorities will be called first.
     */
    default double getPriority() {
        return 0d;
    }
    
    Result handle(Context context);
    
    interface Result {
        static Result createSuccessful() {
            return new ResultImpl();
        }

        static Result createSuccessfulReturningToScreen() {
            return new ResultImpl(true, true, true);
        }

        static Result createNotApplicable() {
            return new ResultImpl(false);
        }
        
        static Result createFailed(String errorKey) {
            return new ResultImpl(errorKey, new IntArrayList(), 1744764928);
        }
        
        static Result createFailedCustomButtonColor(String errorKey, int color) {
            return new ResultImpl(errorKey, new IntArrayList(), color);
        }
        
        static Result createFailed(String errorKey, IntList redSlots) {
            return new ResultImpl(errorKey, redSlots, 1744764928);
        }
        
        static Result createFailedCustomButtonColor(String errorKey, IntList redSlots, int color) {
            return new ResultImpl(errorKey, redSlots, color);
        }
        
        int getColor();
        
        boolean isSuccessful();

        /**
         * Applicable if {@link #isSuccessful()} is true. Will return
         * to the previous screen rather than staying open.
         */
        boolean isReturningToScreen();
        
        boolean isApplicable();
        
        String getErrorKey();
        
        IntList getIntegers();
    }
    
    interface Context {
        static Context create(boolean actuallyCrafting, ContainerScreen<?> containerScreen, RecipeDisplay recipeDisplay) {
            return new ContextImpl(actuallyCrafting, containerScreen, () -> recipeDisplay);
        }
        
        default MinecraftClient getMinecraft() {
            return MinecraftClient.getInstance();
        }
        
        boolean isActuallyCrafting();
        
        ContainerScreen<?> getContainerScreen();
        
        @Deprecated
        default ContainerScreen<?> getHandledScreen() {
            return getContainerScreen();
        }
        
        RecipeDisplay getRecipe();
        
        @Deprecated
        default Container getScreenHandler() {
            return getContainer();
        }
        
        default Container getContainer() {
            return getHandledScreen().getContainer();
        }
    }
    
    @ApiStatus.Internal
    final class ResultImpl implements Result {
        private boolean successful, applicable, returningToScreen;
        private String errorKey;
        private IntList integers = new IntArrayList();
        private int color;
        
        private ResultImpl() {
            this(true, true, false);
        }
        
        public ResultImpl(boolean applicable) {
            this(false, applicable, false);
        }

        public ResultImpl(boolean successful, boolean applicable, boolean returningToScreen) {
            this.successful = successful;
            this.applicable = applicable;
            this.returningToScreen = returningToScreen;
        }

        public ResultImpl(String errorKey, IntList integers, int color) {
            this.successful = false;
            this.applicable = true;
            this.errorKey = errorKey;
            if (integers != null)
                this.integers = integers;
            this.color = color;
        }
        
        @Override
        public int getColor() {
            return color;
        }
        
        @Override
        public boolean isSuccessful() {
            return successful;
        }

        @Override
        public boolean isApplicable() {
            return applicable;
        }

        @Override
        public boolean isReturningToScreen() {
            return returningToScreen;
        }

        @Override
        public String getErrorKey() {
            return errorKey;
        }
        
        @Override
        public IntList getIntegers() {
            return integers;
        }
    }
    
    @ApiStatus.Internal
    final class ContextImpl implements Context {
        boolean actuallyCrafting;
        ContainerScreen<?> containerScreen;
        Supplier<RecipeDisplay> recipeDisplaySupplier;
        
        private ContextImpl(boolean actuallyCrafting, ContainerScreen<?> containerScreen, Supplier<RecipeDisplay> recipeDisplaySupplier) {
            this.actuallyCrafting = actuallyCrafting;
            this.containerScreen = containerScreen;
            this.recipeDisplaySupplier = recipeDisplaySupplier;
        }
        
        @Override
        public boolean isActuallyCrafting() {
            return actuallyCrafting;
        }
        
        @Override
        public ContainerScreen<?> getContainerScreen() {
            return containerScreen;
        }
        
        @Override
        public RecipeDisplay getRecipe() {
            return recipeDisplaySupplier.get();
        }
    }
    
}
