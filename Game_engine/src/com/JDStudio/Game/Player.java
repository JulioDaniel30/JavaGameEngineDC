package com.JDStudio.Game;

import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Object.GameObject;

public class Player extends GameObject {

    public boolean right, left, up, down;
    public double speed = 1.4;

    public Player(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);
    }

    @Override
    public void tick() {
        if (right) {
            x += speed;
        } else if (left) {
            x -= speed;
        }

        if (up) {
            y -= speed;
        } else if (down) {
            y += speed;
        }
    }
}