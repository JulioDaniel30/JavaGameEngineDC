package com.jdstudio.engine.Components;

import com.jdstudio.engine.Object.GameObject;

/**
 * A component to manage quest-related data for a GameObject.
 * Currently, it holds a reference to a quest target object.
 * This can be used by other systems (like a quest indicator) to point the player in the right direction.
 * 
 * @author JDStudio
 */
public class QuestComponent extends Component {
    
    /** The target GameObject for the current quest step. */
    private GameObject questTarget = null;
    
    /**
     * Sets the quest target for this component.
     * 
     * @param target The GameObject that is the target of the quest.
     */
    public void setQuestTarget(GameObject target) {
        this.questTarget = target;
        // You could trigger a "QUEST_UPDATED" event here
    }
    
    /**
     * Gets the current quest target.
     *
     * @return The quest target GameObject, or null if none is set.
     */
    public GameObject getQuestTarget() {
        return this.questTarget;
    }
}
