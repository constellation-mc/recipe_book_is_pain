package me.melontini.recipebookispain;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPainClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");
    public static final boolean isOwOLoaded = FabricLoader.getInstance().isModLoaded("owo");
    public static final boolean isDarkMatterContentLoaded = FabricLoader.getInstance().isModLoaded("dark-matter-content");
    public static List<RecipeBookGroup> CRAFTING_SEARCH_LIST;
    public static List<RecipeBookGroup> CRAFTING_LIST;
    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    @Override
    public void onInitializeClient() {
    }

    public static boolean renderDarkMatter(MatrixStack matrices, int i, RecipeGroupButtonWidget widget, ItemGroup group) {
        if (group.dm$shouldAnimateIcon()) {
            group.dm$getIconAnimation().animateIcon(matrices, widget.x + 9 + i, widget.y + 5, widget.isToggled(), false);
            return true;
        }
        return false;
    }

}
