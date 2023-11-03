package me.melontini.recipebookispain.mixin.groups;

import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.recipebookispain.FeatureMultiverse;
import me.melontini.recipebookispain.RecipeBookIsPain;
import me.melontini.recipebookispain.access.ItemAccess;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
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

import java.util.Optional;

import static me.melontini.recipebookispain.RecipeBookIsPain.*;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {

    @Unique
    private static boolean rbip$firstReload = true;

    @Inject(at = @At("HEAD"), method = "reload")
    private void rbip$reload(CallbackInfo ci) {
        if (!rbip$firstReload) return;
        ItemGroups.updateDisplayParameters(FeatureMultiverse.getFeatureSet(), true);

        ItemGroups.getGroups().stream().filter(itemGroup -> itemGroup.getType() != ItemGroup.Type.INVENTORY && itemGroup.getType() != ItemGroup.Type.HOTBAR && itemGroup.getType() != ItemGroup.Type.SEARCH)
                .forEach(group -> group.getSearchTabStacks().forEach(stack -> ((ItemAccess) stack.getItem()).rbip$setPossibleGroup(group)));

        ItemGroups.getGroups().stream().filter(itemGroup -> itemGroup.getType() != ItemGroup.Type.HOTBAR && itemGroup.getType() != ItemGroup.Type.INVENTORY && itemGroup.getType() != ItemGroup.Type.SEARCH)
                .forEach(itemGroup -> {
                    try {
                        RecipeBookGroup recipeBookGroup = RecipeBookHelper.createGroup(new Identifier("rbip", "crafting_" + ItemGroups.getGroups().indexOf(itemGroup)), itemGroup.getIcon());
                        RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, itemGroup);
                        ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(itemGroup, recipeBookGroup);

                        CRAFTING_LIST.add(recipeBookGroup);
                        CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                    } catch (Exception e) {
                        RecipeBookIsPain.LOGGER.error("Error while processing %s item group".formatted(itemGroup.getDisplayName()), e);
                    }
                });
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);
        CRAFTING_LIST.add(RecipeBookGroup.CRAFTING_MISC);
        CRAFTING_SEARCH_LIST.add(RecipeBookGroup.CRAFTING_MISC);

        RecipeBookGroup.SEARCH_MAP.replace(RecipeBookGroup.CRAFTING_SEARCH, CRAFTING_SEARCH_LIST);
        LOGGER.info("[RBIP] recipe book init complete");
        rbip$firstReload = false;
    }

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void rbip$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        if (RecipeType.CRAFTING.equals(recipe.getType())) {
            ItemStack itemStack = recipe.getOutput(RBIP$THANKS.getCombinedRegistryManager());
            Optional.ofNullable(((ItemAccess) itemStack.getItem()).rbip$getPossibleGroup())
                    .filter(group -> group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH)
                    .map(RecipeBookIsPain::toRecipeBookGroup)
                    .ifPresentOrElse(cir::setReturnValue, () -> cir.setReturnValue(toRecipeBookGroup(ItemGroups.BUILDING_BLOCKS)));
        }
    }
}
