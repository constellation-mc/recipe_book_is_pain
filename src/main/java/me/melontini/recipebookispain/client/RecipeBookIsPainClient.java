package me.melontini.recipebookispain.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.RecipeBookCategories;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPainClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");

    public static Map<String, RecipeBookCategories> ADDED_GROUPS = new HashMap<>();
    @Override
    public void onInitializeClient() {
    }
}
