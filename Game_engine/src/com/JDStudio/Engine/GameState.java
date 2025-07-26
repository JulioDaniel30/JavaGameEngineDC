package com.JDStudio.Engine;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Representa a base abstrata para todos os estados do jogo (ex: Menu, Nível, Game Over).
 * <p>
 * Este é um componente central do padrão de projeto <b>State</b>, que permite que o jogo
 * altere seu comportamento e sua renderização de forma encapsulada. Cada estado
 * gerencia sua própria lista de {@link GameObject} e define sua própria lógica
 * nos métodos {@link #tick()} e {@link #render(Graphics)}.
 *
 * @author JDStudio
 * @since 1.0
 */
public abstract class GameState {

    /** A lista de todos os GameObjects gerenciados por este estado. */
    protected List<GameObject> gameObjects;

    /**
     * Construtor padrão que inicializa a lista interna de objetos do estado.
     */
    public GameState() {
        this.gameObjects = new ArrayList<>();
    }

    /**
     * Adiciona um objeto à lista de gerenciamento do estado.
     *
     * @param go O {@link GameObject} a ser adicionado.
     */
    public void addGameObject(GameObject go) {
        this.gameObjects.add(go);
    }

    /**
     * Remove um objeto da lista de gerenciamento do estado.
     *
     * @param go O {@link GameObject} a ser removido.
     */
    public void removeGameObject(GameObject go) {
        this.gameObjects.remove(go);
    }

    /**
     * Método abstrato que contém a lógica de atualização do estado.
     * <p>
     * Deve ser implementado por subclasses para atualizar todos os seus
     * {@code GameObjects} e executar qualquer outra lógica específica do estado
     * a cada quadro (frame).
     */
    public abstract void tick();

    /**
     * Método abstrato que contém a lógica de renderização do estado.
     * <p>
     * Deve ser implementado por subclasses para renderizar todos os seus
     * {@code GameObjects} e qualquer outro elemento visual (UI, fundo, etc.)
     * na tela a cada quadro (frame).
     *
     * @param g O contexto {@link Graphics} onde o estado será desenhado.
     */
    public abstract void render(Graphics g);
}