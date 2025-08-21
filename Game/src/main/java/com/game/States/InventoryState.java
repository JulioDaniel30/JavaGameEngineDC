package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.InventoryComponent;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Graphics.UI.Elements.UIText;
import com.jdstudio.engine.Graphics.UI.Inventory.UIInventoryView;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.States.EngineMenuState;

public class InventoryState extends EngineMenuState {

 public InventoryState() {
     super();

     
 }
 
 @Override
 public void tick() {
     super.tick();
     // Permite fechar o inventário com a mesma tecla (ou ESC)
     if (InputManager.isActionJustPressed("TOGGLE_INVENTORY") || InputManager.isKeyJustPressed(java.awt.event.KeyEvent.VK_ESCAPE)) {
         Engine.popState();
     }
 }

 @Override
 public void render(Graphics g) {
     // Desenha um fundo semitransparente
     g.setColor(new Color(0, 0, 0, 200));
     g.fillRect(0, 0, Engine.getWIDTH(), Engine.getHEIGHT());
     
     super.render(g);
 }

 @Override
 protected void buildUI() {
	 uiManager.addElement(new UIText(10, 20, new Font("Serif", Font.BOLD, 14), Color.WHITE, "Inventário"));

     // Pega o componente de inventário do jogador
     InventoryComponent playerInventoryComp = PlayingState.player.getComponent(InventoryComponent.class);
     if (playerInventoryComp != null) {
         
    	 Sprite sprSlot = ThemeManager.getInstance().get(UISpriteKey.INVENTORY_BUTTON_NORMAL_30_2);
    	 
         // Cria a visualização da UI, passando o inventário do jogador
         UIInventoryView inventoryView = new UIInventoryView(
             20, 40, // Posição da janela
             null, // Fundo da janela (pode adicionar um sprite depois)
             sprSlot, // Sprite para o fundo de cada slot
             PlayingState.player, playerInventoryComp.inventory,
             3,
             6,    // 6 colunas
             sprSlot.getWidth(),   // Tamanho de cada slot (20x20 pixels)
             2     // Espaçamento entre os slots
         );
         uiManager.addElement(inventoryView);
     }
	
 }
}