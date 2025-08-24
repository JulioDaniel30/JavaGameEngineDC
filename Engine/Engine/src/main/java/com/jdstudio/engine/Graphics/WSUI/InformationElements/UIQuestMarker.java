package com.jdstudio.engine.Graphics.WSUI.InformationElements;

import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-attached UI element that displays a quest status icon above a {@link GameObject}.
 * The icon changes based on the quest's state (available, active, completable, or none).
 * 
 * @author JDStudio
 */
public class UIQuestMarker extends UIWorldAttached {

    /**
     * Defines the possible states of a quest marker.
     */
    public enum QuestState {
        /** No icon is displayed. */
        NONE,
        /** Quest is available (e.g., '!'). */
        AVAILABLE,
        /** Quest is active (e.g., gray '?'). */
        ACTIVE,
        /** Quest is ready to be turned in (e.g., yellow '?'). */
        COMPLETABLE
    }

    private QuestState currentState = QuestState.NONE;
    private final Sprite availableSprite;
    private final Sprite activeSprite;
    private final Sprite completableSprite;
    
    private Sprite currentSprite = null;

    /**
     * Constructs a new UIQuestMarker.
     *
     * @param target      The GameObject this marker is attached to.
     * @param available   The sprite for the AVAILABLE state.
     * @param active      The sprite for the ACTIVE state.
     * @param completable The sprite for the COMPLETABLE state.
     */
    public UIQuestMarker(GameObject target, Sprite available, Sprite active, Sprite completable) {
        super(target, -24); // Y offset slightly higher
        this.availableSprite = available;
        this.activeSprite = active;
        this.completableSprite = completable;
        updateSprite();
    }

    /**
     * Sets the current state of the quest marker.
     * This will automatically update the displayed sprite.
     * 
     * @param newState The new QuestState.
     */
    public void setState(QuestState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            updateSprite();
        }
    }
    
    /**
     * Updates the internal sprite based on the current quest state.
     * Also controls the visibility of the UI element.
     */
    private void updateSprite() {
        switch (currentState) {
            case AVAILABLE: this.currentSprite = availableSprite; break;
            case ACTIVE:    this.currentSprite = activeSprite;    break;
            case COMPLETABLE: this.currentSprite = completableSprite; break;
            case NONE: default: this.currentSprite = null; break;
        }
        this.visible = (this.currentSprite != null);
    }

    /**
     * Renders the current quest marker sprite above its target GameObject.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || currentSprite == null || target == null) return;
        
        int drawX = (this.x - (currentSprite.getWidth() / 2)) - Engine.camera.getX();
        int drawY = this.y - currentSprite.getHeight() - Engine.camera.getY();

        g.drawImage(currentSprite.getImage(), drawX, drawY, null);
    }
}
