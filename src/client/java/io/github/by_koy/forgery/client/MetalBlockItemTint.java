package io.github.by_koy.forgery.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.by_koy.forgery.extras.ForgeryComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record MetalBlockItemTint(int colour) implements ItemTintSource {
    public static final MapCodec<MetalBlockItemTint> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ExtraCodecs.RGB_COLOR_CODEC.fieldOf("colour").forGetter(MetalBlockItemTint::colour)).apply(instance, MetalBlockItemTint::new)
    );

    public MetalBlockItemTint() {
        this(ARGB.opaque(0x00BFFF)); // Color code in hex format
    }

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        if (itemStack.has(ForgeryComponents.COMPOUND)) {
            return ARGB.opaque(itemStack.get(ForgeryComponents.COMPOUND));
        }

        return ARGB.opaque(this.colour); // Colour code in hex format
    }

    @Override @NonNull
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
