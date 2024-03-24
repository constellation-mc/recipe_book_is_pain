package me.melontini.recipebookispain.access;

import net.minecraft.item.ItemGroup;

import java.util.Optional;

public interface ItemAccess {
    Optional<ItemGroup> rbip$getPossibleGroup();
    void rbip$setPossibleGroup(ItemGroup group);
}
