package com.JDStudio.Engine.Components;

import com.JDStudio.Engine.Object.GameObject;

public class QuestComponent extends Component {
    private GameObject questTarget = null;
    
    public void setQuestTarget(GameObject target) {
        this.questTarget = target;
        // Aqui você poderia disparar um evento "QUEST_UPDATED"
    }
    
    public GameObject getQuestTarget() {
        return this.questTarget;
    }
}