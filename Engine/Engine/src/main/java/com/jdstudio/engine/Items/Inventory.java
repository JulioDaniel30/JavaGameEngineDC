package com.jdstudio.engine.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of {@link ItemStack}s for a player or entity.
 * It handles adding and removing items, respecting item stack sizes and inventory capacity.
 * 
 * @author JDStudio
 */
public class Inventory {

    private final List<ItemStack> items;
    private final int capacity; // Number of inventory slots

    /**
     * Constructs a new Inventory with a specified capacity.
     * @param capacity The maximum number of item stacks the inventory can hold.
     */
    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
    }

    /**
     * Attempts to add an item to the inventory.
     * It first tries to stack the item with existing stacks, then creates new stacks in empty slots.
     *
     * @param itemToAdd The item to be added.
     * @param quantity  The quantity to be added.
     * @return The quantity of items that DID NOT fit into the inventory. Returns 0 if everything fit.
     */
    public int addItem(Item itemToAdd, int quantity) {
        int remainingQuantity = quantity;

        // 1. Try to stack with existing stacks
        for (ItemStack stack : items) {
            if (stack.getItem().id.equals(itemToAdd.id)) {
                int canAdd = stack.getItem().maxStackSize - stack.getQuantity();
                if (canAdd > 0) {
                    int amountToAdd = Math.min(remainingQuantity, canAdd);
                    stack.addQuantity(amountToAdd);
                    remainingQuantity -= amountToAdd;
                    if (remainingQuantity <= 0) return 0;
                }
            }
        }

        // 2. If items still remain, try to create new stacks in empty slots
        while (remainingQuantity > 0 && items.size() < capacity) {
            int amountForNewStack = Math.min(remainingQuantity, itemToAdd.maxStackSize);
            items.add(new ItemStack(itemToAdd, amountForNewStack));
            remainingQuantity -= amountForNewStack;
        }

        return remainingQuantity; // Returns what's left over
    }

    /**
     * Removes a specific quantity of an item from the inventory, identified by its ID.
     * It iterates through stacks, removing items until the desired quantity is met.
     *
     * @param itemId   The ID of the item to be removed (e.g., "health_potion").
     * @param quantity The quantity to be removed.
     * @return true if the total quantity was successfully removed, false otherwise.
     */
    public boolean removeItem(String itemId, int quantity) {
        int quantityToRemove = quantity;
        
        // Iterate backwards to safely remove items from the list
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack stack = items.get(i);
            if (stack.getItem().id.equals(itemId)) {
                int amountInStack = stack.getQuantity();
                
                if (amountInStack >= quantityToRemove) {
                    stack.addQuantity(-quantityToRemove); // Decrease quantity
                    quantityToRemove = 0;
                } else {
                    quantityToRemove -= amountInStack;
                    stack.setQuantity(0); // Zero out this stack's quantity
                }

                // If the stack became empty, remove it from the inventory
                if (stack.getQuantity() <= 0) {
                    items.remove(i);
                }

                if (quantityToRemove <= 0) {
                    return true; // Successfully removed everything
                }
            }
        }
        return false; // Could not remove the requested quantity
    }
    
    /**
     * Gets the list of all item stacks currently in the inventory.
     * @return A List of ItemStack objects.
     */
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * Gets the total capacity of the inventory (number of slots).
     * @return The inventory capacity.
     */
    public int getCapacity() {
        return capacity;
    }
}
