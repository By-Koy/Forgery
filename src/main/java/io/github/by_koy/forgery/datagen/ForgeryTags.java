package io.github.by_koy.forgery.datagen;

import io.github.by_koy.forgery.Forgery;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ForgeryTags {
    public static final TagKey<Item> MELTABLE = TagKey.create(Registries.ITEM, Forgery.id("meltable"));
    public static final TagKey<Block> SURVIVES_MELTED = TagKey.create(Registries.BLOCK, Forgery.id("survives_melted"));

    public static void initialize() {
        Forgery.LOGGER.info("Initializing tags...");
    }
}
