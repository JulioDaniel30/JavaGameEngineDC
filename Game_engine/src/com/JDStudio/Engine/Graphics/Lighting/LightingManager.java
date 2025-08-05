package com.JDStudio.Engine.Graphics.Lighting;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;

public class LightingManager {

    private static final LightingManager instance = new LightingManager();
    private final List<Light> lights = new ArrayList<>();
    private BufferedImage lightmap;
    private Color ambientColor = new Color(0, 0, 0, 20);

    private LightingManager() {}

    public static LightingManager getInstance() { return instance; }
    public void addLight(Light light) { this.lights.add(light); }
    public void removeLight(Light light) { this.lights.remove(light); }
    public void setAmbientColor(Color color) { this.ambientColor = color; }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // --- PASSO 1: DESENHAR A MÁSCARA DE ESCURIDÃO---
        if (lightmap == null || lightmap.getWidth() != Engine.WIDTH || lightmap.getHeight() != Engine.HEIGHT) {
            lightmap = new BufferedImage(Engine.WIDTH, Engine.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d_lightmap = (Graphics2D) lightmap.getGraphics();
        g2d_lightmap.setComposite(AlphaComposite.Clear);
        g2d_lightmap.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);
        g2d_lightmap.setComposite(AlphaComposite.SrcOver);
        g2d_lightmap.setColor(ambientColor);
        g2d_lightmap.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);
        g2d_lightmap.setComposite(AlphaComposite.DstOut);
        g2d_lightmap.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        for (Light light : lights) {
            int lightX = (int)light.x - Engine.camera.getX();
            int lightY = (int)light.y - Engine.camera.getY();
            if (light instanceof ConeLight) {
                ConeLight cone = (ConeLight) light;
                Sprite coneSprite = cone.lightSprite;
                if (coneSprite == null) continue;
                AffineTransform oldTransform = g2d_lightmap.getTransform();
                g2d_lightmap.rotate(cone.angle, lightX, lightY);
                double scaleFactor = cone.radius / coneSprite.getWidth();
                int scaledWidth = (int) (coneSprite.getWidth() * scaleFactor);
                int scaledHeight = (int) (coneSprite.getHeight() * scaleFactor);
                g2d_lightmap.drawImage(coneSprite.getImage(), lightX, lightY - scaledHeight / 2, scaledWidth, scaledHeight, null);
                g2d_lightmap.setTransform(oldTransform);
            } else {
                float radius = (float) light.radius;
                Point2D center = new Point2D.Float(lightX, lightY);
                float[] dist = {0.0f, 1.0f};
                Color[] colors = {Color.WHITE, new Color(255, 255, 255, 0)};
                RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
                g2d_lightmap.setPaint(p);
                g2d_lightmap.fillOval((int)(lightX - radius), (int)(lightY - radius), (int)(radius * 2), (int)(radius * 2));
            }
        }
        g2d_lightmap.dispose();

        // --- PASSO 2: PINTAR A COR DAS LUZES POR CIMA ---
        for (Light light : lights) {
            int lightX = (int)light.x - Engine.camera.getX();
            int lightY = (int)light.y - Engine.camera.getY();
            
            float radius;
            // --- LÓGICA ATUALIZADA AQUI ---
            if (light instanceof ConeLight) {
                // Para o cone, desenhamos apenas um "brilho" colorido na sua origem.
                // Usamos um raio pequeno, ex: um quarto da distância do feixe de luz.
                radius = (float) light.radius / 4.0f;
            } else {
                // Para a luz circular, usamos o raio completo.
                radius = (float) light.radius;
            }

            if(radius <= 0) continue;

            Point2D center = new Point2D.Float(lightX, lightY);
            float[] dist = {0.0f, 1.0f};
            Color startColor = light.color;
            Color endColor = new Color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), 0);
            Color[] colors = {startColor, endColor};
            
            RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(p);
            g2d.fillOval((int)(lightX - radius), (int)(lightY - radius), (int)(radius * 2), (int)(radius * 2));
        }

        // --- PASSO 3: DESENHAR A MÁSCARA DE ESCURIDÃO FINAL ---
        g.drawImage(lightmap, 0, 0, null);
    }
    
    public void reset() {
        lights.clear();
        System.out.println("LightingManager resetado.");
    }
    
}