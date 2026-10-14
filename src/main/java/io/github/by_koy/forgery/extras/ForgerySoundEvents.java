package io.github.by_koy.forgery.extras;

import io.github.by_koy.forgery.Forgery;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ForgerySoundEvents {
    public static final SoundEvent MELTED_FLUID_BUBBLE = register("fluid.melted.bubble");

    private static SoundEvent register(final String name) {
        Identifier id = Forgery.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {
        Forgery.LOGGER.info("Initializing sounds...");
    }
}
