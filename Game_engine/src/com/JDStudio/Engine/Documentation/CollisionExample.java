package com.JDStudio.Engine.Documentation;

/**
 * ESTE ARQUIVO É APENAS UM EXEMPLO PARA A DOCUMENTAÇÃO JAVADOC.
 * Ele não faz parte da lógica do jogo e contém classes "dummy" para
 * garantir que o código seja compilável e possa ser usado pela tag @snippet.
 */
public class CollisionExample {

    // --- Definições Mínimas (Dummy Classes) ---
    // Apenas o suficiente para o exemplo compilar.

    /** Classe base dummy para o exemplo. */
    public static abstract class GameObject {
        public static boolean isColliding(GameObject obj1, GameObject obj2) {
            // A lógica real está na engine, aqui é apenas um placeholder.
            return true; 
        }
    }

    /** Classe Player dummy para o exemplo. */
    public static class Player extends GameObject {
        public void takeDamage(int amount) {
            System.out.println("Jogador sofreu " + amount + " de dano.");
        }
    }

    /** Classe Enemy dummy para o exemplo. */
    public static class Enemy extends GameObject {
        // Nenhuma lógica extra necessária para o exemplo.
    }


    // --- O Snippet de Código Real ---
    // Este é o método que contém o trecho que será usado na documentação.

    void checkPlayerCollision(Player player, Enemy enemy) {
        
        //region isCollidingExample
        // Dentro do loop principal do jogo, você chamaria o método assim:
        if (GameObject.isColliding(player, enemy)) {
            // Ações a serem tomadas em caso de colisão, como aplicar dano.
            player.takeDamage(10);
        }
        //endregion isCollidingExample
        
    }
}