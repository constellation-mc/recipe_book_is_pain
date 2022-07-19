package me.melontini.recipebookispain;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.asm.RuntimeEnumExtender;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod("recipe_book_is_pain")
public class RecipeBookIsPain {

    public static final Logger LOGGER = LogManager.getLogger("RBIP");
    public static Map<String, RecipeBookGroup> ADDED_GROUPS = new HashMap<>();
    public static Map<String, ItemGroup> AAAAAAAA = new HashMap<>();

    public static List<RecipeBookGroup> CRAFTING_SEARCH_MAP;

    public RecipeBookIsPain() {
    }
}
