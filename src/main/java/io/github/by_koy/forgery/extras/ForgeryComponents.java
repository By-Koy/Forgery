package io.github.by_koy.forgery.extras;

import io.github.by_koy.forgery.Forgery;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ForgeryComponents {
    public static void initialize() {
        Forgery.LOGGER.info("Registering components");
    }

    public static final DataComponentType<Integer> COMPOUND = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Forgery.id("compound"),
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.RGB_COLOR_CODEC).networkSynchronized(ByteBufCodecs.VAR_INT).build()
    );
}
