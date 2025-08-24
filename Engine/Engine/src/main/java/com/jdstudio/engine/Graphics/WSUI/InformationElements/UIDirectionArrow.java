package com.jdstudio.engine.Graphics.WSUI.InformationElements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-space UI element that displays an arrow above a target {@link GameObject}.
 * The arrow can blink and rotate to point towards another specified {@link GameObject}.
 * 
 * @author JDStudio
 */
public class UIDirectionArrow extends UIWorldAttached {

    private final Sprite arrowSprite;
    private GameObject pointTarget; // The GameObject the arrow should point towards

    // Logic for the blinking effect
    private boolean isBlinking = false;
    private int blinkTimer = 0;
    private int blinkSpeed = 15; // Blinks every 15 ticks

    private double currentAngle = 0.0; // Rotation angle in radians

    /**
     * Constructs a new UIDirectionArrow.
     *
     * @param followTarget The GameObject this arrow will follow (its owner).
     * @param arrowSprite  The sprite to use for the arrow.
     * @param blinking     If true, the arrow will blink.
     */
    public UIDirectionArrow(GameObject followTarget, Sprite arrowSprite, boolean blinking) {
        // The default Y offset can be adjusted to appear above the character's head
        super(followTarget, -20);
        this.arrowSprite = arrowSprite;
        this.isBlinking = blinking;
        if (arrowSprite != null) {
            this.width = arrowSprite.getWidth();
            this.height = arrowSprite.getHeight();
        }
    }

    /**
     * Sets the target GameObject for the arrow to point towards.
     * If null, the arrow will point upwards.
     * @param pointTarget The target GameObject.
     */
    public void setPointTarget(GameObject pointTarget) {
        this.pointTarget = pointTarget;
    }

    /**
     * Updates the arrow's position, blinking state, and rotation.
     * It follows its {@code followTarget} and rotates to point towards its {@code pointTarget}.
     */
    @Override
    public void tick() {
        super.tick(); // The parent class (UIWorldAttached) already handles following the target.
        if (!visible) return;

        // Blinking logic
        if (isBlinking) {
            blinkTimer++;
            if (blinkTimer > blinkSpeed * 2) {
                blinkTimer = 0;
            }
        }
        
        // Rotation logic
        if (pointTarget != null && target != null) {
            double targetX = pointTarget.getCenterX();
            double targetY = pointTarget.getCenterY();
            
            double selfX = target.getCenterX();
            double selfY = target.getCenterY();

            // Calculate the angle between the arrow's owner and the target
            this.currentAngle = Math.atan2(targetY - selfY, targetX - selfX);
        } else {
            // If there's no target to point to, point upwards
            this.currentAngle = -Math.PI / 2; // -90 degrees
        }
    }

    /**
     * Renders the direction arrow, applying blinking and rotation effects.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || target == null || arrowSprite == null) return;

        // If blinking, only draw in the "first half" of the cycle
        if (isBlinking && blinkTimer > blinkSpeed) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        
        // The coordinates 'this.x' and 'this.y' are already updated by the parent's tick().
        int drawX = this.x - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // --- ROTATION LOGIC ---
        AffineTransform oldTransform = g2d.getTransform();
        
        // Translate the rotation point to the center of the image
        int centerX = drawX - width / 2;
        int centerY = drawY - height / 2;
        
        // Rotate the graphics context. Add +90 degrees (PI/2) because most arrows are drawn pointing right.
        g2d.rotate(currentAngle + Math.PI / 2, centerX + width / 2.0, centerY + height / 2.0);

        g2d.drawImage(arrowSprite.getImage(), centerX, centerY, null);
        
        g2d.setTransform(oldTransform); // Restore the transform to not affect other drawings
        g2d.dispose();
    }
}
