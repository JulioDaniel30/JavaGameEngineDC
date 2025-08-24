package com.jdstudio.engine.Graphics.Sprite;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * Represents a spritesheet loaded from an image file.
 * <p>
 * This class serves as a tool to load a single large image that contains multiple sprites
 * and extract individual sub-images (sprites) from it.
 *
 * @author JD Studio
 * @since 1.0
 */
public class Spritesheet {

    /** The complete image of the spritesheet. */
    private final BufferedImage sheet;

    /**
     * Loads a spritesheet from a path in the classpath.
     * It attempts to load the resource using different methods for increased robustness.
     *
     * @param path The path to the image file (e.g., "/textures/sheet.png"). Cannot be null.
     * @throws RuntimeException if the path is null or if there is an error loading the image.
     */
    public Spritesheet(String path) {
        Objects.requireNonNull(path, "Spritesheet path cannot be null.");
        InputStream is = null;
        try {
            // Attempt to get the resource using the ClassLoader of the Spritesheet class itself
            is = getClass().getResourceAsStream(path);

            // If the first attempt fails, try with the system ClassLoader
            if (is == null) {
                System.out.println("DEBUG: Attempting to load resource via system ClassLoader: " + path);
                is = ClassLoader.getSystemResourceAsStream(path);
            }

            // If still fails, try with the current Thread's ClassLoader (can be useful in some frameworks)
            if (is == null) {
                 System.out.println("DEBUG: Attempting to load resource via Thread Context ClassLoader: " + path);
                 is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            }

            // If all attempts fail, the InputStream will still be null
            if (is == null) {
                // Throw IOException to be caught by the catch block below
                throw new IOException("Image file not found in classpath, even after multiple attempts: " + path);
            }

            sheet = ImageIO.read(is); // Use the obtained InputStream

        } catch (IOException e) {
            // Throws an unchecked exception to signal a critical initialization failure.
            throw new RuntimeException("Failed to load spritesheet from: " + path, e);
        } finally {
            if (is != null) {
                try {
                    is.close(); // Ensures the InputStream is closed
                } catch (IOException e) {
                    System.err.println("Error closing InputStream for: " + path + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Extracts an individual sprite from the spritesheet based on pixel coordinates.
     *
     * @param x      The X coordinate of the top-left corner of the sprite on the sheet.
     * @param y      The Y coordinate of the top-left corner of the sprite on the sheet.
     * @param width  The width in pixels of the sprite to be extracted.
     * @param height The height in pixels of the sprite to be extracted.
     * @return A new {@link Sprite} object containing the cropped image.
     */
    public Sprite getSprite(int x, int y, int width, int height) {
        // Ensure 'sheet' is not null before attempting subimage
        if (sheet == null) {
            throw new IllegalStateException("Spritesheet has not been successfully loaded.");
        }
        BufferedImage subImage = sheet.getSubimage(x, y, width, height);
        return new Sprite(subImage);
    }

    /**
     * Returns the total width of the spritesheet in pixels.
     *
     * @return The width of the image.
     */
    public int getWidth() {
        if (sheet == null) {
            return 0; // Or throw an exception, depending on desired behavior
        }
        return sheet.getWidth();
    }

    /**
     * Returns the total height of the spritesheet in pixels.
     *
     * @return The height of the image.
     */
    public int getHeight() {
        if (sheet == null) {
            return 0; // Or throw an exception
        }
        return sheet.getHeight();
    }
}
