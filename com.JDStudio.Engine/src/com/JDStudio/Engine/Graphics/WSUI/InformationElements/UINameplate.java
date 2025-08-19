package com.JDStudio.Engine.Graphics.WSUI.InformationElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.WSUI.UIWorldAttached;
import com.JDStudio.Engine.Object.GameObject;

public class UINameplate extends UIWorldAttached {

 private final Font nameFont;
 private final Color nameColor;

 public UINameplate(GameObject target, int yOffset, Font font, Color color) {
     super(target, yOffset);
     this.nameFont = font;
     this.nameColor = color;

     // A placa de identificação só é visível se o alvo tiver um nome
     if (target.name == null || target.name.isEmpty()) {
         this.visible = false;
     }
 }

 @Override
 public void render(Graphics g) {
     if (!visible) return;
     
     // Usa o nome do objeto alvo como texto
     String text = target.name;
     
     g.setFont(nameFont);
     g.setColor(nameColor);
     
     // Centraliza o texto
     int textWidth = g.getFontMetrics().stringWidth(text);
     int drawX = (this.x - textWidth / 2) - Engine.camera.getX();
     int drawY = this.y - Engine.camera.getY();

     g.drawString(text, drawX, drawY);
 }
}