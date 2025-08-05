package com.JDStudio.Engine.Graphics.UI;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Um UIImage que se posiciona dinamicamente ao longo de uma UIProgressBar.
 */
public class UIMarker extends UIImage {

    private UIProgressBar progressBar;

    public UIMarker(UIProgressBar progressBar, Sprite sprite) {
        // A posição inicial não importa, pois será atualizada no tick()
        super(0, 0, sprite);
        this.progressBar = progressBar;
    }

    @Override
    public void tick() {
        if (!visible || progressBar == null) return;
        
        // Pega a proporção de progresso da barra (0.0 a 1.0)
        float ratio = progressBar.getProgressRatio();
        
        // Calcula a posição X do marcador
        int barTotalWidth = progressBar.width;
        int markerWidth = this.width;
        // A posição é o início da barra + a distância proporcional - metade da largura do marcador para centralizar
        this.x = progressBar.x + (int)(ratio * barTotalWidth) - (markerWidth / 2);
        
        // Calcula a posição Y do marcador (centralizado verticalmente na barra)
        this.y = progressBar.y + (progressBar.height / 2) - (this.height / 2);
    }

    // O método render() é herdado de UIImage e já funciona perfeitamente.
}