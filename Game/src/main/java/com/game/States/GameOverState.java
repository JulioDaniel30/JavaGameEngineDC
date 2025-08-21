package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Graphics.UI.Elements.UIButton;
import com.jdstudio.engine.Graphics.UI.Elements.UIText;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.States.EngineMenuState;

public class GameOverState extends EngineMenuState {

	public GameOverState() {
		super();

		

	}
	
	@Override
	protected void buildUI() {
		// TÍTULO "FIM DE JOGO"
				uiManager.addElement(
						new UIText(Engine.getWIDTH() / 2 - 70, 40, new Font("Serif", Font.BOLD, 28), Color.RED, "Fim de Jogo"));

				// BOTÃO PARA VOLTAR AO MENU
				Runnable backToMenuAction = () -> {
					Engine.setGameState(new MenuState());
				};
				int button_width = (int) ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL).getWidth();
				int button_centerX_Position = Engine.getWIDTH() / 2 - button_width / 2;

				UIButton menuButton = new UIButton(button_centerX_Position, 60, "Menu", new Font("Arial", Font.BOLD, 12),
						backToMenuAction);
				uiManager.addElement(menuButton);

				// --- BOTÃO DE TENTAR NOVAMENTE ---
				Runnable retryAction = () -> {
					// A mágica acontece aqui:
					Engine.restartPreviousState();
				};

				UIButton retryButton = new UIButton(button_centerX_Position, 90, "Tentar Novamente",
						new Font("Arial", Font.BOLD, 12), retryAction);
				uiManager.addElement(retryButton);
	}

	@Override
	public void render(Graphics g) {
		// Desenha um fundo preto
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Engine.getWIDTH(), Engine.getHEIGHT());

		// Desenha a UI por cima
		super.render(g);
	}
}