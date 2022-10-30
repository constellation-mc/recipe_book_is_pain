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
import net.minecraft.item.ItemGroup;
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
    @Nullable
    private RecipeGroupButtonWidget currentTab;

    @Shadow
    public abstract boolean isOpen();

    @Inject(at = @At("RETURN"), method = "reset")
    private void recipe_book_is_pain$init(CallbackInfo ci) {
        int a = (this.parentWidth - 147) / 2 - this.leftOffset;
        int s = (this.parentHeight + 166) / 2;
        this.nextPageButton = new ToggleButtonWidget(a + 14, s, 12, 17, false);
        this.nextPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        this.prevPageButton = new ToggleButtonWidget(a - 35, s, 12, 17, true);
        this.prevPageButton.setTextureUV(1, 208, 13, 18, TEXTURE);
        page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;popMatrix()V", shift = At.Shift.BEFORE), method = "render")
    private void recipe_book_is_pain$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        updatePageSwitchButtons();
        renderPageText(matrices);
        updatePages(mouseX, mouseY, matrices);
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

    @Unique //mmm spaghettio
    private void updatePages(int mouseX, int mouseY, MatrixStack stack) {
        for (RecipeGroupButtonWidget widget : tabButtons) {
            if (((RecipeGroupButtonAccess) widget).getPage() == page) {
                RecipeBookGroup recipeBookGroup = widget.getCategory();
                if (client.currentScreen != null) if (recipeBookGroup.name().contains("_SEARCH")) {
                    widget.visible = true;
                    if (widget.isHovered())
                        client.currentScreen.renderTooltip(stack, ItemGroup.SEARCH.getTranslationKey(), mouseX, mouseY);
                } else if (widget.hasKnownRecipes(recipeBook)) {
                    widget.checkForNewRecipes(this.client);
                    if (RecipeBookIsPainClient.AAAAAAAA.get(recipeBookGroup.name()) != null) {
                        Text text = RecipeBookIsPainClient.AAAAAAAA.get(recipeBookGroup.name()).getTranslationKey();
                        if (text != null)
                            if (widget.isHovered()) client.currentScreen.renderTooltip(stack, text, mouseX, mouseY);
                    }
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
                updatePageSwitchButtons();
                cir.setReturnValue(true);
            } else if (prevPageButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.page > 0) --this.page;
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
        for (RecipeBookGroup group : RecipeBookGroup.method_30285(this.craftingScreenHandler.getCategory())) {
            RecipeGroupButtonWidget widget = new RecipeGroupButtonWidget(group);
            if (group.name().contains("_SEARCH") || widget.hasKnownRecipes(recipeBook)) this.tabButtons.add(widget);
        }

        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter((button) ->
                    button.getCategory().equals(this.currentTab.getCategory())).findFirst().orElse(null);
        }
        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }
        this.currentTab.setToggled(true);

        for (RecipeGroupButtonWidget widget : this.tabButtons) {
            RecipeBookGroup recipeBookGroup = widget.getCategory();
            if (recipeBookGroup.name().contains("_SEARCH") || widget.hasKnownRecipes(recipeBook)) {
                ((RecipeGroupButtonAccess) widget).setPage((int) Math.ceil(p / 6));
                widget.setPos(c, b + 27 * l++);
                widget.visible = ((RecipeGroupButtonAccess) widget).getPage() == page;
                if (l == 6) {
                    l = 0;
                }
                p++;
            }
        }

        --p;
        this.pages = (int) Math.ceil(p / 6);

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
