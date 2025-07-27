package com.game.Game;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Object.GameObject;
public class Enemy extends GameObject {
    public Enemy(double x, double y, int width, int height, Sprite sprite) {
        super(x, y, width, height, sprite);
    }
    @Override
    public void tick() { /* Lógica de IA do inimigo aqui */ }
}		