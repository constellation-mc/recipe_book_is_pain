package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.access.ItemAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ItemAccess {
    public ItemGroup rbip$possibleGroup;

    @Override
    public ItemGroup rbip$getPossibleGroup() {
        return rbip$possibleGroup;
    }

    @Override
    public void rbip$setPossibleGroup(ItemGroup group) {
        rbip$possibleGroup = group;
    }
}
