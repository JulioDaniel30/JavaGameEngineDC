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
    
    TOGGLE_OFF,
    TOGGLE_ON,
    
    SLIDER_TRACK,
    SLIDER_HANDLE,
    
    HEART_FULL,
    HEART_HALF,
    HEART_EMPTY;
}