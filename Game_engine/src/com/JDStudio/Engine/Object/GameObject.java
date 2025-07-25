package com.JDStudio.Engine.Object;


import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.JDStudio.Engine.World.Camera;

/**
 * A classe base para todos os objetos renderizáveis e atualizáveis no jogo.
 * Cada objeto no jogo deve estender esta classe.
 *
 * <p><b>Exemplo de como criar uma nova entidade:</b>
 * {@snippet :
 * class Rocket extends GameObject {
 * public Rocket(double x, double y) {
 * super(x, y, 16, 16, null); // Chama o construtor pai
 * }
 *
 * @Override
 * public void tick() {
 * // Lógica de movimento do foguete
 * this.y -= 5;
 * }
 * }
 *}
 */

public abstract class GameObject {

    protected double x, y; // Double para movimento suave
    protected int width, height;
    protected BufferedImage sprite;

    public GameObject(double x, double y, int width, int height, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
    }

    public abstract void tick();

    public void render(Graphics g) {
        g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
    }
    
    // Getters and Setters
    public void setX(double newX) { this.x = newX; }
    public void setY(double newY) { this.y = newY; }
    public int getX() { return (int)this.x; }
    public int getY() { return (int)this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
}