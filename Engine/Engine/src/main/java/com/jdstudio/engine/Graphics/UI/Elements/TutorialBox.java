package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import com.jdstudio.engine.Engine;

/**
 * A simple UIElement for displaying tutorial text.
 * It is controlled by the {@code TutorialManager} and can be positioned dynamically.
 * 
 * @author JDStudio
 */
public class TutorialBox extends UIElement {

    private String currentText = "";
    private Font font = new Font("Arial", Font.BOLD, 12);
    private Color textColor = Color.WHITE;
    private Color boxColor = new Color(0, 0, 0, 180);

    /**
     * Constructs a new TutorialBox.
     * It starts invisible and its position will be set by the TutorialManager.
     */
    public TutorialBox() {
        super(0, 0); // Position will be set by TutorialManager
        this.visible = false; // Starts invisible
    }

    /**
     * Displays the tutorial box with the given text and positions it based on the specified location.
     *
     * @param text     The text to display in the tutorial box.
     * @param position The desired position (e.g., "BOTTOM_CENTER", or any other string for default top-center).
     */
    public void show(String text, String position) {
        this.currentText = text;
        
        // Basic width estimation (can be improved with FontMetrics)
        this.width = text.length() * 7 + 20; 
        this.setHeight(30);

        if ("BOTTOM_CENTER".equals(position)) {
            this.x = Engine.getWIDTH() / 2 - this.width / 2;
            this.y = Engine.getHEIGHT() - this.getHeight() - 20;
        } else { // Default to top-center
            this.x = Engine.getWIDTH() / 2 - this.width / 2;
            this.y = 20;
        }
        
        this.visible = true;
    }

    /**
     * Hides the tutorial box and clears its text.
     */
    public void hide() {
        this.visible = false;
        this.currentText = "";
    }
    
    /**
     * Renders the tutorial box with its background, text, and current position.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        g.setColor(boxColor);
        g.fillRect(x, y, width, getHeight());
        
        g.setColor(textColor);
        g.setFont(font);
        g.drawString(currentText, x + 10, y + 20);
    }
}
