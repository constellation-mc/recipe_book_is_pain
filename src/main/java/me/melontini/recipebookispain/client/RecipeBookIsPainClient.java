package me.melontini.recipebookispain.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RecipeBookIsPainClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");

    public static Map<String, RecipeBookGroup> ADDED_GROUPS = new HashMap<>();
    public static Map<String, ItemGroup> AAAAAAAA = new HashMap<>();
    @Override
    public void onInitializeClient() {
        //null pointers are epic
    }
}
