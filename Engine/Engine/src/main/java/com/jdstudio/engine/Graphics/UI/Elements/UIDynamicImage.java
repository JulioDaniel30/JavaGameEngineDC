package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Graphics;
import java.util.function.Supplier;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * A UI element that displays an image whose source is dynamic.
 * Every frame, it queries a "supplier" ({@link Supplier}) to get the correct Sprite
 * to be drawn, allowing the image to change in real-time based on the game state.
 * 
 * @author JDStudio
 */
public class UIDynamicImage extends UIElement {

    private Supplier<Sprite> spriteSupplier;
    private Sprite currentSprite;

    /**
     * Constructs a new dynamic image UI element.
     *
     * @param x              The x-position on the screen.
     * @param y              The y-position on the screen.
     * @param spriteSupplier A function that returns the Sprite to be displayed each frame.
     */
    public UIDynamicImage(int x, int y, Supplier<Sprite> spriteSupplier) {
        super(x, y);
        this.spriteSupplier = spriteSupplier;
    }

    /**
     * Updates the current sprite by querying its supplier.
     * Also updates the dimensions of the UI element to match the current sprite.
     */
    @Override
    public void tick() {
        if (!visible || spriteSupplier == null) return;
        
        // Every tick, fetch the latest sprite from the supplier.
        this.currentSprite = spriteSupplier.get();
        
        // Update the element's dimensions based on the current sprite
        if (this.currentSprite != null) {
            this.width = currentSprite.getWidth();
            this.setHeight(currentSprite.getHeight());
        }
    }

    /**
     * Renders the current dynamic sprite at the element's position.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (visible && currentSprite != null) {
            g.drawImage(currentSprite.getImage(), x, y, null);
        }
    }
}
