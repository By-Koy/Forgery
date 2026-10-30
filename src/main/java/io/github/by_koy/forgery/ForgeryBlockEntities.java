package io.github.by_koy.forgery;

import static io.github.by_koy.forgery.ForgeryBlocks.MelterBlock.melterActive.*;
import io.github.by_koy.forgery.datagen.ForgeryTags;
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
            return new MelterMenu(containerId, inventory, this);
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
        public NonNullList<ItemStack> ITEMS;
        private int colour;

        public MeltedFluidEntity(BlockPos pos, BlockState state) {
            this(pos, state, NonNullList.of(ItemStack.EMPTY));
        }

        public MeltedFluidEntity(BlockPos pos, BlockState state, NonNullList<ItemStack> items) {
            super(ForgeryBlockEntities.MELTED_FLUID_ENTITY, pos, state);

            this.ITEMS = items;
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
            for (ItemStack item: this.ITEMS) {
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

}
