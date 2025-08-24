package com.jdstudio.engine.Graphics.Lighting;

import java.awt.Color;

/**
 * Represents a basic light source in the game world.
 * It defines the position, radius, and color of the light.
 * 
 * @author JDStudio
 */
public class Light {

    /** The x-coordinate of the light source in world space. */
    public double x;
    /** The y-coordinate of the light source in world space. */
    public double y;
    /** The radius of the light in pixels. */
    public double radius;
    /** The color of the light. The intensity can be controlled by the Alpha channel of the color. */
    public Color color;

    /**
     * Creates a new light source.
     *
     * @param x      The x-position in world coordinates.
     * @param y      The y-position in world coordinates.
     * @param radius The radius of the light in pixels.
     * @param color  The color of the light. The alpha component controls intensity.
     */
    public Light(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }
}
