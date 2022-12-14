package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.access.RecipeBookWidgetAccess;
import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import me.melontini.recipebookispain.client.RecipeBookIsPainClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements RecipeBookWidgetAccess {
    @Shadow
    @Final
    protected static Identifier TEXTURE;
    @Shadow
    protected MinecraftClient client;
    @Shadow
    protected AbstractRecipeScreenHandler<?> craftingScreenHandler;
    @Unique
    private int page = 0;
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

    @Shadow private @Nullable RecipeGroupButtonWidget currentTab;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget;setToggled(Z)V", shift = At.Shift.BEFORE), method = "reset")
    private void recipe_book_is_pain$init(CallbackInfo ci) {
        int a = (this.parentWidth - 147) / 2 - this.leftOffset;
        int s = (this.parentHeight + 166) / 2;
        this.nextPageButton = new ToggleButtonWidget(a + 14, s, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(a - 35, s, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE), method = "render")
    private void recipe_book_is_pain$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderPageText(matrices);
        for (RecipeGroupButtonWidget widget : tabButtons) {
            if (widget.visible) {
                if (widget.getCategory().name().contains("_SEARCH")) {
                    if (widget.isHovered())
                        client.currentScreen.renderTooltip(matrices, ItemGroups.getSearchGroup().getDisplayName(), mouseX, mouseY);
                } else {
                    if (RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(widget.getCategory()) != null) {
                        Text text = RecipeBookIsPainClient.RECIPE_BOOK_GROUP_TO_ITEM_GROUP.get(widget.getCategory()).getDisplayName();
                        if (text != null)
                            if (widget.isHovered()) client.currentScreen.renderTooltip(matrices, text, mouseX, mouseY);
                    }
                }
            }
        }
        this.prevPageButton.render(matrices, mouseX, mouseY, delta);
        this.nextPageButton.render(matrices, mouseX, mouseY, delta);
    }

    @Unique
    private void renderPageText(MatrixStack matrices) {
        int x = (this.parentWidth - 135) / 2 - this.leftOffset - 30;
        int y = (this.parentHeight + 169) / 2 + 3;
        int displayPage = page + 1;
        int displayPages = pages + 1;
        if (this.pages > 0) {
            String string = "" + displayPage + "/" + displayPages;
            int textLength = this.client.textRenderer.getWidth(string);
            this.client.textRenderer.draw(matrices, string, (x - textLength / 2F + 20F), y, -1);
        }
    }

    @Unique
    @Override
    public void updatePages() {
        for (RecipeGroupButtonWidget widget : tabButtons) {
            if (((RecipeGroupButtonAccess) widget).getPage() == page) {
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
    private void recipe_book_is_pain$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player != null) if (this.isOpen() && !this.client.player.isSpectator()) {
            if (nextPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page <= this.pages) ++this.page;
                updatePages();
                updatePageSwitchButtons();
                cir.setReturnValue(true);
            } else if (prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page > 0) --this.page;
                updatePages();
                updatePageSwitchButtons();
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private void updatePageSwitchButtons() {
        this.nextPageButton.visible = this.pages > 0 && this.page < this.pages;
        this.prevPageButton.visible = this.pages > 0 && this.page != 0;
    }


    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Inject(at = @At("HEAD"), method = "refreshTabButtons", cancellable = true)
    private void recipe_book_is_pain$refresh(CallbackInfo ci) {
        this.pages = 0;
        int p = 0;
        int c = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int b = (this.parentHeight - 166) / 2 + 3;
        int l = 0;

        this.tabButtons.clear();
        for (RecipeBookGroup recipeBookGroup : RecipeBookGroup.getGroups(this.craftingScreenHandler.getCategory())) {
            var widget = new RecipeGroupButtonWidget(recipeBookGroup);
            if (recipeBookGroup.name().contains("_SEARCH") || widget.hasKnownRecipes(this.recipeBook)) {
                this.tabButtons.add(new RecipeGroupButtonWidget(recipeBookGroup));
            }
        }
        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter((button) -> button.getCategory().equals(this.currentTab.getCategory())).findFirst().orElse(null);
        }
        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }

        this.currentTab.setToggled(true);
        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            ((RecipeGroupButtonAccess) widget).setPage((int) Math.ceil(p / 6));
            widget.setPos(c, b + 27 * l++);
            if (l == 6) {
                l = 0;
            }
            p++;
        }

        --p;
        this.pages = (int) Math.ceil(p / 6);
        updatePages();
        updatePageSwitchButtons();
        ci.cancel();
    }

    @Override
    public int getBookPage() {
        return page;
    }

    @Override
    public void setBookPage(int page) {
        this.page = page;
    }
}
