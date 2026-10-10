package io.github.by_koy.forgery;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ForgeryItems {
    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
		.register((creativeTab) -> creativeTab.accept(ForgeryItems.PALLADIUM_INGOT));
    }

    // Create items
	public static final Item PALLADIUM_INGOT = create("palladium_ingot", new Item.Properties());
    public static final Item RAW_PALLADIUM = create("raw_palladium", new Item.Properties());

    // Method to attach the items to the game
    public static Item create(String name, Item.Properties settings) {
        // Create a default Item
        Function<Item.Properties, Item> properties = Item::new;

        // Create item key (ID) and item (with properties)
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Forgery.id(name));
        Item item = properties.apply(settings.setId(itemKey));

        // Register and return
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
	}
}
