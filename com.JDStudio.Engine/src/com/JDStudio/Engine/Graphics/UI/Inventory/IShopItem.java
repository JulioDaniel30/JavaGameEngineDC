package com.JDStudio.Engine.Graphics.UI.Inventory;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Uma interface que define o contrato para um item que pode ser exibido na UIShopView.
 * As classes de item do seu JOGO devem implementar esta interface.
 */
public interface IShopItem {
    /** Retorna o nome do item a ser exibido. */
    String getName();
    /** Retorna o preço do item a ser exibido. */
    int getPrice();
    /** Retorna o sprite do ícone do item. */
    Sprite getSprite();
}