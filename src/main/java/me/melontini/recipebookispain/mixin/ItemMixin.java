package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.access.ItemAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemAccess {
    @Unique
    public ItemGroup rbip$possibleGroup;

    @Override
    public ItemGroup rbip$getPossibleGroup() {
        return rbip$possibleGroup != null ? rbip$possibleGroup : ItemGroups.getDefaultTab();
    }

    @Override
    public void rbip$setPossibleGroup(ItemGroup group) {
        rbip$possibleGroup = group;
    }
}
