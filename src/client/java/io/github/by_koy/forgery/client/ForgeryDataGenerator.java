package io.github.by_koy.forgery.client;

import io.github.by_koy.forgery.datagen.ForgeryFeatures;
import io.github.by_koy.forgery.datagen.ForgeryPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ForgeryDataGenerator implements DataGeneratorEntrypoint {
    @Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ForgeryWorldGen::new);
		pack.addProvider(ForgeryTags.ForgeryItemTags::new);
		pack.addProvider(ForgeryTags.ForgeryFluidTags::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.FEATURE, ForgeryFeatures::configure);
		registryBuilder.add(Registries.PLACED_FEATURE, ForgeryPlacedFeatures::configure);
	}
}