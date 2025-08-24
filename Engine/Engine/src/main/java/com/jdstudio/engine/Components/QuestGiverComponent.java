package com.jdstudio.engine.Components;

import com.jdstudio.engine.Graphics.AssetManager;
import com.jdstudio.engine.Graphics.UI.Managers.UIManager;
import com.jdstudio.engine.Object.GameObject;

/**
 * The base, engine-side class for any component that gives quests.
 * It manages the visual aspect (the UI Quest Marker) while delegating the actual quest logic
 * to game-specific subclasses.
 * 
 * @author JDStudio
 */
public abstract class QuestGiverComponent extends Component {

    /** This component now internally manages the visual indicator. */
    private QuestIndicatorComponent indicator;

    /**
     * Constructor for the base class.
     * 
     * @param uiManager The UIManager from the game state, required to create the UI marker.
     * @param assets    The AssetManager containing the quest icon sprites.
     */
    public QuestGiverComponent(UIManager uiManager, AssetManager assets) {
        this.indicator = new QuestIndicatorComponent(uiManager, assets);
    }
    
    /**
     * Initializes the component, adding the visual indicator to the same owner.
     */
    @Override
    public void initialize(GameObject owner) {
        super.initialize(owner);
        // Ensures the visual indicator is added to the same owner
        owner.addComponent(this.indicator);
        updateMarkerState(); // Sets the initial state of the icon
    }

    /**
     * Updates the component every frame, checking the quest state and updating the visual icon accordingly.
     */
    @Override
    public void update() {
        updateMarkerState();
    }
    
    /**
     * Checks the current quest state by calling the abstract methods and updates the visual indicator.
     */
    private void updateMarkerState() {
        if (indicator == null) return;
        
        // Delegates the logic check to the abstract methods that the game will implement
        if (isQuestCompleted()) {
            indicator.hide();
        } else if (isQuestCompletable()) {
            indicator.showCompletableState();
        } else if (isQuestActive()) {
            indicator.showActiveState();
        } else if (isQuestAvailable()) {
            indicator.showAvailableState();
        } else {
            indicator.hide();
        }
    }

    // --- ABSTRACT METHODS TO BE IMPLEMENTED BY THE GAME ---

    /**
     * GAME-SPECIFIC LOGIC: Must be implemented by the subclass.
     * @return true if the quest is available to be accepted.
     */
    public abstract boolean isQuestAvailable();
    
    /**
     * GAME-SPECIFIC LOGIC: Must be implemented by the subclass.
     * @return true if the quest has been accepted and is in progress.
     */
    public abstract boolean isQuestActive();
    
    /**
     * GAME-SPECIFIC LOGIC: Must be implemented by the subclass.
     * @return true if the quest is in progress AND the objectives have been met.
     */
    public abstract boolean isQuestCompletable();
    
    /**
     * GAME-SPECIFIC LOGIC: Must be implemented by the subclass.
     * @return true if the quest has been turned in and is permanently completed.
     */
    public abstract boolean isQuestCompleted();
}
