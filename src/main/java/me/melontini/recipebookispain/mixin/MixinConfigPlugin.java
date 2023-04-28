package me.melontini.recipebookispain.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    public static final Logger LOGGER = LogManager.getLogger("RBIP-Mixin-Plugin");
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] strings = mixinClassName.split("\\.");
        if (mixinClassName.contains("MouseWheelieCompatMixin") && !FabricLoader.getInstance().isModLoaded("mousewheelie")) {
            return false;
        }
        if ((mixinClassName.contains("widget.RecipeGroupButtonMixin") || mixinClassName.contains("widget.RecipeBookWidgetMixin")) && FabricLoader.getInstance().isModLoaded("cracker-util-recipe-book")) {
            LOGGER.warn("[RBIP] CrackerUtil Recipe Book is loaded, skipping {} application", strings[strings.length - 1]);
            return false;
        }
        if (mixinClassName.contains("MouseWheelieCompatMixin")) {
            LOGGER.warn("[RBIP] MouseWheelie is loaded, applying {}", strings[strings.length - 1]);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.contains("groups.RecipeBookGroupMixin")) {
            for (MethodNode method : targetClass.methods) {
                if (Objects.equals(method.name, "rbip$extend")) {
                    method.access = (method.access & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PUBLIC;
                    //bitwise the private away.
                }
            }
        }
    }
}