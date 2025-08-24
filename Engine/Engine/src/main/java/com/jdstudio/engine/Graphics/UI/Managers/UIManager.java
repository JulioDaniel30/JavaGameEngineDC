package com.jdstudio.engine.Graphics.UI.Managers;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;

/**
 * Manages a collection of User Interface (UI) elements ({@link UIElement}).
 * <p>
 * This class acts as a container for all UI elements, simplifying the process
 * of rendering and managing them together. It also handles registering/unregistering
 * elements with the {@link RenderManager}.
 *
 * @author JDStudio
 * @since 1.0
 */
public class UIManager {

    /** The list that stores all managed UI elements. */
    private List<UIElement> elements;

    /**
     * Default constructor that initializes the UI manager.
     * Creates a new empty list to store the elements.
     */
    public UIManager() {
        elements = new ArrayList<>();
    }

    /**
     * Adds a new UI element to be managed and rendered.
     * The element is also registered with the {@link RenderManager}.
     *
     * @param element The {@link UIElement} to be added. Cannot be null.
     */
    public void addElement(UIElement element) {
        elements.add(element);
        RenderManager.getInstance().register(element);
    }

    /**
     * Removes a UI element from the management list.
     * The element will no longer be rendered.
     *
     * @param element The {@link UIElement} to be removed.
     */
    public void removeElement(UIElement element) {
        elements.remove(element);
        RenderManager.getInstance().unregister(element);
    }
    
    /**
     * Unregisters all UI elements from the {@link RenderManager} and clears the internal list.
     * Essential to be called when a menu state is closed or a scene is unloaded.
     */
    public void unregisterAllElements() {
        for (UIElement element : elements) {
            RenderManager.getInstance().unregister(element);
        }
        elements.clear();
    }
    
    /**
     * Updates the logic of all managed UI elements.
     * It iterates over a copy of the list to prevent {@code ConcurrentModificationException}.
     */
    public void tick() {
        // Create a copy of the elements list to iterate over it.
        // The original list (this.elements) can now be safely modified (e.g., elements added/removed).
        for (UIElement element : new ArrayList<>(this.elements)) {
            if (element.isVisible()) {
                element.tick();
            }
        }
    }

    /**
     * Renders all visible UI elements on the screen.
     * <p>
     * This method iterates over all elements and calls the {@code render(g)}
     * method of each one that is marked as visible.
     *
     * @param g The {@link Graphics} context where the elements will be drawn.
     */
    public void render(Graphics g) {
        for (UIElement element : elements) {
            if (element.isVisible()) {
                element.render(g);
            }
        }
    }
}
