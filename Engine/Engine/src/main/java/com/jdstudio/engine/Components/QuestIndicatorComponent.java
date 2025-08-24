package com.jdstudio.engine.Components;

import com.jdstudio.engine.Graphics.AssetManager;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Managers.UIManager;
import com.jdstudio.engine.Graphics.WSUI.InformationElements.UIQuestMarker;
import com.jdstudio.engine.Graphics.WSUI.InformationElements.UIQuestMarker.QuestState;
import com.jdstudio.engine.Object.GameObject;

/**
 * An engine-side component that manages the visual aspect of a quest marker (UIQuestMarker).
 * It creates the marker and provides simple methods to change its state.
 * The logic for WHEN to change the state belongs to the game-specific implementation (e.g., QuestGiverComponent).
 * 
 * @author JDStudio
 */
public class QuestIndicatorComponent extends Component {

    private UIQuestMarker questMarker;

    /**
     * Constructs a new QuestIndicatorComponent.
     * 
     * @param uiManager The UIManager from the game state, necessary to add the marker.
     * @param assets    The AssetManager containing the icon sprites.
     */
    public QuestIndicatorComponent(UIManager uiManager, AssetManager assets) {
        // Load the necessary sprites
        Sprite availableSprite = assets.getSprite("quest_available_icon"); // '!'
        Sprite activeSprite = assets.getSprite("quest_active_icon");      // Gray '?'
        Sprite completableSprite = assets.getSprite("quest_completable_icon"); // Yellow '?'

        // Create the UIQuestMarker
        // The 'owner' will be set in the initialize method.
        this.questMarker = new UIQuestMarker(null, availableSprite, activeSprite, completableSprite);
        
        // Register the marker with the UI
        uiManager.addElement(this.questMarker);
    }

    /**
     * Initializes the component and sets the marker's target to be the component's owner.
     */
    @Override
    public void initialize(GameObject owner) {
        super.initialize(owner);
        // Set the marker's target to this component's owner
        this.questMarker.target = owner;
    }
    
    // --- PUBLIC METHODS FOR THE GAME TO CONTROL THE MARKER ---

    /**
     * Sets the marker to the AVAILABLE state (e.g., '!').
     */
    public void showAvailableState() {
        this.questMarker.setState(QuestState.AVAILABLE);
    }

    /**
     * Sets the marker to the ACTIVE state (e.g., gray '?').
     */
    public void showActiveState() {
        this.questMarker.setState(QuestState.ACTIVE);
    }

    /**
     * Sets the marker to the COMPLETABLE state (e.g., yellow '?').
     */
    public void showCompletableState() {
        this.questMarker.setState(QuestState.COMPLETABLE);
    }
    
    /**
     * Hides the marker.
     */
    public void hide() {
        this.questMarker.setState(QuestState.NONE);
    }

    /**
     * Cleans up the UI marker when its owner is destroyed.
     * This should be called to prevent memory leaks.
     */
    public void onOwnerDestroyed() {
        if (questMarker != null) {
            questMarker.destroy();
        }
    }
}
