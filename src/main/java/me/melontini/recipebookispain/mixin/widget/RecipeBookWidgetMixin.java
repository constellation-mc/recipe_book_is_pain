package me.melontini.recipebookispain.mixin.widget;

import me.melontini.recipebookispain.access.RecipeBookWidgetAccess;
import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements RecipeBookWidgetAccess {
    @Shadow
    @Final
    protected static Identifier TEXTURE;
    @Shadow
    protected MinecraftClient client;
    @Shadow
    protected AbstractRecipeScreenHandler<?> craftingScreenHandler;
    @Shadow
    private int parentWidth;
    @Shadow
    private int parentHeight;
    @Shadow
    private int leftOffset;
    @Shadow
    @Final
    private List<RecipeGroupButtonWidget> tabButtons;
    @Shadow
    private ClientRecipeBook recipeBook;
    @Unique
    private int page = 0;
    @Unique
    private int pages;
    @Unique
    private ToggleButtonWidget nextPageButton;
    @Unique
    private ToggleButtonWidget prevPageButton;
    @Shadow
    private @Nullable RecipeGroupButtonWidget currentTab;

    @Shadow
    public abstract boolean isOpen();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setToggled(Z)V", shift = At.Shift.BEFORE), method = "reset")
    private void rbip$reset(CallbackInfo ci) {
        int a = (this.parentWidth - 147) / 2 - this.leftOffset;
        int s = (this.parentHeight + 166) / 2;
        this.nextPageButton = new ToggleButtonWidget(a + 14, s, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(a - 35, s, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;popMatrix()V", shift = At.Shift.BEFORE), method = "render")
    private void rbip$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        rbip$renderPageText(matrices);
        this.prevPageButton.render(matrices, mouseX, mouseY, delta);
        this.nextPageButton.render(matrices, mouseX, mouseY, delta);
    }

    @Unique
    private void rbip$renderPageText(MatrixStack matrices) {
        int x = (this.parentWidth - 135) / 2 - this.leftOffset - 30;
        int y = (this.parentHeight + 169) / 2 + 3;
        int displayPage = this.page + 1;
        int displayPages = this.pages;
        if (this.pages > 1) {
            String string = "" + displayPage + "/" + displayPages;
            int textLength = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(matrices, string, (x - textLength / 2F + 20F), y, -1);
        }
    }

    @Unique
    @Override
    public void updatePages() {
        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            if (((RecipeGroupButtonAccess)widget).getPage() == this.page) {
                RecipeBookGroup recipeBookGroup = widget.getCategory();
                if (recipeBookGroup.name().contains("_SEARCH")) {
                    widget.visible = true;
                } else if (widget.hasKnownRecipes(recipeBook)) {
                    widget.visible = true;
                    widget.checkForNewRecipes(this.client);
                }
            } else {
                widget.visible = false;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void rbip$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player != null) if (this.isOpen() && !this.client.player.isSpectator()) {
            if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page < (this.pages - 1)) setPage(++this.page);
                cir.setReturnValue(true);
            } else if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page > 0) setPage(--this.page);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    @Override
    public void updatePageSwitchButtons() {
        if (this.nextPageButton != null) this.nextPageButton.visible = this.pages > 1 && this.page < (this.pages - 1);
        if (this.prevPageButton != null) this.prevPageButton.visible = this.pages > 1 && this.page != 0;
    }

    @Inject(at = @At("HEAD"), method = "refreshTabButtons", cancellable = true)
    private void rbip$refresh(CallbackInfo ci) {
        this.pages = 0;
        int wc = 0;
        int x = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int y = (this.parentHeight - 166) / 2 + 3;
        int index = 0;

        this.tabButtons.clear();
        this.tabButtons.addAll(RecipeBookGroup.method_30285(this.craftingScreenHandler.getCategory())
                .stream().map(RecipeGroupButtonWidget::new)
                .filter(widget -> widget.getCategory().name().contains("_SEARCH") || widget.hasKnownRecipes(this.recipeBook))
                .collect(Collectors.toList()));
        this.currentTab = this.currentTab == null ? this.tabButtons.get(0) : this.tabButtons.stream()
                .filter(button -> button.getCategory().equals(this.currentTab.getCategory()))
                .findFirst().orElse(null);
        this.currentTab.setToggled(true);

        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            ((RecipeGroupButtonAccess)widget).setPage(MathHelper.fastFloor(wc / 6f));
            widget.setPos(x, y + 27 * index++);
            if (index == 6) index = 0;
            wc++;
        }

        this.pages = (int) Math.ceil(wc / 6f);
        updatePages();
        updatePageSwitchButtons();
        ci.cancel();
    }

    @Unique
    @Override
    public int getPage() {
        return this.page;
    }

    @Unique
    @Override
    public void setPage(int page) {
        if (page < 0) page = 0;
        if (page > pages - 1) page = pages - 1;

        this.page = page;
        updatePages();
        updatePageSwitchButtons();
    }

    @Unique
    @Override
    public int getPageCount() {
        return this.pages;
    }
}