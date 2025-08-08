package com.game;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Layers.RenderManager;
import com.JDStudio.Engine.Input.InputManager;
import com.game.States.MenuState;

public class Main {
	public static void main(String[] args) {
		
		InputManager manager = InputManager.instance;

        manager.loadAndMergeBindings("/keybindings.json");
        RenderManager.getInstance().registerLayer(GameLayers.WATER_EFFECTS);
		
		Engine engine = new Engine(60.0);
		engine.requestFocusInWindow();
		//Engine.setGameState(new PlayingState()); // Define a cena inicial
		//Engine.setGameState(new MenuState());// Define a cena inicial
		Engine.setInitialGameState(MenuState.class); // Define a cena inicial
		engine.start();
	}
}