package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Consumer;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Input.InputManager;

/**
 * A UI element representing a toggle button that switches between two states (on/off).
 * It displays different sprites for each state and executes a defined action when its state changes.
 * 
 * @author JDStudio
 */
public class UIToggleButton extends UIElement {

    private Sprite offSprite, onSprite;
    private boolean isOn;
    private Consumer<Boolean> onToggleAction; // Action that receives the new state (true/false)

    private boolean isHovering = false;

    /**
     * Constructs a new UIToggleButton.
     *
     * @param x              The x-coordinate of the button's top-left corner.
     * @param y              The y-coordinate of the button's top-left corner.
     * @param offSprite      The sprite for the button's "off" state.
     * @param onSprite       The sprite for the button's "on" state.
     * @param initialState   The initial state of the toggle button (true for on, false for off).
     * @param onToggleAction The action to run when the button is toggled. Receives the new state as a boolean.
     */
    public UIToggleButton(int x, int y, Sprite offSprite, Sprite onSprite, boolean initialState, Consumer<Boolean> onToggleAction) {
        super(x, y);
        this.offSprite = offSprite;
        this.onSprite = onSprite;
        this.isOn = initialState;
        this.onToggleAction = onToggleAction;
        
        if (offSprite != null) {
            this.width = offSprite.getWidth();
            this.setHeight(offSprite.getHeight());
        }
    }
    
    /**
     * Updates the toggle button's state based on mouse input.
     * It detects hover and click events, toggling the state and executing the onToggleAction.
     */
    @Override
    public void tick() {
        if (!visible) {
            isHovering = false;
            return;
        }

        int mouseX = InputManager.getMouseX() / com.jdstudio.engine.Engine.getSCALE();
        int mouseY = InputManager.getMouseY() / com.jdstudio.engine.Engine.getSCALE();

        Rectangle bounds = new Rectangle(x, y, width, getHeight());
        isHovering = bounds.contains(mouseX, mouseY);

        if (isHovering && InputManager.isLeftMouseButtonJustPressed()) {
            isOn = !isOn; // Invert the state
            if (onToggleAction != null) {
                onToggleAction.accept(isOn); // Execute the action, passing the new state
            }
        }
    }

    /**
     * Renders the toggle button, drawing the appropriate sprite based on its current state (on/off).
     * Also draws a simple outline when the mouse is hovering over it.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        Sprite currentSprite = isOn ? onSprite : offSprite;
        if (currentSprite != null) {
            g.drawImage(currentSprite.getImage(), x, y, null);
        }
        
        // Add a simple outline when the mouse is hovering
        if(isHovering){
            g.setColor(new java.awt.Color(255, 255, 0, 100)); // Semi-transparent yellow
            g.drawRect(x, y, width, getHeight());
        }
    }
}
