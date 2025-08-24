package com.jdstudio.engine.Object;

import java.awt.Color;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;

/**
 * An interface for {@link GameObject}s that can be interacted with by the player or other entities.
 * It defines methods for handling interaction events and providing an interaction radius.
 * 
 * @author JDStudio
 */
public interface Interactable {
    
    /**
     * Called when an entity interacts with this object.
     * 
     * @param source The entity that initiated the interaction.
     */
    void onInteract(GameObject source);

    /**
     * Returns the radius (in pixels) from the center of the object
     * within which interaction is possible.
     * 
     * @return The interaction radius.
     */
    int getInteractionRadius();

    /**
     * Draws the visual representation of the interaction area in debug mode.
     * This is a default method, so classes are not required to implement it,
     * but can if they want a custom debug visual.
     * 
     * @param g The Graphics context to draw on.
     */
    default void renderDebugInteractionArea(Graphics g) {
        if (Engine.isDebug && this instanceof GameObject) {
            GameObject owner = (GameObject) this;
            int radius = getInteractionRadius();
            if (radius <= 0) return;

            // Calculate the center of the object
            int centerX = owner.getX() + owner.getWidth() / 2;
            int centerY = owner.getY() + owner.getHeight() / 2;

            // Draw a yellow circle for the interaction area
            g.setColor(Color.YELLOW);
            g.drawOval(
                centerX - radius - Engine.camera.getX(), 
                centerY - radius - Engine.camera.getY(), 
                radius * 2, 
                radius * 2
            );
        }
    }
}
