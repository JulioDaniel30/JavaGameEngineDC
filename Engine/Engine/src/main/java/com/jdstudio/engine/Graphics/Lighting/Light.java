package com.jdstudio.engine.Graphics.Lighting;

import java.awt.Color;

public class Light {

    public double x, y;
    public double radius;
    public Color color;

    /**
     * Cria uma nova fonte de luz.
     * @param x Posição X no mundo.
     * @param y Posição Y no mundo.
     * @param radius O raio da luz em pixels.
     * @param color A cor da luz. A intensidade pode ser controlada pelo canal Alfa da cor.
     */
    public Light(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }
    
    
    
}