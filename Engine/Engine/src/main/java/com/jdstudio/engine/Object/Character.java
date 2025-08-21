package com.jdstudio.engine.Object;

import org.json.JSONObject;

import com.jdstudio.engine.Events.CharacterSpokeEventData;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * Uma especialização de GameObject que representa um "personagem" no jogo.
 * Adiciona conceitos como vida, dano e morte.
 * Esta é uma classe base para Jogadores, Inimigos, NPCs, etc.
 */
public abstract class Character extends GameObject {

	public double life;
    public double maxLife;
    protected boolean isDead = false;
    
    public Character(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        setCollisionType(CollisionType.CHARACTER_TRIGGER);
        PropertiesReader reader = new PropertiesReader(properties);
        String layerName = reader.getString("renderLayer", "CHARACTERS");
        
        // 2. Pede ao RenderManager para encontrar a camada com esse nome.
        RenderLayer layer = RenderManager.getInstance().getLayerByName(layerName);
        
        // 3. Define a camada de renderização do GameObject.
        if (layer != null) {
            this.setRenderLayer(layer);
        } else {
            // Se o nome da camada no Tiled for inválido ou não estiver registado,
            // usa o padrão da engine e avisa no console.
            System.err.println("Aviso: RenderLayer '" + layerName + "' inválida ou não registada para o objeto '" + this.name + "'. Usando a camada padrão.");
            this.setRenderLayer(StandardLayers.CHARACTERS);
        }
    }

    protected void die() {
        this.isDead = true;
        this.isDestroyed = true; 
        setCollisionType(CollisionType.NO_COLLISION);
    }
    
    /**
     * Faz este personagem "dizer" algo, disparando um evento para que o jogo possa reagir.
     * @param message A mensagem a ser exibida.
     * @param durationInSeconds A duração em segundos.
     */
    public void say(String message, float durationInSeconds) {
        // A engine apenas anuncia o evento, sem saber como ele será renderizado.
        EventManager.getInstance().trigger(
            EngineEvent.CHARACTER_SPOKE, 
            new CharacterSpokeEventData(this, message, durationInSeconds)
        );
    }
    @Override
    public void tick() {
        // Primeiro, verifica o estado do personagem.
        if (isDead || isDestroyed) return;
        
        // Depois, chama o tick do GameObject, que vai atualizar todos os componentes.
        super.tick();
    }

    /**
     * Aplica uma quantidade de dano a este personagem.
     * @param amount A quantidade de dano a ser aplicada.
     */
    public void takeDamage(double amount) {
    	 // --- CORREÇÃO AQUI ---
        // Um personagem com 0 ou menos de vida não pode mais tomar dano.
        if (this.life <= 0) return;

        this.life -= amount;
        if (this.life <= 0) {
            this.life = 0;
            die(); // Chama o método die() que cuida das bandeiras isDead/isDestroyed
        }
    }

    /**
     * Cura uma quantidade de vida a este personagem.
     * @param amount A quantidade de vida a ser curada.
     */
    public void heal(double amount) {
        if (isDead) return;

        this.life += amount;
        if (this.life > this.maxLife) {
            this.life = this.maxLife;
        }
    }

    

    public boolean isDead() {
        return isDead;
    }
}