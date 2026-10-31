package io.github.by_koy.forgery;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ForgeryItems {
    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, FORGERY_CREATIVE_TAB_KEY, FORGERY_CREATIVE_TAB);

        Forgery.LOGGER.info("Initialized items and creative menu");

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

    // Create the creative mod tab
    public static final ResourceKey<CreativeModeTab> FORGERY_CREATIVE_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Forgery.id("forgery_creative_tab")
    );
    public static final CreativeModeTab FORGERY_CREATIVE_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(PALLADIUM_INGOT.item))
            .title(Component.translatable("creativeTab.forgery"))
            .displayItems((params, output) -> {
                // Add Items
                output.accept(PALLADIUM_INGOT.item);
                output.accept(RAW_PALLADIUM.item);

                // Add Blocks
                output.accept(ForgeryBlocks.DEEPSLATE_PALLADIUM_ORE.block);
                output.accept(ForgeryBlocks.PALLADIUM_ORE.block);
                output.accept(ForgeryBlocks.CAST.block);
                output.accept(ForgeryBlocks.MELTER.block);
                output.accept(ForgeryBlocks.NOZZLE.block);
            })
            .build();
}
