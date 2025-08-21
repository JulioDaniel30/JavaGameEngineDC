package com.jdstudio.engine.Graphics.WSUI.BarElements;

import java.awt.Color;
import java.awt.Graphics;
import com.jdstudio.engine.Components.HealthComponent;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

public class UIHealthBar extends UIWorldAttached {

    private final HealthComponent healthComponent;
    private final int barWidth;
    private final int barHeight;
    private final Color backgroundColor;
    private final Color foregroundColor;

    public UIHealthBar(GameObject target, int yOffset, int width, int height) {
        super(target, yOffset);
        this.healthComponent = target.getComponent(HealthComponent.class);
        this.barWidth = width;
        this.barHeight = height;
        this.backgroundColor = Color.DARK_GRAY;
        this.foregroundColor = Color.RED;

        // A barra só é visível se o alvo tiver um HealthComponent
        if (this.healthComponent == null) {
            this.visible = false;
            System.err.println("Aviso: Tentativa de criar uma UIHealthBar para um objeto sem HealthComponent: " + target.name);
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible || healthComponent == null) return;
        
        // Calcula a posição de renderização no ecrã (considerando a câmara)
        int drawX = (this.x - barWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // 1. Desenha o fundo da barra
        g.setColor(backgroundColor);
        g.fillRect(drawX, drawY, barWidth, barHeight);

        // 2. Calcula e desenha o preenchimento (a vida)
        int fillWidth = (int) (barWidth * healthComponent.getHealthPercentage());
        g.setColor(foregroundColor);
        g.fillRect(drawX, drawY, fillWidth, barHeight);
        
        // 3. (Opcional) Desenha uma borda
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, barWidth, barHeight);
    }
}