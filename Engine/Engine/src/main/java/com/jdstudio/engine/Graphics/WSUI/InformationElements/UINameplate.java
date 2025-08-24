package com.jdstudio.engine.Graphics.WSUI.InformationElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-attached UI element that displays the name of a {@link GameObject} above it.
 * The nameplate automatically follows its target and centers the text.
 * 
 * @author JDStudio
 */
public class UINameplate extends UIWorldAttached {

    private final Font nameFont;
    private final Color nameColor;

    /**
     * Constructs a new UINameplate.
     *
     * @param target  The GameObject whose name will be displayed.
     * @param yOffset The vertical offset from the target's Y position.
     * @param font    The font for the name text.
     * @param color   The color for the name text.
     */
    public UINameplate(GameObject target, int yOffset, Font font, Color color) {
        super(target, yOffset);
        this.nameFont = font;
        this.nameColor = color;

        // The nameplate is only visible if the target has a name
        if (target.name == null || target.name.isEmpty()) {
            this.visible = false;
        }
    }

    /**
     * Renders the nameplate, drawing the target's name centered above it.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        // Use the target object's name as text
        String text = target.name;
        
        g.setFont(nameFont);
        g.setColor(nameColor);
        
        // Center the text horizontally relative to its X position
        int textWidth = g.getFontMetrics().stringWidth(text);
        int drawX = (this.x - textWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        g.drawString(text, drawX, drawY);
    }
}
