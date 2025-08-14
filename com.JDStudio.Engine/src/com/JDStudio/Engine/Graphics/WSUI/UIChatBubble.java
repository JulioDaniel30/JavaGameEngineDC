package com.JDStudio.Engine.Graphics.WSUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Object.GameObject;

public class UIChatBubble extends UIWorldAttached {

 private String text;
 private Font font;
 private Color textColor;
 private Color bubbleColor;
 private int lifeTime; // Duração em frames

 public UIChatBubble(GameObject target, String text, int lifeTime) {
     super(target, -10); // Offset padrão acima da cabeça
     this.text = text;
     this.lifeTime = lifeTime;

     // Estilos padrão
     this.font = new Font("Arial", Font.BOLD, 10);
     this.textColor = Color.BLACK;
     this.bubbleColor = new Color(255, 255, 255, 200); // Branco semitransparente
 }
 
 @Override
 public void tick() {
     super.tick(); // Atualiza a posição para seguir o alvo
     if (!visible) return;

     lifeTime--;
     if (lifeTime <= 0) {
         this.visible = false; // O balão "morre" ao tornar-se invisível
     }
 }

 @Override
 public void render(Graphics g) {
     if (!visible) return;

     g.setFont(font);
     
     int padding = 4;
     int textWidth = g.getFontMetrics().stringWidth(text);
     int textHeight = g.getFontMetrics().getAscent();
     
     // Calcula a posição de renderização no ecrã
     int drawX = (this.x - (textWidth / 2)) - Engine.camera.getX();
     int drawY = this.y - Engine.camera.getY();

     // Desenha o fundo do balão
     g.setColor(bubbleColor);
     g.fillRoundRect(drawX - padding, drawY - textHeight, textWidth + (padding * 2), textHeight + (padding * 2), 10, 10);

     // Desenha o texto
     g.setColor(textColor);
     g.drawString(text, drawX, drawY);
 }
}