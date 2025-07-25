// package com.arcastudio.mygame;
package com.JDStudio.Game;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;

public class Player extends GameObject {

    public double speed = 1.4;
    private World world; // <-- ADICIONE O CAMPO PARA GUARDAR O MUNDO

    public Player(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);
        setMaskWidth(9);
        setMaskX((int) (x+3));
    }
    
    /**
     * Define a instância do mundo que o jogador usará para verificar colisões.
     * @param world A instância do mundo do jogo.
     */
    public void setWorld(World world) { // <-- ADICIONE ESTE MÉTODO
        this.world = world;
    }

    @Override
    public void tick() {
        // Movimento Horizontal
        if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT) || InputManager.isKeyPressed(KeyEvent.VK_D)) {
            if (world != null && world.isFree((int)(x + speed), this.getY(), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                x += speed;
            }
        } else if (InputManager.isKeyPressed(KeyEvent.VK_LEFT) || InputManager.isKeyPressed(KeyEvent.VK_A)) {
            if (world != null && world.isFree((int)(x - speed), this.getY(), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                x -= speed;
            }
        }

        // Movimento Vertical
        if (InputManager.isKeyPressed(KeyEvent.VK_UP) || InputManager.isKeyPressed(KeyEvent.VK_W)) {
            if (world != null && world.isFree(this.getX(), (int)(y - speed), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                y -= speed;
            }
        } else if (InputManager.isKeyPressed(KeyEvent.VK_DOWN) || InputManager.isKeyPressed(KeyEvent.VK_S)) {
            if (world != null && world.isFree(this.getX(), (int)(y + speed), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                y += speed;
            }
        }
    }
}