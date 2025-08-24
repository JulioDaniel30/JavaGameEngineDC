package com.jdstudio.engine.Graphics.UI;

/**
 * Enum that defines abstract "keys" for all types of UI sprites.
 * It focuses on the function of the sprite rather than its specific appearance.
 * (e.g., "the image for a normal button", "the image for a selected button").
 * This allows for easy theme switching without changing code.
 */
public enum UISpriteKey {
    /** Key for the normal state of a button. */
    BUTTON_NORMAL,
    /** Key for the hover state of a button. */
    BUTTON_HOVER,
    /** Key for the pressed state of a button. */
    BUTTON_PRESSED,
    
    /** Key for an inventory button of size 20. */
    INVENTORY_BUTTON_NORMAL_20,
    /** Key for an inventory button of size 30. */
    INVENTORY_BUTTON_NORMAL_30,
    /** Key for an inventory button of size 30 (alternative). */
    INVENTORY_BUTTON_NORMAL_30_2,
    /** Key for an inventory button of size 40. */
    INVENTORY_BUTTON_NORMAL_40,
    /** Key for an inventory button of size 60. */
    INVENTORY_BUTTON_NORMAL_60,
    /** Key for an inventory button of size 80. */
    INVENTORY_BUTTON_NORMAL_80,
    
    /** Key for the off state of a toggle switch. */
    TOGGLE_OFF,
    /** Key for the on state of a toggle switch. */
    TOGGLE_ON,
    
    /** Key for the track part of a slider. */
    SLIDER_TRACK,
    /** Key for the handle (thumb) part of a slider. */
    SLIDER_HANDLE,
    
    /** Key for a full heart icon (e.g., in a health bar). */
    HEART_FULL,
    /** Key for a half heart icon. */
    HEART_HALF,
    /** Key for an empty heart icon. */
    HEART_EMPTY;
}
