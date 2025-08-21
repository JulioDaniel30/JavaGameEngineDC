// engine
package com.jdstudio.engine.States;

import java.awt.Graphics;

import com.jdstudio.engine.Graphics.UI.Managers.UIManager;

public abstract class EngineMenuState extends GameState {

    protected UIManager uiManager;

    public EngineMenuState() {
        this.uiManager = new UIManager();
        buildUI(); // Constrói a UI na primeira vez
    }
    
    /**
     * As subclasses devem implementar este método para adicionar todos os seus
     * elementos de UI ao uiManager.
     */
    protected abstract void buildUI();

    /**
     * Limpa a UI antiga e a reconstrói.
     * Chamado pela Engine sempre que este estado volta a ser o de topo na pilha.
     */
    @Override
    public void onEnter() {
        super.onEnter();
        // Limpa qualquer UI que possa ter sobrado e reconstrói
        uiManager.unregisterAllElements(); 
        buildUI();
    }

    @Override
    public void onExit() {
        if (uiManager != null) {
            uiManager.unregisterAllElements();
        }
    }

    @Override
    public void tick() {
        if (uiManager != null) {
            uiManager.tick();
        }
    }

    @Override
    public void render(Graphics g) {
        if (uiManager != null) {
            uiManager.render(g);
        }
    }
}