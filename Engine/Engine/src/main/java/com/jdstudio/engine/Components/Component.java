package com.jdstudio.engine.Components;

import java.awt.Graphics;
import com.jdstudio.engine.Object.GameObject;

/**
 * The base class for all components in the entity-component system.
 * A Component adds a specific behavior or data to a GameObject.
 * Subclasses should implement update() and render() to define their logic.
 * 
 * @author JDStudio
 */
public abstract class Component {

    /**
     * The GameObject to which this component is attached.
     */
    protected GameObject owner;

    /**
     * Called when the component is added to a GameObject.
     * This method should be used to set up the initial state of the component.
     * 
     * @param owner The GameObject to which this component is attached.
     */
    public void initialize(GameObject owner) {
        this.owner = owner;
    }

    /**
     * Called on every frame of the game loop. 
     * This is where the component's main logic should be implemented.
     */
    public void update() {}

    /**
     * Called on every frame of the game loop after the update() method.
     * This is where the component should handle any rendering logic.
     * 
     * @param g The Graphics context to draw on.
     */
    public void render(Graphics g) {}

    /**
     * Sets the owner of this component.
     * This method is final to prevent overriding.
     * 
     * @param owner The GameObject that owns this component.
     */
    public final void setOwner(GameObject owner) {
        this.owner = owner;
    }
}
