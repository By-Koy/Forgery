package io.github.by_koy.forgery.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.by_koy.forgery.*;
import io.github.by_koy.forgery.client.block_entities.MelterScreen;
import io.github.by_koy.forgery.menus.ForgeryMenuTypes;
import io.github.by_koy.forgery.menus.MelterMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public class ForgeryClient implements ClientModInitializer {

    KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Forgery.id("custom_category")
    );

    KeyMapping sendToChatKey = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.forgery.send_to_chat", // The translation key for the key mapping.
                    InputConstants.Type.KEYBOARD, // The type of the keybinding; KEYBOARD for keyboard, MOUSE for mouse.
                    InputConstants.KEY_J, // The keycode of the key.
                    CATEGORY // The category of the mapping.
            ));

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

        new ForgeryBlockEntities.Melter(BlockPos.ZERO, ForgeryBlocks.MELTER.block.defaultBlockState());
        MenuScreens.register(ForgeryMenuTypes.MELTER, (MenuScreens.ScreenConstructor<MelterMenu, MelterScreen>) MelterScreen::new);

        FluidRenderingRegistry.register(
                ForgeryBlocks.MELTED_STILL.fluid,
                ForgeryBlocks.MELTED_FLOWING.fluid,
                new FluidModel.Unbaked(
                        new Material(Forgery.id("blocks/melted_fluid")),
                        new Material(Forgery.id("blocks/melted_fluid")),
                        new Material(Forgery.id("blocks/melted_fluid")),
                        new BlockTintSource() {

                            @Override
                            public int color(BlockState state) {
                                return 0;
                            }

                            @Override
                            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                                return (int) level.getBlockEntityRenderData(pos);
                            }

                            @Override
                            public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                                return BlockTintSource.super.colorAsTerrainParticle(state, level, pos);
                            }

                            @Override
                            public Set<Property<?>> relevantProperties() {
                                return BlockTintSource.super.relevantProperties();
                            }
                        }
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (this.sendToChatKey.consumeClick()) {
                if (client.player == null) return;

                client.player.sendSystemMessage(Component.literal("Key press detected in the world"));
            }
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof CreativeModeInventoryScreen) && !(screen instanceof TitleScreen)) {
                return;
            }

            ScreenKeyboardEvents.beforeKeyPress(screen).register((s, keyEvent) -> {
                if (!this.sendToChatKey.matches(keyEvent)) return;

                this.handleKeyPressInMainScreen(client);
                this.handleKeyPressInGameScreen(client);
            });
        });
	}

    private void handleKeyPressInMainScreen(Minecraft client) {
        if (client.player != null) return;


        Forgery.LOGGER.info("Key press detected in the title screen");
    }

    private void handleKeyPressInGameScreen(Minecraft client) {
        if (client.player == null) return;


        client.player.sendSystemMessage(Component.literal("Key press detected in the GUI with a world open, closing screen"));
        client.gui.setScreen(null);
    }
}