package io.github.by_koy.forgery;

import java.util.function.Function;

import io.github.by_koy.forgery.extras.MeltedFluid;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ForgeryBlocks {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> creativeTab.accept(ForgeryBlocks.PALLADIUM_ORE.blockItem.asItem()));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> creativeTab.accept(ForgeryBlocks.DEEPSLATE_PALLADIUM_ORE.blockItem.asItem()));
    }

    public static class ModBlockItems {
        public Block block;
        public BlockItem blockItem;

        public ModBlockItems(Block block, BlockItem blockItem) {
            this.block = block;
            this.blockItem = blockItem;
        }
    }

    public static class ModBlocks {
        public Block block;
        public ResourceKey<Block> resourceKey;

        public ModBlocks(Block block, ResourceKey<Block> resourceKey) {
            this.block = block;
            this.resourceKey = resourceKey;
        }
    }

    public static class ModFluids {
        public FlowingFluid fluid;
        public ResourceKey<Fluid> resourceKey;

        public ModFluids(FlowingFluid fluid, ResourceKey<Fluid> resourceKey) {
            this.fluid = fluid;
            this.resourceKey = resourceKey;
        }
    }

    // Create fluids
    public static final ModFluids MELTED_FLOWING = createFluid("melted_flowing", new MeltedFluid.Flowing());
    public static final ModFluids MELTED_STILL = createFluid("melted_still", new MeltedFluid.Source());

    // Create blocks
	public static final ModBlockItems PALLADIUM_ORE = create("palladium_ore", BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().destroyTime(0.5F));
    public static final ModBlockItems DEEPSLATE_PALLADIUM_ORE = create("deepslate_palladium_ore", BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().destroyTime(0.7F));
    public static final ModBlockItems MELTER = create("melter", MelterBlock::new, BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().destroyTime(0.4F));
    public static final ModBlocks MELTED_FLUID = createNonItem("melted_fluid", (props) -> new LiquidBlock(MELTED_STILL.fluid, props), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));


    // Method to attach the blocks to the game
    public static ModBlockItems create(String name, BlockBehaviour.Properties settings) {
        // Create a default Item
        Function<BlockBehaviour.Properties, Block> properties = Block::new;

        // Create the block instance
        BlockItemId id = BlockItemId.create(Forgery.id(name), Forgery.id(name));
        Block block = register(id.block(), properties, settings);

        // Register and return
        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return new ModBlockItems(block, blockItem);
	}

    // Overloaded create() method for non-default blocks
    public static ModBlockItems create(String name, Function<BlockBehaviour.Properties, Block> properties, BlockBehaviour.Properties settings) {

        // Create the block instance
        BlockItemId id = BlockItemId.create(Forgery.id(name), Forgery.id(name));
        Block block = register(id.block(), properties, settings);

        // Register and return
        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return new ModBlockItems(block, blockItem);
    }

    private static Block register(ResourceKey<Block> id, Function<BlockBehaviour.Properties,Block> properties, BlockBehaviour.Properties settings) {
        // Create the block instance
        Block block = properties.apply(settings.setId(id));

        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static ModBlocks createNonItem(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        Identifier id = Forgery.id(name);

        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        Block block = blockFactory.apply(properties.setId(key));
        Block registered = Registry.register(BuiltInRegistries.BLOCK, id, block);

        return new ModBlocks(registered, key);
    }

    private static ModFluids createFluid(String name, FlowingFluid fluid) {
        Identifier id = Forgery.id(name);

        ResourceKey<Fluid> key = ResourceKey.create(Registries.FLUID, id);
        FlowingFluid flowingFluid = Registry.register(BuiltInRegistries.FLUID, id, fluid);

        return new ModFluids(flowingFluid, key);
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
