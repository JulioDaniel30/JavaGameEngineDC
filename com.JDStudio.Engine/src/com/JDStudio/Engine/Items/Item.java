package com.JDStudio.Engine.Items;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Object.GameObject;

public abstract class Item {

    public final String id;
    public final String name;
    public final int maxStackSize;
    
    // Construtor protegido, pois esta classe não deve ser instanciada diretamente
    protected Item(String id, String name, int maxStackSize) {
        this.id = id;
        this.name = name;
        this.maxStackSize = Math.max(1, maxStackSize); // Garante que o tamanho mínimo seja 1
    }

    /**
     * Retorna o sprite que representa este item no inventário.
     * Deve ser implementado pelas classes de item do jogo.
     */
    public abstract Sprite getSprite();

    /**
     * Chamado quando o jogador "usa" o item a partir do inventário.
     * A lógica (ex: curar o jogador, equipar a arma) será implementada no jogo.
     */
    public void onUse(GameObject user) {
        System.out.println("Usando item: " + name);
    }
}