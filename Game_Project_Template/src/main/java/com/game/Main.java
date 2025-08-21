package com.game;

import java.awt.event.KeyEvent;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.ResolutionProfile;
import com.jdstudio.engine.Graphics.StandardResolutions;
import com.jdstudio.engine.Input.InputManager;

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

		// --- MÉTODO 1: Usar uma Predefinição ---
        // Escolha um perfil pré-pronto do enum. É limpo e legível.
        ResolutionProfile profile = StandardResolutions.GBA_STYLE.getProfile();
        
        // --- MÉTODO 2: Criar uma Resolução Customizada ---
        // Se nenhuma predefinição servir, crie a sua própria na hora.
        // ResolutionProfile profile = new ResolutionProfile(400, 240, 3);
		// 2. Cria a engine com as configurações desejadas
		Engine engine = new Engine(
				profile.width(), //width
				profile.height(), //height
				profile.recommendedScale(),//Scale 
				false, //Redimencionavel
				"Meu Novo Jogo", //Titulo
				60.0//FPS
				);

		// 3. Define a classe do estado inicial (o Menu)
		Engine.setInitialGameState(MenuState.class);

		// 4. Inicia a engine
		engine.start();
	}
}