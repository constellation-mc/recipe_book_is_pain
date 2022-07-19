package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import me.melontini.recipebookispain.RecipeBookIsPain;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraftforge.client.RecipeBookRegistry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = RecipeBookGroup.class, priority = 666)
@Unique
public abstract class RecipeBookGroupMixin {

    @Unique
    private static List<RecipeBookGroup> CRAFTING_MAP;

    @Inject(at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;field_1805:[Lnet/minecraft/client/recipebook/RecipeBookGroup;", shift = At.Shift.AFTER), method = "<clinit>")
    private static void recipe_book_is_pain$addCustomGroups(CallbackInfo ci) {
        //RecipeBookIsPain.LOGGER.info("Adding to RecipeBookGroup enum");
        for (ItemGroup group : ItemGroup.GROUPS) {
            if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH && group != null) {
                String name = "P_CRAFTING_" + group.getIndex();
                RecipeBookGroup.create(name, new ItemStack(group.getIcon().getItem()));
                var group1 = RecipeBookGroup.valueOf(RecipeBookGroup.class, name);
                RecipeBookIsPain.ADDED_GROUPS.put(name, group1);
                RecipeBookIsPain.AAAAAAAA.put(name, group);
            }
        }

        var groups = new ArrayList<>(Arrays.asList(RecipeBookGroup.values()));

        Arrays.stream(RecipeBookGroup.values()).toList().forEach(group ->
                RecipeBookIsPain.LOGGER.info(group.name()));

        List<RecipeBookGroup> craftingMap = new ArrayList<>();
        List<RecipeBookGroup> craftingSearchMap = new ArrayList<>();
        craftingMap.add(RecipeBookGroup.CRAFTING_SEARCH);
        for (RecipeBookGroup bookGroup : groups) {
            if (bookGroup.toString().contains("P_CRAFTING")) {
                craftingMap.add(bookGroup);
                craftingSearchMap.add(bookGroup);
            }
        }

        RecipeBookIsPain.CRAFTING_SEARCH_MAP = craftingSearchMap;
        CRAFTING_MAP = craftingMap;

        RecipeBookRegistry.addCategoriesToType(RecipeBookCategory.CRAFTING, CRAFTING_MAP);
        RecipeBookIsPain.LOGGER.info("recipe book init complete");
    }

    @Inject(at = @At("HEAD"), method = "getGroups", cancellable = true)
    private static void recipe_book_is_pain$getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_MAP);
    }

}
