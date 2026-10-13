package io.github.by_koy.forgery;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ForgeryMenuTypes {
    public static final MenuType<MelterMenu> MELTER = register("melter", MelterMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(
            final String name,
            final MenuType.MenuSupplier<T> constructor
    ) {
        return Registry.register(BuiltInRegistries.MENU, Forgery.id(name), new MenuType<>(constructor, FeatureFlagSet.of()));
    }

    public static void initialize() {

    }
}
