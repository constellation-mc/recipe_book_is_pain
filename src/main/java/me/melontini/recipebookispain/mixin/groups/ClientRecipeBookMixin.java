package me.melontini.recipebookispain.mixin.groups;

import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static me.melontini.recipebookispain.RecipeBookIsPainClient.*;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {

    @Unique
    private static boolean rbip$firstReload = true;

    @Inject(at = @At("HEAD"), method = "reload")
    private void rbip$reload(CallbackInfo ci) {
        if (!rbip$firstReload) return;

        CRAFTING_SEARCH_LIST = new ArrayList<>();
        CRAFTING_LIST = new ArrayList<>();

        Arrays.stream(ItemGroup.GROUPS).filter(itemGroup -> itemGroup != ItemGroup.HOTBAR && itemGroup != ItemGroup.INVENTORY && itemGroup != ItemGroup.SEARCH)
                .forEach(itemGroup -> {
                    try {
                        RecipeBookGroup recipeBookGroup = RecipeBookHelper.createGroup(new Identifier("rbip", "crafting_" + itemGroup.getIndex()), itemGroup.getIcon());
                        RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, itemGroup);
                        ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(itemGroup, recipeBookGroup);

                        CRAFTING_LIST.add(recipeBookGroup);
                        CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                    } catch (Exception e) {
                        RecipeBookIsPainClient.LOGGER.error("Error while processing %s item group".formatted(itemGroup.getName()), e);
                    }
                });
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);

        RecipeBookGroup.SEARCH_MAP.replace(RecipeBookGroup.CRAFTING_SEARCH, CRAFTING_SEARCH_LIST);
        RecipeBookIsPainClient.LOGGER.info("[RBIP] recipe book init complete");
        rbip$firstReload = false;
    }

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void rbip$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        if (RecipeType.CRAFTING.equals(recipe.getType())) {
            ItemStack itemStack = recipe.getOutput();
            Optional.ofNullable(itemStack.getItem().getGroup())
                    .filter(group -> !group.isSpecial() && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH)
                    .map(ITEM_GROUP_TO_RECIPE_BOOK_GROUP::get)
                    .ifPresentOrElse(cir::setReturnValue, () -> cir.setReturnValue(ITEM_GROUP_TO_RECIPE_BOOK_GROUP.get(ItemGroup.MISC)));
        }
    }

}
