package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(RecipeBookGroup.class)
@Unique
public class RecipeBookGroupMixin {
    //me when I can't use Fabric ASM
    //So much sinnery here
    //this is a mess btw

    //private final static synthetic [Lnet/minecraft/client/recipebook/RecipeBookGroup; field_1805

    @Shadow
    @Final
    @Mutable
    private static RecipeBookGroup[] field_1805;

    @Unique
    private static List<RecipeBookGroup> CRAFTING_SEARCH_MAP;

    @Unique
    private static List<RecipeBookGroup> CRAFTING_MAP;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;field_1805:[Lnet/minecraft/client/recipebook/RecipeBookGroup;", shift = At.Shift.AFTER))
    private static void addCustomVariant(CallbackInfo ci) {
        var groups = new ArrayList<>(Arrays.asList(field_1805));
        var last = groups.get(groups.size() - 1);

        final Logger LOGGER = LogManager.getLogger("RBIP");

        for (ItemGroup group : ItemGroup.GROUPS) {
            if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH) {
                var group1 = Accessor.newGroup("P_CRAFTING_" + group.getName().toUpperCase().replace(".", "_"), last.ordinal() + 1, new ItemStack[]{new ItemStack(group.getIcon().getItem())});
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
        LOGGER.info(CRAFTING_MAP);

        field_1805 = groups.toArray(new RecipeBookGroup[0]);
    }

    @Final
    @Shadow
    @Mutable
    public static Map<RecipeBookGroup, List<RecipeBookGroup>> SEARCH_MAP = ImmutableMap.of(
            RecipeBookGroup.CRAFTING_SEARCH,
            CRAFTING_SEARCH_MAP,
            RecipeBookGroup.FURNACE_SEARCH,
            ImmutableList.of(RecipeBookGroup.FURNACE_FOOD, RecipeBookGroup.FURNACE_BLOCKS, RecipeBookGroup.FURNACE_MISC),
            RecipeBookGroup.BLAST_FURNACE_SEARCH,
            ImmutableList.of(RecipeBookGroup.BLAST_FURNACE_BLOCKS, RecipeBookGroup.BLAST_FURNACE_MISC),
            RecipeBookGroup.SMOKER_SEARCH,
            ImmutableList.of(RecipeBookGroup.SMOKER_FOOD));

    @Inject(at = @At("HEAD"), method = "getGroups", cancellable = true)
    private static void getGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.CRAFTING) cir.setReturnValue(CRAFTING_MAP);
    }

}
