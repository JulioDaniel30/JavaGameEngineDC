package com.JDStudio.Engine.Graphics.UI.Elements;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Supplier;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Uma barra de progresso que se atualiza automaticamente com base nos dados do jogo.
 */
public class UIProgressBar extends UIElement {

    private Sprite backgroundSprite;
    private Color foregroundColor;

    // Suppliers para buscar os valores dinamicamente
    private Supplier<Float> valueSupplier;
    private Supplier<Float> maxSupplier;

    private float currentValue;
    private float maxValue;

    public UIProgressBar(int x, int y, Sprite background, Color foreground, 
                         Supplier<Float> valueSupplier, Supplier<Float> maxSupplier) {
        super(x, y);
        this.backgroundSprite = background;
        this.foregroundColor = foreground;
        this.valueSupplier = valueSupplier;
        this.maxSupplier = maxSupplier;
        
        if (background != null) {
            this.width = background.getWidth();
            this.setHeight(background.getHeight());
        }
    }

    @Override
    public void tick() {
        if (!visible) return;

        // A mágica "automática" acontece aqui: busca os valores mais recentes a cada quadro
        this.currentValue = valueSupplier.get();
        this.maxValue = maxSupplier.get();
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // 1. Desenha o fundo da barra
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, null);
        }

        // 2. Calcula e desenha o preenchimento
        float progressRatio = 0;
        if (maxValue > 0) {
            progressRatio = currentValue / maxValue;
        }
        // Garante que a barra não passe dos limites
        progressRatio = Math.max(0, Math.min(1, progressRatio)); 
        
        int fillWidth = (int)(this.width * progressRatio);
        
        g.setColor(foregroundColor);
        g.fillRect(x, y, fillWidth, this.getHeight());
    }
    
    /**
     * Retorna a proporção atual de progresso (de 0.0 a 1.0).
     * Útil para outros elementos, como o UIMarker.
     */
    public float getProgressRatio() {
        if (maxValue <= 0) return 0;
        return Math.max(0, Math.min(1, currentValue / maxValue));
    }
}