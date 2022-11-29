package me.melontini.recipebookispain.access;

import net.minecraft.item.ItemGroup;

public interface ItemAccess {
    ItemGroup rbip$getPossibleGroup();
    void rbip$setPossibleGroup(ItemGroup group);
}
