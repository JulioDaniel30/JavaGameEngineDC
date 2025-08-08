package com.JDStudio.Engine.Graphics.UI.Managers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Graphics.UI.Elements.UIPopup;
import com.JDStudio.Engine.Object.GameObject;

public class PopupManager {
 
 private static final PopupManager instance = new PopupManager();
 private final List<UIPopup> popupPool = new ArrayList<>();

 private PopupManager() {}

 public static PopupManager getInstance() {
     return instance;
 }

 /**
  * Cria um novo popup de texto sobre um alvo.
  */
 public void createPopup(GameObject target, String text, Font font, Color color, int lifeTime) {
     // Procura por um popup inativo na piscina para reutilizar
     for (UIPopup popup : popupPool) {
         if (!popup.isActive) {
             popup.init(target, text, font, color, lifeTime);
             return;
         }
     }
     
     // Se não houver nenhum inativo, cria um novo
     UIPopup newPopup = new UIPopup();
     newPopup.init(target, text, font, color, lifeTime);
     popupPool.add(newPopup);
 }
 
 public void update() {
     for (UIPopup popup : popupPool) {
         if (popup.isActive) {
             popup.tick();
         }
     }
 }

 public void render(Graphics g) {
     for (UIPopup popup : popupPool) {
         if (popup.isActive) {
             popup.render(g);
         }
     }
 }
}