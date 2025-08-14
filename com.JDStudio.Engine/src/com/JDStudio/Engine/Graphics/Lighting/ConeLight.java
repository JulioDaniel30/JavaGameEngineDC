package com.JDStudio.Engine.Graphics.Lighting;

import java.awt.Color;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;

public class ConeLight extends Light {

    public double angle;
    public Sprite lightSprite;

    /**
     * Cria uma nova luz direcional (cone) com distância e cor customizáveis.
     * @param x Posição X da origem da luz.
     * @param y Posição Y da origem da luz.
     * @param distance O comprimento/distância do cone de luz em pixels.
     * @param angle Ângulo inicial da direção (em radianos).
     * @param lightSprite O sprite pré-desenhado do cone de luz.
     * @param color A cor da "tinta" da luz (deve ser semitransparente).
     */
    public ConeLight(double x, double y, double distance, double angle, Sprite lightSprite, Color color) {
        // Agora passamos a 'distance' como 'radius' e a cor recebida para o construtor pai.
        super(x, y, distance, color); 
        this.angle = angle;
        this.lightSprite = lightSprite;
    }
}