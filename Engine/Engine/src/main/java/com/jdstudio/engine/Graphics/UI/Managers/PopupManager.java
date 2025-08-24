package com.jdstudio.engine.Graphics.UI.Managers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Graphics.UI.Elements.UIPopup;
import com.jdstudio.engine.Object.GameObject;

/**
 * A singleton class that manages a pool of {@link UIPopup} elements.
 * It allows for efficient creation, updating, and rendering of temporary floating text popups
 * (e.g., damage numbers, healing notifications) by reusing objects from a pool.
 * 
 * @author JDStudio
 */
public class PopupManager {
 
    private static final PopupManager instance = new PopupManager();
    
    /** The pool of UIPopup objects for reuse. */
    private final List<UIPopup> popupPool = new ArrayList<>();

    private PopupManager() {}

    /**
     * Gets the single instance of the PopupManager.
     * @return The singleton instance.
     */
    public static PopupManager getInstance() {
        return instance;
    }

    /**
     * Creates a new text popup above a target GameObject.
     * It reuses an inactive popup from the pool if available, otherwise creates a new one.
     *
     * @param target   The GameObject above which the popup should appear.
     * @param text     The text message to display in the popup.
     * @param font     The font for the popup text.
     * @param color    The color of the popup text.
     * @param lifeTime The duration in frames for which the popup should be visible.
     */
    public void createPopup(GameObject target, String text, Font font, Color color, int lifeTime) {
        // Look for an inactive popup in the pool to reuse
        for (UIPopup popup : popupPool) {
            if (!popup.isActive) {
                popup.init(target, text, font, color, lifeTime);
                return;
            }
        }
        
        // If no inactive ones, create a new one
        UIPopup newPopup = new UIPopup();
        newPopup.init(target, text, font, color, lifeTime);
        popupPool.add(newPopup);
    }
    
    /**
     * Updates all active popups in the pool.
     * This method should be called once per game frame.
     */
    public void update() {
        for (UIPopup popup : popupPool) {
            if (popup.isActive) {
                popup.tick();
            }
        }
    }

    /**
     * Renders all active popups.
     * This method should be called once per game frame.
     *
     * @param g The Graphics context to draw on.
     */
    public void render(Graphics g) {
        for (UIPopup popup : popupPool) {
            if (popup.isActive) {
                popup.render(g);
            }
        }
    }
}
