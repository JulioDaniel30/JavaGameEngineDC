package com.jdstudio.engine.Graphics.WSUI;

import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Object.GameObject;

/**
 * An abstract base class for UI elements that are attached to a {@link GameObject} in the world space.
 * These elements automatically follow their target GameObject's position.
 * 
 * @author JDStudio
 */
public abstract class UIWorldAttached extends UIElement {

    /** The GameObject that this UI element is attached to and follows. */
    public GameObject target;
    
    /** Vertical offset in pixels relative to the target's Y position. */
    protected int yOffset;

    /**
     * Constructs a new UIWorldAttached element.
     *
     * @param target  The GameObject to attach to.
     * @param yOffset The vertical offset from the target's Y position.
     */
    public UIWorldAttached(GameObject target, int yOffset) {
        super(0, 0); // Initial position is irrelevant as it will be updated in tick()
        this.target = target;
        this.yOffset = yOffset;
    }

    /**
     * Updates the position of the UI element to follow its target GameObject.
     * It centers horizontally on the target and applies the vertical offset.
     * If the target is destroyed, the UI element becomes invisible.
     */
    @Override
    public void tick() {
        if (target == null || target.isDestroyed) {
            this.visible = false;
            return;
        }
        
        // The UI position is updated to follow the target
        // Centers horizontally, and applies the vertical offset
        this.x = target.getX() + (target.getWidth() / 2);
        this.y = target.getY() + yOffset;
    }
}
