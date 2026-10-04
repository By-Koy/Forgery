package io.github.by_koy.forgery;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forgery implements ModInitializer {
	public static final String MOD_ID = "forgery";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	// public static final RegistryKey<PlacedFeature> PALLADIUM_ORE_PLACED_KEY = Registry.register(Registries.PLACED_FEATURE, id("ore_custom"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModItems.initialize();
		ModBlocks.initialize();

		LOGGER.info("Forgery initialized!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
