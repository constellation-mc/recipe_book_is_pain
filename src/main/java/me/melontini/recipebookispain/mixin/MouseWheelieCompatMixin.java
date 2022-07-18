package me.melontini.recipebookispain.mixin;

import de.siphalor.mousewheelie.client.mixin.gui.other.MixinRecipeBookWidget;
import de.siphalor.mousewheelie.client.util.ScrollAction;
import me.melontini.recipebookispain.access.RecipeBookWidgetAccess;
import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(value = RecipeBookWidget.class, priority = 1001)
public abstract class MouseWheelieCompatMixin {
    @Shadow
    @Final
    private List<RecipeGroupButtonWidget> tabButtons;

    @Shadow
    @Nullable
    private RecipeGroupButtonWidget currentTab;

    @Shadow
    protected abstract void refreshResults(boolean resetCurrentPage);

    //pain
    //pretty funny tho
    @SuppressWarnings("ReferenceToMixin")
    @Dynamic(mixin = MixinRecipeBookWidget.class)
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget.setToggled (Z)V", ordinal = 0, shift = At.Shift.BEFORE), method = "mouseWheelie_scrollRecipeBook", cancellable = true)
    private void inject(double mouseX, double mouseY, double scrollAmount, CallbackInfoReturnable<ScrollAction> cir) {
        RecipeBookWidget bookWidget = (RecipeBookWidget) (Object) this;
        int index;
        index = this.tabButtons.indexOf(this.currentTab);
        int newIndex = MathHelper.clamp(index + (int) Math.round(scrollAmount), 0, this.tabButtons.size() - 1);
        this.currentTab.setToggled(false);
        this.currentTab = this.tabButtons.get(newIndex);
        this.currentTab.setToggled(true);
        if (((RecipeBookWidgetAccess) bookWidget).getBookPage() != ((RecipeGroupButtonAccess) this.currentTab).getPage()) {
            ((RecipeBookWidgetAccess) bookWidget).setBookPage(((RecipeGroupButtonAccess) this.currentTab).getPage());
        }
        this.refreshResults(true);
        cir.setReturnValue(ScrollAction.SUCCESS);
    }
}
