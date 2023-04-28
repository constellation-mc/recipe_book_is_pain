package me.melontini.recipebookispain.mixin.cracker_compat;

import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeGroupButtonWidget.class) //What's this? an empty mixin to ensure that RecipeGroupButtonAccess is always implemented
public class RecipeGroupButtonMixin implements RecipeGroupButtonAccess {
}