package io.github.by_koy.forgery.extras;

import io.github.by_koy.forgery.ForgeryItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class MeltedColours {
    public enum colours {;
        final static int IRON = 0xff9c9c9c;
        final static int GOLD = 0xffdcb343;
        final static int PALLADIUM = 0xff585955;
    }
    public static int getColour(Item item) {
        if (item == Items.IRON_INGOT || item == Items.RAW_IRON) {
            return colours.IRON;
        } else if (item == Items.GOLD_INGOT || item == Items.RAW_GOLD) {
            return colours.GOLD;
        } else if (item == ForgeryItems.PALLADIUM_INGOT.item || item == ForgeryItems.RAW_PALLADIUM.item) {
            return colours.PALLADIUM;
        } else {
            return 0x00000000;
        }
    }
}
