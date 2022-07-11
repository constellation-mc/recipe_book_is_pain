package me.melontini.recipebookispain.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin {
    @Shadow
    @Final
    protected static Identifier TEXTURE;
    @Unique
    private static int page = 0;
    @Unique
    public List<Pair<Integer, RecipeGroupButtonWidget>> groupTab = Lists.newArrayList();
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
    @Unique
    private int pages;
    @Unique
    private ToggleButtonWidget nextPageButton;
    @Unique
    private ToggleButtonWidget prevPageButton;

    @Shadow
    public abstract boolean isOpen();

    @Inject(at = @At("RETURN"), method = "reset")
    private void recipe_book_is_pain$init(CallbackInfo ci) {
        int a = (this.parentWidth - 147) / 2 - this.leftOffset;
        int s = (this.parentHeight + 166) / 2;
        this.nextPageButton = new ToggleButtonWidget(a + 10, s, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(a - 10, s, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE), method = "render")
    private void recipe_book_is_pain$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.prevPageButton.render(matrices, mouseX, mouseY, delta);
        this.nextPageButton.render(matrices, mouseX, mouseY, delta);
        updatePages();
        reloadPages();
    }

    @Unique
    private void reloadPages() {
        int c = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int b = (this.parentHeight - 166) / 2 + 3;
        int l = 0;
        for (Pair<Integer, RecipeGroupButtonWidget> pair : groupTab) {
            RecipeGroupButtonWidget widget = pair.getRight();
            if (pair.getLeft() == page) {
                RecipeBookGroup recipeBookGroup = widget.getCategory();
                if (recipeBookGroup == RecipeBookGroup.CRAFTING_SEARCH || recipeBookGroup == RecipeBookGroup.FURNACE_SEARCH) {
                    widget.setPos(c, b + 27 * l++);
                    widget.visible = true;
                    if (l == 6) {
                        l = 0;
                    }
                } else if (widget.hasKnownRecipes(recipeBook)) {
                    widget.setPos(c, b + 27 * l++);
                    widget.checkForNewRecipes(this.client);
                    if (l == 6) {
                        l = 0;
                    }
                }
            } else {
                widget.visible = false;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void recipe_book_is_pain$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.isOpen() && !this.client.player.isSpectator()) {
            if (nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (page <= this.pages) ++page;
                updatePages();
                cir.setReturnValue(true);
            } else if (prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (page > 0) --page;
                updatePages();
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private void updatePages() {
        this.nextPageButton.visible = this.pages > 0 && page < this.pages;
        this.prevPageButton.visible = this.pages > 0 && page != 0;
    }


    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Inject(at = @At("HEAD"), method = "refreshTabButtons", cancellable = true)
    private void recipe_book_is_pain$refresh(CallbackInfo ci) {
        groupTab.clear();
        this.pages = 0;
        int p = 0;

        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            RecipeBookGroup recipeBookGroup = widget.getCategory();
            if (recipeBookGroup == RecipeBookGroup.CRAFTING_SEARCH || recipeBookGroup == RecipeBookGroup.FURNACE_SEARCH || widget.hasKnownRecipes(recipeBook)) {
                groupTab.add(new Pair<>((int) Math.ceil(p / 6), widget));
                p++;
            }
        }

        this.pages = (int) Math.ceil(p / 6);

        ci.cancel();
    }
}
