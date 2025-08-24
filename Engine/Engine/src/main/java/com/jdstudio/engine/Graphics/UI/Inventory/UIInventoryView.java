package com.jdstudio.engine.Graphics.UI.Inventory;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Items.Inventory;
import com.jdstudio.engine.Items.ItemStack;
import com.jdstudio.engine.Object.GameObject;

/**
 * A UI element that displays a grid-based inventory.
 * It manages individual inventory slots, updates their content based on the linked {@link Inventory},
 * and handles user input for navigation and item usage.
 * 
 * @author JDStudio
 */
public class UIInventoryView extends UIElement {

	private final Inventory inventory;
    private final List<UIInventorySlot> slots;
    private final GameObject inventoryOwner;
    private Sprite backgroundSprite;
    private int rows, columns;
    private int slotSize, padding;
    private int selectedIndex = 0; // Starts with the first slot selected

    /**
     * Constructs a new UIInventoryView.
     *
     * @param x              The x-coordinate of the view's top-left corner.
     * @param y              The y-coordinate of the view's top-left corner.
     * @param background     The sprite for the background of the inventory view.
     * @param slotBackground The sprite for the background of individual inventory slots.
     * @param owner          The GameObject that owns this inventory.
     * @param inventory      The Inventory instance to display.
     * @param rows           The number of rows in the inventory grid.
     * @param columns        The number of columns in the inventory grid.
     * @param slotSize       The size (width and height) of each individual slot.
     * @param padding        The padding between slots and from the edges of the view.
     */
    public UIInventoryView(int x, int y, Sprite background, Sprite slotBackground, GameObject owner, Inventory inventory, int rows, int columns, int slotSize, int padding) {
        super(x, y);
        this.inventoryOwner = owner;
        this.inventory = inventory;
        this.rows = rows;
        this.columns = columns;
        this.slotSize = slotSize;
        this.padding = padding;
        this.slots = new ArrayList<>();

        if (background != null) { this.width = background.getWidth(); this.setHeight(background.getHeight()); }

        createSlots(slotBackground);
    }

    /**
     * Creates the individual {@link UIInventorySlot} elements and positions them within the view.
     * @param slotBackground The sprite to use for the background of each slot.
     */
    private void createSlots(Sprite slotBackground) {
        int totalSlotsToCreate = Math.min(inventory.getCapacity(), rows * columns);
        for (int i = 0; i < totalSlotsToCreate; i++) {
            int col = i % columns;
            int row = i / columns;
            int slotX = this.x + padding + col * (slotSize + padding);
            int slotY = this.y + padding + row * (slotSize + padding);
            slots.add(new UIInventorySlot(slotX, slotY, slotBackground, this.inventoryOwner));
        }
    }

    /**
     * Updates the content of each {@link UIInventorySlot} based on the current state of the linked {@link Inventory}.
     */
    private void updateSlots() {
        List<ItemStack> items = inventory.getItems();
        for (int i = 0; i < slots.size(); i++) {
            if (i < items.size()) {
                slots.get(i).setItemStack(items.get(i));
            } else {
                slots.get(i).setItemStack(null);
            }
        }
    }

    /**
     * Updates the inventory view logic, including slot content, keyboard navigation, and mouse interaction.
     */
    @Override
    public void tick() {
        if (!visible) return;
        
        updateSlots();
        handleKeyboardInput();
        
        // Update each individual slot and check for mouse clicks
        for (int i = 0; i < slots.size(); i++) {
            UIInventorySlot slot = slots.get(i);
            slot.setSelected(i == selectedIndex);
            slot.tick();
            
            // If this slot was clicked, use the item in it
            if (slot.wasClicked()) {
                useItemInSlot(i);
            }
            // If the mouse is hovering over this slot, update the selected index
            if (slot.isHovering()) {
                selectedIndex = i;
            }
        }
    }

    /**
     * Attempts to use the item in the specified slot index.
     * @param index The index of the slot whose item should be used.
     */
    private void useItemInSlot(int index) {
        if (index >= 0 && index < slots.size()) {
            ItemStack stack = slots.get(index).getItemStack();
            if (stack != null) {
                stack.getItem().onUse(inventoryOwner);
            }
        }
    }
    
    /**
     * Handles keyboard input for navigating through inventory slots and using items.
     */
    private void handleKeyboardInput() {
    	if (selectedIndex == -1) { // If nothing is selected, select the first slot
            selectedIndex = 0;
        }

        if (InputManager.isActionJustPressed("UI_DOWN")) {
            selectedIndex += columns; // Move to the row below
        }
        if (InputManager.isActionJustPressed("UI_UP")) {
            selectedIndex -= columns; // Move to the row above
        }
        if (InputManager.isActionJustPressed("UI_RIGHT")) {
            selectedIndex++; // Move right
        }
        if (InputManager.isActionJustPressed("UI_LEFT")) {
            selectedIndex--; // Move left
        }

        // Ensure the index stays within bounds
        if (selectedIndex >= slots.size()) selectedIndex = selectedIndex % columns; // Wrap to the first row
        if (selectedIndex < 0) selectedIndex = slots.size() - 1; // Go to the last slot

        // Logic to USE the selected item
        if (InputManager.isActionJustPressed("INTERACT")) {
            useItemInSlot(selectedIndex);
        }
    }

    /**
     * Renders the inventory view, including its background and all individual inventory slots.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // Draw the inventory window background
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, null);
        }
        
        // Draw each slot
        for (UIInventorySlot slot : slots) {
            slot.render(g);
        }
    }
}
