package me.melontini.recipebookispain.mixin.widget;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeGroupButtonWidget.class)
public abstract class RecipeGroupButtonMixin extends ToggleButtonWidget {
    @Shadow
    @Final
    private RecipeBookGroup category;
    @Shadow
    private float bounce;

    public RecipeGroupButtonMixin(int x, int y, int width, int height, boolean toggled) {
        super(x, y, width, height, toggled);
    }

    @Inject(at = @At("HEAD"), method = "renderIcons", cancellable = true)
    private void rbip$render(ItemRenderer itemRenderer, CallbackInfo ci) {
        ItemGroup group = RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(this.category);
        if (group == null) return;

        int i = this.toggled ? -2 : 0;
        MatrixStack matrices = new MatrixStack();

        if (this.bounce > 0.0F) {
            float f = 1.0F + 0.1F * (float) Math.sin(this.bounce / 15.0F * (float) Math.PI);
            matrices.push();
            matrices.translate((float) (this.getX() + 8), (float) (this.getY() + 12), 0.0F);
            matrices.scale(1.0F, f, 1.0F);
            matrices.translate((float) (-(this.getX() + 8)), (float) (-(this.getY() + 12)), 0.0F);
        }

        if (RecipeBookIsPainClient.isOwOLoaded) {
            if (RecipeBookIsPainClient.rbip$renderOwo(matrices, i, (RecipeGroupButtonWidget) (Object) this, group)) {
                ci.cancel();
                return;
            }
        }

        if (RecipeBookIsPainClient.isDarkMatterContentLoaded) {
            if (RecipeBookIsPainClient.renderDarkMatter(matrices, i, (RecipeGroupButtonWidget) (Object) this, group)) {
                ci.cancel();
            }
        }

        if (this.bounce > 0.0F) {
            matrices.pop();
        }
    }
}
