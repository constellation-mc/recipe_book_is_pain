package me.melontini.recipebookispain.mixin.widget;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = RecipeBookWidget.class, priority = 1001)
public class RecipeBookTooltipMixin {
    @Shadow protected MinecraftClient client;

    @Shadow @Final private List<RecipeGroupButtonWidget> tabButtons;

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;popMatrix()V", shift = At.Shift.BEFORE), method = "render")
    private void rbip$renderTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (client.currentScreen != null) {
            this.tabButtons.stream().filter(widget -> widget.visible && widget.isHovered()).forEach(widget -> {
                if (widget.getCategory().name().contains("_SEARCH")) {
                    client.currentScreen.renderTooltip(matrices, ItemGroup.SEARCH.getTranslationKey(), mouseX, mouseY);
                } else {
                    if (RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.containsKey(widget.getCategory())) {
                        Text text = RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(widget.getCategory()).getTranslationKey();
                        if (text != null) {
                            client.currentScreen.renderTooltip(matrices, text, mouseX, mouseY);
                        }
                    }
                }
            });
        }
    }
}