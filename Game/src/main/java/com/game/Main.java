package com.game;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Input.InputManager;
import com.game.States.MenuState;

public class Main {
	public static void main(String[] args) {
		
		InputManager manager = InputManager.instance;

        manager.loadAndMergeBindings("/keybindings.json");
        RenderManager.getInstance().registerLayer(GameLayers.WATER_EFFECTS);
		
        int gameWidth = 240;
        int gameHeight = 160;
        int gameScale = 3;
        boolean isResizable = false; 
        String gameTitle = "A Aventura Mágica";
        
		Engine engine = new Engine(gameWidth,gameHeight,gameScale,isResizable,gameTitle,60.0);
		engine.requestFocusInWindow();
		//Engine.setGameState(new PlayingState()); // Define a cena inicial
		//Engine.setGameState(new MenuState());// Define a cena inicial
		Engine.setInitialGameState(MenuState.class); // Define a cena inicial
		engine.start();
	}
}