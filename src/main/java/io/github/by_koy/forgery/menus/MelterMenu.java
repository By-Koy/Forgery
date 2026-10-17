package io.github.by_koy.forgery.menus;

import io.github.by_koy.forgery.ForgeryBlockEntities.Melter;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import java.util.ArrayList;

import java.util.List;

public class MelterMenu extends AbstractContainerMenu {
    private final static int MELT_SLOTS_START = 0;
    private final static int MELT_SLOTS_END = 3;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_START = 31;
    private static final int USE_ROW_END = 40;
    private static final int SLOT_SPACING = 25;

    private Container container;
    private Melter melter;
    private final List<Slot> meltSlots;

    // Client-side constructors
    public MelterMenu(int containerId, Container inventory) {
        this(containerId, inventory, new SimpleContainer(3));
    }

    public MelterMenu(int containerId, Container inventory, Container container) {
        super(ForgeryMenuTypes.MELTER, containerId);

        this.container = container;
        this.meltSlots = createSlots(container);
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    // Useful constructor
    public MelterMenu(int containerId, Container inventory, Melter melter) {
        super(ForgeryMenuTypes.MELTER, containerId);

        this.melter = melter;
        this.meltSlots = createSlots(melter);
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    private List<Slot> createSlots(Container container) {
        List<Slot> Slots = new ArrayList<>();
        for (int i = MELT_SLOTS_START; i < MELT_SLOTS_END; i++) {
            Slot slot = new Slot(container, i, 37 + i * (SLOT_SIZE + SLOT_SPACING), 35);

            this.addSlot(slot);
            Slots.add(slot);
        }

        return Slots;
    }

    @Override @NullMarked
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            clicked = slot.getItem();
            if (slot.index != 0) {
                if (this.container.canPlaceItem(0, clicked)) {
                    if (!meltSlots.get(0).hasItem()) {
                        this.moveItemStackTo(clicked, MELT_SLOTS_START, INV_SLOT_START, false);
                    }
                }
            } else {
                this.moveItemStackTo(clicked, INV_SLOT_START, USE_ROW_END, true);
            }

            slot.setChanged();
        } else {
            slot.setByPlayer(ItemStack.EMPTY);
        }
        slot.onTake(player, clicked);

        return clicked;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        if (this.container == null) {
            return this.melter.stillValid(player);

        } else {
            return this.container.stillValid(player);
        }
    }

    public void createFluid() {
        melter.createMeltedFluid();
    }
}

