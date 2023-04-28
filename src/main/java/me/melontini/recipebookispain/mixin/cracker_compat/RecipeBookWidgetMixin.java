package me.melontini.recipebookispain.mixin.cracker_compat;

import me.melontini.recipebookispain.access.RecipeBookWidgetAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeBookWidget.class) //What's this? an empty mixin to ensure that RecipeBookWidgetAccess is always implemented
public abstract class RecipeBookWidgetMixin implements RecipeBookWidgetAccess {
}