package com.JDStudio.Engine.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Engine;

public class Tile {

    protected BufferedImage sprite;
    protected int x, y;
    public boolean isSolid = false; // <-- ADICIONE ESTA LINHA

    public Tile(int x, int y, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public void render(Graphics g) {
        // Desenha o tile normal
        g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
        
        // Se o modo de debug estiver ativo E o tile for sólido, desenha a hitbox
        if (Engine.isDebug && this.isSolid) {
            g.setColor(Color.BLUE); // Define uma cor diferente para os tiles
            g.drawRect(x - Camera.x, y - Camera.y, 16, 16);
        }
    }
}