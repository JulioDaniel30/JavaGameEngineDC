package com.JDStudio.Engine.Object;

import java.awt.Graphics;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Dialogue.ConditionManager;
import com.JDStudio.Engine.Dialogue.Dialogue;
import com.JDStudio.Engine.Dialogue.DialogueManager;
import com.JDStudio.Engine.Dialogue.DialogueNode;
import com.JDStudio.Engine.Dialogue.DialogueParser;
import com.JDStudio.Engine.Utils.PropertiesReader;

public class DialogableGameObject extends GameObject{

	protected Dialogue dialogue;
	protected String dialoguePath;
    protected int interactionRadius;
	
	public DialogableGameObject(JSONObject properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initialize(JSONObject properties) {
		// TODO Auto-generated method stub
		super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.interactionRadius = reader.getInt("interactionRadius", 24);
        
        
        InteractionComponent interaction = new InteractionComponent();

        // 2. Cria uma zona de interação circular com o TIPO "DIALOGUE" e o mesmo raio de antes
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, interactionRadius));

        // 3. Adiciona o componente ao GameObject
        this.addComponent(interaction);
           
       
        dialoguePath = reader.getString("dialogueFile", null);
        if (dialoguePath != null && !dialoguePath.isEmpty()) {
            System.out.println("NPC '" + this.name + "' tentando carregar diálogo de: " + dialoguePath);
            this.dialogue = DialogueParser.parseDialogue(dialoguePath);
            if (this.dialogue == null) {
                System.err.println("ERRO: O diálogo para o NPC '" + this.name + "' falhou ao carregar.");
            }
        } else {
            System.err.println("AVISO: NPC '" + this.name + "' não tem a propriedade 'dialogueFile' definida no Tiled.");
        }
	}
	
	//Método para o EventListener poder aceder ao diálogo
    public Dialogue getDialogue() {
        return this.dialogue;
    }
    
    public void startFilteredDialogue(GameObject interactor) {
        // Cria uma cópia fresca do diálogo para não modificar o original
        Dialogue filteredDialogue = DialogueParser.parseDialogue(this.dialoguePath); // Supondo que o NPC tem o caminho do seu diálogo
        if (filteredDialogue == null) return;
        
        // Percorre todos os nós do diálogo
        for (DialogueNode node : filteredDialogue.getNodes().values()) {
            node.getChoices().removeIf(choice -> {
                String condition = choice.getCondition();
                if (condition == null) {
                    return false; // Sem condição, a escolha nunca é removida
                }

                // --- A LÓGICA AGORA É DELEGADA AO CONDITION MANAGER ---
                // A engine pergunta ao jogo se a condição foi satisfeita.
                boolean conditionMet = ConditionManager.getInstance().checkCondition(condition, interactor);
                
                // A escolha será removida se a condição NÃO for satisfeita
                return !conditionMet;
            });
        }

        // Inicia o diálogo com as escolhas já filtradas
        DialogueManager.getInstance().startDialogue(filteredDialogue, this, interactor);
    }
    
    @Override
    public void renderDebug(Graphics g) {
    	// TODO Auto-generated method stub
    	super.renderDebug(g);
    }

}
