package com.JDStudio.Engine.Graphics.UI;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Consumer;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Input.InputManager;


public class UISlider extends UIElement {

    private Sprite trackSprite, handleSprite;
    private float minValue, maxValue, currentValue;
    private Consumer<Float> onValueChanged;

    private boolean isDragging = false;
    private int handleWidth, handleHeight;

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
            this.height = trackSprite.getHeight();
        }
        if (handleSprite != null) {
            this.handleWidth = handleSprite.getWidth();
            this.handleHeight = handleSprite.getHeight();
        }
    }
    
    @Override
    public void tick() {
        if (!visible) {
            isDragging = false;
            return;
        }

        int mouseX = InputManager.getMouseX() / com.JDStudio.Engine.Engine.SCALE;

        int handleX = getHandleX();
        int handleY = y + (height - handleHeight) / 2;
        Rectangle handleBounds = new Rectangle(handleX, handleY, handleWidth, handleHeight);

        if (InputManager.isLeftMouseButtonJustPressed() && handleBounds.contains(mouseX, InputManager.getMouseY() / com.JDStudio.Engine.Engine.SCALE)) {
            isDragging = true;
        }

        if (!InputManager.isLeftMouseButtonPressed()) {
            isDragging = false;
        }

        if (isDragging) {
            // Garante que o mouseX está dentro dos limites da barra
            float clampedMouseX = Math.max(x, Math.min(mouseX, x + width - handleWidth));
            
            // Converte a posição do mouse em um valor
            float ratio = (clampedMouseX - x) / (float)(width - handleWidth);
            float newValue = minValue + ratio * (maxValue - minValue);

            if (newValue != currentValue) {
                currentValue = newValue;
                if (onValueChanged != null) {
                    onValueChanged.accept(currentValue);
                }
            }
        }
    }
    
    private int getHandleX() {
        float valueRatio = (currentValue - minValue) / (maxValue - minValue);
        return x + (int)(valueRatio * (width - handleWidth));
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // Desenha a barra
        if (trackSprite != null) {
            g.drawImage(trackSprite.getImage(), x, y, null);
        }
        
        // Desenha o cursor/alça
        if (handleSprite != null) {
            g.drawImage(handleSprite.getImage(), getHandleX(), y + (height - handleHeight) / 2, null);
        }
    }
}