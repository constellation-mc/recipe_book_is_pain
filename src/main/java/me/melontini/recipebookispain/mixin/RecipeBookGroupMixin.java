package me.melontini.recipebookispain.mixin;

import net.minecraft.client.recipebook.RecipeBookGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

import static me.melontini.recipebookispain.client.RecipeBookIsPainClient.CRAFTING_LIST;

@Mixin(RecipeBookGroup.class)
@Unique
public class RecipeBookGroupMixin {
    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;CRAFTING:Ljava/util/List;"), method = "getGroups")
    private static List<RecipeBookGroup> recipe_book_is_pain$getGroups() {
        return CRAFTING_LIST;
    }
}