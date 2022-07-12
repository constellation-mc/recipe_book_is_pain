package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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

@Mixin(RecipeBookCategories.class)
@Unique
public class RecipeBookGroupMixin {
    //me when I can't use Fabric ASM
    @Shadow
    @Final
    @Mutable
    private static RecipeBookCategories[] $VALUES;

    @Unique
    private static List<RecipeBookCategories> CRAFTING_SEARCH_MAP;
    @Unique
    private static List<RecipeBookCategories> CRAFTING_MAP;

    // pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"), remap = false, method = "<clinit>")
    private static <E> ImmutableList<E> listOf(E e1, E e2, E e3, E e4) {
        if (e1 == RecipeBookCategories.CRAFTING_EQUIPMENT) {
            return (ImmutableList<E>) ImmutableList.copyOf(CRAFTING_SEARCH_MAP);
        }
        return ImmutableList.of(e1, e2, e3, e4);
    }

    @Inject(at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/RecipeBookCategories;$VALUES:[Lnet/minecraft/client/RecipeBookCategories;", shift = At.Shift.AFTER), method = "<clinit>")
    private static void recipe_book_is_pain$addCustomGroups(CallbackInfo ci) {
        var groups = new ArrayList<>(Arrays.asList($VALUES));
        var last = groups.get(groups.size() - 1);

        for (CreativeModeTab group : CreativeModeTab.TABS) {
            if (group != CreativeModeTab.TAB_HOTBAR && group != CreativeModeTab.TAB_INVENTORY && group != CreativeModeTab.TAB_SEARCH) {
                var group1 = Accessor.newGroup("P_CRAFTING_" + group.getDisplayName().toString().toUpperCase().replace(".", "_"), last.ordinal() + 1, new ItemStack(group.getIconItem().getItem()));
                RecipeBookIsPainClient.ADDED_GROUPS.put("P_CRAFTING_" + group.getDisplayName().toString().toUpperCase().replace(".", "_"), group1);
                groups.add(group1);
            }
        }
        List<RecipeBookCategories> craftingMap = new ArrayList<>();
        List<RecipeBookCategories> craftingSearchMap = new ArrayList<>();
        craftingMap.add(RecipeBookCategories.CRAFTING_SEARCH);
        for (RecipeBookCategories bookGroup : groups) {
            if (bookGroup.toString().contains("P_CRAFTING")) {
                craftingMap.add(bookGroup);
                craftingSearchMap.add(bookGroup);
            }
        }
        CRAFTING_SEARCH_MAP = craftingSearchMap;
        CRAFTING_MAP = craftingMap;

        $VALUES = groups.toArray(new RecipeBookCategories[0]);
        RecipeBookIsPainClient.LOGGER.info("[RBIP] recipe book init complete");
    }

    @Inject(at = @At("HEAD"), method = "getCategories", cancellable = true)
    private static void recipe_book_is_pain$getGroups(RecipeBookType recipeBookType, CallbackInfoReturnable<List<RecipeBookCategories>> cir) {
        if (recipeBookType == RecipeBookType.CRAFTING) cir.setReturnValue(CRAFTING_MAP);
    }

}
