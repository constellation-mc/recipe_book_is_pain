package me.melontini.recipebookispain.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
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

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookWidgetMixin {
    @Shadow
    @Final
    protected static ResourceLocation RECIPE_BOOK_LOCATION;
    @Unique
    private int page = 0;
    @Unique
    public List<Tuple<Integer, RecipeBookTabButton>> groupTab = Lists.newArrayList();
    @Shadow
    protected Minecraft minecraft;
    @Shadow
    private int height;
    @Shadow
    private int xOffset;
    @Shadow
    private ClientRecipeBook book;
    @Shadow
    @Final
    private List<RecipeBookTabButton> tabButtons;
    @Shadow
    private int width;
    @Unique
    private int pages;
    @Unique
    private StateSwitchingButton nextPageButton;
    @Unique
    private StateSwitchingButton prevPageButton;

    @Shadow public abstract boolean isVisible();

    @Inject(at = @At("RETURN"), method = "init")
    private void recipe_book_is_pain$init(CallbackInfo ci) {
        int a = (this.width - 147) / 2 - this.xOffset;
        int s = (this.height + 166) / 2;
        this.nextPageButton = new StateSwitchingButton(a + 10, s, 12, 17, false);
        this.nextPageButton.initTextureValues(1, 208, 13, 18, RECIPE_BOOK_LOCATION);
        this.prevPageButton = new StateSwitchingButton(a - 10, s, 12, 17, true);
        this.prevPageButton.initTextureValues(1, 208, 13, 18, RECIPE_BOOK_LOCATION);
        page = 0;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.BEFORE), method = "render")
    private void recipe_book_is_pain$render(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        updatePageSwitchButtons();
        this.prevPageButton.render(poseStack, i, j, f);
        this.nextPageButton.render(poseStack, i, j, f);
        updatePages();
    }

    @Unique
    private void updatePages() {
        for (Tuple<Integer, RecipeBookTabButton> pair : groupTab) {
            RecipeBookTabButton widget = pair.getB();
            if (pair.getA() == page) {
                RecipeBookCategories recipeBookGroup = widget.getCategory();
                if (recipeBookGroup == RecipeBookCategories.CRAFTING_SEARCH || recipeBookGroup == RecipeBookCategories.FURNACE_SEARCH) {
                    widget.visible = true;
                } else if (widget.updateVisibility(book)) {
                    widget.startAnimation(this.minecraft);
                }
            } else {
                widget.visible = false;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void recipe_book_is_pain$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.isVisible() && !this.minecraft.player.isSpectator()) {
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
    @Inject(at = @At("HEAD"), method = "updateTabs", cancellable = true)
    private void recipe_book_is_pain$refresh(CallbackInfo ci) {
        groupTab.clear();
        this.pages = 0;
        int p = 0;
        int c = (this.width - 147) / 2 - this.xOffset - 30;
        int b = (this.height - 166) / 2 + 3;
        int l = 0;

        for (RecipeBookTabButton widget : this.tabButtons) {
            RecipeBookCategories recipeBookGroup = widget.getCategory();
            if (recipeBookGroup == RecipeBookCategories.CRAFTING_SEARCH || recipeBookGroup == RecipeBookCategories.FURNACE_SEARCH || widget.updateVisibility(book)) {
                groupTab.add(new Tuple<>((int) Math.ceil(p / 6), widget));
                widget.visible = false;
                widget.setPosition(c, b + 27 * l++);
                if (l == 6) {
                    l = 0;
                }
                p++;
            }
        }

        this.pages = (int) Math.ceil(p / 6);

        ci.cancel();
    }
}
