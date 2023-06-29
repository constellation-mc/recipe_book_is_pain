package me.melontini.recipebookispain;

import me.melontini.dark_matter.util.PrependingLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPainClient implements ClientModInitializer {

    public static final PrependingLogger LOGGER = new PrependingLogger(LogManager.getLogger("RBIP"), PrependingLogger.LOGGER_NAME);
    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    @Override
    public void onInitializeClient() {
    }
}
