package com.jdstudio.engine.Graphics.Effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public class Particle {

    public boolean isActive = false;

    private Point2D.Double position;
    private Point2D.Double velocity;
    
    private Color startColor, endColor, currentColor;
    private float startSize, endSize, currentSize;
    
    private int life, maxLife;
    
    //private static final Random random = new Random();

    public Particle() {
        this.position = new Point2D.Double();
        this.velocity = new Point2D.Double();
    }

    /**
     * Inicializa ou "acorda" uma partícula da piscina com novas propriedades.
     */
    public void init(double x, double y, double velX, double velY, float startSize, float endSize,
                     Color startColor, Color endColor, int life) {
        this.position.setLocation(x, y);
        this.velocity.setLocation(velX, velY);
        this.startSize = startSize;
        this.endSize = endSize;
        this.currentSize = startSize;
        this.startColor = startColor;
        this.endColor = endColor;
        this.currentColor = startColor;
        this.maxLife = life;
        this.life = life;
        this.isActive = true;
    }

    /**
     * Atualiza a lógica da partícula a cada quadro.
     */
    public void update() {
        if (!isActive) return;

        life--;
        if (life <= 0) {
            isActive = false;
            return;
        }

        // Atualiza posição
        position.x += velocity.x;
        position.y += velocity.y;
        
        // Interpolação linear para tamanho e cor
        float lifeRatio = (float) life / (float) maxLife; // 1.0 no início, 0.0 no fim

        // Tamanho
        currentSize = endSize + (startSize - endSize) * lifeRatio;
        
        // Cor (interpola cada canal: R, G, B, A)
        int r = (int) (endColor.getRed() + (startColor.getRed() - endColor.getRed()) * lifeRatio);
        int g = (int) (endColor.getGreen() + (startColor.getGreen() - endColor.getGreen()) * lifeRatio);
        int b = (int) (endColor.getBlue() + (startColor.getBlue() - endColor.getBlue()) * lifeRatio);
        int a = (int) (endColor.getAlpha() + (startColor.getAlpha() - endColor.getAlpha()) * lifeRatio);
        
        currentColor = new Color(r, g, b, a);
    }

    /**
     * Renderiza a partícula.
     */
    public void render(Graphics g, int cameraX, int cameraY) {
        if (!isActive || currentSize <= 0) return;
        
        g.setColor(currentColor);
        
        // Calcula a posição de renderização
        int drawX = (int) (position.x - (currentSize / 2)) - cameraX;
        int drawY = (int) (position.y - (currentSize / 2)) - cameraY;
        
        g.fillOval(drawX, drawY, (int)currentSize, (int)currentSize);
    }
}