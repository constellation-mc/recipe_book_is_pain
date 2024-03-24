package me.melontini.recipebookispain.mixin;

import me.melontini.recipebookispain.access.ItemAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin implements ItemAccess {
    @Unique
    public ItemGroup rbip$possibleGroup;

    @Override
    public Optional<ItemGroup> rbip$getPossibleGroup() {
        return Optional.ofNullable(rbip$possibleGroup);
    }

    @Override
    public void rbip$setPossibleGroup(ItemGroup group) {
        rbip$possibleGroup = group;
    }
}
