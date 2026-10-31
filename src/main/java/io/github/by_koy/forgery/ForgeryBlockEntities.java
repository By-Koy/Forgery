package io.github.by_koy.forgery;

import static io.github.by_koy.forgery.ForgeryBlocks.CastBlock.READY;
import static io.github.by_koy.forgery.ForgeryBlocks.MelterBlock.melterActive.*;
import static io.github.by_koy.forgery.ForgeryBlocks.NozzleBlock.FACING;

import io.github.by_koy.forgery.datagen.ForgeryTags;
import io.github.by_koy.forgery.extras.ForgeryComponents;
import io.github.by_koy.forgery.extras.ImplementedContainer;
import io.github.by_koy.forgery.extras.MeltedColours;
import io.github.by_koy.forgery.menus.MelterMenu;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class ForgeryBlockEntities {

    public static final BlockEntityType<Melter> MELTER_BLOCK_ENTITY =
            register("melter", Melter::new, ForgeryBlocks.MELTER.block);

    public static final BlockEntityType<MeltedFluidEntity> MELTED_FLUID_ENTITY =
            register("melted_fluid", MeltedFluidEntity::new, ForgeryBlocks.MELTED_FLUID.block);

    public static final BlockEntityType<Cast> CAST_ENTITY =
            register("cast", Cast::new, ForgeryBlocks.CAST.block);

    public static final BlockEntityType<Nozzle> NOZZLE_ENTITY =
            register("nozzle", Nozzle::new, ForgeryBlocks.NOZZLE.block);

    @NonNull
    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Forgery.id(name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static class Melter extends BlockEntity implements ImplementedContainer, MenuProvider {
        private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
        private int tick = 0;
        private ForgeryBlocks.MelterBlock.melterActive active = off;

        protected final ContainerData dataAccess = new ContainerData() {
            @Override
            public int get(final int dataId) {
                return switch (dataId) {
                    case 0 -> switch (Melter.this.active) {
                                case off -> 0;
                                case on -> 1;
                                case soul -> 2;
                              };
                    case 1 -> Melter.this.tick;
                    default -> 0;
                };
            }

            @Override
            public void set(final int dataId, final int value) {
                switch (dataId) {
                    case 0:
                        Melter.this.active = switch (value) {
                                                case 1 -> on;
                                                case 2 -> soul;
                                                default -> off;
                        };
                    case 1:
                        Melter.this.tick = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };


        public Melter(BlockPos pos, BlockState state) {
            super(ForgeryBlockEntities.MELTER_BLOCK_ENTITY, pos, state);
        }

        @Override @NonNull
        public Component getDisplayName() {
            return Component.translatable("block.forgery.melter");
        }

        @Override
        public boolean canPlaceItem(final int slot, final ItemStack stack) {
            return stack.tags().anyMatch(Predicate.isEqual(ForgeryTags.MELTABLE));
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
            return new MelterMenu(containerId, inventory, this, dataAccess);
        }

        public static void tick(final Level level, final BlockPos pos, final BlockState selfState, final Melter entity) {
            if (level.getBlockState(pos.relative(Direction.DOWN)).is(Blocks.CAMPFIRE)) {
                level.setBlockAndUpdate(pos, selfState.setValue(ForgeryBlocks.MelterBlock.ACTIVE, on));
            } else if (level.getBlockState(pos.relative(Direction.DOWN)).is(Blocks.SOUL_CAMPFIRE)) {
                level.setBlockAndUpdate(pos, selfState.setValue(ForgeryBlocks.MelterBlock.ACTIVE, soul));
            } else {
                level.setBlockAndUpdate(pos, selfState.setValue(ForgeryBlocks.MelterBlock.ACTIVE, off));
            }

            if (selfState.getValue(ForgeryBlocks.MelterBlock.ACTIVE) != off) {
                if (!entity.getItems().equals(NonNullList.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY))) {
                    if (entity.tick >= 80) {
                        boolean melt = false;

                        for (int i = 0; i < entity.getContainerSize(); i++) {

                            ItemStack stack = entity.getItems().get(i);

                            if (entity.canPlaceItem(i, stack)) {

                                entity.createMeltedFluid(level);
                                entity.decrementItems(i, stack);
                                melt = true;
                            }

                            if (melt) {
                                break;
                            }
                        }

                        entity.tick = 0;
                    } else {
                        entity.tick++;
                    }
                }
            }
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            return this.items;
        }

        public void decrementItems(int slotIndex, ItemStack stack) {
            stack.shrink(1);
            this.setItem(slotIndex, stack);
        }

        public void createMeltedFluid(Level level) {
            BlockPos pos = this.worldPosition.relative(this.getBlockState().getValue(ForgeryBlocks.MelterBlock.FACING), 1);
            level.setBlock(pos, ForgeryBlocks.MELTED_FLUID.block.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.setBlockEntity(new MeltedFluidEntity(pos, ForgeryBlocks.MELTED_FLUID.block.defaultBlockState(), this.getItems()));
        }

        @Override
        protected void loadAdditional(@NonNull ValueInput input) {
            super.loadAdditional(input);
            ContainerHelper.loadAllItems(input, this.items);
        }

        @Override
        protected void saveAdditional(@NonNull ValueOutput output) {
            ContainerHelper.saveAllItems(output, this.items);
            super.saveAdditional(output);
        }
    }

    public static class MeltedFluidEntity extends BlockEntity {
        public NonNullList<ItemStack> items;
        private int colour;

        public MeltedFluidEntity(BlockPos pos, BlockState state) {
            this(pos, state, NonNullList.of(ItemStack.EMPTY));
        }

        public MeltedFluidEntity(BlockPos pos, BlockState state, NonNullList<ItemStack> items) {
            super(ForgeryBlockEntities.MELTED_FLUID_ENTITY, pos, state);

            this.items = items;
            getColourFromList();
            this.setChanged();
        }

        private void addColour(int newColour) {
            this.colour = ARGB.opaque(ARGB.addRgb(this.getColour(), newColour));
            this.setChanged();
        }

        public int getColour() {
            return this.colour;
        }

        public void getColourFromList() {
            for (ItemStack item: this.items) {
                this.addColour(MeltedColours.getColour(item.getItem()));
            }
        }

        @Override
        protected void loadAdditional(@NonNull ValueInput input) {
            super.loadAdditional(input);
            this.colour = input.getIntOr("colour" , ARGB.opaque(0x000000));

            this.setChanged();
        }

        @Override
        public void setChanged() {
            super.setChanged();

            if (level == null) return;

            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }

        @Override
        protected void saveAdditional(@NonNull ValueOutput output) {
            super.saveAdditional(output);
            output.putInt("colour", this.colour);
        }

        @Override @NonNull
        public CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
            return this.saveWithoutMetadata(registries);
        }

        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override @Nullable
        public Object getRenderData() {
            // Send the colour for tinting on the client
            return this.getColour() == 0 ? null : this.getColour();
        }
    }

    public static class Nozzle extends BlockEntity implements ImplementedContainer {
        private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
        private int fluidColour;

        public Nozzle(BlockPos pos, BlockState state) {
            super(ForgeryBlockEntities.NOZZLE_ENTITY, pos, state);
        }

        @Override
        public boolean canPlaceItem(final int slot, final ItemStack stack) {
            return stack.tags().anyMatch(Predicate.isEqual(ForgeryTags.MELTABLE));
        }

        public static void tick(final Level level, final BlockPos pos, final BlockState selfState, final Nozzle entity) {
            BlockPos checkPos = pos.relative(selfState.getValue(FACING));
            BlockState checkState = level.getBlockState(checkPos);

            if (checkState.is(ForgeryBlocks.MELTED_FLUID.block)) {
                BlockPos sourcePos = ForgeryBlocks.MeltedFluidBlock.interpretEntityPos(checkState, checkPos);
                MeltedFluidEntity sourceEntity = level.getBlockEntity(sourcePos).is(MELTED_FLUID_ENTITY) ? (MeltedFluidEntity) level.getBlockEntity(sourcePos) : null;

                entity.fluidColour = level.getBlockEntityRenderData(sourcePos) != null ? (int) level.getBlockEntityRenderData(sourcePos) : 0;
                entity.items = sourceEntity.items;

                level.setBlockAndUpdate(sourcePos, Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(checkPos, Blocks.AIR.defaultBlockState());

                entity.Cast(level);
            }
        }

        private void Cast(Level level) {
            BlockPos castPos = this.worldPosition.below().relative(this.getBlockState().getValue(FACING).getOpposite());
            BlockState castState = level.getBlockState(castPos);
            Cast castEntity = level.getBlockEntity(castPos).is(CAST_ENTITY) ? (Cast) level.getBlockEntity(castPos) : null;

            if (castState.is(ForgeryBlocks.CAST.block)) {
               if (castState.getValue(READY) && castEntity != null) {
                   castEntity.castItem(this.items);
               }
            }
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            return this.items;
        }

        @Override
        protected void loadAdditional(@NonNull ValueInput input) {
            super.loadAdditional(input);
            ContainerHelper.loadAllItems(input, this.items);
        }

        @Override
        protected void saveAdditional(@NonNull ValueOutput output) {
            ContainerHelper.saveAllItems(output, this.items);
            super.saveAdditional(output);
        }
    }

    public static class Cast extends BlockEntity implements ImplementedContainer {
        public enum stencilTypes {
            block,
        } private ItemStack stencil = ItemStack.EMPTY;

        private ItemStack result = ItemStack.EMPTY;


        public Cast(BlockPos pos, BlockState state) {
            super(ForgeryBlockEntities.CAST_ENTITY, pos, state);
        }

        @Override
        public boolean canPlaceItem(final int slot, final ItemStack stack) {
            return stack.tags().anyMatch(Predicate.isEqual(ForgeryTags.MELTABLE));
        }

        public void castItem(NonNullList<ItemStack> items) {
            this.getBlockState().setValue(READY, false);

            ItemStack type = switch (stencil) {
                                    default -> new ItemStack(ForgeryBlocks.METAL_BLOCK.blockItem);
                                };

            if (result.isEmpty()) {
                type.set(ForgeryComponents.COMPOUND, items);
                result = type;
            }

            this.getBlockState().setValue(READY, true);
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            return NonNullList.of(this.stencil, this.result);
        }

        public ItemStack getResult() {
            return this.result;
        }

        @Override
        protected void loadAdditional(@NonNull ValueInput input) {
            super.loadAdditional(input);

            result = input.read("result", ItemStack.CODEC).orElse(ItemStack.EMPTY);
            stencil = input.read("stencil", ItemStack.CODEC).orElse(ItemStack.EMPTY);

            this.setChanged();
        }

        @Override
        protected void saveAdditional(@NonNull ValueOutput output) {
            output.store("result", ItemStack.CODEC, this.result);
            output.store("stencil", ItemStack.CODEC, this.stencil);

            super.saveAdditional(output);
        }
    }


}
