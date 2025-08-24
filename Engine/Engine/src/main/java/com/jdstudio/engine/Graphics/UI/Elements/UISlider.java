package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Consumer;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.Input.InputManager;

/**
 * A UI element representing a slider that allows the user to select a value within a defined range
 * by dragging a handle along a track. It supports custom sprites for the track and handle,
 * or can use sprites from the current UI theme.
 * 
 * @author JDStudio
 */
public class UISlider extends UIElement {

    private Sprite trackSprite, handleSprite;
    private float minValue, maxValue, currentValue;
    private Consumer<Float> onValueChanged;

    private boolean isDragging = false;
    private int handleWidth, handleHeight;

    /**
     * Constructs a UISlider with custom sprites for its track and handle.
     *
     * @param x              The x-coordinate of the slider's top-left corner.
     * @param y              The y-coordinate of the slider's top-left corner.
     * @param trackSprite    The sprite for the slider's track.
     * @param handleSprite   The sprite for the slider's draggable handle.
     * @param min            The minimum value of the slider's range.
     * @param max            The maximum value of the slider's range.
     * @param initialValue   The initial value of the slider.
     * @param onValueChanged A Consumer that will be called with the new value when the slider's value changes.
     */
    public UISlider(int x, int y, Sprite trackSprite, Sprite handleSprite, float min, float max, float initialValue, Consumer<Float> onValueChanged) {
        super(x, y);
        this.trackSprite = trackSprite;
        this.handleSprite = handleSprite;
        this.minValue = min;
        this.maxValue = max;
        this.currentValue = initialValue;
        this.onValueChanged = onValueChanged;

        if (trackSprite != null) {
            this.width = trackSprite.getWidth();
            this.setHeight(trackSprite.getHeight());
        }
        if (handleSprite != null) {
            this.handleWidth = handleSprite.getWidth();
            this.handleHeight = handleSprite.getHeight();
        }
    }

    /**
     * Constructs a UISlider using sprites from the currently active {@link com.jdstudio.engine.Graphics.UI.UITheme}.
     * This constructor is convenient for creating themed sliders.
     *
     * @param x              The x-coordinate of the slider's top-left corner.
     * @param y              The y-coordinate of the slider's top-left corner.
     * @param min            The minimum value of the slider's range.
     * @param max            The maximum value of the slider's range.
     * @param initialValue   The initial value of the slider.
     * @param onValueChanged A Consumer that will be called with the new value when the slider's value changes.
     */
    public UISlider(int x, int y, float min, float max, float initialValue, Consumer<Float> onValueChanged) {
        super(x, y);
        this.trackSprite = ThemeManager.getInstance().get(UISpriteKey.SLIDER_TRACK);
        this.handleSprite = ThemeManager.getInstance().get(UISpriteKey.SLIDER_HANDLE);
        this.minValue = min;
        this.maxValue = max;
        this.currentValue = initialValue;

        if (trackSprite != null) {
            this.width = trackSprite.getWidth();
            this.setHeight(trackSprite.getHeight());
        }
        if (handleSprite != null) {
            this.handleWidth = handleSprite.getWidth();
            this.handleHeight = handleSprite.getHeight();
        }
    }
    
    /**
     * Updates the slider's state based on mouse input.
     * It detects when the handle is being dragged and updates the current value accordingly.
     */
    @Override
    public void tick() {
        if (!visible) {
            isDragging = false;
            return;
        }

        int mouseX = InputManager.getMouseX() / com.jdstudio.engine.Engine.getSCALE();

        int handleX = getHandleX();
        int handleY = y + (getHeight() - handleHeight) / 2;
        Rectangle handleBounds = new Rectangle(handleX, handleY, handleWidth, handleHeight);

        // Start dragging if mouse is pressed over the handle
        if (InputManager.isLeftMouseButtonJustPressed() && handleBounds.contains(mouseX, InputManager.getMouseY() / com.jdstudio.engine.Engine.getSCALE())) {
            isDragging = true;
        }

        // Stop dragging if mouse button is released
        if (!InputManager.isLeftMouseButtonPressed()) {
            isDragging = false;
        }

        // If dragging, update the value based on mouse position
        if (isDragging) {
            // Clamp mouseX within the bounds of the track
            float clampedMouseX = Math.max(x, Math.min(mouseX, x + width - handleWidth));
            
            // Convert mouse position to a value within the slider's range
            float ratio = (clampedMouseX - x) / (float)(width - handleWidth);
            float newValue = minValue + ratio * (maxValue - minValue);

            // Update value and trigger callback if changed
            if (newValue != currentValue) {
                currentValue = newValue;
                if (onValueChanged != null) {
                    onValueChanged.accept(currentValue);
                }
            }
        }
    }
    
    /**
     * Calculates the x-coordinate for the slider's handle based on the current value.
     * @return The x-coordinate for the handle.
     */
    private int getHandleX() {
        float valueRatio = (currentValue - minValue) / (maxValue - minValue);
        return x + (int)(valueRatio * (width - handleWidth));
    }

    /**
     * Renders the slider, drawing the track sprite and then the handle sprite at its calculated position.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // Draw the track
        if (trackSprite != null) {
            g.drawImage(trackSprite.getImage(), x, y, null);
        }
        
        // Draw the handle
        if (handleSprite != null) {
            g.drawImage(handleSprite.getImage(), getHandleX(), y + (getHeight() - handleHeight) / 2, null);
        }
    }
}
