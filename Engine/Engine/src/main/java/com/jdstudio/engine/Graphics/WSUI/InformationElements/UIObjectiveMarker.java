package com.jdstudio.engine.Graphics.WSUI.InformationElements;

import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-space UI marker that indicates a quest objective.
 * It floats smoothly above a target {@link GameObject} to draw attention.
 * 
 * @author JDStudio
 */
public class UIObjectiveMarker extends UIWorldAttached {

    private final Sprite markerSprite;

    // Logic for the bobbing (floating) animation
    private int bobbingTimer = 0;
    private int bobbingSpeed = 5; // How fast it bobs
    private int bobbingAmount = 4;  // How many pixels it moves up and down

    /**
     * Constructs a new objective marker.
     *
     * @param target       The GameObject that is the quest objective.
     * @param markerSprite The Sprite to be used as the marker icon.
     */
    public UIObjectiveMarker(GameObject target, Sprite markerSprite) {
        // The Y offset positions the marker above the target
        super(target, -24); 
        this.markerSprite = markerSprite;
        this.visible = (target != null);

        if (markerSprite != null) {
            this.width = markerSprite.getWidth();
            this.height = markerSprite.getHeight();
        }
    }

    /**
     * Updates the marker's position and bobbing animation.
     * It also checks if the target GameObject has been destroyed.
     */
    @Override
    public void tick() {
        // Visibility is controlled externally, but if the target is destroyed, it disappears.
        if (target == null || target.isDestroyed) {
            this.visible = false;
        }
        if (!visible) return;

        // The parent class (UIWorldAttached) already handles following the target's base position.
        super.tick(); 
        
        // Update the timer for the bobbing animation
        bobbingTimer++;
    }

    /**
     * Renders the objective marker, applying the bobbing (floating) effect.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || target == null || markerSprite == null) return;

        // Calculate the vertical bobbing offset using a sine function
        // for a smooth up and down movement.
        double bobbingOffset = Math.sin(bobbingTimer * (Math.PI / 180.0) * bobbingSpeed) * bobbingAmount;
        
        // The 'this.x' and 'this.y' coordinates already follow the target.
        // The calculation centers the sprite and applies the camera and bobbing offset.
        int drawX = (this.x - (this.width / 2)) - Engine.camera.getX();
        int drawY = (int) (this.y - this.height + bobbingOffset) - Engine.camera.getY();

        g.drawImage(markerSprite.getImage(), drawX, drawY, null);
    }
    
    /**
     * Allows changing the target of the marker dynamically.
     * @param newTarget The new GameObject target.
     */
    public void setTarget(GameObject newTarget) {
        this.target = newTarget;
        this.visible = (newTarget != null);
    }
}
