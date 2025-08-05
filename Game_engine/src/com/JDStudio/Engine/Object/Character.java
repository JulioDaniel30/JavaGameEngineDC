package com.JDStudio.Engine.Object;

import org.json.JSONObject;

import com.JDStudio.Engine.Utils.PropertiesReader;

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
        // A lógica de inicialização de vida, etc., pode ficar aqui
        // se for comum a todos os personagens.
    }

    protected void die() {
        this.isDead = true;
        this.isDestroyed = true; 
        setCollisionType(CollisionType.NO_COLLISION);
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