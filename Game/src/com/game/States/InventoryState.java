package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.InventoryComponent;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.Elements.UIText;
import com.JDStudio.Engine.Graphics.UI.Inventory.UIInventoryView;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.States.EngineMenuState;

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
     g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);
     
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