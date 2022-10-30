package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
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
    @Shadow
    @Final
    @Mutable
    private static RecipeBookGroup[] field_1805;

    @Unique
    private static List<RecipeBookGroup> CRAFTING_SEARCH_MAP;
    @Unique
    private static List<RecipeBookGroup> CRAFTING_MAP;

    // pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work
    @SuppressWarnings("unchecked")
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"), remap = false, method = "<clinit>")
    private static <E> ImmutableList<E> listOf(E e1, E e2, E e3, E e4) {
        if (e1 == RecipeBookGroup.CRAFTING_EQUIPMENT) {
            return (ImmutableList<E>) ImmutableList.copyOf(CRAFTING_SEARCH_MAP);
        }
        return ImmutableList.of(e1, e2, e3, e4);
    }

    @Inject(at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;field_1805:[Lnet/minecraft/client/recipebook/RecipeBookGroup;", shift = At.Shift.AFTER), method = "<clinit>")
    private static void recipe_book_is_pain$addCustomGroups(CallbackInfo ci) {
        ArrayList<RecipeBookGroup> groups = new ArrayList<>(Arrays.asList(field_1805));
        RecipeBookGroup last = groups.get(groups.size() - 1);

        for (ItemGroup group : ItemGroup.GROUPS) {
            if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH) {
                String name = "P_CRAFTING_" + group.getIndex();
                RecipeBookGroup group1 = Accessor.newGroup(name, last.ordinal() + 1, new ItemStack(group.getIcon().getItem()));
                RecipeBookIsPainClient.ADDED_GROUPS.put(name, group1);
                RecipeBookIsPainClient.AAAAAAAA.put(name, group);
                groups.add(group1);
            }
        }

        List<RecipeBookGroup> craftingMap = new ArrayList<>();
        List<RecipeBookGroup> craftingSearchMap = new ArrayList<>();
        craftingMap.add(RecipeBookGroup.CRAFTING_SEARCH);
        for (RecipeBookGroup bookGroup : groups) {
            if (bookGroup.toString().contains("P_CRAFTING")) {
                craftingMap.add(bookGroup);
                craftingSearchMap.add(bookGroup);
            }
        }
        CRAFTING_SEARCH_MAP = craftingSearchMap;
        CRAFTING_MAP = craftingMap;

        field_1805 = groups.toArray(new RecipeBookGroup[0]);
        RecipeBookIsPainClient.LOGGER.info("[RBIP] recipe book init complete");
    }

    @Inject(at = @At("HEAD"), method = "method_30285", cancellable = true)
    private static void recipe_book_is_pain$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_MAP);
    }
}
