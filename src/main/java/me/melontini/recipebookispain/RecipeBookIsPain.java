package me.melontini.recipebookispain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPain {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");
    public static final boolean isOwOLoaded = FabricLoader.getInstance().isModLoaded("owo");
    public static final boolean isDarkMatterContentLoaded = FabricLoader.getInstance().isModLoaded("dark-matter-content");

    public static List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();
    public static List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();
    public static BiMap<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = HashBiMap.create();

    public static ItemGroup toItemGroup(RecipeBookGroup recipeBookGroup) {
        return RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(recipeBookGroup);
    }

    public static RecipeBookGroup toRecipeBookGroup(ItemGroup itemGroup) {
        return RECIPE_BOOK_GROUP_TO_ITEM_GROUP.inverse().get(itemGroup);
    }

    public static boolean rbip$renderOwo(DrawContext context, int i, RecipeGroupButtonWidget widget, ItemGroup group) {
        if (group instanceof OwoItemGroup owoItemGroup) {
            MinecraftClient client = MinecraftClient.getInstance();
            double e = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double f = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
            owoItemGroup.icon().render(context, widget.getX() + 9 + i, widget.getY() + 5, (int) e, (int) f, client.getTickDelta());
            return true;
        }
        return false;
    }

    public static boolean renderDarkMatter(DrawContext context, int i, RecipeGroupButtonWidget widget, ItemGroup group) {
        return ItemGroupAnimaton.getIconAnimation(group).map(animation -> {
            animation.animateIcon(group, context, widget.getX() + 9 + i, widget.getY() + 5, widget.isToggled(), false);
            return true;
        }).orElse(false);
    }
}
