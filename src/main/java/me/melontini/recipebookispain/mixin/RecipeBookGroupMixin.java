package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.melontini.recipebookispain.client.RecipeBookIsPainClient.CRAFTING_LIST;
import static me.melontini.recipebookispain.client.RecipeBookIsPainClient.CRAFTING_SEARCH_LIST;

@Mixin(RecipeBookGroup.class)
@Unique
public class RecipeBookGroupMixin {
    @Shadow
    @Final
    @Mutable
    private static RecipeBookGroup[] field_1805;

    @Invoker("<init>")
    static RecipeBookGroup newGroup(String internalName, int internalId, ItemStack... stacks) {
        throw new AssertionError();
    }

    // pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work
    @SuppressWarnings("unchecked")
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"), remap = false, method = "<clinit>")
    private static <E> ImmutableList<E> listOf(E e1, E e2, E e3, E e4) {
        if (e1 == RecipeBookGroup.CRAFTING_EQUIPMENT) {
            return (ImmutableList<E>) ImmutableList.copyOf(CRAFTING_SEARCH_LIST);
        }
        return ImmutableList.of(e1, e2, e3, e4);
    }

    @Inject(at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;field_1805:[Lnet/minecraft/client/recipebook/RecipeBookGroup;", shift = At.Shift.AFTER), method = "<clinit>")
    private static void recipe_book_is_pain$addCustomGroups(CallbackInfo ci) {
        var groups = new ArrayList<>(Arrays.asList(field_1805));
        var last = groups.get(groups.size() - 1);

        for (ItemGroup group : ItemGroups.getGroups()) {
            if (group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH) {
                String name = "P_CRAFTING_" + ItemGroups.getGroups().indexOf(group);

                var recipeBookGroup = newGroup(name, last.ordinal() + 1, group.getIcon());
                RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, group);
                RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(group, recipeBookGroup);

                CRAFTING_LIST.add(recipeBookGroup);
                CRAFTING_SEARCH_LIST.add(recipeBookGroup);

                groups.add(recipeBookGroup);
            }
        }
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);
        CRAFTING_LIST.add(RecipeBookGroup.CRAFTING_MISC);
        CRAFTING_SEARCH_LIST.add(RecipeBookGroup.CRAFTING_MISC);

        field_1805 = groups.toArray(RecipeBookGroup[]::new);
        RecipeBookIsPainClient.LOGGER.info("[RBIP] recipe book init complete");
    }

    @Inject(at = @At("HEAD"), method = "getGroups", cancellable = true)
    private static void recipe_book_is_pain$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_LIST);
    }
}
