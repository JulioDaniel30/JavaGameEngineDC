package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import com.jdstudio.engine.Engine;

/**
 * Um UIElement simples para exibir o texto de um tutorial.
 * É controlado pelo TutorialManager.
 */
public class TutorialBox extends UIElement {

    private String currentText = "";
    private Font font = new Font("Arial", Font.BOLD, 12);
    private Color textColor = Color.WHITE;
    private Color boxColor = new Color(0, 0, 0, 180);

    public TutorialBox() {
        super(0, 0); // A posição será definida pelo TutorialManager
        this.visible = false; // Começa invisível
    }

    public void show(String text, String position) {
        this.currentText = text;
        
        // Lógica de posicionamento (pode ser expandida)
        this.width = text.length() * 7 + 20; // Estimativa de largura
        this.setHeight(30);

        if ("BOTTOM_CENTER".equals(position)) {
            this.x = Engine.getWIDTH() / 2 - this.width / 2;
            this.y = Engine.getHEIGHT() - this.getHeight() - 20;
        } else { // Padrão
            this.x = Engine.getWIDTH() / 2 - this.width / 2;
            this.y = 20;
        }
        
        this.visible = true;
    }

    public void hide() {
        this.visible = false;
        this.currentText = "";
    }
    
    @Override
    public void render(Graphics g) {
        if (!visible) return;
        
        g.setColor(boxColor);
        g.fillRect(x, y, width, getHeight());
        
        g.setColor(textColor);
        g.setFont(font);
        g.drawString(currentText, x + 10, y + 20);
    }
}