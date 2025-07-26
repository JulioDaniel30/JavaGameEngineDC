package com.JDStudio.Game;

import com.JDStudio.Engine.Graphics.Sprite;
import com.JDStudio.Engine.Object.GameObject;
public class Weapon extends GameObject {
    public Weapon(double x, double y, int width, int height, Sprite sprite) {
        super(x, y, width, height, sprite);
    }
    @Override
    public void tick() { /* Lógica da arma aqui */ }
}