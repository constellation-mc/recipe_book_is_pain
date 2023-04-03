package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.fabricmc.loader.api.FabricLoader;
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

import static me.melontini.recipebookispain.RecipeBookIsPainClient.rbip$renderOwo;

@Mixin(RecipeGroupButtonWidget.class)
public abstract class RecipeGroupButtonMixin extends ToggleButtonWidget {
    private static final boolean isOwOLoaded = FabricLoader.getInstance().isModLoaded("owo");
    @Shadow
    @Final
    private RecipeBookGroup category;

    public RecipeGroupButtonMixin(int x, int y, int width, int height, boolean toggled) {
        super(x, y, width, height, toggled);
    }

    @Inject(at = @At("HEAD"), method = "renderIcons", cancellable = true)
    private void rbip$render(MatrixStack matrices, ItemRenderer itemRenderer, CallbackInfo ci) {
        ItemGroup group = RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(this.category);
        if (group == null) return;

        int i = this.toggled ? -2 : 0;

        if (isOwOLoaded) {
            if (rbip$renderOwo(matrices, i, (RecipeGroupButtonWidget) (Object) this, group)) {
                ci.cancel();
                return;
            }
        }
        if (group.shouldAnimateIcon()) {
            group.getIconAnimation().animateIcon(matrices, this.getX() + 9 + i, this.getY() + 5, this.toggled, false);
            ci.cancel();
        }
    }
}