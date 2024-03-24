package me.melontini.recipebookispain.mixin.groups;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.recipebookispain.RecipeBookIsPain;
import me.melontini.recipebookispain.access.ItemAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.melontini.recipebookispain.RecipeBookIsPain.*;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {

    @Unique
    private static boolean rbip$firstReload = true;

    @Inject(at = @At("HEAD"), method = "reload")
    private void rbip$reload(CallbackInfo ci, @Local(argsOnly = true) DynamicRegistryManager manager) {
        if (!rbip$firstReload) return;
        ItemGroups.updateDisplayContext(FeatureFlags.FEATURE_MANAGER.getFeatureSet(), false, manager);

        ItemGroups.getGroups().stream().filter(itemGroup -> itemGroup.getType() != ItemGroup.Type.INVENTORY && itemGroup.getType() != ItemGroup.Type.HOTBAR && itemGroup.getType() != ItemGroup.Type.SEARCH)
                .forEach(group -> group.getSearchTabStacks().stream().filter(stack -> ((ItemAccess) stack.getItem()).rbip$getPossibleGroup().isEmpty())
                        .forEach(stack -> ((ItemAccess) stack.getItem()).rbip$setPossibleGroup(group)));

        ItemGroups.getGroups().stream().filter(itemGroup -> itemGroup.getType() != ItemGroup.Type.HOTBAR && itemGroup.getType() != ItemGroup.Type.INVENTORY && itemGroup.getType() != ItemGroup.Type.SEARCH)
                .forEach(itemGroup -> {
                    try {
                        RecipeBookGroup recipeBookGroup = RecipeBookHelper.createGroup(new Identifier("rbip", "crafting_" + ItemGroups.getGroups().indexOf(itemGroup)), itemGroup.getIcon());
                        RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, itemGroup);

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
            var nh = MinecraftClient.getInstance().getNetworkHandler();
            if (nh == null) return;

            ItemStack itemStack = recipe.getOutput(nh.getRegistryManager());
            ((ItemAccess) itemStack.getItem()).rbip$getPossibleGroup()
                    .filter(group -> group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH)
                    .map(RecipeBookIsPain::toRecipeBookGroup)
                    .ifPresentOrElse(cir::setReturnValue, () -> cir.setReturnValue(toRecipeBookGroup(Registries.ITEM_GROUP.get(ItemGroups.BUILDING_BLOCKS))));
        }
    }
}
