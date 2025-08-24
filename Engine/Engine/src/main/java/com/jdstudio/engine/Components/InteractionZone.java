package com.jdstudio.engine.Components;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import com.jdstudio.engine.Object.GameObject;

/**
 * Represents a single interaction zone with a defined shape, type, and owner.
 * These zones are managed by the InteractionComponent.
 * 
 * @author JDStudio
 */
public class InteractionZone {

    private GameObject owner;
    private Shape shape;
    
    /** The type of the zone, used to categorize interactions (e.g., "AGGRO", "ATTACK"). */
    public final String type;
    
    /** Type identifier for zones that trigger aggressive behavior. */
    public static final String TYPE_AGGRO = "AGGRO";
    /** Type identifier for zones where an attack can be performed. */
    public static final String TYPE_ATTACK = "ATTACK";
    /** Type identifier for zones that initiate dialogue. */
    public static final String TYPE_DIALOGUE = "DIALOGUE";
    /** Type identifier for generic trigger zones. */
    public static final String TYPE_TRIGGER = "TRIGGER";

    /**
     * Constructs a circular InteractionZone.
     *
     * @param owner  The GameObject that owns this zone.
     * @param type   The type of the zone (e.g., TYPE_AGGRO).
     * @param radius The radius of the circular zone.
     */
    public InteractionZone(GameObject owner, String type, double radius) {
        this.owner = owner;
        this.type = type;
        // Creates an ellipse (circle) centered on the owner. The size will be updated.
        this.shape = new Ellipse2D.Double(0, 0, radius * 2, radius * 2);
        updatePosition();
    }

    /**
     * Constructs a rectangular InteractionZone.
     *
     * @param owner   The GameObject that owns this zone.
     * @param type    The type of the zone.
     * @param width   The width of the rectangular zone.
     * @param height  The height of the rectangular zone.
     * @param offsetX The horizontal offset from the owner's position.
     * @param offsetY The vertical offset from the owner's position.
     */
    public InteractionZone(GameObject owner, String type, double width, double height, double offsetX, double offsetY) {
        this.owner = owner;
        this.type = type;
        // Creates a rectangle with an offset relative to the owner.
        this.shape = new Rectangle2D.Double(0, 0, width, height);
        updatePosition(offsetX, offsetY);
    }

    /**
     * Updates the position of the zone to follow its owner.
     * This is typically called each frame before checking for interactions.
     */
    public void updatePosition() {
        if (owner == null) return;

        if (shape instanceof Ellipse2D) {
            Ellipse2D circle = (Ellipse2D) shape;
            double centerX = owner.getX() + owner.getWidth() / 2.0;
            double centerY = owner.getY() + owner.getHeight() / 2.0;
            circle.setFrame(centerX - (circle.getWidth() / 2), centerY - (circle.getHeight() / 2), circle.getWidth(), circle.getHeight());
        }
    }
    
    /**
     * Overloaded method to update the position of rectangular zones with an offset.
     *
     * @param offsetX The horizontal offset from the owner's position.
     * @param offsetY The vertical offset from the owner's position.
     */
    public void updatePosition(double offsetX, double offsetY) {
        if (owner == null) return;
        if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            rect.setRect(owner.getX() + offsetX, owner.getY() + offsetY, rect.getWidth(), rect.getHeight());
        } else {
            updatePosition(); // Use the default logic for other shapes
        }
    }

    /**
     * Checks if a point (e.g., the center of another GameObject) is inside this zone.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return true if the point is inside the zone, false otherwise.
     */
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }
    
    /**
     * Returns the Shape object for debug rendering.
     *
     * @return The java.awt.Shape object representing the zone.
     */
    public Shape getShape() {
        return this.shape;
    }
}
