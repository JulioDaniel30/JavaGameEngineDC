package com.jdstudio.engine.Components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Layers.IRenderable;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Object.GameObject;

/**
 * A component that renders a shadow for a GameObject.
 * The shadow is drawn on the GAMEPLAY_BELOW layer, beneath the character,
 * and its Z-depth is calculated to always appear under its owner.
 * This component implements IRenderable to hook into the engine's rendering system.
 * 
 * @author JDStudio
 */
public class ShadowComponent extends Component implements IRenderable {

    /**
     * Defines the type of shadow to be rendered.
     */
    public enum ShadowType {
        /** Draws a soft oval shadow using procedural generation. */
        PROCEDURAL_OVAL,
        /** Draws a provided sprite as the shadow. */
        SPRITE_BASED
    }

    private boolean isActive = true;
    private final ShadowType type;
    private Sprite shadowSprite;
    private int width;
    private int height;
    private float opacity;
    private int yOffset; // Vertical offset of the shadow relative to the owner's base

    /**
     * Creates a procedural (soft oval) shadow.
     *
     * @param width   The width of the shadow ellipse.
     * @param height  The height of the shadow ellipse.
     * @param opacity The opacity of the shadow (0.0f to 1.0f).
     * @param yOffset The vertical offset of the shadow from the owner's base.
     */
    public ShadowComponent(int width, int height, float opacity, int yOffset) {
        this.type = ShadowType.PROCEDURAL_OVAL;
        this.width = width;
        this.height = height;
        this.opacity = opacity;
        this.yOffset = yOffset;
    }

    /**
     * Creates a shadow using a custom sprite.
     *
     * @param shadowSprite The sprite to be used as the shadow.
     * @param yOffset      The vertical offset of the shadow from the owner's base.
     */
    public ShadowComponent(Sprite shadowSprite, int yOffset) {
        this.type = ShadowType.SPRITE_BASED;
        this.shadowSprite = shadowSprite;
        this.yOffset = yOffset;
        if (shadowSprite != null) {
            this.width = shadowSprite.getWidth();
            this.height = shadowSprite.getHeight();
            this.opacity = 1.0f; // Default to full opacity for sprites
        }
    }

    @Override
    public void initialize(GameObject owner) {
        super.initialize(owner);
        // The shadow component registers itself to be rendered.
        com.jdstudio.engine.Graphics.Layers.RenderManager.getInstance().register(this);
    }
    
    // The update() method is not needed, as the shadow just follows the owner.

    // --- IRenderable Interface Methods ---

    @Override
    public void render(Graphics g) {
        if (owner == null || !isActive) return;
        
        Graphics2D g2d = (Graphics2D) g.create(); // Create a copy of the graphics context to not affect other drawings

        // Shadow position: centered on the owner's X, at the base of the owner's Y + offset
        int shadowX = owner.getX() + (owner.getWidth() / 2) - (this.width / 2) - Engine.camera.getX();
        int shadowY = owner.getY() + owner.getHeight() - (this.height / 2) + this.yOffset - Engine.camera.getY();

        if (type == ShadowType.SPRITE_BASED && shadowSprite != null) {
            // Set the opacity for the sprite
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(shadowSprite.getImage(), shadowX, shadowY, null);
            
        } else if (type == ShadowType.PROCEDURAL_OVAL) {
            // Use a radial gradient to create a soft shadow
            Point2D center = new Point2D.Float(shadowX + width / 2f, shadowY + height / 2f);
            float radius = width / 2f;
            float[] dist = {0.0f, 1.0f};
            
            Color transparentBlack = new Color(0, 0, 0, 0);
            Color shadowColor = new Color(0, 0, 0, (int)(opacity * 255));
            
            Color[] colors = {shadowColor, transparentBlack};
            
            RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(p);
            
            // Draw the ellipse/oval
            g2d.fillOval(shadowX, shadowY, width, height);
        }

        g2d.dispose(); // Release the graphics context copy
    }

    @Override
    public RenderLayer getRenderLayer() {
        // The shadow should be drawn below characters, but above the ground.
        return StandardLayers.GAMEPLAY_BELOW;
    }

    @Override
    public int getZOrder() {
        // The shadow should have a Z-Order slightly less than its owner
        // to ensure it is drawn strictly beneath it.
        return (owner != null) ? owner.getZOrder() - 1 : 0;
    }

    @Override
    public boolean isVisible() {
        return (owner != null && owner.isVisible());
    }

    /**
     * Activates or deactivates the shadow's rendering.
     * @param active true to show the shadow, false to hide it.
     */
    public void setActive(boolean active) {
    	this.isActive = active;
    }

    /**
     * Checks if the shadow is currently active.
     * @return true if the shadow is active, false otherwise.
     */
    public boolean isActive() {
    	return this.isActive;
    }
}
