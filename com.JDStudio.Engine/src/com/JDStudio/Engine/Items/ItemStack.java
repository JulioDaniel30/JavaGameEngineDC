package com.JDStudio.Engine.Items;

public class ItemStack {

    private Item item;
    private int quantity;

    public ItemStack(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }
}