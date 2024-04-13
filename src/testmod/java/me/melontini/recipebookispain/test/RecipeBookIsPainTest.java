package me.melontini.recipebookispain.test;

import me.melontini.handytests.client.ClientTestContext;
import me.melontini.handytests.client.ClientTestEntrypoint;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class RecipeBookIsPainTest implements ClientTestEntrypoint {

    @Override
    public void onClientTest(ClientTestContext context) {
        context.sendCommand("gamemode survival");
        context.sendCommand("difficulty peaceful");
        context.sendCommand("recipe give @s *");
        context.openInventory();
        context.waitForWorldTicks(40);

        context.openInventory();
        context.executeForScreen(InventoryScreen.class, (client, screen) -> {
            if (!screen.getRecipeBookWidget().isOpen()) {
                ((InventoryScreenAccessor) screen).rbip$pressRecipeBookButton();
            }
            return null;
        });
        context.takeScreenshot("recipe-book-open");
        context.executeForScreen(InventoryScreen.class, (client, screen) -> {
            if (screen.getRecipeBookWidget().isOpen()) {
                ((InventoryScreenAccessor) screen).rbip$pressRecipeBookButton();
            }
            return null;
        });
        context.closeScreen();

        context.sendCommand("gamemode creative");
        context.waitForWorldTicks(20);
    }
}
