package com.jdstudio.engine.Graphics;

/**
 * Represents a resolution profile, containing the base width and height
 * and a recommended scale for the game window.
 * This allows the game to render internally at a lower resolution and then scale up
 * for display, providing a pixel-art aesthetic or performance benefits.
 *
 * @param width            The base internal resolution width (in pixels).
 * @param height           The base internal resolution height (in pixels).
 * @param recommendedScale The suggested multiplication scale for the final window size.
 * @author JDStudio
 */
public record ResolutionProfile(int width, int height, int recommendedScale) {
    
    /**
     * Calculates the final width of the game window based on the base width and recommended scale.
     * @return The base width multiplied by the recommended scale.
     */
    public int getFinalWidth() {
        return width * recommendedScale;
    }

    /**
     * Calculates the final height of the game window based on the base height and recommended scale.
     * @return The base height multiplied by the recommended scale.
     */
    public int getFinalHeight() {
        return height * recommendedScale;
    }
}
