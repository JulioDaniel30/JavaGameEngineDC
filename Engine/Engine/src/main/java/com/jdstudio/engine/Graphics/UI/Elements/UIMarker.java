package com.jdstudio.engine.Graphics.UI.Elements;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * A {@link UIImage} that dynamically positions itself along a {@link UIProgressBar}.
 * It is typically used to indicate a specific point or threshold on a progress bar.
 * 
 * @author JDStudio
 */
public class UIMarker extends UIImage {

    private UIProgressBar progressBar;

    /**
     * Constructs a new UIMarker.
     *
     * @param progressBar The UIProgressBar this marker will be attached to.
     * @param sprite      The Sprite to use for the marker.
     */
    public UIMarker(UIProgressBar progressBar, Sprite sprite) {
        // The initial position doesn't matter, as it will be updated in tick()
        super(0, 0, sprite);
        this.progressBar = progressBar;
    }

    /**
     * Updates the marker's position based on the progress of its associated UIProgressBar.
     * It centers the marker horizontally on the progress point and vertically on the bar.
     */
    @Override
    public void tick() {
        if (!visible || progressBar == null) return;
        
        // Get the progress ratio of the bar (0.0 to 1.0)
        float ratio = progressBar.getProgressRatio();
        
        // Calculate the marker's X position
        int barTotalWidth = progressBar.width;
        int markerWidth = this.width;
        // The position is the start of the bar + proportional distance - half of the marker's width for centering
        this.x = progressBar.x + (int)(ratio * barTotalWidth) - (markerWidth / 2);
        
        // Calculate the marker's Y position (vertically centered on the bar)
        this.y = progressBar.y + (progressBar.getHeight() / 2) - (this.getHeight() / 2);
    }

    // The render() method is inherited from UIImage and already works perfectly.
}
