// game
package com.game;

import org.json.JSONObject;
import com.JDStudio.Engine.Object.EngineNPC;

/**
 * A classe Ferreiro é uma implementação específica de um EngineNPC.
 * Ela herda toda a lógica de interação e diálogo da engine.
 */
public class Ferreiro extends EngineNPC {

    public Ferreiro(JSONObject properties) {
        super(properties); // Passa as propriedades para a classe pai (EngineNPC -> Character -> GameObject)

        // A classe pai (EngineNPC) já leu as propriedades "dialogueFile" e
        // "interactionRadius" do JSON e configurou o diálogo e o raio.

        // A única responsabilidade do Ferreiro é definir o que é específico dele,
        // como sua aparência e seus status.
        setSprite(PlayingState.assets.getSprite("npc_sprite"));
        
        // Estes valores também poderiam vir de propriedades customizadas no Tiled,
        // mas podemos defini-los aqui como padrão para este tipo de NPC.
        this.maxLife = 50;
        this.life = this.maxLife;
    }
    
    @Override
    public void initialize(JSONObject properties) {
    	// TODO Auto-generated method stub
    	super.initialize(properties);
    	
    	
    	
    }

    @Override
    public void tick() {
        super.tick();
        // A lógica de tick do EngineNPC já é chamada.
        // Adicione aqui qualquer comportamento único do Ferreiro,
        // como tocar um som de martelada periodicamente.
    }
    
    // Os métodos onInteract() e getInteractionRadius() já são herdados e funcionam
    // automaticamente, não precisamos reescrevê-los.
}