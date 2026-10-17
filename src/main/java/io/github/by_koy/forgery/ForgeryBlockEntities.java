package io.github.by_koy.forgery;

import io.github.by_koy.forgery.extras.ImplementedContainer;
import io.github.by_koy.forgery.extras.MeltedColours;
import io.github.by_koy.forgery.menus.MelterMenu;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
        private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

        public Melter(BlockPos pos, BlockState state) {
            super(ForgeryBlockEntities.MELTER_BLOCK_ENTITY, pos, state);
        }

        private int ticks = 0;

        public int getTicks() {
            return this.ticks;
        }

        public void incrementTicks() {
            this.ticks++;
            this.setChanged();
        }

        @Override @NonNull
        public Component getDisplayName() {
            return Component.translatable("block.forgery.melter");
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
            return new MelterMenu(containerId, inventory, this);
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            return this.items;
        }

        public void createMeltedFluid() {
            new MeltedFluidEntity(this.worldPosition.relative(Direction.UP, 1), ForgeryBlocks.MELTED_FLUID.block.defaultBlockState(), this.items);
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
        private int colour;

        public MeltedFluidEntity(BlockPos pos, BlockState state) {
            this(pos, state, NonNullList.of(ItemStack.EMPTY));
        }

        public MeltedFluidEntity(BlockPos pos, BlockState state, NonNullList<ItemStack> items) {
            super(ForgeryBlockEntities.MELTED_FLUID_ENTITY, pos, state);
            this.colour = 0x00000000;
            getColourFromList(items);
        }

        private void addColour(int newColour) {
            this.colour = ARGB.addRgb(colour, newColour);
        }

        public int getColour() {
            return colour;
        }

        public void getColourFromList(NonNullList<ItemStack> list) {
            for (ItemStack item: list) {
                this.addColour(MeltedColours.getColour(item.getItem()));
            }
        }

        @Override
        protected void loadAdditional(@NonNull ValueInput input) {
            super.loadAdditional(input);
            this.colour = input.getIntOr("colour" ,0x00000000);
        }

        @Override
        protected void saveAdditional(@NonNull ValueOutput output) {
            output.putInt("colour", this.colour);

            super.saveAdditional(output);
        }

        @Override
        public @Nullable Object getRenderData() {
            // Send the colour for tinting on the client
            return this.colour;
        }
    }

}
