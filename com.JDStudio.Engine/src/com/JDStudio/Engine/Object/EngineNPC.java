// engine
package com.JDStudio.Engine.Object;

import java.awt.Graphics;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Dialogue.Dialogue;
import com.JDStudio.Engine.Dialogue.DialogueParser;
import com.JDStudio.Engine.Utils.PropertiesReader;

public abstract class EngineNPC extends Character{

    protected Dialogue dialogue;
    protected int interactionRadius;

    public EngineNPC(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties); // Inicializa a base (GameObject -> Character)
        
        // --- CORREÇÃO AQUI ---
        // As propriedades customizadas do Tiled vêm dentro de um array "properties".
        // Precisamos primeiro pegar esse array e depois ler de dentro dele.
        JSONObject customProperties = new JSONObject();
        if (properties.has("properties")) {
            // O Tiled exporta um array de objetos, então precisamos iterar para encontrar o que queremos
            JSONArray propsArray = properties.getJSONArray("properties");
            for (int i = 0; i < propsArray.length(); i++) {
                JSONObject prop = propsArray.getJSONObject(i);
                // Adicionamos cada propriedade a um novo JSONObject para fácil acesso
                customProperties.put(prop.getString("name"), prop.get("value"));
            }
            }
        
        // Agora, usamos o nosso reader neste novo objeto de propriedades customizadas
        PropertiesReader reader = new PropertiesReader(customProperties);
        
        this.interactionRadius = reader.getInt("interactionRadius", 24);
        
        
        InteractionComponent interaction = new InteractionComponent();

        // 2. Cria uma zona de interação circular com o TIPO "DIALOGUE" e o mesmo raio de antes
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, interactionRadius));

        // 3. Adiciona o componente ao GameObject
        this.addComponent(interaction);
           
       
        String dialoguePath = reader.getString("dialogueFile", null);
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

 // Método para o EventListener poder aceder ao diálogo
    public Dialogue getDialogue() {
        return this.dialogue;
    }

    @Override
    public void tick() {
        super.tick();
        // NPCs não se movem por padrão, então não chamamos movement.tick()
    }
    
    @Override
    public void renderDebug(Graphics g) {
    	// TODO Auto-generated method stub
    	super.renderDebug(g);
    }
    
}