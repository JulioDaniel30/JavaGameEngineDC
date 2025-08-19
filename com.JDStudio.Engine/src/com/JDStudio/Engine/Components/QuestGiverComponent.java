package com.JDStudio.Engine.Components;

import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.UI.Managers.UIManager;
import com.JDStudio.Engine.Object.GameObject;

/**
 * A classe base "engine-side" para qualquer componente que dá missões.
 * Ela gere a parte visual (o UIQuestMarker), enquanto delega a lógica
 * da missão para as subclasses do jogo.
 */
public abstract class QuestGiverComponent extends Component {

    // Este componente agora gere internamente o indicador visual
    private QuestIndicatorComponent indicator;

    /**
     * Construtor para a classe base.
     * @param uiManager O UIManager do estado de jogo, necessário para criar o marcador de UI.
     * @param assets O AssetManager que contém os sprites dos ícones de missão.
     */
    public QuestGiverComponent(UIManager uiManager, AssetManager assets) {
        this.indicator = new QuestIndicatorComponent(uiManager, assets);
    }
    
    @Override
    public void initialize(GameObject owner) {
        super.initialize(owner);
        // Garante que o indicador visual seja adicionado ao mesmo dono
        owner.addComponent(this.indicator);
        updateMarkerState(); // Define o estado inicial do ícone
    }

    @Override
    public void update() {
        // A lógica principal do componente: a cada frame, verifica o estado da missão
        // e atualiza o ícone visual de acordo.
        updateMarkerState();
    }
    
    private void updateMarkerState() {
        if (indicator == null) return;
        
        // Delega a verificação da lógica para os métodos abstratos que o jogo irá implementar
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

    // --- MÉTODOS ABSTRATOS PARA SEREM IMPLEMENTADOS PELO JOGO ---

    /**
     * O JOGO deve implementar esta lógica.
     * @return true se a missão estiver disponível para ser aceite.
     */
    public abstract boolean isQuestAvailable();
    
    /**
     * O JOGO deve implementar esta lógica.
     * @return true se a missão já foi aceite e está em andamento.
     */
    public abstract boolean isQuestActive();
    
    /**
     * O JOGO deve implementar esta lógica.
     * @return true se a missão está em andamento E os objetivos foram cumpridos.
     */
    public abstract boolean isQuestCompletable();
    
    /**
     * O JOGO deve implementar esta lógica.
     * @return true se a missão já foi entregue e está permanentemente concluída.
     */
    public abstract boolean isQuestCompleted();
}