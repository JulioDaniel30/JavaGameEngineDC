package com.JDStudio.Engine.Graphics.UI;

/**
 * Enum que define as "chaves" abstratas para todos os tipos de sprites de UI.
 * Não se preocupa com a aparência, apenas com a função do sprite.
 * (Ex: "a imagem de um botão normal", "a imagem de um botão selecionado").
 */
public enum UISpriteKey {
    BUTTON_NORMAL,
    BUTTON_HOVER,
    BUTTON_PRESSED,
    INVENTORY_BUTTON_NORMAL_20,
    INVENTORY_BUTTON_NORMAL_30,
    INVENTORY_BUTTON_NORMAL_30_2,
    INVENTORY_BUTTON_NORMAL_40,
    INVENTORY_BUTTON_NORMAL_60,
    INVENTORY_BUTTON_NORMAL_80,
    
    TOGGLE_OFF,
    TOGGLE_ON,
    
    SLIDER_TRACK,
    SLIDER_HANDLE,
    
    HEART_FULL,
    HEART_HALF,
    HEART_EMPTY;
}