package me.melontini.recipebookispain.client;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
public class RecipeBookIsPainClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");
    public static List<RecipeBookGroup> CRAFTING_SEARCH_LIST = new ArrayList<>();
    public static List<RecipeBookGroup> CRAFTING_LIST = new ArrayList<>();
    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    public static boolean rbip$renderOwo(MatrixStack matrices, int i, RecipeGroupButtonWidget widget) {
        if (RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.containsKey(widget.getCategory())) {
            if (RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(widget.getCategory()) instanceof io.wispforest.owo.itemgroup.OwoItemGroup owoItemGroup) {
                MinecraftClient client = MinecraftClient.getInstance();
                double e = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                double f = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
                owoItemGroup.icon().render(matrices, widget.getX() + 9 + i, widget.getY() + 5, (int) e, (int) f, client.getTickDelta());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInitializeClient() {
    }
}
