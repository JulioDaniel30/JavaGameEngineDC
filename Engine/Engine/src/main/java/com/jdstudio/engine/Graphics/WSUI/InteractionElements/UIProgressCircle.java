package com.jdstudio.engine.Graphics.WSUI.InteractionElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.function.Supplier;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-space UI element that draws a progress circle.
 * Ideal for indicating spell charging, lengthy interactions, or any other progress.
 * Its visibility and progress are controlled by {@link Supplier} functions.
 * 
 * @author JDStudio
 */
public class UIProgressCircle extends UIWorldAttached {

    private final int radius;
    private final float strokeWidth;
    private final Color backgroundColor;
    private final Color foregroundColor;

    // Suppliers to control the state dynamically
    private final Supplier<Float> progressSupplier; // Provides progress from 0.0f to 1.0f
    private final Supplier<Boolean> visibilitySupplier; // Provides whether the circle should be visible

    /**
     * Constructs a new UIProgressCircle.
     *
     * @param target             The GameObject this progress circle is attached to.
     * @param yOffset            The vertical offset from the target's Y position.
     * @param radius             The radius of the circle.
     * @param strokeWidth        The width of the line used to draw the circle and arc.
     * @param background         The background color of the circle (the empty part).
     * @param foreground         The foreground color of the progress arc (the filled part).
     * @param progressSupplier   A Supplier that provides the current progress (0.0f to 1.0f).
     * @param visibilitySupplier A Supplier that provides the visibility state of the circle.
     */
    public UIProgressCircle(GameObject target, int yOffset, int radius, float strokeWidth, 
                            Color background, Color foreground,
                            Supplier<Float> progressSupplier, Supplier<Boolean> visibilitySupplier) {
        super(target, yOffset);
        this.radius = radius;
        this.strokeWidth = strokeWidth;
        this.backgroundColor = background;
        this.foregroundColor = foreground;
        this.progressSupplier = progressSupplier;
        this.visibilitySupplier = visibilitySupplier;
    }

    /**
     * Updates the circle's visibility based on its visibility supplier.
     * It also updates its position by calling the parent's tick method.
     */
    @Override
    public void tick() {
        super.tick(); // Update position to follow the target
        
        // Visibility is controlled by the supplier
        if (this.visibilitySupplier != null) {
            this.visible = this.visibilitySupplier.get();
        }
    }

    /**
     * Renders the progress circle, drawing the background circle and then the progress arc.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || target == null) return;
        
        Graphics2D g2d = (Graphics2D) g.create();

        // Improve circle drawing quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate rendering position on screen
        int drawX = this.x - radius - Engine.camera.getX();
        int drawY = this.y - radius - Engine.camera.getY();
        int diameter = radius * 2;

        // 1. Draw the background circle (the "path" of the progress)
        g2d.setColor(backgroundColor);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawOval(drawX, drawY, diameter, diameter);

        // 2. Draw the progress arc
        float progress = progressSupplier.get();
        if (progress > 0) {
            g2d.setColor(foregroundColor);
            // The angle is 360 * progress. Starts at the top (90 degrees) and draws counter-clockwise.
            g2d.draw(new Arc2D.Float(drawX, drawY, diameter, diameter, 90, -360 * progress, Arc2D.OPEN));
        }
        
        g2d.dispose();
    }
}
