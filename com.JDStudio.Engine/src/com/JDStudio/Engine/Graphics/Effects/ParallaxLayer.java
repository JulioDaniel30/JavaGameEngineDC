package com.JDStudio.Engine.Graphics.Effects;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

public class ParallaxLayer {

    private final Sprite sprite;
    private final double scrollFactor;

    /**
     * Cria uma nova camada de fundo para o efeito parallax.
     * @param sprite O sprite (imagem) a ser usado para esta camada.
     * @param scrollFactor A velocidade de rolagem. 0.0 = parado; 1.0 = move com a câmera; < 1.0 = fundo; > 1.0 = frente.
     */
    public ParallaxLayer(Sprite sprite, double scrollFactor) {
        this.sprite = sprite;
        this.scrollFactor = scrollFactor;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public double getScrollFactor() {
        return scrollFactor;
    }
}