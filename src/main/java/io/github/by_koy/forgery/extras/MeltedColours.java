package io.github.by_koy.forgery.extras;

import io.github.by_koy.forgery.ForgeryItems;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class MeltedColours {
    public static int getColour(Item item) {
        if (item == Items.IRON_INGOT || item == Items.RAW_IRON) {
            return ARGB.opaque(0x9c9c9c);
        } else if (item == Items.GOLD_INGOT || item == Items.RAW_GOLD) {
            return ARGB.opaque(0xdcb343);
        } else if (item == ForgeryItems.PALLADIUM_INGOT.item || item == ForgeryItems.RAW_PALLADIUM.item) {
            return ARGB.opaque(0x585955);
        } else {
            return ARGB.transparent(0x000000);
        }
    }
}
