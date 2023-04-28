package me.melontini.recipebookispain.mixin;

import de.siphalor.mousewheelie.client.mixin.gui.other.MixinRecipeBookWidget;
import de.siphalor.mousewheelie.client.util.ScrollAction;
import me.melontini.recipebookispain.access.RecipeBookWidgetAccess;
import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = RecipeBookWidget.class, priority = 1001)
public abstract class MouseWheelieCompatMixin {

    @Shadow
    @Nullable
    private RecipeGroupButtonWidget currentTab;

    @SuppressWarnings("ReferenceToMixin")
    @Dynamic(mixin = MixinRecipeBookWidget.class)
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/recipebook/RecipeGroupButtonWidget.setToggled (Z)V", ordinal = 1, shift = At.Shift.AFTER), method = "mouseWheelie_scrollRecipeBook")
    private void rbip$scrollPages(double mouseX, double mouseY, double scrollAmount, CallbackInfoReturnable<ScrollAction> cir) {
        RecipeBookWidget bookWidget = (RecipeBookWidget) (Object) this;
        RecipeBookWidgetAccess widgetAccess = ((RecipeBookWidgetAccess) bookWidget);
        RecipeGroupButtonAccess buttonAccess = ((RecipeGroupButtonAccess) this.currentTab);

        assert buttonAccess != null;
        if (widgetAccess.getPage() != buttonAccess.getPage()) {
            widgetAccess.setPage(buttonAccess.getPage());
        }
    }
}