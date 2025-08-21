package com.jdstudio.engine.Graphics.WSUI;
//World Space UI

import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Object.GameObject;

public abstract class UIWorldAttached extends UIElement {

    public GameObject target;
    protected int yOffset; // Deslocamento vertical em relação ao alvo

    public UIWorldAttached(GameObject target, int yOffset) {
        super(0, 0); // Posição inicial é irrelevante
        this.target = target;
        this.yOffset = yOffset;
    }

    @Override
    public void tick() {
        if (target == null || target.isDestroyed) {
            this.visible = false;
            return;
        }
        
        // A posição da UI é atualizada para seguir o alvo
        // Centraliza horizontalmente, e aplica o offset vertical
        this.x = target.getX() + (target.getWidth() / 2);
        this.y = target.getY() + yOffset;
    }
}