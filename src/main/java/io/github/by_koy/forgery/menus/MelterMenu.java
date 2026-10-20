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
    private final static int MELT_SLOTS_END = 2;
    private final static int INV_SLOT_START = 3;
    private final static int INV_SLOT_END = 30;
    private final static int USE_ROW_START = 30;
    private final static int USE_ROW_END = 39;
    private final static int SLOT_SPACING = 25;

    private final Container container;
    private Melter melter;
    private final List<Slot> meltSlots;

    // Client-side constructors
    public MelterMenu(int containerId, Container inventory) {
        this(containerId, inventory, new SimpleContainer(3));
    }

    public MelterMenu(int containerId, Container inventory, Container container) {
        super(ForgeryMenuTypes.MELTER, containerId);

        this.container = container;
        this.meltSlots = createSlots();
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    // Useful constructor
    public MelterMenu(int containerId, Container inventory, Melter melter) {
        super(ForgeryMenuTypes.MELTER, containerId);

        this.melter = melter;
        this.container = melter;
        this.meltSlots = createSlots();
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    private List<Slot> createSlots() {
        List<Slot> Slots = new ArrayList<>();
        for (int i = MELT_SLOTS_START; i <= MELT_SLOTS_END; i++) {
            Slot slot = new Slot(this.container, i, 37 + i * (SLOT_SIZE + SLOT_SPACING), 35);

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
            if (slot.index > MELT_SLOTS_END) { // if shift clicked from inventory
                for (Slot meltSlot : meltSlots) {
                    if (this.container.canPlaceItem(meltSlot.index, clicked)) {
                        if (!meltSlot.hasItem()) {
                            this.moveItemStackTo(clicked, meltSlot.index, INV_SLOT_START, false);
                        }
                    }
                }
            } else { // // if shift clicked from melter
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
}

