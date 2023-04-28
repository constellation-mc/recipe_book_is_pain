package me.melontini.recipebookispain.mixin.groups;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import me.melontini.recipebookispain.util.EnumUtils;
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

import static me.melontini.recipebookispain.RecipeBookIsPainClient.CRAFTING_LIST;
import static me.melontini.recipebookispain.RecipeBookIsPainClient.CRAFTING_SEARCH_LIST;

@Mixin(value = ClientRecipeBook.class, priority = 999)
public class ClientRecipeBookMixin {
    @Inject(at = @At("HEAD"), method = "<clinit>")
    private static void rbip$clinit(CallbackInfo ci) {
        CRAFTING_SEARCH_LIST = new ArrayList<>();
        CRAFTING_LIST = new ArrayList<>();

        Arrays.stream(ItemGroup.GROUPS).filter(itemGroup -> itemGroup != ItemGroup.HOTBAR && itemGroup != ItemGroup.INVENTORY && itemGroup != ItemGroup.SEARCH)
                .forEach(itemGroup -> {
                    try {
                        String name = "P_CRAFTING_" + itemGroup.getIndex();
                        RecipeBookGroup recipeBookGroup = EnumUtils.callRecipeBookEnumInvoker(name, itemGroup.getIcon());
                        RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, itemGroup);
                        RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(itemGroup, recipeBookGroup);

                        CRAFTING_LIST.add(recipeBookGroup);
                        CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                    } catch (Exception e) {
                        String info;
                        try {
                            info = String.format("%s item group", itemGroup.getName());
                        } catch (Throwable t) {
                            try {
                                info = String.format("%s item group", itemGroup.getTranslationKey().asString());
                            } catch (Throwable t1) {
                                try {
                                    info = String.format("item group with %s icon", itemGroup.getIcon().toString());
                                } catch (Throwable t2) {
                                    info = "item group";
                                }
                            }
                        }
                        RecipeBookIsPainClient.LOGGER.error(String.format("Error while processing %s", info), e);
                    }
                });
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);

        RecipeBookGroup.field_25783.get(RecipeBookGroup.CRAFTING_SEARCH).clear();
        RecipeBookGroup.field_25783.get(RecipeBookGroup.CRAFTING_SEARCH).addAll(CRAFTING_SEARCH_LIST);
        RecipeBookIsPainClient.LOGGER.info("[RBIP] recipe book init complete");
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