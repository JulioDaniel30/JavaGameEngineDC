package com.jdstudio.engine.Graphics.UI.Inventory;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;

public class UIShopView extends UIElement {

    private List<UIShopSlot> slots;
    private Sprite backgroundSprite;

    public UIShopView(int x, int y, int width, Sprite background, List<? extends IShopItem> itemsForSale, Consumer<IShopItem> onBuyAction) {
        super(x, y);
        this.width = width;
        this.backgroundSprite = background;
        this.slots = new ArrayList<>();

        Font defaultFont = new Font("Arial", Font.BOLD, 12);

        int currentY = y + 5; // Padding inicial
        for (IShopItem item : itemsForSale) {
            UIShopSlot slot = new UIShopSlot(x + 5, currentY, width - 10, item, defaultFont, onBuyAction);
            slots.add(slot);
            currentY += slot.getHeight() + 5; // Adiciona padding entre os slots
        }
        
        this.setHeight(currentY - y);
    }

    @Override
    public void tick() {
        if (!visible) return;
        for (UIShopSlot slot : slots) {
            slot.tick();
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, width, getHeight(), null);
        } else {
            g.setColor(new Color(20, 20, 80, 200));
            g.fillRect(x, y, width, getHeight());
        }

        for (UIShopSlot slot : slots) {
            slot.render(g);
        }
    }
}