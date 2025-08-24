package com.jdstudio.engine.Items;

/**
 * Represents a stack of a particular item, commonly used in inventories.
 * It holds a reference to the {@link Item} type and the quantity of that item.
 * 
 * @author JDStudio
 */
public class ItemStack {

    private Item item;
    private int quantity;

    /**
     * Constructs a new ItemStack.
     *
     * @param item     The type of item in this stack.
     * @param quantity The number of items in this stack.
     */
    public ItemStack(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Gets the Item type of this stack.
     * @return The Item object.
     */
    public Item getItem() { 
        return item; 
    }

    /**
     * Gets the quantity of items in this stack.
     * @return The quantity.
     */
    public int getQuantity() { 
        return quantity; 
    }

    /**
     * Sets the quantity of items in this stack.
     * Ensures the quantity does not go below zero.
     * @param quantity The new quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    /**
     * Adds a specified amount to the current quantity of items in this stack.
     * @param amount The amount to add.
     */
    public void addQuantity(int amount) {
        this.quantity += amount;
    }
}
