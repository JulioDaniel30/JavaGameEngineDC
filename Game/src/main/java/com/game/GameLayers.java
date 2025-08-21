// game
package com.game;

import com.jdstudio.engine.Graphics.Layers.RenderLayer;

/**
 * Define as instâncias das camadas de renderização customizadas para este jogo.
 */
public final class GameLayers {
    private GameLayers() {}
    
    // Uma nova camada para efeitos de água, que fica entre os personagens e o primeiro plano.
    public static final RenderLayer WATER_EFFECTS = new RenderLayer("WATER_EFFECTS", 55);
}