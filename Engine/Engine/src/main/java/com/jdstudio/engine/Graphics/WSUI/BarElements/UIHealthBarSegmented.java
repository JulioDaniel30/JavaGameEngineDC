package com.jdstudio.engine.Graphics.WSUI.BarElements;

import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.HealthComponent;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Object.GameObject;

/**
 * A health bar that displays health in discrete segments (like hearts),
 * instead of a continuous bar. It automatically determines the number of segments
 * based on the target's max health and a defined health per segment.
 * 
 * @author JDStudio
 */
public class UIHealthBarSegmented extends UIWorldAttached {

    private final HealthComponent healthComponent;
    private final int maxSegments;
    private final int healthPerSegment;

    // Sprites for the different states of a segment
    private Sprite fullSegmentSprite;
    private Sprite halfSegmentSprite;
    private Sprite emptySegmentSprite;

    /**
     * Constructs a new segmented health bar.
     *
     * @param target           The GameObject whose health will be displayed.
     * @param yOffset          The vertical offset from the target.
     * @param healthPerSegment How many health points each segment represents.
     */
    public UIHealthBarSegmented(GameObject target, int yOffset, int healthPerSegment) {
        super(target, yOffset);
        this.healthComponent = target.getComponent(HealthComponent.class);
        this.healthPerSegment = Math.max(1, healthPerSegment); // Avoid division by zero

        // The bar is only visible if the target has a HealthComponent
        if (this.healthComponent == null) {
            this.visible = false;
            System.err.println("Warning: Attempted to create UIHealthBarSegmented for object without HealthComponent: " + target.name);
            this.maxSegments = 0;
        } else {
            // Calculate how many segments are needed to represent max health
            this.maxSegments = (int) Math.ceil((double) this.healthComponent.maxHealth / this.healthPerSegment);
        }

        // Load sprites from the ThemeManager
        this.fullSegmentSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_FULL);
        this.halfSegmentSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_HALF);
        this.emptySegmentSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_EMPTY);
    }
    
    /**
     * Sets the sprites to be used for the health bar segments.
     *
     * @param fullSegmentSpr  Sprite for a full heart/segment.
     * @param halfSegmentSpr  Sprite for a half heart/segment.
     * @param emptySegmentSpr Sprite for an empty heart/segment.
     */
    public void setSprites(Sprite fullSegmentSpr, Sprite halfSegmentSpr, Sprite emptySegmentSpr) {
    	this.fullSegmentSprite = fullSegmentSpr;
    	this.halfSegmentSprite = halfSegmentSpr;
    	this.emptySegmentSprite = emptySegmentSpr;
    	
    }
    
    /**
     * Renders the segmented health bar.
     * It iterates through each segment, determining whether to draw a full, half, or empty sprite
     * based on the current health.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || healthComponent == null || fullSegmentSprite == null) return;
        
        int currentHealth = healthComponent.currentHealth;
        int segmentWidth = fullSegmentSprite.getWidth();
        int segmentPadding = 2; // Spacing between segments

        // Calculate the total width of all segments to center the bar
        int totalBarWidth = (maxSegments * segmentWidth) + ((maxSegments - 1) * segmentPadding);
        
        // The initial X position is calculated so the set of hearts is centered above the target
        int startDrawX = (this.x - totalBarWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // Iterate over each segment the health bar can have
        for (int i = 0; i < maxSegments; i++) {
            // Calculate the health threshold for this segment to be "full"
            int healthThreshold = (i + 1) * healthPerSegment;
            Sprite spriteToDraw;

            if (currentHealth >= healthThreshold) {
                // If current health is greater than or equal to the threshold, the heart is full
                spriteToDraw = fullSegmentSprite;
            } else if (currentHealth >= healthThreshold - (healthPerSegment / 2.0)) {
                // If current health is in the upper half of the segment, the heart is half full
                spriteToDraw = halfSegmentSprite;
            } else {
                // Otherwise, the heart is empty
                spriteToDraw = emptySegmentSprite;
            }

            // Calculate the X position for this specific heart
            int currentDrawX = startDrawX + i * (segmentWidth + segmentPadding);
            
            if (spriteToDraw != null) {
                g.drawImage(spriteToDraw.getImage(), currentDrawX, drawY, null);
            }
        }
    }
}
