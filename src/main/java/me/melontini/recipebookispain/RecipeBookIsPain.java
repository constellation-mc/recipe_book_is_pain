package me.melontini.recipebookispain;

import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
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
    public static Map<String, RecipeBookGroup> ADDED_GROUPS = new HashMap<>();
    public static Map<String, ItemGroup> AAAAAAAA = new HashMap<>();

    public static List<RecipeBookGroup> CRAFTING_SEARCH_MAP = new ArrayList<>();
    public static List<RecipeBookGroup> CRAFTING_MAP = new ArrayList<>();

    public RecipeBookIsPain() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, false, RegisterRecipeBookCategoriesEvent.class, registerRecipeBookCategoriesEvent -> {
            List<RecipeBookGroup> groups = new ArrayList<>();

            for (ItemGroup group : ItemGroup.GROUPS) {
                if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH) {
                    String name = "P_CRAFTING_" + group.getIndex();
                    var group1 = RecipeBookGroup.create(name, group.getIcon());
                    RecipeBookIsPain.ADDED_GROUPS.put(name, group1);
                    RecipeBookIsPain.AAAAAAAA.put(name, group);
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
            RecipeBookIsPain.CRAFTING_SEARCH_MAP.addAll(craftingSearchMap);
            CRAFTING_MAP.addAll(craftingMap);

            registerRecipeBookCategoriesEvent.aggregateCategories.remove(RecipeBookGroup.CRAFTING_SEARCH);
            registerRecipeBookCategoriesEvent.registerAggregateCategory(RecipeBookGroup.CRAFTING_SEARCH, CRAFTING_SEARCH_MAP);
            RecipeBookIsPain.LOGGER.info("[RBIP] recipe book init complete");
        });
    }
}
