package me.melontini.recipebookispain.mixin;

import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookGroup.class)
public interface Accessor {
    @Invoker("<init>")
    static RecipeBookGroup newGroup(String internalName, int internalId, ItemStack... stacks) {
        throw new AssertionError();
    }
}
