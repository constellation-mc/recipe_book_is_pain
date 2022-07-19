package me.melontini.recipebookispain.mixin;

import com.google.common.collect.ImmutableList;
import me.melontini.recipebookispain.RecipeBookIsPain;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraftforge.client.RecipeBookRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeBookRegistry.class) //cope
public class ForgeRecipeBookRegistryMixin {
    // pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work pls work
    @SuppressWarnings("unchecked")
    @Redirect(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"), remap = false, method = "<clinit>")
    private static <E> ImmutableList<E> listOf(E e1, E e2, E e3, E e4) {
        if (e1 == RecipeBookGroup.CRAFTING_EQUIPMENT) {
            return (ImmutableList<E>) ImmutableList.copyOf(RecipeBookIsPain.CRAFTING_SEARCH_MAP);
        }
        return ImmutableList.of(e1, e2, e3, e4);
    }
}
