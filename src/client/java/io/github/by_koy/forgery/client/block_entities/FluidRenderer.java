package io.github.by_koy.forgery.client.block_entities;

import io.github.by_koy.forgery.Forgery;
import io.github.by_koy.forgery.ForgeryBlocks;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;

import java.util.Set;

import static io.github.by_koy.forgery.ForgeryBlocks.MeltedFluidBlock.interpretEntityPos;

public class FluidRenderer {
    public static void initialize() {
        FluidRenderingRegistry.register(
                ForgeryBlocks.MELTED_STILL.fluid,
                ForgeryBlocks.MELTED_FLOWING.fluid,
                new FluidModel.Unbaked(
                        new Material(Forgery.id("block/melted_fluid")),
                        new Material(Forgery.id("block/melted_fluid")),
                        new Material(Forgery.id("block/melted_fluid")),
                        new BlockTintSource() {

                            @Override
                            public int color(@NonNull BlockState state) {
                                return 0;
                            }

                            @Override
                            public int colorInWorld(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
                                BlockPos entityPos = interpretEntityPos(
                                        state.getValue(ForgeryBlocks.MeltedFluidBlock.relPosX),
                                        state.getValue(ForgeryBlocks.MeltedFluidBlock.relPosY),
                                        state.getValue(ForgeryBlocks.MeltedFluidBlock.relPosZ),
                                        pos);

                                Object fluidData = level.getBlockEntityRenderData(entityPos);
                                return fluidData != null ? ARGB.opaque((int) fluidData) : 0;
                            }

                            @Override
                            public int colorAsTerrainParticle(@NonNull BlockState state, @NonNull BlockAndTintGetter level, @NonNull BlockPos pos) {
                                return BlockTintSource.super.colorAsTerrainParticle(state, level, pos);
                            }

                            @Override @NonNull
                            public Set<Property<?>> relevantProperties() {
                                return BlockTintSource.super.relevantProperties();
                            }
                        }
                )
        );
    }
}
