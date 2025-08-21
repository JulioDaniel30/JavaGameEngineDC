// engine
package com.jdstudio.engine.Graphics.Layers;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {
    
    private static final RenderManager instance = new RenderManager();
    
    // Agora temos uma lista de camadas e um mapa para a fila de renderização
    private final List<RenderLayer> layers = new ArrayList<>();
    private final Map<RenderLayer, List<IRenderable>> renderQueue = new HashMap<>();

    private RenderManager() {
        // Regista as camadas padrão da engine
        registerLayer(StandardLayers.PARALLAX_BACKGROUND);
        registerLayer(StandardLayers.WORLD_BACKGROUND);
        registerLayer(StandardLayers.GAMEPLAY_BELOW);
        registerLayer(StandardLayers.CHARACTERS);
        registerLayer(StandardLayers.GAMEPLAY_ABOVE);
        registerLayer(StandardLayers.PROJECTILES);
        registerLayer(StandardLayers.PARTICLES);
        registerLayer(StandardLayers.WORLD_FOREGROUND);
        registerLayer(StandardLayers.LIGHTING);
        registerLayer(StandardLayers.POPUPS); 
        registerLayer(StandardLayers.UI);
    }

    public static RenderManager getInstance() { return instance; }

    /**
     * O jogo usa este método para adicionar as suas próprias camadas de renderização.
     */
    public void registerLayer(RenderLayer layer) {
        if (!layers.contains(layer)) {
            layers.add(layer);
            renderQueue.put(layer, new ArrayList<>());
            // Reordena a lista de camadas por profundidade sempre que uma nova é adicionada
            Collections.sort(layers);
        }
    }
    
    /**
     * Procura e retorna uma camada de renderização pelo seu nome.
     * @param name O nome da camada (ex: "CHARACTERS").
     * @return O objeto RenderLayer correspondente, ou null se não for encontrado.
     */
    public RenderLayer getLayerByName(String name) {
        for (RenderLayer layer : layers) {
            if (layer.getName().equalsIgnoreCase(name)) {
                return layer;
            }
        }
        return null; // Retorna nulo se nenhuma camada com esse nome foi registada.
    }

    public void register(IRenderable renderable) {
        if (renderable != null) {
            renderQueue.get(renderable.getRenderLayer()).add(renderable);
        }
    }

    public void unregister(IRenderable renderable) {
        if (renderable != null) {
            renderQueue.get(renderable.getRenderLayer()).remove(renderable);
        }
    }

    public void render(Graphics g) {
        // Itera sobre as camadas já ordenadas por profundidade
        for (RenderLayer layer : layers) {
            List<IRenderable> renderables = renderQueue.get(layer);
            renderables.sort(Comparator.comparingInt(IRenderable::getZOrder));
            for (IRenderable renderable : renderables) {
                if (renderable.isVisible()) {
                    renderable.render(g);
                }
            }
        }
    }

    public void clear() {
        for (List<IRenderable> list : renderQueue.values()) {
            list.clear();
        }
    }
}