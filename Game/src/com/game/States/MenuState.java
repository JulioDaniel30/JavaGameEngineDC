package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.UI.ThemeManager;
import com.JDStudio.Engine.Graphics.UI.UIButton;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.UIText;
import com.JDStudio.Engine.States.EngineMenuState;
import com.JDStudio.Engine.Graphics.UI.UITheme;
import com.JDStudio.Engine.Input.InputManager;

/**
 * O estado de menu principal do jogo.
 */
public class MenuState extends EngineMenuState {

	public MenuState() {
		super(); // Chama o construtor de EngineMenuState, que cria o uiManager

		// Define o tema que o menu usará (pode ser diferente do jogo se você quiser)
		ThemeManager.getInstance().setTheme(UITheme.MEDIEVAL);

		// --- TÍTULO DO JOGO ---
		uiManager.addElement(new UIText(Engine.WIDTH / 2 - 50, 40, // Posição (x, y)
				new Font("Serif", Font.BOLD, 24), Color.WHITE, "Meu Jogo"));

		// --- BOTÃO DE INICIAR JOGO ---
		Runnable startGameAction = () -> {
			System.out.println("Iniciando o jogo...");
			Engine.transitionToState(new PlayingState(), 10, Color.BLACK);
			// Engine.setGameState(new PlayingState());
		};

		int button_width = (int) ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL).getWidth();
		int button_centerX_Position = Engine.WIDTH / 2 - button_width / 2;

		UIButton startButton = new UIButton(button_centerX_Position, 60, // Posição centralizada
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL),
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_HOVER),
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_PRESSED), "Iniciar", new Font("Arial", Font.BOLD, 12),
				startGameAction);
		uiManager.addElement(startButton);

		Runnable loadActionRunnable = () -> {

			PlayingState pState = new PlayingState();
			pState.loadGame();
			Engine.transitionToState(pState);
		};

		UIButton loadButton = new UIButton(button_centerX_Position, 90, "Load",
				new Font("Arial", Font.BOLD, 12), loadActionRunnable);

		uiManager.addElement(loadButton);

		// --- BOTÃO DE SAIR ---
		Runnable quitAction = () -> System.exit(0);

		UIButton quitButton = new UIButton(button_centerX_Position, 120, "Sair",
				new Font("Arial", Font.BOLD, 12), quitAction);
		uiManager.addElement(quitButton);

	}

	@Override
	public void tick() {
		super.tick();
		if (InputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	public void render(Graphics g) {
		// Desenha um fundo simples para o menu
		g.setColor(new Color(20, 20, 80)); // Azul escuro
		g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);

		// Chama o render da classe pai para desenhar todos os elementos da UI (botões,
		// texto)
		super.render(g);
	}
}