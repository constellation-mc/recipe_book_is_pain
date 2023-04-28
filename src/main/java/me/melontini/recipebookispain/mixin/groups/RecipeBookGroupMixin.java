package me.melontini.recipebookispain.mixin.groups;

import me.melontini.recipebookispain.util.EnumUtils;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.melontini.recipebookispain.RecipeBookIsPainClient.CRAFTING_LIST;

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


    @Shadow
    @Final
    @Mutable
    public static Map<RecipeBookGroup, List<RecipeBookGroup>> field_25783;

    @Unique
    private static RecipeBookGroup rbip$extend(String internalName, ItemStack... stacks) {
        RecipeBookGroup last = field_1805[field_1805.length - 1];
        RecipeBookGroup enumConst = newGroup(internalName, last.ordinal() + 1, stacks);
        field_1805 = ArrayUtils.add(field_1805, enumConst);
        EnumUtils.clearEnumCache(RecipeBookGroup.class);
        return enumConst;
    }

    @Inject(at = @At("HEAD"), method = "method_30285", cancellable = true)
    private static void rbip$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_LIST);
    }

    @Inject(at = @At("TAIL"), method = "<clinit>")
    private static void rbip$makeMutable(CallbackInfo ci) {
        Map<RecipeBookGroup, List<RecipeBookGroup>> groupListMap = new HashMap<>();
        field_25783.forEach((group, groups) -> groupListMap.put(group, new ArrayList<>(groups)));
        field_25783 = groupListMap;
    }
}