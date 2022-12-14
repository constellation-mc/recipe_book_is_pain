package me.melontini.recipebookispain.mixin;

import me.melontini.crackerutil.interfaces.AnimatedItemGroup;
import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.melontini.recipebookispain.client.RecipeBookIsPainClient.rbip$renderOwo;

@Mixin(RecipeGroupButtonWidget.class)
public abstract class RecipeGroupButtonMixin extends ToggleButtonWidget implements RecipeGroupButtonAccess {
    @Shadow
    @Final
    private RecipeBookGroup category;
    @Shadow
    private float bounce;
    @Unique
    private int page = -1;

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

    @Inject(at = @At("HEAD"), method = "renderIcons", cancellable = true)
    private void rbip$render(ItemRenderer itemRenderer, CallbackInfo ci) {
        int i = this.toggled ? -2 : 0;
        MatrixStack matrices = new MatrixStack();
        if (this.bounce > 0.0F) {
            float f = 1.0F + 0.1F * (float) Math.sin(this.bounce / 15.0F * (float) Math.PI);
            matrices.push();
            matrices.translate((float) (this.getX() + 8), (float) (this.getY() + 12), 0.0F);
            matrices.scale(1.0F, f, 1.0F);
            matrices.translate((float) (-(this.getX() + 8)), (float) (-(this.getY() + 12)), 0.0F);
        }
        if (FabricLoader.getInstance().isModLoaded("owo")) {
            if (rbip$renderOwo(matrices, i, (RecipeGroupButtonWidget) (Object) this)) {
                ci.cancel();
                return;
            }
        }
        if (RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.containsKey(this.category)) {
            if (RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(this.category) instanceof AnimatedItemGroup animatedItemGroup) {
                animatedItemGroup.animateIcon(matrices, this.getX() + 9 + i, this.getY() + 5);
                ci.cancel();
            }
        }
    }
}
