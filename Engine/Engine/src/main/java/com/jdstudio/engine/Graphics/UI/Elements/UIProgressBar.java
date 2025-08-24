package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Supplier;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * A progress bar UI element that automatically updates based on dynamic game data.
 * It displays a visual representation of a value (e.g., health, mana, experience)
 * relative to a maximum value.
 * 
 * @author JDStudio
 */
public class UIProgressBar extends UIElement {

    private Sprite backgroundSprite;
    private Color foregroundColor;

    // Suppliers to fetch values dynamically
    private Supplier<Float> valueSupplier;
    private Supplier<Float> maxSupplier;

    private float currentValue;
    private float maxValue;

    /**
     * Constructs a new UIProgressBar.
     *
     * @param x              The x-coordinate of the progress bar's top-left corner.
     * @param y              The y-coordinate of the progress bar's top-left corner.
     * @param background     The Sprite to use for the background of the bar.
     * @param foreground     The Color to use for the filled foreground of the bar.
     * @param valueSupplier  A Supplier that provides the current value of the progress.
     * @param maxSupplier    A Supplier that provides the maximum value of the progress.
     */
    public UIProgressBar(int x, int y, Sprite background, Color foreground, 
                         Supplier<Float> valueSupplier, Supplier<Float> maxSupplier) {
        super(x, y);
        this.backgroundSprite = background;
        this.foregroundColor = foreground;
        this.valueSupplier = valueSupplier;
        this.maxSupplier = maxSupplier;
        
        if (background != null) {
            this.width = background.getWidth();
            this.setHeight(background.getHeight());
        }
    }

    /**
     * Updates the progress bar by fetching the latest values from its suppliers.
     * This method should be called every game tick.
     */
    @Override
    public void tick() {
        if (!visible) return;

        // The "automatic" magic happens here: fetches the latest values every frame
        this.currentValue = valueSupplier.get();
        this.maxValue = maxSupplier.get();
    }

    /**
     * Renders the progress bar, drawing its background sprite and then the filled foreground.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // 1. Draw the background of the bar
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, null);
        }

        // 2. Calculate and draw the fill
        float progressRatio = 0;
        if (maxValue > 0) {
            progressRatio = currentValue / maxValue;
        }
        // Ensure the bar doesn't exceed limits
        progressRatio = Math.max(0, Math.min(1, progressRatio)); 
        
        int fillWidth = (int)(this.width * progressRatio);
        
        g.setColor(foregroundColor);
        g.fillRect(x, y, fillWidth, this.getHeight());
    }
    
    /**
     * Returns the current progress as a ratio (from 0.0 to 1.0).
     * Useful for other elements, such as a {@link UIMarker}.
     *
     * @return The progress ratio.
     */
    public float getProgressRatio() {
        if (maxValue <= 0) return 0;
        return Math.max(0, Math.min(1, currentValue / maxValue));
    }
}
