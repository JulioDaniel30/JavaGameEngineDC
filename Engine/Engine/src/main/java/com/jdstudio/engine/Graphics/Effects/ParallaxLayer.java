package com.jdstudio.engine.Graphics.Effects;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * Represents a single layer in a parallax background effect.
 * Each layer has a sprite and a scroll factor that determines its movement relative to the camera.
 * 
 * @author JDStudio
 */
public class ParallaxLayer {

    /** The sprite (image) used for this layer. */
    private final Sprite sprite;
    
    /** 
     * The scroll speed factor. 
     * 0.0 = stationary, 1.0 = moves with the camera, < 1.0 = background, > 1.0 = foreground.
     */
    private final double scrollFactor;

    /**
     * Creates a new background layer for the parallax effect.
     * 
     * @param sprite     The sprite (image) to be used for this layer.
     * @param scrollFactor The scroll speed factor.
     */
    public ParallaxLayer(Sprite sprite, double scrollFactor) {
        this.sprite = sprite;
        this.scrollFactor = scrollFactor;
    }

    /**
     * Gets the sprite for this parallax layer.
     * @return The Sprite object.
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Gets the scroll factor for this parallax layer.
     * @return The scroll factor as a double.
     */
    public double getScrollFactor() {
        return scrollFactor;
    }
}
