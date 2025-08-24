package com.jdstudio.engine.Graphics.Lighting;

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

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * A singleton class responsible for managing and rendering dynamic lighting effects in the game.
 * It uses a lightmap approach to create areas of darkness and light, supporting both
 * radial lights and cone lights.
 * 
 * @author JDStudio
 */
public class LightingManager {

    private static final LightingManager instance = new LightingManager();
    
    /** The list of all active light sources in the scene. */
    private final List<Light> lights = new ArrayList<>();
    
    /** The BufferedImage used as a lightmap to calculate and apply lighting effects. */
    private BufferedImage lightmap;
    
    /** The ambient color that defines the base level of darkness in the scene. */
    private Color ambientColor = new Color(0, 0, 0, 5);

    private LightingManager() {}

    /**
     * Gets the single instance of the LightingManager.
     * @return The singleton instance.
     */
    public static LightingManager getInstance() { 
        return instance; 
    }

    /**
     * Adds a light source to the scene.
     * @param light The Light object to add.
     */
    public void addLight(Light light) { 
        this.lights.add(light); 
    }

    /**
     * Removes a light source from the scene.
     * @param light The Light object to remove.
     */
    public void removeLight(Light light) { 
        this.lights.remove(light); 
    }

    /**
     * Sets the ambient color for the scene.
     * This color determines the base level of darkness.
     * @param color The new ambient Color.
     */
    public void setAmbientColor(Color color) { 
        this.ambientColor = color; 
    }

    /**
     * Renders the lighting effects onto the main graphics context.
     * This method performs a multi-pass rendering process:
     * 1. Draws a darkness mask (lightmap) with ambient color, then punches out light areas.
     * 2. Draws the colored light sources on top.
     * 3. Applies the final lightmap to the screen.
     * 
     * @param g The Graphics context to draw on.
     */
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // --- STEP 1: DRAW THE DARKNESS MASK ---
        // Ensure the lightmap matches the current screen size
        if (lightmap == null || lightmap.getWidth() != Engine.getWIDTH() || lightmap.getHeight() != Engine.getHEIGHT()) {
            lightmap = new BufferedImage(Engine.getWIDTH(), Engine.getHEIGHT(), BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2d_lightmap = (Graphics2D) lightmap.getGraphics();
        
        // Clear the lightmap and fill with ambient darkness
        g2d_lightmap.setComposite(AlphaComposite.Clear);
        g2d_lightmap.fillRect(0, 0, Engine.getWIDTH(), Engine.getHEIGHT());
        g2d_lightmap.setComposite(AlphaComposite.SrcOver);
        g2d_lightmap.setColor(ambientColor);
        g2d_lightmap.fillRect(0, 0, Engine.getWIDTH(), Engine.getHEIGHT());
        
        // Punch out light areas from the darkness mask
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
                Color[] colors = {Color.WHITE, new Color(255, 255, 255, 0)}; // White light, fading to transparent
                RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
                g2d_lightmap.setPaint(p);
                g2d_lightmap.fillOval((int)(lightX - radius), (int)(lightY - radius), (int)(radius * 2), (int)(radius * 2));
            }
        }
        g2d_lightmap.dispose();

        // --- STEP 2: PAINT THE COLOR OF THE LIGHTS ON TOP ---
        // This pass applies the actual color of the lights.
        for (Light light : lights) {
            int lightX = (int)light.x - Engine.camera.getX();
            int lightY = (int)light.y - Engine.camera.getY();
            
            float radius;
            if (light instanceof ConeLight) {
                // For cone lights, draw a small colored glow at its origin.
                // Using a small radius, e.g., a quarter of the beam distance.
                radius = (float) light.radius / 4.0f;
            } else {
                // For circular lights, use the full radius.
                radius = (float) light.radius;
            }

            if(radius <= 0) continue;

            Point2D center = new Point2D.Float(lightX, lightY);
            float[] dist = {0.0f, 1.0f};
            Color startColor = light.color;
            Color endColor = new Color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), 0); // Fade to transparent version of light color
            Color[] colors = {startColor, endColor};
            
            RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(p);
            g2d.fillOval((int)(lightX - radius), (int)(lightY - radius), (int)(radius * 2), (int)(radius * 2));
        }

        // --- STEP 3: DRAW THE FINAL DARKNESS MASK ---
        // Apply the pre-calculated lightmap to the screen.
        g.drawImage(lightmap, 0, 0, null);
    }
    
    /**
     * Clears all registered light sources.
     */
    public void reset() {
        lights.clear();
        System.out.println("LightingManager reset.");
    }
}
