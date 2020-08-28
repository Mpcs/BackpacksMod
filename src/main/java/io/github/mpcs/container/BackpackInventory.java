package io.github.mpcs.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.collection.DefaultedList;

import java.util.Iterator;

public class BackpackInventory implements Inventory {
    public static final int SECTION_SIZE = 9;
    private final DefaultedList<ItemStack> stacks;
    public final PlayerEntity accessor;
    private BackpackContainer container;

    public BackpackInventory(int rows, BackpackContainer container, PlayerEntity player) {
        this.accessor = player;
        this.stacks = DefaultedList.ofSize(SECTION_SIZE * rows, ItemStack.EMPTY);
        this.container = container;
    }

    public DefaultedList<ItemStack> getList(DefaultedList dl) {
        dl = this.stacks;
        return dl;
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator var1 = this.stacks.iterator();

        ItemStack itemStack_1;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack_1 = (ItemStack)var1.next();
        } while(itemStack_1.isEmpty());

        return false;
    }

    @Override
    public ItemStack getStack(int i) {
        return i >= stacks.size() ? ItemStack.EMPTY : stacks.get(i);
    }

    @Override
    public ItemStack removeStack(int int_1, int int_2) {
        ItemStack itemStack_1 = Inventories.splitStack(this.stacks, int_1, int_2);
        if (!itemStack_1.isEmpty()) {
            this.container.onContentChanged(this);
        }

        return itemStack_1;
    }

    @Override
    public ItemStack removeStack(int i) {
        return Inventories.removeStack(this.stacks, i);
    }

    @Override
    public void setStack(int i, ItemStack itemStack) {
        this.stacks.set(i, itemStack);
        this.container.onContentChanged(this);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public void clear() {
        stacks.clear();
    }
}
