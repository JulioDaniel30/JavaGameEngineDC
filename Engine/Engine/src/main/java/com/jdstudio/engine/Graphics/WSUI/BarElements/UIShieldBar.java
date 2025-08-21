package com.jdstudio.engine.Graphics.WSUI.BarElements;

import java.awt.Color;
import java.awt.Graphics;
import com.jdstudio.engine.Components.ShieldComponent;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

public class UIShieldBar extends UIWorldAttached {

    private final ShieldComponent shieldComponent;
    private int barWidth;
    private int barHeight;
    private Color backgroundColor;
    private Color foregroundColor;
    private boolean borderVisible; // Flag to control border visibility
    private Color borderColor; // Default border color

    public UIShieldBar(GameObject target, int yOffset, int width, int height) {
        super(target, yOffset);
        this.shieldComponent = target.getComponent(ShieldComponent.class);
        this.barWidth = width;
        this.barHeight = height;
        this.backgroundColor = Color.DARK_GRAY;
        this.foregroundColor = Color.BLUE; // Cor do escudo
        this.visible = true; // A barra está visível por padrão
        this.borderVisible = true; // A borda está visível por padrão
        this.borderColor = Color.BLACK; // Cor da borda padrão

        // A barra só é visível se o alvo tiver um ShieldComponent
        if (this.shieldComponent == null) {
            this.visible = false;
            System.err.println("Aviso: Tentativa de criar uma UISHieldBar para um objeto sem ShieldComponent: " + target.name);
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible || shieldComponent == null) return;

        // Calcula a posição de renderização no ecrã (considerando a câmara)
        int drawX = (this.x - barWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // 1. Desenha o fundo da barra
        g.setColor(backgroundColor);
        g.fillRect(drawX, drawY, barWidth, barHeight);

        // 2. Calcula e desenha o preenchimento (o escudo)
        int fillWidth = (int) (barWidth * (shieldComponent.getShieldHealth() / shieldComponent.getMaxShieldHealth()));
        g.setColor(foregroundColor);
        g.fillRect(drawX, drawY, fillWidth, barHeight);

        // 3. (Opcional) Desenha uma borda
        if (borderVisible) {
            g.setColor(borderColor);
            g.drawRect(drawX, drawY, barWidth, barHeight);
        }
    }

    // Métodos para controlar a visibilidade da borda
    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
    }
    public boolean isBorderVisible() {
        return this.borderVisible;
    }
    // Método para definir a cor da borda
    public void setBorderColor(Color color) {
        this.borderColor = color;
    }
    public Color getBorderColor() {
        return this.borderColor;
    }
    // Método para atualizar a largura e altura da barra
    public void setBarDimensions(int width, int height) {
        this.barWidth = width;
        this.barHeight = height;
    }
    public int getBarWidth() {
        return barWidth;
    }
    public int getBarHeight() {
        return barHeight;
    }
    // Método para atualizar a posição da barra
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    // Método para verificar se a barra está visível
    public boolean isVisible() {
        return this.visible;
    }
    // Método para definir a visibilidade da barra
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    // Método para obter o componente de escudo
    public ShieldComponent getShieldComponent() {
        return this.shieldComponent;
    }
    //metodo para controlar as cores
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    public void setForegroundColor(Color color) {
        this.foregroundColor = color;
    }
    public Color getForegroundColor() {
        return this.foregroundColor;
    }
    // Método para obter o progresso de recarga do escudo
    public float getRechargeProgress() {
        if (shieldComponent == null || shieldComponent.getChargeableComponent() == null) {
            return 0;
        }
        return shieldComponent.getChargeableComponent().getProgress();
    }
    


}