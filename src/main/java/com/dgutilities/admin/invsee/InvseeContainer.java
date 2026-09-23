package com.dgutilities.admin.invsee;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.NotNull;

public class InvseeContainer implements Container {

    private static final int SIZE = 45;

    private final Inventory targetInventory;
    private final Player target;
    private final SimpleContainer reserveSlots = new SimpleContainer(4);

    public InvseeContainer(Player target) {
        this.target = target;
        this.targetInventory = target.getInventory();
    }

    /*
     * Converte o índice do container de 45 slots
     * para o índice real do inventário do jogador.
     *
     * Container:
     *
     * 0  -> capacete    (39)
     * 1  -> peitoral    (38)
     * 2  -> calças      (37)
     * 3  -> botas       (36)
     * 4  -> offhand     (40)
     * 5-8 -> bloqueados
     *
     * 9-35  -> inventário principal (9-35)
     * 36-44 -> hotbar (0-8)
     */
    private int mapSlot(int slot) {

        return switch (slot) {
            case 0 -> 39;
            case 1 -> 38;
            case 2 -> 37;
            case 3 -> 36;
            case 4 -> 40;

            default -> {
                if (slot >= 9 && slot <= 35) {
                    yield slot;
                }

                if (slot >= 36 && slot <= 44) {
                    yield slot - 36;
                }

                // Slots 5, 6, 7 e 8
                yield -1;
            }
        };
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {

        for (int slot = 0; slot < SIZE; slot++) {

            int mapped = mapSlot(slot);

            if (mapped != -1 &&
                    !targetInventory.getItem(mapped).isEmpty()) {

                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {

        if (isReserveSlot(slot)) {
            return reserveSlots.getItem(slot - 5);
        }

        int mapped = mapSlot(slot);

        if (mapped == -1) {
            return ItemStack.EMPTY;
        }

        return targetInventory.getItem(mapped);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {

        if (isReserveSlot(slot)) {
            return reserveSlots.removeItem(slot - 5, amount);
        }

        int mapped = mapSlot(slot);

        if (mapped == -1) {
            return ItemStack.EMPTY;
        }

        ItemStack result =
                targetInventory.removeItem(mapped, amount);

        setChanged();

        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {

        if (isReserveSlot(slot)) {
            return reserveSlots.removeItemNoUpdate(slot - 5);
        }

        int mapped = mapSlot(slot);

        if (mapped == -1) {
            return ItemStack.EMPTY;
        }

        return targetInventory.removeItemNoUpdate(mapped);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {

        if (isReserveSlot(slot)) {
            reserveSlots.setItem(slot - 5, stack);
            return;
        }

        int mapped = mapSlot(slot);

        if (mapped == -1) {
            return;
        }

        targetInventory.setItem(mapped, stack);

        setChanged();
    }

    @Override
    public void setChanged() {
        targetInventory.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {

        /*
         * O menu permanece aberto enquanto
         * o jogador alvo ainda estiver válido.
         *
         * Isso também permite /invsee em si mesmo.
         */
        return target.isAlive();
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (isReserveSlot(slot)) {
            return false;
        }
        return mapSlot(slot) != -1;
    }

    @Override
    public void clearContent() {
        //Limpa apenas os 41 slots reais.
        for (int slot = 0; slot < SIZE; slot++) {
            if (isReserveSlot(slot)) {
                continue;
            }
            int mapped = mapSlot(slot);
            if (mapped != -1) {
                targetInventory.setItem(
                        mapped,
                        ItemStack.EMPTY
                );
            }
        }
        reserveSlots.clearContent();
        setChanged();
    }

    private boolean isReserveSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }

    public boolean isTarget(Player player) {
        return target.getUUID().equals(player.getUUID());
    }
}