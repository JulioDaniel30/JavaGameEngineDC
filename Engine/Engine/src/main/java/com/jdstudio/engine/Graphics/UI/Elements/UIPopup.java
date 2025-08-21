package com.jdstudio.engine.Graphics.UI.Elements;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

public class UIPopup extends UIElement {

 public boolean isActive = false;
 private String text;
 private Font font;
 private Color color;
 private int lifeTime; // Duração em frames
 private GameObject target; // O GameObject a seguir
 private double initialY; // Posição Y inicial para o efeito de subida

 public UIPopup() {
     super(0, 0);
     this.visible = false;
 }

 /**
  * Inicializa ou "acorda" um popup da piscina.
  */
 public void init(GameObject target, String text, Font font, Color color, int lifeTime) {
     this.target = target;
     this.text = text;
     this.font = font;
     this.color = color;
     this.lifeTime = lifeTime;
     
     // Posição inicial acima do alvo
     this.x = target.getX() + target.getWidth() / 2;
     this.initialY = target.getY();
     this.y = (int)this.initialY;
     
     this.isActive = true;
     this.visible = true;
 }

 @Override
 public void tick() {
     if (!isActive) return;

     lifeTime--;
     if (lifeTime <= 0) {
         isActive = false;
         visible = false;
         return;
     }
     
     // Efeito de subir lentamente
     this.y--;
     
     // Segue o alvo no eixo X
     if (target != null) {
         this.x = target.getX() + target.getWidth() / 2;
     }
 }

 @Override
 public void render(Graphics g) {
     if (!visible) return;

     g.setFont(font);
     g.setColor(color);
     
     // Centraliza o texto
     int textWidth = g.getFontMetrics().stringWidth(text);
     int drawX = (x - textWidth / 2) - Engine.camera.getX();
     int drawY = y - Engine.camera.getY();

     g.drawString(text, drawX, drawY);
 }
}