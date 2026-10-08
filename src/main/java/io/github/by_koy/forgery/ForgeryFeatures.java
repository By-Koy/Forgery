package io.github.by_koy.forgery;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.by_koy.forgery.Forgery.LOGGER;

public class ForgeryFeatures {

    public static final ResourceKey<Feature> PALLADIUM_ORE_VEIN_FEATURE_KEY =
            ResourceKey.create(
                    Registries.FEATURE,
                    Forgery.id("palladium_ore_vein")
            );

    public static void configure(BootstrapContext<Feature> context) {
        LOGGER.info("Configuring Features!");

        List<BlockReplacement> stoneRule = oreRules(Optional.of(BlockTags.STONE_ORE_REPLACEABLES), Optional.empty(), ModBlocks.PALLADIUM_ORE);
        List<BlockReplacement> deepslateRule = oreRules(Optional.of(BlockTags.DEEPSLATE_ORE_REPLACEABLES), Optional.empty(), ModBlocks.DEEPSLATE_PALLADIUM_ORE);

        List<BlockReplacement> palladiumOreConfig =
                Stream.concat(
                        stoneRule.stream(),
                        deepslateRule.stream()
                ).toList();

        context.register(
                PALLADIUM_ORE_VEIN_FEATURE_KEY,
                new OreFeature(
                        palladiumOreConfig,
                        4,
                        0.45F
                        ) {
                }
        );
    }

    static List<BlockReplacement> oreRules(Optional<TagKey<Block>> Tags, Optional<Block> Blocks, Block Replaceable) {
        List<BlockReplacement> list = new java.util.ArrayList<>();

        if (Tags.isPresent()) {
            list.add(BlockReplacement.replace(new TagMatchTest(Tags.orElse(null)), Replaceable.defaultBlockState()));
        }

        if (Blocks.isPresent()) {
            list.add(BlockReplacement.replace(new BlockMatchTest(Blocks.orElse(null)), Replaceable.defaultBlockState()));
        }

        return list;
    }
}
