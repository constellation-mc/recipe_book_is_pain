package me.melontini.recipebookispain.mixin.groups;

import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static me.melontini.recipebookispain.RecipeBookIsPain.CRAFTING_LIST;

@Mixin(RecipeBookGroup.class)
@Unique
public class RecipeBookGroupMixin {

    @Inject(at = @At("HEAD"), method = "getGroups", cancellable = true)
    private static void rbip$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_LIST);
    }
}
