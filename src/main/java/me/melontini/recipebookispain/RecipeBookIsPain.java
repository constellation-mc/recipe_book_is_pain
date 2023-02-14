package me.melontini.recipebookispain;

import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod("recipe_book_is_pain")
public class RecipeBookIsPain {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");

    public static List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();
    public static List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();

    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    public RecipeBookIsPain() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, false, RegisterRecipeBookCategoriesEvent.class, registerRecipeBookCategoriesEvent -> {
            for (ItemGroup group : ItemGroups.getGroups()) {
                if (group.getType() != ItemGroup.Type.INVENTORY && group.getType() != ItemGroup.Type.HOTBAR && group.getType() != ItemGroup.Type.SEARCH) {
                    String name = "P_CRAFTING_" + ItemGroups.getGroups().indexOf(group);

                    RecipeBookGroup recipeBookGroup = RecipeBookGroup.create(name, group.getIcon());
                    RecipeBookIsPain.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.put(recipeBookGroup, group);
                    RecipeBookIsPain.ITEM_GROUP_TO_RECIPE_BOOK_GROUP.put(group, recipeBookGroup);

                    CRAFTING_LIST.add(recipeBookGroup);
                    CRAFTING_SEARCH_LIST.add(recipeBookGroup);
                }
            }
            CRAFTING_LIST.add(0, RecipeBookGroup.CRAFTING_SEARCH);
            CRAFTING_LIST.add(RecipeBookGroup.CRAFTING_MISC);
            CRAFTING_SEARCH_LIST.add(RecipeBookGroup.CRAFTING_MISC);

            registerRecipeBookCategoriesEvent.aggregateCategories.remove(RecipeBookGroup.CRAFTING_SEARCH);
            registerRecipeBookCategoriesEvent.registerAggregateCategory(RecipeBookGroup.CRAFTING_SEARCH, CRAFTING_SEARCH_LIST);
            RecipeBookIsPain.LOGGER.info("[RBIP] recipe book init complete");
        });
    }
}
