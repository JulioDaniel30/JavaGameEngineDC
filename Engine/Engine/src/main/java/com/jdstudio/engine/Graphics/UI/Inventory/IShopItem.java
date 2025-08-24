package com.jdstudio.engine.Graphics.UI.Inventory;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * An interface that defines the contract for an item that can be displayed in the {@link UIShopView}.
 * Game-specific item classes should implement this interface to be compatible with the shop UI.
 * 
 * @author JDStudio
 */
public interface IShopItem {
    /** 
     * Returns the name of the item to be displayed.
     * @return The item's name.
     */
    String getName();
    
    /** 
     * Returns the price of the item to be displayed.
     * @return The item's price.
     */
    int getPrice();
    
    /** 
     * Returns the sprite of the item's icon.
     * @return The item's icon sprite.
     */
    Sprite getSprite();
}
