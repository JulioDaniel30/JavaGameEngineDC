package com.jdstudio.engine.Items;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Object.GameObject;

/**
 * An abstract base class for all items in the game.
 * Defines common properties like ID, name, and maximum stack size.
 * Game-specific item logic should extend this class.
 * 
 * @author JDStudio
 */
public abstract class Item {

    /** A unique identifier for the item. */
    public final String id;
    /** The display name of the item. */
    public final String name;
    /** The maximum number of this item that can be stacked in a single inventory slot. */
    public final int maxStackSize;
    
    /**
     * Protected constructor, as this class should not be instantiated directly.
     *
     * @param id           A unique identifier for the item.
     * @param name         The display name of the item.
     * @param maxStackSize The maximum number of this item that can be stacked.
     */
    protected Item(String id, String name, int maxStackSize) {
        this.id = id;
        this.name = name;
        this.maxStackSize = Math.max(1, maxStackSize); // Ensures minimum stack size is 1
    }

    /**
     * Returns the sprite that represents this item in the inventory or UI.
     * Must be implemented by game-specific item classes.
     * 
     * @return The Sprite representing the item.
     */
    public abstract Sprite getSprite();

    /**
     * Called when the player "uses" the item from the inventory.
     * Game-specific logic (e.g., healing the player, equipping a weapon) should be implemented here.
     * 
     * @param user The GameObject that is using the item (typically the player).
     */
    public void onUse(GameObject user) {
        System.out.println("Using item: " + name);
    }
}
