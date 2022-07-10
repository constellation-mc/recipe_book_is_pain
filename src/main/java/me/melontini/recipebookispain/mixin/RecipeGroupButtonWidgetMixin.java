package me.melontini.recipebookispain.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RecipeGroupButtonWidget.class)
public class RecipeGroupButtonWidgetMixin extends ToggleButtonWidget {
    @Shadow
    @Final
    private RecipeBookGroup category;

    public RecipeGroupButtonWidgetMixin(int x, int y, int width, int height, boolean toggled) {
        super(x, y, width, height, toggled);
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 35, ordinal = 0))
    private static int init(int constant) {
        return 20;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), method = "renderButton")
    private void renderButtons(RecipeGroupButtonWidget instance, MatrixStack matrixStack, int x, int y, int i, int j, int width, int height) {
        instance.drawTexture(matrixStack, this.x, y, i, j, width, height);
    }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    private void renderIcons(ItemRenderer itemRenderer) {
        List<ItemStack> list = this.category.getIcons();
        if (list.size() == 1) {
            itemRenderer.renderInGui(list.get(0), this.x + 4, this.y + 5);
        } else if (list.size() == 2) {
            //impossible btw
            itemRenderer.renderInGui(list.get(0), this.x + 2, this.y + 5);
            itemRenderer.renderInGui(list.get(1), this.x + 4, this.y + 5);
        }
    }
}
