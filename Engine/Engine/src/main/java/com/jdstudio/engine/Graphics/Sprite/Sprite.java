package com.jdstudio.engine.Graphics.Sprite;

import java.awt.image.BufferedImage;
import java.util.Objects;

import com.jdstudio.engine.Utils.ImageUtils;

/**
 * Represents a single image (sprite) that can be drawn on the screen.
 * <p>
 * This class acts as a wrapper for {@link BufferedImage}, allowing for future
 * extensions, such as adding animation data or collision metadata, without
 * modifying all parts of the engine that handle images.
 *
 * @author JDStudio
 * @since 1.0
 */
public class Sprite {

    /** The rasterized image containing the pixels of the sprite. */
    private BufferedImage image;

    /**
     * Creates a new Sprite instance from a BufferedImage object.
     *
     * @param image The image to be encapsulated by this sprite. Cannot be null.
     * @throws NullPointerException if the provided image is null.
     */
    public Sprite(BufferedImage image) {
        // Validation to ensure a sprite is never created without an image.
        this.image = Objects.requireNonNull(image, "Sprite image cannot be null.");
    }

    /**
     * Returns the underlying {@link BufferedImage} object for rendering operations.
     *
     * @return The image contained within this sprite.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the width of the sprite in pixels.
     *
     * @return The width of the image.
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Returns the height of the sprite in pixels.
     *
     * @return The height of the image.
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**flip this Strite in X axies*/
    public void flipX(){
        image = ImageUtils.flipHorizontal(image);
    }
    /**flip this Strite in Y axies*/
    public void flipY(){
        image = ImageUtils.flipVertical(image);
    }

    /**
     * return Flipped Sprite in X axis
     * @return {@link com.jdstudio.engine.Graphics.Sprite} this sprite Flipped in X axies
     */
    public Sprite FlipX(){
        return new Sprite(ImageUtils.flipHorizontal(image));
    }

    /**
     *     * Flipped a {@link com.jdstudio.engine.Graphics.Sprite} in X axis
     * @param sprite the {@link com.jdstudio.engine.Graphics.Sprite} to be flipped in X axis
     * @return The {@link com.jdstudio.engine.Graphics.Sprite} flipped in X axis
     */
    public static Sprite FlipX(Sprite sprite){
        return new Sprite(ImageUtils.flipHorizontal(sprite.getImage()));
    }

    /**
     * Return Flipped Sprite in Y axis
     * @return {@link com.jdstudio.engine.Graphics.Sprite} this sprite Flipped in Y axis
     */

    public Sprite FlipY(){
        return new Sprite(ImageUtils.flipVertical(image));
    }

    /**
     * Flipped a {@link com.jdstudio.engine.Graphics.Sprite} in Y axis
     * @param sprite the {@link com.jdstudio.engine.Graphics.Sprite} to be flipped in Y axis
     * @return The {@link com.jdstudio.engine.Graphics.Sprite} flipped in Y axis
     */
    public static Sprite FlipY(Sprite sprite){
        return new Sprite(ImageUtils.flipVertical(sprite.getImage()));
    }



}
