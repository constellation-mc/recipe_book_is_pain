package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {
    @Inject(at = @At("HEAD"), method = "getCategory", cancellable = true)
    private static void recipe_book_is_pain$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cir) {
        RecipeType<?> recipeType = recipe.getType();
        if (recipeType == RecipeType.CRAFTING) {
            ItemStack itemStack = recipe.getResultItem();
            CreativeModeTab group = itemStack.getItem().getItemCategory();
            if (group != null) {
                if (group != CreativeModeTab.TAB_HOTBAR && group != CreativeModeTab.TAB_INVENTORY && group != CreativeModeTab.TAB_SEARCH)
                    cir.setReturnValue(RecipeBookIsPainClient.ADDED_GROUPS.get("P_CRAFTING_" + group.getDisplayName().toString().toUpperCase().replace(".", "_")));
            }
        }
    }
}
