package me.melontini.recipebookispain.mixin;

import me.melontini.crackerutil.util.EnumWrapper;
import me.melontini.recipebookispain.RecipeBookIsPainClient;
import me.melontini.recipebookispain.access.ItemAccess;
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

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {
    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void rbip$setupGroups(CallbackInfo ci) {
        ItemGroups.updateDisplayParameters(FeatureSet.of(FeatureFlags.BUNDLE, FeatureFlags.UPDATE_1_20, FeatureFlags.VANILLA), true);

        ItemGroups.getGroups().stream().filter(group -> group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH)
                .forEach(group -> group.getSearchTabStacks().forEach(stack -> {
                    if (((ItemAccess) stack.getItem()).rbip$getPossibleGroup() == ItemGroups.getDefaultTab()) {
                        ((ItemAccess) stack.getItem()).rbip$setPossibleGroup(group);
                    }
                }));

        List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();
        List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();

        ItemGroups.getGroups().stream().filter(group -> group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH)
                .forEach(group -> {
                    String name = "P_CRAFTING_" + ItemGroups.getGroups().indexOf(group);

                    RecipeBookGroup recipeBookGroup = EnumWrapper.RecipeBookGroup.extend(name, group.getIcon());
                    RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, group);
                    RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(group, recipeBookGroup);

                    CRAFTING_LIST.add(recipeBookGroup);
                    CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                });
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);
        CRAFTING_LIST.add(RecipeBookGroup.CRAFTING_MISC);
        CRAFTING_SEARCH_LIST.add(RecipeBookGroup.CRAFTING_MISC);

        RecipeBookGroup.SEARCH_MAP.get(RecipeBookGroup.CRAFTING_SEARCH).clear();
        RecipeBookGroup.SEARCH_MAP.get(RecipeBookGroup.CRAFTING_SEARCH).addAll(CRAFTING_SEARCH_LIST);
        RecipeBookGroup.CRAFTING.clear();
        RecipeBookGroup.CRAFTING.addAll(CRAFTING_LIST);
        RecipeBookIsPainClient.LOGGER.info("done preparing recipe book groups");
    }

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void rbip$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        if (recipe instanceof CraftingRecipe) {
            ItemStack itemStack = recipe.getOutput();
            ItemGroup group = ((ItemAccess) itemStack.getItem()).rbip$getPossibleGroup();
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
