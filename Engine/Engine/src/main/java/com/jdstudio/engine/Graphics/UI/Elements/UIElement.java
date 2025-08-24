package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Graphics;

import com.jdstudio.engine.Graphics.Layers.IRenderable;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;

/**
 * Represents the abstract base for all User Interface (UI) elements.
 * <p>
 * This class establishes the fundamental structure that every UI element should have,
 * such as position, dimensions, and visibility. It also defines the contract that
 * every element must be able to render itself on the screen.
 *
 * @author JDStudio
 * @since 1.0
 */
public abstract class UIElement implements IRenderable{

    /** The horizontal coordinate of the element (X-axis). */
    protected int x;

    /** The vertical coordinate of the element (Y-axis). */
    protected int y;

    /** The width of the element in pixels. */
    protected int width;

    /** The height of the element in pixels. */
    protected int height;

    /** Controls whether the element will be rendered. */
    protected boolean visible = true;

    /**
     * Base constructor for a UI element.
     *
     * @param x The initial horizontal position.
     * @param y The initial vertical position.
     */
    public UIElement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns the rendering layer for UI elements, which is {@link StandardLayers#UI}.
     * @return The UI render layer.
     */
    @Override
    public RenderLayer getRenderLayer() { return StandardLayers.UI; }
    
    /**
     * Destroys the UI element, making it invisible and unregistering it from the RenderManager.
     * This should be called when the UI element is no longer needed.
     */
    public void destroy() {
        this.visible = false;
        RenderManager.getInstance().unregister(this);
    }
    
    /**
     * Updates the logic of the UI element.
     * Called every frame for elements that need to react to time or input.
     * Subclasses (like buttons) should override this method.
     */
    public void tick() {
        // Left blank in the base class, as not every element has update logic.
    }

    /**
     * Abstract method that subclasses must implement to draw the element.
     * <p>
     * This method is called by the graphics engine every frame
     * for the element to draw itself on the screen.
     *
     * @param g The {@link Graphics} context used for drawing operations.
     */
    public abstract void render(Graphics g);

    /**
     * Sets whether the element should be visible and rendered.
     *
     * @param visible {@code true} to make visible, {@code false} to hide.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Checks if the element is currently visible.
     *
     * @return {@code true} if the element is visible, {@code false} otherwise.
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Updates the position of the element on the screen.
     *
     * @param x The new horizontal coordinate (X-axis).
     * @param y The new vertical coordinate (Y-axis).
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

	/**
	 * Gets the height of the UI element.
	 * @return The height in pixels.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the UI element.
	 * @param height The new height in pixels.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the X coordinate of the UI element.
	 * @return The X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate of the UI element.
	 * @return The Y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the width of the UI element.
	 * @return The width in pixels.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the UI element.
	 * @param width The new width in pixels.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}
