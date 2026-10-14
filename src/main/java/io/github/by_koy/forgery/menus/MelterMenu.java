package io.github.by_koy.forgery.menus;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

public class MelterMenu extends AbstractContainerMenu {
    private final static int MELT_SLOT = 0;
    private static final int INV_SLOT_START = 1;
    private static final int INV_SLOT_END = 28;
    private static final int USE_ROW_START = 28;
    private static final int USE_ROW_END = 37;

    private final Container melter;
    private final Slot meltSlot;

    // Client-side constructor
    public MelterMenu(int containerId, Container inventory) {
        this(containerId, inventory, new SimpleContainer(1));
    }


    public MelterMenu(int containerId, Container inventory, Container container) {
        super(ForgeryMenuTypes.MELTER, containerId);

        this.melter = container;
        meltSlot = new Slot(this.melter, MELT_SLOT, 80, 35);
        this.addSlot(meltSlot);
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    @Override @NullMarked
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            clicked = slot.getItem();
            if (slot.index != 0) {
                if (this.melter.canPlaceItem(0, clicked)) {
                    if (!meltSlot.hasItem()) {
                        this.moveItemStackTo(clicked, MELT_SLOT, INV_SLOT_START, false);
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
        return this.melter.stillValid(player);
    }
}

