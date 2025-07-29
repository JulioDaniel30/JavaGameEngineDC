// game
package com.game;

import com.JDStudio.Engine.Dialogue.Dialogue;
import com.JDStudio.Engine.Dialogue.DialogueParser;
import com.JDStudio.Engine.Object.EngineNPC; // <-- Herda da nova classe da engine

public class Ferreiro extends EngineNPC {

	public Ferreiro(double x, double y, String dialoguePath) {
        super(x, y, 16, 16);
        
        // Carrega o diálogo a partir do arquivo usando o parser
        Dialogue d = DialogueParser.parseDialogue(dialoguePath);
        
        setDialogue(d);
        setSprite(PlayingState.assets.getSprite("npc_sprite"));
        setInteractionRadius(32);
        
        this.maxLife = 50;
        this.life = this.maxLife;
    }

    @Override
    public void tick() {
        super.tick();
        // Se o ferreiro tivesse alguma lógica única a cada frame, ela viria aqui.
        // Por exemplo, tocar um som de martelada de vez em quando.
    }
    
    // Os métodos onInteract() e getInteractionRadius() JÁ VÊM DA CLASSE PAI!
    // Não precisamos reescrevê-los.
}