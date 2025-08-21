package com.game.Items;

import com.jdstudio.engine.Components.InventoryComponent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Items.Item;
import com.jdstudio.engine.Object.GameObject;
import com.game.States.PlayingState;
import com.game.gameObjects.Player;
import com.game.manegers.GameEvent;

/**
 * Uma implementação concreta de um item: a Poção de Vida.
 */
public class HealthPotion extends Item {

    public HealthPotion() {
        // id, nome, tamanho máximo da pilha
        super("health_potion", "Poção de Vida", 10);
    }

    @Override
    public Sprite getSprite() {
        // Retorna o sprite do item a partir do AssetManager do jogo
        // Supondo que você carregou um sprite com a chave "lifepack"
        return PlayingState.assets.getSprite("lifepack");
    }

    @Override
    public void onUse(GameObject user) {
    	
    	 // Garante que a entidade que está a usar o item é o jogador.
        if (!(user instanceof Player)) {
            return;
        }

        Player player = (Player) user;

        // --- A VERIFICAÇÃO PRINCIPAL ESTÁ AQUI ---
        // Se a vida do jogador já estiver no máximo, não faz nada.
        if (player.life >= player.maxLife) {
            System.out.println("A vida do jogador já está cheia!");
            return; // Sai do método sem consumir o item.
        }
        
        // Se a vida não estiver cheia, prossegue...
        
        // Pega o componente de inventário do jogador
        InventoryComponent invComp = user.getComponent(InventoryComponent.class);
        if (invComp == null) return;

        // Tenta remover 1 poção do inventário
        boolean removed = invComp.inventory.removeItem(this.id, 1);
        
        // Apenas cura o jogador SE o item foi realmente removido
        if (removed) {
            super.onUse(user); // Imprime a mensagem "Usando item..."
            player.heal(50);
            EventManager.getInstance().trigger(GameEvent.PLAYER_HEALED, 50.0);
            System.out.println("Jogador curado! Vida atual: " + player.life);
        }
    }
}