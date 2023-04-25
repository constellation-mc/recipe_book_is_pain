package me.melontini.recipebookispain;


import io.wispforest.owo.itemgroup.OwoItemGroup;
import me.melontini.crackerutil.util.PrependingLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPainClient implements ClientModInitializer {
    public static final boolean isOwOLoaded = FabricLoader.getInstance().isModLoaded("owo");
    public static final boolean isCrackerContentLoaded = FabricLoader.getInstance().isModLoaded("cracker-util-content");
    public static final PrependingLogger LOGGER = new PrependingLogger(LogManager.getLogger("RBIP"), PrependingLogger.LOGGER_NAME);
    public static Map<RecipeBookGroup, ItemGroup> RECIPE_BOOK_GROUP_TO_ITEM_GROUP = new HashMap<>();
    public static Map<ItemGroup, RecipeBookGroup> ITEM_GROUP_TO_RECIPE_BOOK_GROUP = new HashMap<>();

    public static boolean rbip$renderOwo(MatrixStack matrices, int i, RecipeGroupButtonWidget widget, ItemGroup group) {
        if (group instanceof OwoItemGroup owoItemGroup) {
            MinecraftClient client = MinecraftClient.getInstance();
            double e = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double f = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
            owoItemGroup.icon().render(matrices, widget.getX() + 9 + i, widget.getY() + 5, (int) e, (int) f, client.getTickDelta());
            return true;
        }
        return false;
    }

    public static boolean renderCracker(MatrixStack matrices, int i, RecipeGroupButtonWidget widget, ItemGroup group) {
        if (group.shouldAnimateIcon()) {
            group.getIconAnimation().animateIcon(matrices, widget.getX() + 9 + i, widget.getY() + 5, widget.isToggled(), false);
            return true;
        }
        return false;
    }

    @Override
    public void onInitializeClient() {
    }
}
