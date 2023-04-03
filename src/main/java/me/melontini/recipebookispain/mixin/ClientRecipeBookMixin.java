package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {
    @Inject(at = @At("HEAD"), method = "<clinit>")
    private static void rbip$clinit(CallbackInfo ci) {
        RecipeBookGroup.values();
    }
    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void rbip$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        RecipeType<?> recipeType = recipe.getType();
        if (recipeType == RecipeType.CRAFTING) {
            ItemStack itemStack = recipe.getOutput();
            ItemGroup group = itemStack.getItem().getGroup();
            if (group != null) {
                if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH) {
                    if (RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.containsKey(group)) {
                        cir.setReturnValue(RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.get(group));
                        return;
                    }
                }
            }
            cir.setReturnValue(RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.get(ItemGroup.MISC));
        }
    }
}
