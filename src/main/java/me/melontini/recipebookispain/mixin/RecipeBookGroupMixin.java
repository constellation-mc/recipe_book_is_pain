package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.apache.commons.lang3.ArrayUtils;
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

@Mixin(RecipeBookGroup.class)
@Unique
public class RecipeBookGroupMixin {
    //me when I can't use Fabric ASM

    @Invoker("<init>")
    static RecipeBookGroup newGroup(String internalName, int internalId, ItemStack... stacks) {
        throw new IllegalStateException();
    }

    @Shadow
    @Final
    @Mutable
    private static RecipeBookGroup[] field_1805;

    @Unique
    private static List<RecipeBookGroup> CRAFTING_SEARCH_LIST;
    @Unique
    private static List<RecipeBookGroup> CRAFTING_LIST;

    // pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work
    @SuppressWarnings("unchecked")
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"), remap = false, method = "<clinit>")
    private static <E> ImmutableList<E> rbip$listOf(E e1, E e2, E e3, E e4) {
        if (e1 == RecipeBookGroup.CRAFTING_EQUIPMENT) {
            return (ImmutableList<E>) ImmutableList.copyOf(CRAFTING_SEARCH_LIST);
        }
        return ImmutableList.of(e1, e2, e3, e4);
    }

    @Inject(at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;field_1805:[Lnet/minecraft/client/recipebook/RecipeBookGroup;", shift = At.Shift.AFTER), method = "<clinit>")
    private static void rbip$addCustomGroups(CallbackInfo ci) {
        CRAFTING_SEARCH_LIST = new ArrayList<>();
        CRAFTING_LIST = new ArrayList<>();

        Arrays.stream(ItemGroup.GROUPS).filter(itemGroup -> itemGroup != ItemGroup.HOTBAR && itemGroup != ItemGroup.INVENTORY && itemGroup != ItemGroup.SEARCH)
                .forEach(itemGroup -> {
                    String name = "P_CRAFTING_" + itemGroup.getIndex();
                    var recipeBookGroup = rbip$extend(name, itemGroup.getIcon());
                    RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, itemGroup);
                    RecipeBookIsPainClient.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(itemGroup, recipeBookGroup);

                    CRAFTING_LIST.add(recipeBookGroup);
                    CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                });
        CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);
        RecipeBookIsPainClient.LOGGER.info("[RBIP] recipe book init complete");
    }

    private static RecipeBookGroup rbip$extend(String internalName, ItemStack... stacks) {
        RecipeBookGroup last = field_1805[field_1805.length - 1];
        RecipeBookGroup enumConst = newGroup(internalName, last.ordinal() + 1, stacks);
        field_1805 = ArrayUtils.add(field_1805, enumConst);
        return enumConst;
    }

    @Inject(at = @At("HEAD"), method = "getGroups", cancellable = true)
    private static void rbip$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_LIST);
    }
}
