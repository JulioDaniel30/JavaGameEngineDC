package com.jdstudio.engine.Graphics.Layers;

/**
 * Define as instâncias das camadas de renderização padrão fornecidas pela engine.
 */
public final class StandardLayers {
    private StandardLayers() {} // Garante que a classe não seja instanciada
    /**Fundo mais distante (0)*/
    public static final RenderLayer PARALLAX_BACKGROUND = new RenderLayer("PARALLAX_BACKGROUND", 0);
    /**Tiles de fundo (10)*/
    public static final RenderLayer WORLD_BACKGROUND = new RenderLayer("WORLD_BACKGROUND", 10);
    /**Objetos do jogo que ficam atrás do jogador (20)*/
    public static final RenderLayer GAMEPLAY_BELOW = new RenderLayer("GAMEPLAY_BELOW", 20);
    /**O jogador e os inimigos (30)*/
    public static final RenderLayer CHARACTERS = new RenderLayer("CHARACTERS", 30);
    /**Objetos do jogo que ficam à frente do jogador (40)*/
    public static final RenderLayer GAMEPLAY_ABOVE = new RenderLayer("GAMEPLAY_ABOVE", 40);
    /**Projéteis, para garantir que apareçam sobre os personagens (50)*/
    public static final RenderLayer PROJECTILES = new RenderLayer("PROJECTILES", 50);
    /**Partículas, como explosões, que devem aparecer sobre tudo (60)*/
    public static final RenderLayer PARTICLES = new RenderLayer("PARTICLES", 60);
    /**Tiles de primeiro plano (ex: copas de árvores) (70)*/
    public static final RenderLayer WORLD_FOREGROUND = new RenderLayer("WORLD_FOREGROUND", 70);
    /**A máscara de iluminação (80)*/
    public static final RenderLayer LIGHTING = new RenderLayer("LIGHTING", 80);
    /**OS POPUPS*/
    public static final RenderLayer POPUPS = new RenderLayer("POPUPS", 90);
    /**A UI (100)*/
    public static final RenderLayer UI = new RenderLayer("UI", 100);
}