package io.github.mpcs.container;

import io.github.mpcs.BackpackItem;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockCallback;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.network.PacketByteBuf;

public class BackpackContainer extends ScreenHandler {
    private final BackpackInventory inv;
    //private final BlockContext context;
    private final PlayerEntity player;
    private int backpackSlots;
    private Hand hand;

    public BackpackContainer(int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        this(syncId, playerInv, buf.readInt(), buf.readInt() == 1 ? Hand.MAIN_HAND : Hand.OFF_HAND);
    }

    public BackpackContainer(int syncId, PlayerInventory playerInv, int backpackSlots, Hand hand) {
        super(null, syncId);
        //super(ScreenHandlerType.GENERIC_9X3, syncId,playerInv,new BackpackInventory(backpackSlots, this, playerInv.player),backpackSlots);
        this.inv = new BackpackInventory(backpackSlots, this, playerInv.player);
        this.player = playerInv.player;
        this.backpackSlots = backpackSlots;
        this.hand = hand;

        int spacing;
        if(backpackSlots % 9 == 0)
            spacing = 30 + (backpackSlots /9) * 18 + ((backpackSlots /9) < 5 ? 0 : 2);
        else
            spacing = 30 + (backpackSlots /9 + 1) * 18 + ((backpackSlots /9) < 5 ? 0 : 2);

        for(int y = 0; y < (backpackSlots /9); y++) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new BackpackSlot(inv, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
        if((backpackSlots % 9) != 0)
            for(int x = 0; x < (backpackSlots % 9); x++) {
                this.addSlot(new BackpackSlot(inv, x + (backpackSlots /9) * 9, 8 + x * 18, 18 + (backpackSlots /9) * 18));
            }


        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, spacing + y * 18));
            }
        }

        for(int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInv, x, 8 + x * 18, 58 + spacing));
        }

        DefaultedList<ItemStack> ad = DefaultedList.ofSize(backpackSlots, ItemStack.EMPTY);
        BackpackItem.getInventory(player.getStackInHand(this.hand), ad);
        if(ad.size() == 0)
            return;

        for(int x = 0; x < 9; x++)
            for(int y = 0; y < backpackSlots /9; y++) {
                this.getSlot(x + y * 9).setStack(ad.get(x+y*9));
            }

        for(int x = 0; x < (backpackSlots % 9); x++) {
            this.getSlot(x + (backpackSlots /9)*9).setStack(ad.get(x+(backpackSlots /9)*9));
        }

    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public PlayerInventory getInventory() {
        return player.inventory;
    }

    public void onContentChanged(Inventory inv) {
        super.onContentChanged(inv);
        if (inv == this.inv) {
            this.updateInv();
        }
    }

    public void close(PlayerEntity player) {
        super.close(player);

        DefaultedList<ItemStack> items = (DefaultedList<ItemStack>) DefaultedList.ofSize(backpackSlots *9, ItemStack.EMPTY);
        items = inv.getList(items);
        BackpackItem.setInventory(player.getStackInHand(this.hand), items);
        //this.context.run((world, pos) -> {
        //    this.dropInventory(player, world, this.inv);
        //});
        inv.onClose(player);
    }

    public void updateInv() {
        this.sendContentUpdates();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotNum) {
        ItemStack copy = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(slotNum);
        if (clickedSlot != null && clickedSlot.hasStack()) {
            ItemStack clickedStack = clickedSlot.getStack();
            copy = clickedStack.copy();
            if (slotNum < backpackSlots) {
                if (!this.insertItem(clickedStack, backpackSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(clickedStack, 0, backpackSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                clickedSlot.setStack(ItemStack.EMPTY);
            } else {
                clickedSlot.markDirty();
            }
        }

        return copy;
    }

    @Override
    public ItemStack onSlotClick(int int_1, int int_2, SlotActionType slotActionType_1, PlayerEntity playerEntity_1) {
        if(int_1 > 0) {
            if (this.getSlot(int_1).getStack().equals(playerEntity_1.getStackInHand(hand)))
                return ItemStack.EMPTY;
        }
        return super.onSlotClick(int_1, int_2, slotActionType_1, playerEntity_1);
    }
}
