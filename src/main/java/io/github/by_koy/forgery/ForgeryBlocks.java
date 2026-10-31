package io.github.by_koy.forgery;

import java.util.function.Function;

import io.github.by_koy.forgery.extras.MeltedFluid;
import io.github.by_koy.forgery.ForgeryBlockEntities.Melter;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static io.github.by_koy.forgery.ForgeryBlockEntities.MELTER_BLOCK_ENTITY;

public class ForgeryBlocks {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> creativeTab.accept(ForgeryBlocks.PALLADIUM_ORE.blockItem.asItem()));

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
            .register((creativeTab) -> creativeTab.accept(ForgeryBlocks.DEEPSLATE_PALLADIUM_ORE.blockItem.asItem()));

        Forgery.LOGGER.info("Initialized blocks!");
    }

    // Create block structs
    public static class ModBlockItems {
        public Block block;
        public BlockItem blockItem;
        public BlockItemId blockItemId;

        public ModBlockItems(Block block, BlockItem blockItem, BlockItemId blockItemId) {
            this.block = block;
            this.blockItem = blockItem;
            this.blockItemId = blockItemId;
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
    public static final ModBlocks MELTED_FLUID = createNonItem("melted_fluid", (props) -> new MeltedFluidBlock(MELTED_STILL.fluid, props), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA));


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
        return new ModBlockItems(block, blockItem, id);
	}

    // Overloaded create() method for non-default blocks
    public static ModBlockItems create(String name, Function<BlockBehaviour.Properties, Block> properties, BlockBehaviour.Properties settings) {

        // Create the block instance
        BlockItemId id = BlockItemId.create(Forgery.id(name), Forgery.id(name));
        Block block = register(id.block(), properties, settings);

        // Register and return
        BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
        Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);
        return new ModBlockItems(block, blockItem, id);
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
    public static class MelterBlock extends BaseEntityBlock {
        public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

        public enum  melterActive implements StringRepresentable {
            off,
            soul,
            on;

            @Override @NonNull
            public String getSerializedName() {
                return this.name();
            }
        } public static final EnumProperty<melterActive> ACTIVE = EnumProperty.create("active", melterActive.class);

        public MelterBlock(Properties properties) {
            super(properties);

            registerDefaultState(defaultBlockState()
                    .setValue(FACING, Direction.NORTH)
                    .setValue(ACTIVE, melterActive.off));
        }

        @Override
        public BlockState getStateForPlacement(final BlockPlaceContext context) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection());
        }

        @Override @Nullable
        public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
            return new Melter(worldPosition, blockState);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING, ACTIVE);
        }

        @Override @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NonNull Level level, final @NonNull BlockState blockState, final @NonNull BlockEntityType<T> type) {
            return createTickerHelper(type, MELTER_BLOCK_ENTITY, Melter::tick);
        }

        @Override @NonNull
        protected InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof Melter melter) {
                player.openMenu(melter);
            }

            return InteractionResult.SUCCESS;
        }
    }

    public static class MeltedFluidBlock extends LiquidBlock implements EntityBlock {
        public static final IntegerProperty relPosX = IntegerProperty.create("relative_pos_x", 0, 16);
        public static final IntegerProperty relPosY = IntegerProperty.create("relative_pos_y", 0, 16);
        public static final IntegerProperty relPosZ = IntegerProperty.create("relative_pos_z", 0, 16);


        @Override
        protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(relPosX, relPosY, relPosZ);
        }

        public MeltedFluidBlock(FlowingFluid fluid, Properties properties) {
            super(fluid, properties);

            registerDefaultState(defaultBlockState()
                    .setValue(relPosX, 0)
                    .setValue(relPosY, 0)
                    .setValue(relPosZ, 0));
        }

        @Override @Nullable
        public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
            return newBlockEntity(worldPosition, blockState, NonNullList.of(ItemStack.EMPTY));
        }

        public BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState, NonNullList<ItemStack> items) {
            return new ForgeryBlockEntities.MeltedFluidEntity(worldPosition, blockState, items);
        }

        @Override @NonNull
        protected InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
            return InteractionResult.SUCCESS;
        }

        @Override
        public BlockState getStateForPlacement(final @NonNull BlockPlaceContext context) {
            return context.canPlace() ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        public static BlockPos interpretEntityPos(int x, int y, int z, BlockPos pos) {
            int parsedX = x <= 8 ? x : -(x - 8);
            int parsedY = y <= 8 ? y : -(y - 8);
            int parsedZ = z <= 8 ? z : -(z - 8);

            int newX = parsedX + pos.getX();
            int newY = parsedY + pos.getY();
            int newZ = parsedZ + pos.getZ();

            BlockPos entityPos = new BlockPos(newX, newY, newZ);
            Forgery.LOGGER.info("Found BlockPos: {} from x: {}, y: {}, z: {}, thisPos: {}", entityPos, x, y, z, pos);
            return entityPos;
        }

        public static BlockPos createEntityPos(BlockPos thisPos, BlockPos entityPos) {
            int newX = thisPos.getX() - entityPos.getX() >= 0 ? thisPos.getX() - entityPos.getX() : -(thisPos.getX() - entityPos.getX()) + 8;
            int newY = thisPos.getY() - entityPos.getY() >= 0 ? thisPos.getY() - entityPos.getY() : -(thisPos.getY() - entityPos.getY()) + 8;
            int newZ = thisPos.getZ() - entityPos.getZ() >= 0 ? thisPos.getZ() - entityPos.getZ() : -(thisPos.getZ() - entityPos.getZ()) + 8;

            return new BlockPos(newX, newY, newZ);
        }
    }

}
