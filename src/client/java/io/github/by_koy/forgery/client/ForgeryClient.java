package io.github.by_koy.forgery.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.by_koy.forgery.Forgery;
import io.github.by_koy.forgery.client.render.ForgeryRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;

public class ForgeryClient implements ClientModInitializer {

    KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Forgery.id("custom_category")
    );

    KeyMapping sendToChatKey = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.example-mod.send_to_chat", // The translation key for the key mapping.
                    InputConstants.Type.KEYBOARD, // The type of the keybinding; KEYSYM for keyboard, MOUSE for mouse.
                    InputConstants.KEY_J, // The keycode of the key.
                    CATEGORY // The category of the mapping.
            ));

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

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