package io.github.by_koy.forgery;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
		.register((creativeTab) -> creativeTab.accept(ModBlocks.PALLADIUM_ORE.asItem()));
    }

    // Create items
	public static final Block PALLADIUM_ORE = create("palladium_ore", BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops());

    // Method to attach the blocks to the game
    public static Block create(String name, BlockBehaviour.Properties settings) {
        // Create a default Item
        Function<BlockBehaviour.Properties, Block> properties = Block::new;

        // Create the block instance
        BlockItemId id = BlockItemId.create(Forgery.id(name), Forgery.id(name));
        Block block = register(id.block(), properties, settings);
        // Block block = properties.apply(settings.setId(id));

        // Register and return
        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return block;
	}

    private static Block register(ResourceKey<Block> id, Function<BlockBehaviour.Properties,Block> properties, BlockBehaviour.Properties settings) {
        // Create the block instance
		Block block = properties.apply(settings.setId(id));

		return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }
}
