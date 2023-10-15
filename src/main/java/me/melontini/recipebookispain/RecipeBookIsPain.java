package me.melontini.recipebookispain;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPain {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");
    public static final boolean isOwOLoaded = FabricLoader.getInstance().isModLoaded("owo");
    public static final boolean isDarkMatterContentLoaded = FabricLoader.getInstance().isModLoaded("dark-matter-content");

    public static List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();
    public static List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();
    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    public static ItemGroup toItemGroup(RecipeBookGroup recipeBookGroup) {
        return RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(recipeBookGroup);
    }

    public static RecipeBookGroup toRecipeBookGroup(ItemGroup itemGroup) {
        return ITEM_GROUP_TO_RECIPE_BOOK_GROUP.get(itemGroup);
    }

    public static boolean renderDarkMatter(MatrixStack matrices, int i, RecipeGroupButtonWidget widget, ItemGroup group) {
        if (group.dm$shouldAnimateIcon()) {
            group.dm$getIconAnimation().animateIcon(group, matrices, widget.x + 9 + i, widget.y + 5, widget.isToggled(), false);
            return true;
        }
        return false;
    }
}
