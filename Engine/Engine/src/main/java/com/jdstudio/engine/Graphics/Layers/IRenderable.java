package com.jdstudio.engine.Graphics.Layers;

import java.awt.Graphics;

/**
 * Interface for objects that can be rendered by the {@link RenderManager}.
 * Implementing classes must provide methods for drawing themselves, specifying their
 * rendering layer, Z-order, and visibility.
 * 
 * @author JDStudio
 */
public interface IRenderable {
    /**
     * Draws the object onto the provided Graphics context.
     * This method is called by the RenderManager during the rendering phase.
     * 
     * @param g The Graphics context to draw on.
     */
    void render(Graphics g);

    /**
     * Returns the rendering layer on which this object should be drawn.
     * Objects on higher layers are drawn on top of objects on lower layers.
     * 
     * @return The {@link RenderLayer} for this object.
     */
    RenderLayer getRenderLayer();

    /**
     * Returns the specific Z-depth of the object within its layer (z-index).
     * Objects with a higher Z-value are drawn on top within the same layer.
     * By default, the object's Y position is often used for this.
     * 
     * @return The Z-order value.
     */
    default int getZOrder() {
        return 0; // Default
    }
    
    /**
     * Checks if the object should be rendered.
     * If this method returns false, the object will be skipped during rendering.
     * 
     * @return true if the object is visible and should be rendered, false otherwise.
     */
    boolean isVisible();
}
