package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

/**
 * A UI element representing a temporary popup message that floats up from a target GameObject.
 * Commonly used for damage numbers, healing notifications, or short status messages.
 * 
 * @author JDStudio
 */
public class UIPopup extends UIElement {

    /** Flag indicating if the popup is currently active and visible. */
    public boolean isActive = false;
    private String text;
    private Font font;
    private Color color;
    private int lifeTime; // Duration in frames
    private GameObject target; // The GameObject to follow
    private double initialY; // Initial Y position for the floating effect

    /**
     * Constructs a new UIPopup.
     * It starts inactive and invisible.
     */
    public UIPopup() {
        super(0, 0);
        this.visible = false;
    }

    /**
     * Initializes or "wakes up" a popup from a pool with new properties.
     * Sets its initial position above the target GameObject.
     *
     * @param target   The GameObject this popup should follow.
     * @param text     The text message to display.
     * @param font     The font for the text.
     * @param color    The color of the text.
     * @param lifeTime The duration in frames for which the popup should be visible.
     */
    public void init(GameObject target, String text, Font font, Color color, int lifeTime) {
        this.target = target;
        this.text = text;
        this.font = font;
        this.color = color;
        this.lifeTime = lifeTime;
        
        // Initial position above the target
        this.x = target.getX() + target.getWidth() / 2;
        this.initialY = target.getY();
        this.y = (int)this.initialY;
        
        this.isActive = true;
        this.visible = true;
    }

    /**
     * Updates the popup's logic each frame.
     * It decrements the lifetime, moves the popup upwards, and follows the target's X position.
     */
    @Override
    public void tick() {
        if (!isActive) return;

        lifeTime--;
        if (lifeTime <= 0) {
            isActive = false;
            visible = false;
            return;
        }
        
        // Float slowly upwards
        this.y--;
        
        // Follow the target on the X-axis
        if (target != null) {
            this.x = target.getX() + target.getWidth() / 2;
        }
    }

    /**
     * Renders the popup text.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        g.setFont(font);
        g.setColor(color);
        
        // Center the text horizontally relative to its X position
        int textWidth = g.getFontMetrics().stringWidth(text);
        int drawX = (x - textWidth / 2) - Engine.camera.getX();
        int drawY = y - Engine.camera.getY();

        g.drawString(text, drawX, drawY);
    }
}
