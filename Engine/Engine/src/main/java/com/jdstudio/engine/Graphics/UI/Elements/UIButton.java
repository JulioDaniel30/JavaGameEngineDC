package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.Input.InputManager;

/**
 * A UI element representing a clickable button.
 * It supports different visual states (normal, hover, pressed) and executes a defined action when clicked.
 * Buttons can be created with custom sprites or by using sprites from the current UI theme.
 * 
 * @author JDStudio
 */
public class UIButton extends UIElement {
    private Sprite normalSprite, hoverSprite, pressedSprite;
    private String text;
    private Font font;
    private Runnable onClickAction;

    private boolean isHovering = false;
    private boolean isPressed = false;

    /**
     * Constructs a UIButton with custom sprites for its states.
     *
     * @param x             The x-coordinate of the button's top-left corner.
     * @param y             The y-coordinate of the button's top-left corner.
     * @param normalSprite  The sprite for the button's normal state.
     * @param hoverSprite   The sprite for the button's hover state.
     * @param pressedSprite The sprite for the button's pressed state.
     * @param text          The text to display on the button.
     * @param font          The font for the button's text.
     * @param onClickAction The action to run when the button is clicked.
     */
    public UIButton(int x, int y, Sprite normalSprite, Sprite hoverSprite, Sprite pressedSprite, String text, Font font, Runnable onClickAction) {
        super(x, y);
        this.normalSprite = normalSprite;
        this.hoverSprite = hoverSprite;
        this.pressedSprite = pressedSprite;
        this.text = text;
        this.font = font;
        this.onClickAction = onClickAction;
        
        if (normalSprite != null) {
            this.width = normalSprite.getWidth();
            this.setHeight(normalSprite.getHeight());
        }
    }
    
    /**
     * Constructs a UIButton using sprites from the currently active {@link com.jdstudio.engine.Graphics.UI.UITheme}.
     * This constructor is convenient for creating themed buttons without manually providing sprites.
     *
     * @param x             The x-coordinate of the button's top-left corner.
     * @param y             The y-coordinate of the button's top-left corner.
     * @param text          The text to display on the button.
     * @param font          The font for the button's text.
     * @param onClickAction The action to run when the button is clicked.
     */
    public UIButton(int x, int y, String text, Font font, Runnable onClickAction) {
        super(x, y);
        this.normalSprite = ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL);
        this.hoverSprite = ThemeManager.getInstance().get(UISpriteKey.BUTTON_HOVER);
        this.pressedSprite = ThemeManager.getInstance().get(UISpriteKey.BUTTON_PRESSED);
        this.text = text;
        this.font = font;
        this.onClickAction = onClickAction;
        
        if (normalSprite != null) {
            this.width = normalSprite.getWidth();
            this.setHeight(normalSprite.getHeight());
        }
    }
    
    /**
     * Updates the button's state based on mouse input.
     * It detects hover and pressed states and triggers the onClickAction when clicked.
     */
    @Override
    public void tick() {
        if (!visible) {
            isHovering = false;
            isPressed = false;
            return;
        }

        // Adjust mouse coordinates by engine scale
        int mouseX = InputManager.getMouseX() / com.jdstudio.engine.Engine.getSCALE();
        int mouseY = InputManager.getMouseY() / com.jdstudio.engine.Engine.getSCALE();

        Rectangle bounds = new Rectangle(this.x, this.y, this.width, this.getHeight());
        isHovering = bounds.contains(mouseX, mouseY);

        if (isHovering) {
            if (InputManager.isLeftMouseButtonJustPressed()) {
                if (onClickAction != null) {
                    onClickAction.run(); // Execute the action!
                }
            }
            isPressed = InputManager.isLeftMouseButtonPressed();
        } else {
            isPressed = false;
        }
    }

    /**
     * Renders the button, drawing the appropriate sprite based on its state (normal, hover, pressed)
     * and centering the text on top of it.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible) return;

        Sprite currentSprite = normalSprite;
        if (isPressed) {
            currentSprite = pressedSprite;
        } else if (isHovering) {
            currentSprite = hoverSprite;
        }

        if (currentSprite != null) {
            g.drawImage(currentSprite.getImage(), x, y, null);
        }

        // Draw text centered on the button
        if (text != null && font != null) {
            g.setFont(font);
            int textWidth = g.getFontMetrics().stringWidth(text);
            int textHeight = g.getFontMetrics().getAscent();
            int textX = x + (width - textWidth) / 2;
            int textY = y + (getHeight() + textHeight) / 2;
            g.drawString(text, textX, textY);
        }
    }
}
