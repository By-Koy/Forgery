package io.github.by_koy.forgery;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class ForgeryPlacedFeatures {
    public static final ResourceKey<PlacedFeature> PALLADIUM_ORE_VEIN_PLACED_FEATURE =
            ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    Forgery.id("palladium_ore_vein_placed")
            );

    public static void configure(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> featureKeys = context.lookup(Registries.FEATURE);

        List<PlacementModifier> palladiumOreVeinModifiers =
                List.of(
                        CountPlacement.of(4),
                        HeightRangePlacement.of(TrapezoidHeight.of(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(16), 0))
                );

        context.register(
                PALLADIUM_ORE_VEIN_PLACED_FEATURE,
                new PlacedFeature(
                        featureKeys.getOrThrow(ForgeryFeatures.PALLADIUM_ORE_VEIN_FEATURE_KEY),
                        palladiumOreVeinModifiers
                )
        );
    }
}