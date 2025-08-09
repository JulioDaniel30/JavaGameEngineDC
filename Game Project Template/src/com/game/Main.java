package com.game;

import java.awt.event.KeyEvent;
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Input.InputManager;

public class Main {

	public static void setupInputBindings() {
		InputManager manager = InputManager.instance;
		// Movimento
		manager.bindKey("MOVE_UP", KeyEvent.VK_W);
		manager.bindKey("MOVE_UP", KeyEvent.VK_UP);
		manager.bindKey("MOVE_DOWN", KeyEvent.VK_S);
		manager.bindKey("MOVE_DOWN", KeyEvent.VK_DOWN);
		manager.bindKey("MOVE_LEFT", KeyEvent.VK_A);
		manager.bindKey("MOVE_LEFT", KeyEvent.VK_LEFT);
		manager.bindKey("MOVE_RIGHT", KeyEvent.VK_D);
		manager.bindKey("MOVE_RIGHT", KeyEvent.VK_RIGHT);
		// Ações
		manager.bindKey("INTERACT", KeyEvent.VK_E);
		// manager.loadAndMergeBindings("keybinding.json");
	}

	public static void main(String[] args) {
		// 1. Configura os controlos
		setupInputBindings();

		// 2. Cria a engine com as configurações desejadas
		Engine engine = new Engine(240, 160, 3, false, "Meu Novo Jogo", 60.0);

		// 3. Define a classe do estado inicial (o Menu)
		Engine.setInitialGameState(MenuState.class);

		// 4. Inicia a engine
		engine.start();
	}
}