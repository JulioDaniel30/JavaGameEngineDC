package com.JDStudio.Engine.Graphics.UI.Elements;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Consumer;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Input.InputManager;

/**/

public class UIToggleButton extends UIElement {

    private Sprite offSprite, onSprite;
    private boolean isOn;
    private Consumer<Boolean> onToggleAction; // Ação que recebe o novo estado (true/false)

    private boolean isHovering = false;

    public UIToggleButton(int x, int y, Sprite offSprite, Sprite onSprite, boolean initialState, Consumer<Boolean> onToggleAction) {
        super(x, y);
        this.offSprite = offSprite;
        this.onSprite = onSprite;
        this.isOn = initialState;
        this.onToggleAction = onToggleAction;
        
        if (offSprite != null) {
            this.width = offSprite.getWidth();
            this.height = offSprite.getHeight();
        }
    }
    
    @Override
    public void tick() {
        if (!visible) {
            isHovering = false;
            return;
        }

        int mouseX = InputManager.getMouseX() / com.JDStudio.Engine.Engine.SCALE;
        int mouseY = InputManager.getMouseY() / com.JDStudio.Engine.Engine.SCALE;

        Rectangle bounds = new Rectangle(x, y, width, height);
        isHovering = bounds.contains(mouseX, mouseY);

        if (isHovering && InputManager.isLeftMouseButtonJustPressed()) {
            isOn = !isOn; // Inverte o estado
            if (onToggleAction != null) {
                onToggleAction.accept(isOn); // Executa a ação, passando o novo estado
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;

        Sprite currentSprite = isOn ? onSprite : offSprite;
        if (currentSprite != null) {
            g.drawImage(currentSprite.getImage(), x, y, null);
        }
        
        // Adiciona um contorno simples quando o mouse está em cima
        if(isHovering){
            g.setColor(new java.awt.Color(255, 255, 0, 100)); // Amarelo semitransparente
            g.drawRect(x, y, width, height);
        }
    }
}