package com.JDStudio.Engine.Components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.time.chrono.IsoChronology;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Layers.IRenderable;
import com.JDStudio.Engine.Graphics.Layers.RenderLayer;
import com.JDStudio.Engine.Graphics.Layers.StandardLayers;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Um componente que renderiza uma sombra para um GameObject.
 * A sombra é desenhada na camada GAMEPLAY_BELOW, abaixo do personagem,
 * e sua profundidade Z é calculada para aparecer sempre sob o dono.
 */
public class ShadowComponent extends Component implements IRenderable {

    public enum ShadowType {
        /** Desenha uma sombra oval suave usando código. */
        PROCEDURAL_OVAL,
        /** Desenha um sprite fornecido como sombra. */
        SPRITE_BASED
    }
    private boolean isActive = true;
    private final ShadowType type;
    private Sprite shadowSprite;
    private int width;
    private int height;
    private float opacity;
    private int yOffset; // Deslocamento vertical da sombra em relação à base do dono

    // --- Construtor para Sombra Procedural ---
    /**
     * Cria uma sombra procedural (oval e suave).
     * @param width A largura da elipse da sombra.
     * @param height A altura da elipse da sombra.
     * @param opacity A opacidade da sombra (0.0f a 1.0f).
     * @param yOffset O deslocamento vertical da sombra em relação à base do dono.
     */
    public ShadowComponent(int width, int height, float opacity, int yOffset) {
        this.type = ShadowType.PROCEDURAL_OVAL;
        this.width = width;
        this.height = height;
        this.opacity = opacity;
        this.yOffset = yOffset;
    }

    // --- Construtor para Sombra baseada em Sprite ---
    /**
     * Cria uma sombra usando um sprite customizado.
     * @param shadowSprite O sprite a ser usado como sombra.
     * @param yOffset O deslocamento vertical da sombra em relação à base do dono.
     */
    public ShadowComponent(Sprite shadowSprite, int yOffset) {
        this.type = ShadowType.SPRITE_BASED;
        this.shadowSprite = shadowSprite;
        this.yOffset = yOffset;
        if (shadowSprite != null) {
            this.width = shadowSprite.getWidth();
            this.height = shadowSprite.getHeight();
        }
    }

    @Override
    public void initialize(GameObject owner) {
        super.initialize(owner);
        // O componente de sombra se registra para ser renderizado
        com.JDStudio.Engine.Graphics.Layers.RenderManager.getInstance().register(this);
    }
    
    // O método update() não é necessário, pois a sombra apenas segue o 'owner'.

    // --- Métodos da Interface IRenderable ---

    @Override
    public void render(Graphics g) {
        if (owner == null || !isActive) return;
        
        Graphics2D g2d = (Graphics2D) g.create(); // Cria uma cópia do contexto gráfico para não afetar outros desenhos

        // Posição da sombra: centralizada no X do dono, na base do Y do dono + offset
        int shadowX = owner.getX() + (owner.getWidth() / 2) - (this.width / 2) - Engine.camera.getX();
        int shadowY = owner.getY() + owner.getHeight() - (this.height / 2) + this.yOffset - Engine.camera.getY();

        if (type == ShadowType.SPRITE_BASED && shadowSprite != null) {
            // Define a opacidade para o sprite
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.drawImage(shadowSprite.getImage(), shadowX, shadowY, null);
            
        } else if (type == ShadowType.PROCEDURAL_OVAL) {
            // Usa um gradiente radial para criar uma sombra suave
            Point2D center = new Point2D.Float(shadowX + width / 2f, shadowY + height / 2f);
            float radius = width / 2f;
            float[] dist = {0.0f, 1.0f};
            
            Color transparentBlack = new Color(0, 0, 0, 0);
            Color shadowColor = new Color(0, 0, 0, (int)(opacity * 255));
            
            Color[] colors = {shadowColor, transparentBlack};
            
            RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(p);
            
            // Desenha a elipse/oval
            g2d.fillOval(shadowX, shadowY, width, height);
        }

        g2d.dispose(); // Libera a cópia do contexto gráfico
    }

    @Override
    public RenderLayer getRenderLayer() {
        // A sombra deve ser desenhada abaixo dos personagens, mas acima do chão.
        return StandardLayers.GAMEPLAY_BELOW;
    }

    @Override
    public int getZOrder() {
        // A sombra deve ter um Z-Order um pouco menor que o seu dono
        // para garantir que seja desenhada estritamente abaixo dele.
        return (owner != null) ? owner.getZOrder() - 1 : 0;
    }

    @Override
    public boolean isVisible() {
        return (owner != null && owner.isVisible());
    }
    public void setActive(boolean active) {
    	this.isActive = active;
    }
    public boolean isActive() {
    	return this.isActive;
    }
}