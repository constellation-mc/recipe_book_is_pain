package me.melontini.recipebookispain.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPainClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");
    public static final List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();
    public static final List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();
    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    @Override
    public void onInitializeClient() {
    }
}
