package io.github.by_koy.forgery.extras;

import io.github.by_koy.forgery.ForgeryBlocks;
import io.github.by_koy.forgery.datagen.ForgeryTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.jspecify.annotations.NonNull;
import java.util.Optional;

public abstract class MeltedFluid extends FlowingFluid {
    @Override
    public void animateTick(@NonNull Level world, @NonNull BlockPos pos, FluidState state, @NonNull RandomSource random) {
        if (!state.isSource() && state.getValue(FALLING)) {
            if (random.nextInt(64) == 0) {
                world.playLocalSound(
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ForgerySoundEvents.MELTED_FLUID_BUBBLE,
                        SoundSource.AMBIENT,
                        random.nextFloat() * 0.25F + 0.75F,
                        random.nextFloat() + 0.5F,
                        false);
            }
        }
    }

    @Override
    protected boolean canConvertToSource(ServerLevel world) {
        return world.getGameRules().get(GameRules.WATER_SOURCE_CONVERSION);
    }

    @Override
    protected void spread(@NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull FluidState fluidState) {
//        super.spread(level, pos, state, fluidState);
//
//        if (state.getBlock() == ForgeryBlocks.MELTED_FLUID.block) {
//            BlockPos entityPos = ForgeryBlocks.MeltedFluidBlock.interpretEntityPos(
//                    state.getValue(ForgeryBlocks.MeltedFluidBlock.relPosX),
//                    state.getValue(ForgeryBlocks.MeltedFluidBlock.relPosY),
//                    state.getValue(ForgeryBlocks.MeltedFluidBlock.relPosZ),
//                    pos
//            );
//
//            for (Direction direction : Direction.Plane.HORIZONTAL) {
//                BlockPos newPos = pos.relative(direction, 1);
//                if (canMelt(level.getBlockState(newPos))) {
//                    level.setBlock(pos, defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL);
//                }
//
//                BlockState newState = level.getBlockState(newPos);
//
//                if (newState.getBlock() == ForgeryBlocks.MELTED_FLUID.block) {
//                    BlockPos subPos = ForgeryBlocks.MeltedFluidBlock.createEntityPos(
//                            newPos,
//                            entityPos
//                    );
//
//                level.setBlockAndUpdate(pos,
//                        newState.setValue(ForgeryBlocks.MeltedFluidBlock.relPosX, subPos.getX())
//                                .setValue(ForgeryBlocks.MeltedFluidBlock.relPosY, subPos.getY())
//                                .setValue(ForgeryBlocks.MeltedFluidBlock.relPosZ, subPos.getZ()));
//                }
//            }
//        }
    }

    private Boolean canMelt(BlockState state) {
        return !state.is(ForgeryTags.SURVIVES_MELTED);
    }

    @Override
    protected void beforeDestroyingBlock(@NonNull LevelAccessor world, @NonNull BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropResources(state, world, pos, blockEntity);
    }

    @Override
    protected void entityInside(@NonNull Level world, @NonNull BlockPos pos, @NonNull Entity entity, InsideBlockEffectApplier handler) {
        handler.apply(InsideBlockEffectType.EXTINGUISH);

        if (!(world instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity livingEntity)) return;

        if (world.getGameTime() % 10 == 0) { // Run every half-tick
            // Hurt and weaken entities that step inside.
            livingEntity.hurtServer(serverLevel, world.damageSources().magic(), 2.0F); // 2 hearts/sec
        }
    }

    @Override
    protected int getSlopeFindDistance(@NonNull LevelReader world) {
        return 4;
    }

    @Override
    public int getDropOff(@NonNull LevelReader world) {
        return 1;
    }

    @Override
    public int getTickDelay(@NonNull LevelReader world) {
        return 5;
    }

    @Override
    public boolean canBeReplacedWith(@NonNull FluidState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull Fluid fluid,
                                     @NonNull Direction direction) {
        return direction == Direction.DOWN && !fluid.isSame(this.getFlowing()) || !fluid.isSame(this.getSource());
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override @NonNull
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Override @NonNull
    public Item getBucket() {
        return Items.AIR;
    }

    @Override @NonNull
    public BlockState createLegacyBlock(final @NonNull FluidState fluidState) {
        return ForgeryBlocks.MELTED_FLUID.block.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
    }

    @Override @NonNull
    public Fluid getSource() {
        return ForgeryBlocks.MELTED_STILL.fluid;
    }

    @Override @NonNull
    public Fluid getFlowing() {
        return ForgeryBlocks.MELTED_FLOWING.fluid;
    }

    @Override
    public boolean isSame(@NonNull Fluid fluid) {
        return fluid == this.getSource() || fluid == this.getFlowing();
    }

    // Source class
    public static class Source extends MeltedFluid {

        @Override
        public boolean isSource(@NonNull FluidState fluidState) {
            return true;
        }

        @Override
        public int getAmount(@NonNull FluidState state) {
            return 8;
        }
    }

    // Flowing class
    public static class Flowing extends MeltedFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.@NonNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@NonNull FluidState state) {
            return false;
        }
    }
}
