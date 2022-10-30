package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.RecipeBookIsPain;
import me.melontini.recipebookispain.access.RecipeGroupButtonAccess;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Mixin(RecipeGroupButtonWidget.class)
public class RecipeGroupButtonMixin implements RecipeGroupButtonAccess {
    @Unique
    private int page = -1;

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;getIcons()Ljava/util/List;"), method = "renderIcons")
    private List<ItemStack> render(RecipeBookGroup instance) {
        if (RecipeBookIsPain.ADDED_GROUPS.containsValue(instance))
            return Collections.singletonList(RecipeBookIsPain.AAAAAAAA.get(instance.name()).getIcon());
        return instance.getIcons();
    }
}
