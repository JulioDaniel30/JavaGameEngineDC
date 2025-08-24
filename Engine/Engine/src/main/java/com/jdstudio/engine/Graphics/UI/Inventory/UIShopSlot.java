package com.jdstudio.engine.Graphics.UI.Inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.Consumer;

import com.jdstudio.engine.Graphics.UI.Elements.UIButton;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Graphics.UI.Elements.UIImage;
import com.jdstudio.engine.Graphics.UI.Elements.UIText;

/**
 * A UI element representing a single item slot within a shop interface.
 * It displays the item's icon, name, price, and a "Buy" button.
 * 
 * @author JDStudio
 */
public class UIShopSlot extends UIElement {

    private final UIImage itemIcon;
    private final UIText itemName;
    private final UIText itemPrice;
    private final UIButton buyButton;

    /**
     * Constructs a new UIShopSlot.
     *
     * @param x           The x-coordinate of the slot's top-left corner.
     * @param y           The y-coordinate of the slot's top-left corner.
     * @param width       The width of the shop slot.
     * @param item        The IShopItem to display in this slot.
     * @param font        The base font to use for text elements.
     * @param onBuyAction A Consumer that will be called with the IShopItem when the buy button is clicked.
     */
    public UIShopSlot(int x, int y, int width, IShopItem item, Font font, Consumer<IShopItem> onBuyAction) {
        super(x, y);
        this.width = width;
        this.setHeight(40); // Fixed height for each shop row

        this.itemIcon = new UIImage(x + 5, y + (this.getHeight() - 32) / 2, item.getSprite());
        this.itemName = new UIText(x + 42, y + 15, font, Color.WHITE, item.getName());
        this.itemPrice = new UIText(x + 42, y + 30, new Font(font.getName(), Font.PLAIN, 10), Color.YELLOW, item.getPrice() + "g");
        
        this.buyButton = new UIButton(x + width - 65, y + (this.getHeight() - 24) / 2, "Comprar", new Font(font.getName(), Font.BOLD, 10), () -> {
            if (onBuyAction != null) {
                onBuyAction.accept(item);
            }
        });
    }
    
    /**
     * Updates the shop slot's logic, primarily by updating its internal buy button.
     */
    @Override
    public void tick() {
        if (!visible) return;
        buyButton.tick();
    }

    /**
     * Renders the shop slot, including the item icon, name, price, and buy button.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        itemIcon.render(g);
        itemName.render(g);
        itemPrice.render(g);
        buyButton.render(g);
    }
}
