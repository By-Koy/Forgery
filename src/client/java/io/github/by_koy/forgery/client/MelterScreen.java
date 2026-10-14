package io.github.by_koy.forgery.client;

import io.github.by_koy.forgery.Forgery;
import io.github.by_koy.forgery.menus.MelterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MelterScreen extends AbstractContainerScreen<MelterMenu> {
    private static final Identifier MENU_SPRITE = Forgery.id("textures/gui/container/melter_off.png");
    private static final Identifier ON_SPRITE = Forgery.id("textures/gui/container/melter_on.png");
    private static final Identifier SOUL_SPRITE = Forgery.id("textures/gui/container/melter_soul.png");
    private static final int MELT_TIME = 100;

    private final MelterMenu melter;
    private final Inventory playerInv;
    private final Component title;
    private final int imageWidth;
    private final int imageHeight;

    public MelterScreen(MelterMenu menu, Inventory inventory, Component title) {
        this(menu, inventory, title, 176, 166);
    }

    public MelterScreen(MelterMenu menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);

        this.melter = menu;
        this.playerInv = inventory;
        this.title = title;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, MENU_SPRITE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, BACKGROUND_TEXTURE_WIDTH, BACKGROUND_TEXTURE_HEIGHT);
    }
}
