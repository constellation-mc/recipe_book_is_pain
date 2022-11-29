package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.access.ItemAccess;
import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void clinit(CallbackInfo ci) {
        ItemGroups.updateDisplayParameters(FeatureSet.of(FeatureFlags.BUNDLE, FeatureFlags.UPDATE_1_20, FeatureFlags.VANILLA), true);
        for (ItemGroup group : ItemGroups.getGroups()) {
            if (group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH) {
                group.getSearchTabStacks().forEach(stack -> {
                    if (((ItemAccess) stack.getItem()).rbip$getPossibleGroup() == ItemGroups.getDefaultTab()) {
                        ((ItemAccess) stack.getItem()).rbip$setPossibleGroup(group);
                    }
                });
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void recipe_book_is_pain$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        if (recipe instanceof CraftingRecipe) {
            ItemStack itemStack = recipe.getOutput();
            ItemGroup group = ((ItemAccess)itemStack.getItem()).rbip$getPossibleGroup();
            if (group != null) {
                if (group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH) {
                    if (RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.get(group) != null) {
                        cir.setReturnValue(RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.get(group));
                        return;
                    }
                }
            }
            cir.setReturnValue(RecipeBookGroup.CRAFTING_MISC);
        }
    }
}
