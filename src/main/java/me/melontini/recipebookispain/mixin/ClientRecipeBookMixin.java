package me.melontini.recipebookispain.mixin;

import me.melontini.crackerutil.CrackerLog;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {

    @Inject(at = @At("HEAD"), method = "<clinit>")
    private static void recipe_book_is_pain$clinit(CallbackInfo ci) {
        List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();
        List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();

        Arrays.stream(ItemGroup.GROUPS).filter(itemGroup -> itemGroup != ItemGroup.HOTBAR && itemGroup != ItemGroup.INVENTORY && itemGroup != ItemGroup.SEARCH)
                .forEach(itemGroup -> {
                    String name = "P_CRAFTING_" + itemGroup.getIndex();
                    RecipeBookGroup recipeBookGroup = (RecipeBookGroup) RecipeBookGroup.CRAFTING_SEARCH.extend(name, (Object[]) new ItemStack[]{itemGroup.getIcon()});
                    RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, itemGroup);
                    RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(itemGroup, recipeBookGroup);

                    CRAFTING_LIST.add(recipeBookGroup);
                    CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                });
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);

        RecipeBookGroup.SEARCH_MAP.get(RecipeBookGroup.CRAFTING_SEARCH).clear();
        RecipeBookGroup.SEARCH_MAP.get(RecipeBookGroup.CRAFTING_SEARCH).addAll(CRAFTING_SEARCH_LIST);
        RecipeBookGroup.CRAFTING.clear();
        RecipeBookGroup.CRAFTING.addAll(CRAFTING_LIST);
        CrackerLog.info("Finished setting up recipe book groups!");
    }

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void recipe_book_is_pain$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
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
