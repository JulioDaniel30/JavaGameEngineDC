package com.JDStudio.Engine.States;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Uma especialização de GameState projetada para fases de jogo ativas.
 * Gerencia automaticamente uma lista de GameObjects e a ordem de atualização/renderização.
 */
@SuppressWarnings("static-access")
public abstract class EnginePlayingState extends GameState {

    protected static List<GameObject> gameObjects;
    public static AssetManager assets;
    // Poderíamos adicionar um 'world' e 'player' aqui no futuro para automatizar ainda mais

    public EnginePlayingState() {
        this.gameObjects = new ArrayList<>();
    }

    public static List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public final void setGameObjects(List<GameObject> gameObjects) {
		this.gameObjects = gameObjects;
	}

	public void addGameObject(GameObject go) {
        this.gameObjects.add(go);
    }

    public void removeGameObject(GameObject go) {
        this.gameObjects.remove(go);
    }
    
    @Override
    public void tick() {
        // Atualiza todos os objetos
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).tick();
        }

       /* // Remove objetos marcados para destruição
        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            if (gameObjects.get(i).isDestroyed) {
                gameObjects.remove(i);
            }
        }*/
        gameObjects.removeIf(go -> go.isDestroyed && !go.isProtectedFromCleanup); // Remove tudo o que está destruído, exceto o jogador
        
    }

    @Override
    public void render(Graphics g) {
        // Renderiza todos os objetos
        for (GameObject go : gameObjects) {
            go.render(g);
        }
    }
}