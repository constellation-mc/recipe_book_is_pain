package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeGroupButtonWidget.class)
public class RecipeGroupButtonMixin extends ToggleButtonWidget implements RecipeGroupButtonAccess {
    private int page = 0;

    public RecipeGroupButtonMixin(int x, int y, int width, int height, boolean toggled) {
        super(x, y, width, height, toggled);
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }
}
