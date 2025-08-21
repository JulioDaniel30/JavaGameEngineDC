package com.jdstudio.engine.Graphics.WSUI.InteractionElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.function.Supplier;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * Um elemento de UI no mundo do jogo que desenha um círculo de progresso.
 * Ideal para indicar o carregamento de feitiços, interações demoradas, etc.
 * A sua visibilidade e progresso são controlados por 'suppliers'.
 */
public class UIProgressCircle extends UIWorldAttached {

    private final int radius;
    private final float strokeWidth;
    private final Color backgroundColor;
    private final Color foregroundColor;

    // Fornecedores para controlar o estado dinamicamente
    private final Supplier<Float> progressSupplier; // Fornece o progresso de 0.0f a 1.0f
    private final Supplier<Boolean> visibilitySupplier; // Fornece se o círculo deve ser visível

    public UIProgressCircle(GameObject target, int yOffset, int radius, float strokeWidth, 
                            Color background, Color foreground,
                            Supplier<Float> progressSupplier, Supplier<Boolean> visibilitySupplier) {
        super(target, yOffset);
        this.radius = radius;
        this.strokeWidth = strokeWidth;
        this.backgroundColor = background;
        this.foregroundColor = foreground;
        this.progressSupplier = progressSupplier;
        this.visibilitySupplier = visibilitySupplier;
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza a posição para seguir o alvo
        
        // A visibilidade é controlada pelo fornecedor
        if (this.visibilitySupplier != null) {
            this.visible = this.visibilitySupplier.get();
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible || target == null) return;
        
        Graphics2D g2d = (Graphics2D) g.create();

        // Melhora a qualidade do desenho do círculo
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcula a posição de renderização no ecrã
        int drawX = this.x - radius - Engine.camera.getX();
        int drawY = this.y - radius - Engine.camera.getY();
        int diameter = radius * 2;

        // 1. Desenha o círculo de fundo (o "caminho" do progresso)
        g2d.setColor(backgroundColor);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawOval(drawX, drawY, diameter, diameter);

        // 2. Desenha o arco de progresso
        float progress = progressSupplier.get();
        if (progress > 0) {
            g2d.setColor(foregroundColor);
            // O ângulo é 360 * progresso. Começa no topo (90 graus) e desenha no sentido anti-horário.
            g2d.draw(new Arc2D.Float(drawX, drawY, diameter, diameter, 90, -360 * progress, Arc2D.OPEN));
        }
        
        g2d.dispose();
    }
}