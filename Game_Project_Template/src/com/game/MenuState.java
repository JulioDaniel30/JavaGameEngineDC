package com.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.UITheme;
import com.JDStudio.Engine.Graphics.UI.Elements.UIButton;
import com.JDStudio.Engine.Graphics.UI.Elements.UIText;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.States.EngineMenuState;

public class MenuState extends EngineMenuState {

	@Override
	protected void buildUI() {
		// Define o tema da UI que a engine deve usar
		ThemeManager.getInstance().setTheme(UITheme.MEDIEVAL);

		// Título
		uiManager.addElement(
				new UIText(Engine.WIDTH / 2 - 50, 40, new Font("Serif", Font.BOLD, 24), Color.WHITE, "Meu Jogo"));

		// Botão de Iniciar Jogo
		UIButton startButton = new UIButton(Engine.WIDTH / 2 - 40, 80,
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL),
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_HOVER),
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_PRESSED), "Iniciar", new Font("Arial", Font.BOLD, 12),
				() -> Engine.transitionToState(new PlayingState()) // Inicia a transição para o jogo
		);
		uiManager.addElement(startButton);

		// Botão de Sair
		UIButton quitButton = new UIButton(Engine.WIDTH / 2 - 40, 110,
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL),
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_HOVER),
				ThemeManager.getInstance().get(UISpriteKey.BUTTON_PRESSED), "Sair", new Font("Arial", Font.BOLD, 12),
				() -> System.exit(0) // Fecha a aplicação
		);
		uiManager.addElement(quitButton);
	}

	@Override
	public void render(Graphics g) {
		// Desenha um fundo simples para o menu
		g.setColor(new Color(20, 20, 80));
		g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);

		super.render(g); // Desenha os elementos da UI
	}
}