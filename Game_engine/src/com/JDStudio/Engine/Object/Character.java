package com.JDStudio.Engine.Object;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Uma especialização de GameObject que representa um "personagem" no jogo.
 * Adiciona conceitos como vida, dano e morte.
 * Esta é uma classe base para Jogadores, Inimigos, NPCs, etc.
 */
public abstract class Character extends GameObject {

    public double life;
    public double maxLife;
    protected boolean isDead = false;

    public Character(double x, double y, int width, int height) {
        super(x, y, width, height);
    }
    public Character(double x, double y, int width, int height, Sprite sprite) {
        super(x, y, width, height, sprite);
    }

    /**
     * Aplica uma quantidade de dano a este personagem.
     * @param amount A quantidade de dano a ser aplicada.
     */
    public void takeDamage(double amount) {
        if (isDead) return;

        this.life -= amount;
        if (this.life <= 0) {
            this.life = 0;
            die();
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

    /**
     * Chamado quando a vida do personagem chega a zero.
     * Classes filhas podem sobrescrever este método para adicionar
     * animações de morte, sons, ou dropar itens.
     */
    protected void die() {
        this.isDead = true;
        // Por padrão, marca o objeto para ser removido do jogo.
        this.isDestroyed = true; 
        System.out.println("Um personagem morreu!");
    }

    @Override
    public void tick() {
        // Se o personagem já está morto, ele não deve fazer mais nada.
        if (isDead) {
            // Poderíamos adicionar uma lógica de animação de morte aqui no futuro
            return;
        }
        
        super.tick(); // Executa a lógica normal de GameObject (movimento, animação)
    }

    public boolean isDead() {
        return isDead;
    }
}