package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {
    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void recipe_book_is_pain$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        RecipeType<?> recipeType = recipe.getType();
        if (recipeType == RecipeType.CRAFTING) {
            ItemStack itemStack = recipe.getOutput();
            ItemGroup group = itemStack.getItem().getGroup();
            if (group != null) {
                if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH)
                    if (RecipeBookIsPainClient.ADDED_GROUPS.get("P_CRAFTING_" + group.toString().toUpperCase().replace(".", "_")) != null)
                        cir.setReturnValue(RecipeBookIsPainClient.ADDED_GROUPS.get("P_CRAFTING_" + group.toString().toUpperCase().replace(".", "_")));
                    else
                        cir.setReturnValue(RecipeBookIsPainClient.ADDED_GROUPS.get("P_CRAFTING_" + ItemGroup.MISC.toString().toUpperCase().replace(".", "_")));
            }
        }
    }
}
