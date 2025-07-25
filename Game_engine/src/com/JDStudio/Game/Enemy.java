package com.JDStudio.Game;

import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Object.GameObject;
public class Enemy extends GameObject {
    public Enemy(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);
    }
    @Override
    public void tick() { /* Lógica de IA do inimigo aqui */ }
}		