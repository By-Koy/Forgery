package io.github.by_koy.forgery.menus;

import io.github.by_koy.forgery.Forgery;
import io.github.by_koy.forgery.ForgeryBlockEntities.Melter;
import io.github.by_koy.forgery.ForgeryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import java.util.ArrayList;

import java.util.List;

import static io.github.by_koy.forgery.ForgeryBlocks.MelterBlock.melterActive.off;

public class MelterMenu extends AbstractContainerMenu {
    public final static int MELT_SLOTS_START = 0;
    public final static int MELT_SLOTS_END = 2;
    private final static int INV_SLOT_START = 3;
//    private final static int INV_SLOT_END = 30;
//    private final static int USE_ROW_START = 30;
    private final static int USE_ROW_END = 39;
    private final static int SLOT_SPACING = 25;

    private final ContainerData data;
    public final List<Slot> meltSlots;
    public Melter melter;

    // Client-side constructor
    public MelterMenu(int containerId, Container inventory) {
        this(containerId, inventory, new Melter(BlockPos.ZERO, ForgeryBlocks.MELTER.block.defaultBlockState()), new SimpleContainerData(2));
    }

    // Server-side constructor
    public MelterMenu(int containerId, Container inventory, Melter melter, ContainerData data) {
        super(ForgeryMenuTypes.MELTER, containerId);

        this.data = data;
        this.addDataSlots(data);
        this.melter = melter;
        this.meltSlots = createSlots();
        this.addStandardInventorySlots(inventory, 8, 84);

        setActive();

    }

    private List<Slot> createSlots() {
        List<Slot> Slots = new ArrayList<>();
        for (int i = MELT_SLOTS_START; i <= MELT_SLOTS_END; i++) {
            Slot slot = new Slot(this.melter, i, 37 + i * (SLOT_SIZE + SLOT_SPACING), 35);

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
                    if (this.melter.canPlaceItem(meltSlot.index, clicked)) {
                        if (!meltSlot.hasItem()) {
                            this.moveItemStackTo(clicked, meltSlot.index, INV_SLOT_START, false);
                            this.melter.setItem(meltSlot.index, clicked);
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

    public ForgeryBlocks.MelterBlock.melterActive getActive() {
        return switch (data.get(0)) {
            case 0 -> off;
            case 1 -> ForgeryBlocks.MelterBlock.melterActive.on;
            case 2 -> ForgeryBlocks.MelterBlock.melterActive.soul;
            default -> setActive();
        };
    }

    public ForgeryBlocks.MelterBlock.melterActive setActive() {
         switch (this.melter.getBlockState().getValue(ForgeryBlocks.MelterBlock.ACTIVE)) {
             case off -> data.set(0, 0);
             case on -> data.set(0, 1);
             case soul -> data.set(0, 2);
         }

         Forgery.LOGGER.info("set data to: {}", data.get(0));
         return getActive();
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return this.melter.stillValid(player);
    }
}
