package com.jdstudio.engine.Graphics.WSUI.InteractionElements;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * Um elemento de UI no mundo do jogo que exibe um prompt de interação.
 * Herda de UIWorldAttached para seguir automaticamente o seu alvo.
 */
public class UIInteractionPrompt extends UIWorldAttached {

    private String text = "";
    
    // Estilos
    private Font font = new Font("Arial", Font.BOLD, 10);
    private Color textColor = Color.WHITE;
    private Color boxColor = new Color(0, 0, 0, 180);

    public UIInteractionPrompt() {
        // Inicia sem um alvo e com um offset padrão acima da cabeça
        super(null, -10); 
        this.visible = false; // Começa invisível
    }
    
    /**
     * Define o GameObject que o prompt deve seguir e o texto a ser exibido.
     * Se o alvo for nulo, o prompt fica invisível.
     * @param target O novo GameObject alvo.
     * @param text O novo texto do prompt.
     */
    public void setTarget(GameObject target, String text) {
        this.target = target; // Define o alvo na classe pai (UIWorldAttached)
        this.text = text;
        this.visible = (target != null);
    }

    // O método tick() é herdado de UIWorldAttached e já faz o trabalho de seguir o alvo.
    // Não precisamos de o reescrever!

    @Override
    public void render(Graphics g) {
        if (!visible || target == null) return;

        g.setFont(font);
        
        int padding = 4;
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getAscent();
        
        // As coordenadas 'this.x' e 'this.y' já são atualizadas pelo tick() da classe pai
        int drawX = (this.x - textWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        g.setColor(boxColor);
        g.fillRoundRect(drawX - padding, drawY - textHeight, textWidth + (padding * 2), textHeight + (padding * 2), 8, 8);

        g.setColor(textColor);
        g.drawString(text, drawX, drawY);
    }
}