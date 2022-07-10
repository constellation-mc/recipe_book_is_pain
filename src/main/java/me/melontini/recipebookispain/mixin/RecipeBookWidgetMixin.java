package me.melontini.recipebookispain.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Shadow
    protected MinecraftClient client;
    @Shadow
    private int parentHeight;
    @Shadow
    private int leftOffset;
    @Shadow
    private ClientRecipeBook recipeBook;
    @Shadow
    @Final
    private List<RecipeGroupButtonWidget> tabButtons;

    @Shadow
    private int parentWidth;

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Inject(at = @At("HEAD"), method = "refreshTabButtons", cancellable = true)
    private void refresh(CallbackInfo ci) {
        int i = (this.parentWidth - 147) / 2 - this.leftOffset - 16;
        int j = (this.parentHeight - 166) / 2 + 3;
        int l = 0;
        int p = 0;
        int a;

        //I'm in pain
        for (RecipeGroupButtonWidget recipeGroupButtonWidget : this.tabButtons) {
            RecipeBookGroup recipeBookGroup = recipeGroupButtonWidget.getCategory();
            if (recipeBookGroup == RecipeBookGroup.CRAFTING_SEARCH || recipeBookGroup == RecipeBookGroup.FURNACE_SEARCH) {
                recipeGroupButtonWidget.visible = true;
                recipeGroupButtonWidget.setPos(i, j + 27 * l++);
            } else if (recipeGroupButtonWidget.hasKnownRecipes(recipeBook)) {
                p++;
                a = (int) Math.ceil(p / 6);
                if (l / 6 == 1 | l / 6 == 2 | l / 6 == 3 | l / 6 == 4 | l / 6 == 5) {
                    l = 0;
                }
                recipeGroupButtonWidget.setPos(i - 20 * a, j + 27 * l++);
                recipeGroupButtonWidget.checkForNewRecipes(this.client);
            }
        }
        ci.cancel();
    }
}
