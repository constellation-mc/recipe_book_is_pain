package me.melontini.recipebookispain.mixin.widget;

import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeGroupButtonWidget.class)
public class RecipeGroupButtonMixin implements RecipeGroupButtonAccess {
    @Unique
    private int page = -1;

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }
}