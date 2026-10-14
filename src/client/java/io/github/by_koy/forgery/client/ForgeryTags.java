package io.github.by_koy.forgery.client;

import io.github.by_koy.forgery.Forgery;
import io.github.by_koy.forgery.ForgeryItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ForgeryTags {
    public static class ForgeryItemTags extends FabricTagsProvider.ItemTagsProvider {

        //Add tags
        public static final TagKey<Item> MELTABLE = TagKey.create(Registries.ITEM, Forgery.id("meltable"));

        public ForgeryItemTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider registries) {
            builder(MELTABLE)
                    .add(ItemIds.IRON_INGOT)
                    .add(ItemIds.RAW_IRON)
                    .add(BlockItemIds.IRON_BLOCK)
                    .add(ItemIds.GOLD_INGOT)
                    .add(ItemIds.RAW_GOLD)
                    .add(BlockItemIds.GOLD_BLOCK)
                    .add(ForgeryItems.PALLADIUM_INGOT.resourceKey)
                    .add(ForgeryItems.RAW_PALLADIUM.resourceKey)
                    .setReplace(true);
        }
    }
}
