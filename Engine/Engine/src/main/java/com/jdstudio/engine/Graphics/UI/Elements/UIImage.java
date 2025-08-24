package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Graphics;
import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * A UI element that simply renders a static {@link Sprite} on the screen.
 * Ideal for icons, menu background images, avatars, etc.
 * 
 * @author JDStudio
 */
public class UIImage extends UIElement {

    private Sprite sprite;

    /**
     * Constructs a new image UI element.
     *
     * @param x      The x-position on the screen.
     * @param y      The y-position on the screen.
     * @param sprite The Sprite to be drawn.
     */
    public UIImage(int x, int y, Sprite sprite) {
        super(x, y);
        setSprite(sprite); // Use the setter to set the sprite and dimensions
    }

    // tick() is inherited from UIElement and could be used in the future for animations (e.g., blinking).
    // For now, it doesn't need logic.

    /**
     * Renders the image at the element's position.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (visible && sprite != null) {
            // Draw the sprite at the x, y position on the screen (no camera offset)
            g.drawImage(sprite.getImage(), x, y, null);
        }
    }

    /**
     * Allows changing the image of the element dynamically during the game.
     * Updates the element's width and height to match the new sprite.
     *
     * @param newSprite The new Sprite to be displayed.
     */
    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
        if (this.sprite != null) {
            this.width = this.sprite.getWidth();
            this.setHeight(this.sprite.getHeight());
        }
    }
}
