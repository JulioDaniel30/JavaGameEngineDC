package com.jdstudio.engine.Components;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import com.jdstudio.engine.Object.GameObject;

/**
 * Representa uma única zona de interação com um formato, tipo e dono definidos.
 */
public class InteractionZone {

    private GameObject owner;
    private Shape shape;
    public final String type; // ex: "AGGRO", "ATTACK", "DIALOGUE"
    
    public static final String TYPE_AGGRO = "AGGRO";
    public static final String TYPE_ATTACK = "ATTACK";
    public static final String TYPE_DIALOGUE = "DIALOGUE";
    public static final String TYPE_TRIGGER = "TRIGGER";

    // --- Construtor para Círculo ---
    public InteractionZone(GameObject owner, String type, double radius) {
        this.owner = owner;
        this.type = type;
        // Cria uma elipse (círculo) centrada no dono. O tamanho será atualizado.
        this.shape = new Ellipse2D.Double(0, 0, radius * 2, radius * 2);
        updatePosition();
    }

    // --- Construtor para Retângulo ---
    public InteractionZone(GameObject owner, String type, double width, double height, double offsetX, double offsetY) {
        this.owner = owner;
        this.type = type;
        // Cria um retângulo com um deslocamento em relação ao dono.
        this.shape = new Rectangle2D.Double(0, 0, width, height);
        updatePosition(offsetX, offsetY);
    }

    /**
     * Atualiza a posição da zona para seguir o seu dono.
     */
    public void updatePosition() {
        if (owner == null) return;

        if (shape instanceof Ellipse2D) {
            Ellipse2D circle = (Ellipse2D) shape;
            double centerX = owner.getX() + owner.getWidth() / 2.0;
            double centerY = owner.getY() + owner.getHeight() / 2.0;
            circle.setFrame(centerX - (circle.getWidth() / 2), centerY - (circle.getHeight() / 2), circle.getWidth(), circle.getHeight());
        }
    }
    
    // Sobrecarga para retângulos com offset
    public void updatePosition(double offsetX, double offsetY) {
        if (owner == null) return;
        if (shape instanceof Rectangle2D) {
            Rectangle2D rect = (Rectangle2D) shape;
            rect.setRect(owner.getX() + offsetX, owner.getY() + offsetY, rect.getWidth(), rect.getHeight());
        } else {
            updatePosition(); // Usa a lógica padrão para outras formas
        }
    }

    /**
     * Verifica se um ponto (ex: o centro de outro GameObject) está dentro desta zona.
     */
    public boolean contains(double x, double y) {
        return shape.contains(x, y);
    }
    
    /**
     * Retorna o objeto Shape para renderização de debug.
     */
    public Shape getShape() {
        return this.shape;
    }
}