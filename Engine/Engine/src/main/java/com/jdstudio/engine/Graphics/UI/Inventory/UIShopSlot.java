package com.jdstudio.engine.Graphics.UI.Inventory;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.Consumer;

import com.jdstudio.engine.Graphics.UI.Elements.UIButton;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Graphics.UI.Elements.UIImage;
import com.jdstudio.engine.Graphics.UI.Elements.UIText;

public class UIShopSlot extends UIElement {

    private final UIImage itemIcon;
    private final UIText itemName;
    private final UIText itemPrice;
    private final UIButton buyButton;

    public UIShopSlot(int x, int y, int width, IShopItem item, Font font, Consumer<IShopItem> onBuyAction) {
        super(x, y);
        this.width = width;
        this.setHeight(40); // Altura fixa para cada linha da loja

        this.itemIcon = new UIImage(x + 5, y + (this.getHeight() - 32) / 2, item.getSprite());
        this.itemName = new UIText(x + 42, y + 15, font, Color.WHITE, item.getName());
        this.itemPrice = new UIText(x + 42, y + 30, new Font(font.getName(), Font.PLAIN, 10), Color.YELLOW, item.getPrice() + "g");
        
        this.buyButton = new UIButton(x + width - 65, y + (this.getHeight() - 24) / 2, "Comprar", new Font(font.getName(), Font.BOLD, 10), () -> {
            if (onBuyAction != null) {
                onBuyAction.accept(item);
            }
        });
    }
    
    @Override
    public void tick() {
        if (!visible) return;
        buyButton.tick();
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        itemIcon.render(g);
        itemName.render(g);
        itemPrice.render(g);
        buyButton.render(g);
    }
}