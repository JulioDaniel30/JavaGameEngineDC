package com.jdstudio.engine.Graphics.Layers;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A singleton class responsible for managing and orchestrating the rendering of all {@link IRenderable} objects
 * across different {@link RenderLayer}s. It ensures objects are drawn in the correct order based on their layer depth
 * and Z-order within each layer.
 * 
 * @author JDStudio
 */
public class RenderManager {
    
    private static final RenderManager instance = new RenderManager();
    
    /** A sorted list of registered render layers. */
    private final List<RenderLayer> layers = new ArrayList<>();
    
    /** A map where keys are RenderLayers and values are lists of IRenderable objects to be rendered on that layer. */
    private final Map<RenderLayer, List<IRenderable>> renderQueue = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern.
     * Registers the engine's standard rendering layers.
     */
    private RenderManager() {
        // Register the engine's standard layers
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

    /**
     * Gets the single instance of the RenderManager.
     * @return The singleton instance.
     */
    public static RenderManager getInstance() { 
        return instance; 
    }

    /**
     * Registers a new rendering layer with the manager.
     * The game can use this method to add its own custom rendering layers.
     * Layers are automatically sorted by their depth.
     * 
     * @param layer The RenderLayer to register.
     */
    public void registerLayer(RenderLayer layer) {
        if (!layers.contains(layer)) {
            layers.add(layer);
            renderQueue.put(layer, new ArrayList<>());
            // Re-sort the layer list by depth whenever a new one is added
            Collections.sort(layers);
        }
    }
    
    /**
     * Finds and returns a registered render layer by its name.
     * @param name The name of the layer (e.g., "CHARACTERS").
     * @return The corresponding RenderLayer object, or null if not found.
     */
    public RenderLayer getLayerByName(String name) {
        for (RenderLayer layer : layers) {
            if (layer.getName().equalsIgnoreCase(name)) {
                return layer;
            }
        }
        return null; // Returns null if no layer with that name is registered.
    }

    /**
     * Registers an {@link IRenderable} object to be drawn on its specified layer.
     * 
     * @param renderable The object to register for rendering.
     */
    public void register(IRenderable renderable) {
        if (renderable != null) {
            renderQueue.get(renderable.getRenderLayer()).add(renderable);
        }
    }

    /**
     * Unregisters an {@link IRenderable} object, removing it from the rendering queue.
     * 
     * @param renderable The object to unregister.
     */
    public void unregister(IRenderable renderable) {
        if (renderable != null) {
            renderQueue.get(renderable.getRenderLayer()).remove(renderable);
        }
    }

    /**
     * Performs the actual rendering process.
     * It iterates through all registered layers in order of depth, and for each layer,
     * it draws all visible {@link IRenderable} objects sorted by their Z-order.
     * 
     * @param g The Graphics context to draw on.
     */
    public void render(Graphics g) {
        // Iterate over layers already sorted by depth
        for (RenderLayer layer : layers) {
            List<IRenderable> renderables = renderQueue.get(layer);
            // Sort renderables within the layer by their Z-order
            renderables.sort(Comparator.comparingInt(IRenderable::getZOrder));
            for (IRenderable renderable : renderables) {
                if (renderable.isVisible()) {
                    renderable.render(g);
                }
            }
        }
    }

    /**
     * Clears all {@link IRenderable} objects from all rendering layers.
     * This is typically called when changing game states or loading a new level.
     */
    public void clear() {
        for (List<IRenderable> list : renderQueue.values()) {
            list.clear();
        }
    }
}
