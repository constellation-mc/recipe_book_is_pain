package me.melontini.recipebookispain.util;

import me.melontini.recipebookispain.RecipeBookIsPainClient;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class EnumUtils {
    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                unsafe = constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException ex) {
                throw new RuntimeException("Couldn't access Unsafe", ex);
            }
        }
        UNSAFE = unsafe;
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
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        UNSAFE.putObject(o, l, value);
    }
}
