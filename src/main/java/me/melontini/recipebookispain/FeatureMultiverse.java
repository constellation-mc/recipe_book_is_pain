package me.melontini.recipebookispain;

import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FeatureMultiverse {

    public static final FeatureFlag[] FLAGS;

    static {
        List<FeatureFlag> flags = new ArrayList<>();
        for (Field field : FeatureFlags.class.getFields()) {
            if (field.getType() == FeatureFlag.class) {
                try {
                    flags.add((FeatureFlag) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        flags.remove(FeatureFlags.VANILLA);
        FLAGS = flags.toArray(FeatureFlag[]::new);
    }

    public static FeatureSet getFeatureSet() {
        return FeatureSet.of(FeatureFlags.VANILLA, FLAGS);
    }
}
