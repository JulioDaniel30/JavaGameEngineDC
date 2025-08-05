package com.JDStudio.Engine.States;


import java.awt.Graphics;

import com.JDStudio.Engine.Graphics.UI.UIManager;

/**
* Uma especialização de GameState projetada para menus e telas de UI.
* Gerencia automaticamente um UIManager.
*/
public abstract class EngineMenuState extends GameState {

 protected UIManager uiManager;

 public EngineMenuState() {
     this.uiManager = new UIManager();
 }

 @Override
 public void tick() {
     // Menus geralmente não têm lógica de 'tick' complexa,
     // mas as subclasses podem adicionar se necessário.
	 uiManager.tick();
 }

 @Override
 public void render(Graphics g) {
     // Renderiza todos os elementos de UI
     uiManager.render(g);
 }
}