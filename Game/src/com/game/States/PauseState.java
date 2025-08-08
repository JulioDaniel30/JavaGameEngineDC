package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.Elements.UIButton;
import com.JDStudio.Engine.Graphics.UI.Elements.UIText;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.States.EngineMenuState;

public class PauseState extends EngineMenuState {

    public PauseState() {
        super();

        
    }
    @Override
    protected void buildUI() {
    	uiManager.addElement(new UIText(Engine.WIDTH / 2 - 40, 40, new Font("Serif", Font.BOLD, 20), Color.WHITE, "Pausado"));

        int button_width = (int) ThemeManager.getInstance().get(UISpriteKey.BUTTON_NORMAL).getWidth();
		int button_centerX_Position = Engine.WIDTH / 2 - button_width / 2;
        
        // BOTÃO DE CONTINUAR
        UIButton continueButton = new UIButton(
            button_centerX_Position, 60,
            "Continuar", new Font("Arial", Font.BOLD, 12),
            () -> Engine.popState() // Ação: remove o estado de pausa da pilha
        );
        uiManager.addElement(continueButton);
        
     // --- BOTÃO DE OPÇÕES ---
        UIButton optionsButton = new UIButton(
            button_centerX_Position, 90,
            "Opções",
            new Font("Arial", Font.BOLD, 12),
            () -> Engine.pushState(new OptionsState()) // Abre a nova tela de opções
        );
        uiManager.addElement(optionsButton);

        
        // BOTÃO DE SAIR PARA O MENU
        UIButton quitButton = new UIButton(
        		button_centerX_Position, 120,
            "Sair para o Menu", new Font("Arial", Font.BOLD, 12),
            () -> Engine.setGameState(new MenuState()) // Substitui a pilha pelo MenuState
        );
        uiManager.addElement(quitButton);
    	
    }

    @Override
    public void tick() {
        super.tick();
        // Também podemos despausar com a mesma tecla
        if (InputManager.isActionJustPressed("PAUSE_GAME")) {
            Engine.popState();
        }
    }

    @Override
    public void render(Graphics g) {
        // Desenha uma sobreposição escura e semitransparente
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);
        
        // Desenha os botões e texto por cima
        super.render(g);
    }
}