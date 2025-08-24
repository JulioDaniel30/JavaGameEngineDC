package com.jdstudio.engine.Graphics.WSUI.PersonalityElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-attached UI element that displays a temporary chat bubble above a {@link GameObject}.
 * It shows a short text message for a limited duration.
 * 
 * @author JDStudio
 */
public class UIChatBubble extends UIWorldAttached {

    private String text;
    private Font font;
    private Color textColor;
    private Color bubbleColor;
    private int lifeTime; // Duration in frames

    /**
     * Constructs a new UIChatBubble.
     *
     * @param target   The GameObject this chat bubble is attached to.
     * @param text     The text message to display in the bubble.
     * @param lifeTime The duration in frames for which the bubble should be visible.
     */
    public UIChatBubble(GameObject target, String text, int lifeTime) {
        super(target, -10); // Default offset above the head
        this.text = text;
        this.lifeTime = lifeTime;

        // Default styles
        this.font = new Font("Arial", Font.BOLD, 10);
        this.textColor = Color.BLACK;
        this.bubbleColor = new Color(255, 255, 255, 200); // Semi-transparent white
    }
    
    /**
     * Updates the chat bubble's logic each frame.
     * It decrements the lifetime, making the bubble disappear when it reaches zero.
     */
    @Override
    public void tick() {
        super.tick(); // Update position to follow the target
        if (!visible) return;

        lifeTime--;
        if (lifeTime <= 0) {
            this.visible = false; // The bubble "dies" by becoming invisible
        }
    }

    /**
     * Renders the chat bubble, drawing its background and the text.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        g.setFont(font);
        
        int padding = 4;
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getAscent();
        
        // Calculate rendering position on screen
        int drawX = (this.x - (textWidth / 2)) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // Draw the bubble background
        g.setColor(bubbleColor);
        g.fillRoundRect(drawX - padding, drawY - textHeight, textWidth + (padding * 2), textHeight + (padding * 2), 10, 10);

        // Draw the text
        g.setColor(textColor);
        g.drawString(text, drawX, drawY);
    }
}
