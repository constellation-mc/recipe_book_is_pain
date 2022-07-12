package me.melontini.recipebookispain.mixin;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookCategories.class)
public interface Accessor {
    @Invoker("<init>")
    static RecipeBookCategories newGroup(String internalName, int internalId, ItemStack... stacks) {
        throw new AssertionError();
    }
}
