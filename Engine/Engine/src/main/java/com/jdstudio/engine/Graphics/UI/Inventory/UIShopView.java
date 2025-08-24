package com.jdstudio.engine.Graphics.UI.Inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;

/**
 * A UI element that displays a list of items for sale in a shop interface.
 * It dynamically creates and manages {@link UIShopSlot} elements for each item.
 * 
 * @author JDStudio
 */
public class UIShopView extends UIElement {

    private List<UIShopSlot> slots;
    private Sprite backgroundSprite;

    /**
     * Constructs a new UIShopView.
     *
     * @param x              The x-coordinate of the shop view's top-left corner.
     * @param y              The y-coordinate of the shop view's top-left corner.
     * @param width          The width of the shop view.
     * @param background     The sprite for the background of the shop view (can be null for a solid color).
     * @param itemsForSale   A list of {@link IShopItem} objects to display for sale.
     * @param onBuyAction    A Consumer that will be called with the {@link IShopItem} when an item's buy button is clicked.
     */
    public UIShopView(int x, int y, int width, Sprite background, List<? extends IShopItem> itemsForSale, Consumer<IShopItem> onBuyAction) {
        super(x, y);
        this.width = width;
        this.backgroundSprite = background;
        this.slots = new ArrayList<>();

        Font defaultFont = new Font("Arial", Font.BOLD, 12);

        int currentY = y + 5; // Initial padding
        for (IShopItem item : itemsForSale) {
            UIShopSlot slot = new UIShopSlot(x + 5, currentY, width - 10, item, defaultFont, onBuyAction);
            slots.add(slot);
            currentY += slot.getHeight() + 5; // Add padding between slots
        }
        
        this.setHeight(currentY - y); // Set the total height of the shop view
    }

    /**
     * Updates the shop view logic, primarily by updating all its internal {@link UIShopSlot} elements.
     */
    @Override
    public void tick() {
        if (!visible) return;
        for (UIShopSlot slot : slots) {
            slot.tick();
        }
    }

    /**
     * Renders the shop view, including its background and all individual {@link UIShopSlot} elements.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        // Draw background sprite or solid color
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, width, getHeight(), null);
        } else {
            g.setColor(new Color(20, 20, 80, 200)); // Default solid background color
            g.fillRect(x, y, width, getHeight());
        }

        // Render each shop slot
        for (UIShopSlot slot : slots) {
            slot.render(g);
        }
    }
}
