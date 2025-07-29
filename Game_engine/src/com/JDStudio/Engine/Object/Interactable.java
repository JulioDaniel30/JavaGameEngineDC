package com.JDStudio.Engine.Object;

import java.awt.Color;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;

/**
 * Uma interface para GameObjects com os quais o jogador pode interagir.
 */
public interface Interactable {
    
    /**
     * Chamado quando uma entidade interage com este objeto.
     * @param source A entidade que iniciou a interação.
     */
    void onInteract(GameObject source);

    /**
     * Retorna o raio (em pixels) a partir do centro do objeto
     * dentro do qual a interação é possível.
     * @return O raio de interação.
     */
    int getInteractionRadius();

    /**
     * Desenha a representação visual da área de interação no modo de debug.
     * Este é um método 'default', então as classes não são obrigadas a implementá-lo,
     * mas podem se quiserem um visual de debug customizado.
     */
    default void renderDebugInteractionArea(Graphics g) {
        if (Engine.isDebug && this instanceof GameObject) {
            GameObject owner = (GameObject) this;
            int radius = getInteractionRadius();
            if (radius <= 0) return;

            // Calcula o centro do objeto
            int centerX = owner.getX() + owner.getWidth() / 2;
            int centerY = owner.getY() + owner.getHeight() / 2;

            // Desenha um círculo amarelo para a área de interação
            g.setColor(Color.YELLOW);
            g.drawOval(
                centerX - radius - Engine.camera.getX(), 
                centerY - radius - Engine.camera.getY(), 
                radius * 2, 
                radius * 2
            );
        }
    }
}