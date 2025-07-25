package com.JDStudio.Engine;

import java.awt.Graphics;

public abstract class GameState {
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract void keyPressed(int keyCode);
    public abstract void keyReleased(int keyCode);
}