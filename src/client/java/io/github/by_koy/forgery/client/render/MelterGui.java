package io.github.by_koy.forgery.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ForgeryGuiRendering extends Screen {
    public ForgeryGuiRendering() {
        super(Component.empty());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int rectangleX = 10;
        int rectangleY = 10;
        int rectangleWidth = 100;
        int rectangleHeight = 50;
// x1, y1, x2, y2, color
        graphics.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, 0xFF0000FF);
    }
}
