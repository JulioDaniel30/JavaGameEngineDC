package com.JDStudio.Engine.Graphics.UI;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Input.InputManager;




public class UIButton extends UIElement {

    private Sprite normalSprite, hoverSprite, pressedSprite;
    private String text;
    private Font font;
    private Runnable onClickAction;

    private boolean isHovering = false;
    private boolean isPressed = false;

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
            this.height = normalSprite.getHeight();
        }
    }
    
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
            this.height = normalSprite.getHeight();
        }
    }
    
    @Override
    public void tick() {
        if (!visible) {
            isHovering = false;
            isPressed = false;
            return;
        }

        int mouseX = InputManager.getMouseX()/ com.JDStudio.Engine.Engine.SCALE;
        int mouseY = InputManager.getMouseY() / com.JDStudio.Engine.Engine.SCALE;

        Rectangle bounds = new Rectangle(this.x, this.y, this.width, this.height);
        isHovering = bounds.contains(mouseX, mouseY);

        if (isHovering) {
            if (InputManager.isLeftMouseButtonJustPressed()) {
                if (onClickAction != null) {
                    onClickAction.run(); // Executa a ação!
                }
            }
            isPressed = InputManager.isLeftMouseButtonPressed();
        } else {
            isPressed = false;
        }
    }

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

        // Desenha o texto centralizado no botão
        if (text != null && font != null) {
            g.setFont(font);
            int textWidth = g.getFontMetrics().stringWidth(text);
            int textHeight = g.getFontMetrics().getAscent();
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height + textHeight) / 2;
            g.drawString(text, textX, textY);
        }
    }
}