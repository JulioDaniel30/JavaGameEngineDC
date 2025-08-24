package com.jdstudio.engine.Graphics.Lighting;

import java.awt.Color;
import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * Represents a directional cone-shaped light source.
 * It extends the basic Light class and adds properties for angle and a sprite to define its shape.
 * 
 * @author JDStudio
 */
public class ConeLight extends Light {

    /** The angle of the cone light in radians. */
    public double angle;
    /** The pre-drawn sprite used to render the cone light. */
    public Sprite lightSprite;

    /**
     * Creates a new directional (cone) light with customizable distance and color.
     *
     * @param x           The x-position of the light origin.
     * @param y           The y-position of the light origin.
     * @param distance    The length/distance of the light cone in pixels.
     * @param angle       The initial angle of the direction (in radians).
     * @param lightSprite The pre-drawn sprite of the light cone.
     * @param color       The color of the light "paint" (should be semi-transparent).
     */
    public ConeLight(double x, double y, double distance, double angle, Sprite lightSprite, Color color) {
        // Pass 'distance' as 'radius' and the received color to the parent constructor.
        super(x, y, distance, color); 
        this.angle = angle;
        this.lightSprite = lightSprite;
    }
}
