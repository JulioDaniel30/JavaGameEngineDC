package com.jdstudio.engine.Graphics.UI.Inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Items.ItemStack;
import com.jdstudio.engine.Object.GameObject;

/**
 * Represents a single slot in an inventory display.
 * It shows the item stack it contains, handles hover and click states,
 * and provides visual feedback for selection.
 * 
 * @author JDStudio
 */
public class UIInventorySlot extends UIElement {

    private ItemStack itemStack;
    private Sprite backgroundSprite;
    private Font quantityFont = new Font("Arial", Font.BOLD, 10);
    private boolean isHovering = false;
    private boolean isSelected = false;
    private boolean wasClicked = false;
    @SuppressWarnings("unused")
	private GameObject inventoryOwner;

    /**
     * Constructs a new UIInventorySlot.
     *
     * @param x                The x-coordinate of the slot's top-left corner.
     * @param y                The y-coordinate of the slot's top-left corner.
     * @param backgroundSprite The sprite to use for the slot's background.
     * @param owner            The GameObject that owns the inventory this slot belongs to.
     */
    public UIInventorySlot(int x, int y, Sprite backgroundSprite, GameObject owner) {
        super(x, y);
        this.backgroundSprite = backgroundSprite;
        this.inventoryOwner = owner;
        if (backgroundSprite != null) {
            this.width = backgroundSprite.getWidth();
            this.setHeight(backgroundSprite.getHeight());
        }
    }
    
    /**
     * Sets the ItemStack to be displayed in this slot.
     * @param stack The ItemStack to set.
     */
    public void setItemStack(ItemStack stack) { 
        this.itemStack = stack; 
    }

    /**
     * Gets the ItemStack currently in this slot.
     * @return The ItemStack.
     */
    public ItemStack getItemStack() { 
        return this.itemStack; 
    }

    /**
     * Sets the selected state of the slot.
     * @param selected true to mark as selected, false otherwise.
     */
    public void setSelected(boolean selected) { 
        this.isSelected = selected; 
    }

    /**
     * Checks if the slot was clicked in the current frame.
     * @return true if clicked, false otherwise.
     */
    public boolean wasClicked() { 
        return this.wasClicked; 
    }

    /**
     * Checks if the mouse cursor is currently hovering over the slot.
     * @return true if hovering, false otherwise.
     */
    public boolean isHovering() { 
        return this.isHovering; 
    }
    
    /**
     * Updates the slot's state, detecting mouse hover and clicks.
     */
    @Override
    public void tick() {
        if (!visible) return;

        // Reset click state each frame
        wasClicked = false; 

        int mouseX = InputManager.getMouseX() / com.jdstudio.engine.Engine.getSCALE();
        int mouseY = InputManager.getMouseY() / com.jdstudio.engine.Engine.getSCALE();

        Rectangle bounds = new Rectangle(this.x, this.y, this.width, this.getHeight());
        isHovering = bounds.contains(mouseX, mouseY);

        // Only detect click and set a flag. UIInventoryView will decide what to do.
        if (isHovering && InputManager.isLeftMouseButtonJustPressed() && itemStack != null) {
            wasClicked = true;
        }
    }

    /**
     * Renders the inventory slot, including its background, item sprite, quantity, and selection/hover highlights.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // Draw the slot background
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, null);
        }

        // If there's an item in the slot, draw it
        if (itemStack != null && itemStack.getItem() != null) {
            Sprite itemSprite = itemStack.getItem().getSprite();
            if (itemSprite != null) {
                // Draw the item sprite centered within the slot
                int itemX = x + (width - itemSprite.getWidth()) / 2;
                int itemY = y + (getHeight() - itemSprite.getHeight()) / 2;
                g.drawImage(itemSprite.getImage(), itemX, itemY, null);
            }

            // Draw quantity if greater than 1
            if (itemStack.getQuantity() > 1) {
                g.setFont(quantityFont);
                g.setColor(Color.WHITE);
                String quantityText = String.valueOf(itemStack.getQuantity());
                int textWidth = g.getFontMetrics().stringWidth(quantityText);
                g.drawString(quantityText, x + width - textWidth - 2, y + getHeight() - 2);
            }
        }
        
        // Draw an outline if the mouse is hovering over the slot OR if it is selected
        if (isHovering || isSelected) {
            g.setColor(Color.YELLOW);
            g.drawRect(x, y, width - 1, getHeight() - 1);
        }
        
        // Add an extra highlight if it's selected
        if (isSelected) {
            g.setColor(new Color(255, 255, 0, 70)); // Semi-transparent yellow
            g.fillRect(x, y, width, getHeight());
        }
    }
}
