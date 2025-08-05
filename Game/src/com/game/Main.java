package com.game;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Input.InputManager;
import com.game.States.MenuState;

public class Main {
	public static void main(String[] args) {
		
		InputManager manager = InputManager.instance;

        manager.loadAndMergeBindings("/keybindings.json");
		
		Engine engine = new Engine(60.0);
		engine.requestFocusInWindow();
		//Engine.setGameState(new PlayingState()); // Define a cena inicial
		//Engine.setGameState(new MenuState());
		Engine.setInitialGameState(MenuState.class);
		engine.start();
	}
}