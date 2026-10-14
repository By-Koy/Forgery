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
		.register((creativeTab) -> creativeTab.accept(ForgeryItems.PALLADIUM_INGOT.item));
    }

    public static class ModItems {
        public Item item;
        public ResourceKey<Item> resourceKey;

        public ModItems(ResourceKey<Item> resourceKey, Item item) {
            this.resourceKey = resourceKey;
            this.item = item;
        }
    }

    // Create items
	public static final ModItems PALLADIUM_INGOT = register("palladium_ingot", new Item.Properties());
    public static final ModItems RAW_PALLADIUM = register("raw_palladium", new Item.Properties());

    public static ModItems register(String name, Item.Properties settings) {

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Forgery.id(name));
        Item item = create(itemKey, settings);

        return new ModItems(ResourceKey.create(Registries.ITEM, Forgery.id(name)), item);

    }

    // Method to attach the items to the game
    public static Item create(ResourceKey<Item> itemKey, Item.Properties settings) {
        // Create a default Item
        Function<Item.Properties, Item> properties = Item::new;

        // Create item key (ID) and item (with properties)
        Item item = properties.apply(settings.setId(itemKey));

        // Register and return
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
	}
}
