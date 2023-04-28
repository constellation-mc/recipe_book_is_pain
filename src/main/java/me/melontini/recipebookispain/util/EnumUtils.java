package me.melontini.recipebookispain.util;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EnumUtils {
    private static final Object unsafe;

    private static final Method staticFieldOffset;
    private static final Method objectFieldOffset;
    private static final Method putObject;

    static {
        try {
            Class<?> unsafeClass;
            try {
                unsafeClass = Class.forName("sun.misc.Unsafe");
            } catch (Exception e) {
                unsafeClass = Class.forName("jdk.internal.misc.Unsafe");
            }

            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = f.get(null);

            staticFieldOffset = unsafeClass.getMethod("staticFieldOffset", Field.class);
            staticFieldOffset.setAccessible(true);
            objectFieldOffset = unsafeClass.getMethod("objectFieldOffset", Field.class);
            objectFieldOffset.setAccessible(true);
            putObject = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
            putObject.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void clearEnumCache(Class<? extends Enum<?>> cls) {
        try {
            writeField(Class.class.getDeclaredField("enumConstants"), cls, null);
        } catch (Exception e) {
            RecipeBookIsPainClient.LOGGER.error("Couldn't clear enumConstants. This shouldn't really happen", e);
        }

        try {
            writeField(Class.class.getDeclaredField("enumConstantDirectory"), cls, null);
        } catch (Exception e) {
            RecipeBookIsPainClient.LOGGER.error("Couldn't clear enumConstantDirectory. This shouldn't really happen", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T callRecipeBookEnumInvoker(String internalName, ItemStack... params) {
        try {
            return (T) RecipeBookGroup.class.getMethod("rbip$extend", String.class, ItemStack[].class).invoke(RecipeBookGroup.class, internalName, params);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeField(Field field, Object o, Object value) {
        try {
            long l = (long) (Modifier.isStatic(field.getModifiers()) ? staticFieldOffset.invoke(unsafe, field) : objectFieldOffset.invoke(unsafe, field));
            putObject.invoke(unsafe, o, l, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
