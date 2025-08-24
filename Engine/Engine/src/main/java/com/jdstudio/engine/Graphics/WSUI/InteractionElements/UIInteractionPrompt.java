package com.jdstudio.engine.Graphics.WSUI.InteractionElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-space UI element that displays an interaction prompt (e.g., "Press E to Interact").
 * It inherits from {@link UIWorldAttached} to automatically follow its target GameObject.
 * 
 * @author JDStudio
 */
public class UIInteractionPrompt extends UIWorldAttached {

    private String text = "";
    
    // Styling properties
    private Font font = new Font("Arial", Font.BOLD, 10);
    private Color textColor = Color.WHITE;
    private Color boxColor = new Color(0, 0, 0, 180);

    /**
     * Constructs a new UIInteractionPrompt.
     * It starts without a target and with a default offset above the head.
     */
    public UIInteractionPrompt() {
        super(null, -10); // Starts without a target and with a default offset above the head
        this.visible = false; // Starts invisible
    }
    
    /**
     * Sets the GameObject that the prompt should follow and the text to be displayed.
     * If the target is null, the prompt becomes invisible.
     * 
     * @param target The new target GameObject.
     * @param text   The new prompt text.
     */
    public void setTarget(GameObject target, String text) {
        this.target = target; // Set the target in the parent class (UIWorldAttached)
        this.text = text;
        this.visible = (target != null);
    }

    // The tick() method is inherited from UIWorldAttached and already handles following the target.
    // We don't need to override it!

    /**
     * Renders the interaction prompt, drawing a rounded rectangle background and the text.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || target == null) return;

        g.setFont(font);
        
        int padding = 4;
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getAscent();
        
        // The coordinates 'this.x' and 'this.y' are already updated by the parent's tick()
        int drawX = (this.x - textWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        g.setColor(boxColor);
        g.fillRoundRect(drawX - padding, drawY - textHeight, textWidth + (padding * 2), textHeight + (padding * 2), 8, 8);

        g.setColor(textColor);
        g.drawString(text, drawX, drawY);
    }
}
