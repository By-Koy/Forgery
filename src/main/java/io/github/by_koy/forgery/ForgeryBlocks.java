package io.github.by_koy.forgery;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ForgeryBlocks {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> creativeTab.accept(ForgeryBlocks.PALLADIUM_ORE.asItem()));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> creativeTab.accept(ForgeryBlocks.DEEPSLATE_PALLADIUM_ORE.asItem()));
    }

    // Create blocks
	public static final Block PALLADIUM_ORE = create("palladium_ore", BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().destroyTime(0.5F));
    public static final Block DEEPSLATE_PALLADIUM_ORE = create("deepslate_palladium_ore", BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().destroyTime(0.7F));
    public static final Block MELTER = create("melter", MelterBlock::new, BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().destroyTime(0.4F));

    // Method to attach the blocks to the game
    public static Block create(String name, BlockBehaviour.Properties settings) {
        // Create a default Item
        Function<BlockBehaviour.Properties, Block> properties = Block::new;

        // Create the block instance
        BlockItemId id = BlockItemId.create(Forgery.id(name), Forgery.id(name));
        Block block = register(id.block(), properties, settings);

        // Register and return
        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return block;
	}

    // Overloaded create() method for non-default blocks
    public static Block create(String name, Function<BlockBehaviour.Properties, Block> properties, BlockBehaviour.Properties settings) {

        // Create the block instance
        BlockItemId id = BlockItemId.create(Forgery.id(name), Forgery.id(name));
        Block block = register(id.block(), properties, settings);

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

    // Block entity classes
    public static class MelterBlock extends Block implements EntityBlock {

        public MelterBlock(Properties properties) {
            super(properties);
        }

        @Override @Nullable
        public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
            return new ForgeryBlockEntities.Melter(worldPosition, blockState);
        }

        @Override @NonNull
        protected InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ForgeryBlockEntities.Melter melter) {
                player.openMenu(melter);
            }

            return InteractionResult.SUCCESS;
        }
    }

}
