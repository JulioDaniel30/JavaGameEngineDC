package com.JDStudio.Game;

import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Object.GameObject;
public class Lifepack extends GameObject {
    public Lifepack(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);
    }
    @Override
    public void tick() { /* Lógica do item aqui */ }
}