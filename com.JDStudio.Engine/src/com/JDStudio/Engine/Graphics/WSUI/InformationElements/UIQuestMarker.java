package com.JDStudio.Engine.Graphics.WSUI.InformationElements;

import java.awt.Graphics;
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.WSUI.UIWorldAttached;
import com.JDStudio.Engine.Object.GameObject;

public class UIQuestMarker extends UIWorldAttached {

    public enum QuestState {
        NONE,           // Sem ícone
        AVAILABLE,      // Missão disponível (ex: !)
        ACTIVE,         // Missão em andamento (ex: ? cinzento)
        COMPLETABLE     // Missão pronta para entregar (ex: ? amarelo)
    }

    private QuestState currentState = QuestState.NONE;
    private final Sprite availableSprite;
    private final Sprite activeSprite;
    private final Sprite completableSprite;
    
    private Sprite currentSprite = null;

    public UIQuestMarker(GameObject target, Sprite available, Sprite active, Sprite completable) {
        super(target, -24); // Offset Y um pouco mais alto
        this.availableSprite = available;
        this.activeSprite = active;
        this.completableSprite = completable;
        updateSprite();
    }

    public void setState(QuestState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            updateSprite();
        }
    }
    
    private void updateSprite() {
        switch (currentState) {
            case AVAILABLE: this.currentSprite = availableSprite; break;
            case ACTIVE:    this.currentSprite = activeSprite;    break;
            case COMPLETABLE: this.currentSprite = completableSprite; break;
            case NONE: default: this.currentSprite = null; break;
        }
        this.visible = (this.currentSprite != null);
    }

    @Override
    public void render(Graphics g) {
        if (!visible || currentSprite == null || target == null) return;
        
        int drawX = (this.x - (currentSprite.getWidth() / 2)) - Engine.camera.getX();
        int drawY = this.y - currentSprite.getHeight() - Engine.camera.getY();

        g.drawImage(currentSprite.getImage(), drawX, drawY, null);
    }
}