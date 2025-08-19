package com.JDStudio.Engine.Components;

import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.UI.Managers.UIManager;
import com.JDStudio.Engine.Graphics.WSUI.InformationElements.UIQuestMarker;
import com.JDStudio.Engine.Graphics.WSUI.InformationElements.UIQuestMarker.QuestState;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Um componente "engine-side" que gere a parte visual de um marcador de missão (UIQuestMarker).
 * Ele cria o marcador e fornece métodos simples para alterar o seu estado.
 * A lógica de QUANDO alterar o estado pertence ao jogo.
 */
public class QuestIndicatorComponent extends Component {

    private UIQuestMarker questMarker;

    /**
     * @param uiManager O UIManager do estado de jogo, necessário para adicionar o marcador.
     * @param assets O AssetManager que contém os sprites dos ícones.
     */
    public QuestIndicatorComponent(UIManager uiManager, AssetManager assets) {
        // Carrega os sprites necessários
        Sprite availableSprite = assets.getSprite("quest_available_icon"); // '!'
        Sprite activeSprite = assets.getSprite("quest_active_icon");      // '?' cinzento
        Sprite completableSprite = assets.getSprite("quest_completable_icon"); // '?' amarelo

        // Cria o UIQuestMarker
        // O 'owner' será definido no método initialize.
        this.questMarker = new UIQuestMarker(null, availableSprite, activeSprite, completableSprite);
        
        // Regista o marcador na UI
        uiManager.addElement(this.questMarker);
    }

    @Override
    public void initialize(GameObject owner) {
        super.initialize(owner);
        // Define o alvo do marcador como o dono deste componente
        this.questMarker.target = owner;
    }
    
    // --- MÉTODOS PÚBLICOS PARA O JOGO CONTROLAR O MARCADOR ---

    public void showAvailableState() {
        this.questMarker.setState(QuestState.AVAILABLE);
    }

    public void showActiveState() {
        this.questMarker.setState(QuestState.ACTIVE);
    }

    public void showCompletableState() {
        this.questMarker.setState(QuestState.COMPLETABLE);
    }
    
    public void hide() {
        this.questMarker.setState(QuestState.NONE);
    }

    public void onOwnerDestroyed() {
        // Quando o NPC for destruído, garante que o seu marcador também é
        if (questMarker != null) {
            questMarker.destroy();
        }
    }
}