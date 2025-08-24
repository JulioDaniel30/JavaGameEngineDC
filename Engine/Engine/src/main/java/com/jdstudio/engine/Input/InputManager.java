package com.jdstudio.engine.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jdstudio.engine.Engine;

/**
 * A singleton class that manages all keyboard and mouse input for the game.
 * It provides methods to check the state of individual keys/buttons and to query
 * custom-defined actions (e.g., "MOVE_UP", "INTERACT").
 * It uses an event queue for reliable input processing, ensuring no events are missed.
 * 
 * @author JDStudio
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {

    public static final InputManager instance = new InputManager();

    /**
     * Represents a single input event (key press/release, mouse button press/release).
     * @param code The key code (for keyboard) or a negative button code (for mouse).
     * @param pressed True if the key/button was pressed, false if released.
     */
    private record InputEvent(int code, boolean pressed) {}
    
    /** A thread-safe queue to store incoming input events. */
    private final ConcurrentLinkedQueue<InputEvent> eventQueue = new ConcurrentLinkedQueue<>();

    // --- INTERNAL STATE (CONTROLLED BY THE QUEUE) ---
    /** Current state of all keyboard keys (true if pressed, false otherwise). */
    private final boolean[] keys = new boolean[256];
    /** Previous state of all keyboard keys (used for "just pressed" logic). */
    private final boolean[] prevKeys = new boolean[256];
    
    /** State of Ctrl key. */
    private boolean isCtrlDown;
    /** State of Shift key. */
    private boolean isShiftDown;
    /** State of Alt key. */
    private boolean isAltDown;

    /** Current X coordinate of the mouse. */
    private int mouseX;
    /** Current Y coordinate of the mouse. */
    private int mouseY;
    
    /** Current state of mouse buttons (true if pressed, false otherwise). */
    private final boolean[] mouseButtons = new boolean[4]; // 0=N/A, 1=Left, 2=Middle, 3=Right
    /** Previous state of mouse buttons (used for "just pressed" logic). */
    private final boolean[] prevMouseButtons = new boolean[4];
    
    // --- ACTION MAPPING SYSTEM ---
    /** Path to the default engine keybindings JSON file. */
    private static final String ENGINE_BINDINGS_PATH = "/Engine/engine_keybindings.json";
    
    /**
     * Represents a key binding for an action, including modifier keys.
     * @param keyCode The key code (or negative button code for mouse).
     * @param ctrl True if Ctrl must be down.
     * @param shift True if Shift must be down.
     * @param alt True if Alt must be down.
     */
    private record KeyBinding(int keyCode, boolean ctrl, boolean shift, boolean alt) {}
    
    /** Map storing action names to a list of their associated key bindings. */
    private final Map<String, List<KeyBinding>> keyBindings = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern.
     * Loads default engine keybindings.
     */
    private InputManager() {
        loadAndMergeBindings(ENGINE_BINDINGS_PATH);
    }

    /**
     * Gets the single instance of the InputManager.
     * @return The singleton instance.
     */
    public static InputManager getInstance() {
        return instance;
    }

    /**
     * Loads key bindings from a JSON resource file and merges them with existing bindings.
     * This allows for custom key configurations.
     * 
     * @param resourcePath The path to the JSON file in the classpath.
     */
    public void loadAndMergeBindings(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("Warning: Bindings file '" + resourcePath + "' not found. Skipping.");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);
            
            for (String action : json.keySet()) {
                keyBindings.computeIfAbsent(action, k -> new ArrayList<>()); // Ensure list exists
                JSONArray bindingsForAction = json.getJSONArray(action);
                for (int i = 0; i < bindingsForAction.length(); i++) {
                    JSONObject bindingJson = bindingsForAction.getJSONObject(i);
                    String keyString = bindingJson.getString("key");
                    boolean ctrl = bindingJson.optBoolean("ctrl", false);
                    boolean shift = bindingJson.optBoolean("shift", false);
                    boolean alt = bindingJson.optBoolean("alt", false);
                    
                    try {
                        int keyCode = stringToKeyCode(keyString);
                        keyBindings.get(action).add(new KeyBinding(keyCode, ctrl, shift, alt));
                    } catch (Exception e) {
                        System.err.println("Warning: Key '" + keyString + "' in file '" + resourcePath + "' is invalid.");
                    }
                }
            }
            System.out.println("Key bindings (with combinations) successfully merged from: " + resourcePath);
        } catch (Exception e) {
            System.err.println("Error processing bindings file '" + resourcePath + "'.");
            e.printStackTrace();
        }
    }
    
    /**
     * Processes all input events from the queue.
     * This method MUST be called at the BEGINNING of each game tick in the game loop
     * to update the current and previous input states.
     */
    public void pollEvents() {
        // 1. Archive the previous state
        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
        System.arraycopy(mouseButtons, 0, prevMouseButtons, 0, mouseButtons.length);
        
        // 2. Process all events that have accumulated in the queue since the last frame
        InputEvent event;
        while ((event = eventQueue.poll()) != null) {
            if (event.code >= 0 && event.code < keys.length) { // Keyboard Event
                keys[event.code] = event.pressed;
                if (event.code == KeyEvent.VK_CONTROL) isCtrlDown = event.pressed;
                if (event.code == KeyEvent.VK_SHIFT) isShiftDown = event.pressed;
                if (event.code == KeyEvent.VK_ALT) isAltDown = event.pressed;
            } else if (event.code < 0) { // Mouse Event (using negative codes)
                int buttonIndex = Math.abs(event.code);
                if (buttonIndex < mouseButtons.length) {
                    mouseButtons[buttonIndex] = event.pressed;
                }
            }
        }
    }

    /**
     * Converts a string representation of a key (e.g., "A", "SPACE", "MOUSE1") to its corresponding key code.
     * Mouse buttons are represented by negative integers.
     * 
     * @param keyString The string representation of the key.
     * @return The integer key code.
     * @throws Exception if the key string is invalid.
     */
    private int stringToKeyCode(String keyString) throws Exception {
        keyString = keyString.toUpperCase();
        // Special check for mouse buttons
        switch (keyString) {
            case "MOUSE1": return -1; // Left button
            case "MOUSE2": return -2; // Right button
            case "MOUSE3": return -3; // Middle button
        }
        // Reflection logic for keyboard keys
        String fieldName = "VK_" + keyString;
        Field field = KeyEvent.class.getField(fieldName);
        return (int) field.get(null);
    }

    // --- ACTION CHECKING METHODS ---
    /**
     * Helper method to check the state of a binding (pressed, just pressed, released).
     * @param binding The KeyBinding to check.
     * @param state The desired state ("pressed", "just_pressed", "released").
     * @return True if the binding matches the state and modifiers, false otherwise.
     */
    private static boolean checkBindingState(KeyBinding binding, String state) {
        int keyCode = binding.keyCode();
        boolean isCorrectState = false;

        // Check if it's a keyboard key or mouse button
        if (keyCode >= 0) { // Keyboard
            switch (state) {
                case "pressed": isCorrectState = isKeyPressed(keyCode); break;
                case "just_pressed": isCorrectState = isKeyJustPressed(keyCode); break;
                case "released": isCorrectState = isKeyReleased(keyCode); break;
            }
        } else { // Mouse (negative codes)
            switch (state) {
                case "pressed": isCorrectState = isMouseButtonPressed(keyCode); break;
                case "just_pressed": isCorrectState = isMouseButtonJustPressed(keyCode); break;
                case "released": isCorrectState = isMouseButtonReleased(keyCode); break;
            }
        }
        
        return isCorrectState && checkModifiers(binding);
    }

    /**
     * Binds a single key to an action without modifier keys.
     * @param action The name of the action.
     * @param keyCode The key code to bind.
     */
    public void bindKey(String action, int keyCode) { keyBindings.computeIfAbsent(action, k -> new ArrayList<>()).add(new KeyBinding(keyCode, false, false, false)); }
    
    // --- PUBLIC ACTION CHECKING METHODS ---
    /**
     * Checks if an action is currently being held down.
     * @param action The name of the action (e.g., "MOVE_UP").
     * @return true if any bound key/button for the action is pressed, false otherwise.
     */
    public static boolean isActionPressed(String action) {
        List<KeyBinding> bindings = instance.keyBindings.get(action);
        if (bindings == null) return false;
        for (KeyBinding binding : bindings) {
            if (checkBindingState(binding, "pressed")) return true;
        }
        return false;
    }

    /**
     * Checks if an action was just pressed in the current frame.
     * @param action The name of the action.
     * @return true if any bound key/button for the action was just pressed, false otherwise.
     */
    public static boolean isActionJustPressed(String action) {
        List<KeyBinding> bindings = instance.keyBindings.get(action);
        if (bindings == null) return false;
        for (KeyBinding binding : bindings) {
            if (checkBindingState(binding, "just_pressed")) return true;
        }
        return false;
    }
    
    /**
     * Checks if an action was just released in the current frame.
     * @param action The name of the action.
     * @return true if any bound key/button for the action was just released, false otherwise.
     */
    public static boolean isActionReleased(String action) {
        List<KeyBinding> bindings = instance.keyBindings.get(action);
        if (bindings == null) return false;
        for (KeyBinding binding : bindings) {
            if (checkBindingState(binding, "released")) return true;
        }
        return false;
    }
    
    /**
     * Helper method that checks if the current state of modifier keys
     * matches what is required by a KeyBinding.
     * @param binding The KeyBinding to check.
     * @return true if modifiers match, false otherwise.
     */
    private static boolean checkModifiers(KeyBinding binding) {
        return binding.ctrl == instance.isCtrlDown &&
               binding.shift == instance.isShiftDown &&
               binding.alt == instance.isAltDown;
    }

    // --- LISTENERS NOW ONLY ADD EVENTS TO THE QUEUE ---
    @Override
    public void keyPressed(KeyEvent e) { eventQueue.add(new InputEvent(e.getKeyCode(), true)); }
    @Override
    public void keyReleased(KeyEvent e) { eventQueue.add(new InputEvent(e.getKeyCode(), false)); }
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) eventQueue.add(new InputEvent(-1, true));
        if (e.getButton() == MouseEvent.BUTTON2) eventQueue.add(new InputEvent(-3, true)); // Middle button is 3
        if (e.getButton() == MouseEvent.BUTTON3) eventQueue.add(new InputEvent(-2, true)); // Right button is 2
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) eventQueue.add(new InputEvent(-1, false));
        if (e.getButton() == MouseEvent.BUTTON2) eventQueue.add(new InputEvent(-3, false));
        if (e.getButton() == MouseEvent.BUTTON3) eventQueue.add(new InputEvent(-2, false));
    }

    @Override
    public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override
    public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    
    // --- PUBLIC MOUSE GETTERS ---
    /**
     * Gets the current X coordinate of the mouse cursor on the screen.
     * @return The mouse X coordinate.
     */
    public static int getMouseX() { return instance.mouseX; }
    /**
     * Gets the current Y coordinate of the mouse cursor on the screen.
     * @return The mouse Y coordinate.
     */
    public static int getMouseY() { return instance.mouseY; }
    
    /**
     * Checks if the left mouse button is currently pressed.
     * @return true if pressed, false otherwise.
     */
    public static boolean isLeftMouseButtonPressed() { return isMouseButtonPressed(-1); }
    /**
     * Checks if the right mouse button is currently pressed.
     * @return true if pressed, false otherwise.
     */
    public static boolean isRightMouseButtonPressed() { return isMouseButtonPressed(-2); }
    /**
     * Checks if the middle mouse button is currently pressed.
     * @return true if pressed, false otherwise.
     */
    public static boolean isMiddleMouseButtonPressed() { return isMouseButtonPressed(-3); }
    
    /**
     * Checks if the left mouse button was just pressed in the current frame.
     * @return true if just pressed, false otherwise.
     */
    public static boolean isLeftMouseButtonJustPressed() { return isMouseButtonJustPressed(-1); }
    /**
     * Checks if the right mouse button was just pressed in the current frame.
     * @return true if just pressed, false otherwise.
     */
    public static boolean isRightMouseButtonJustPressed() { return isMouseButtonJustPressed(-2); }
    /**
     * Checks if the middle mouse button was just pressed in the current frame.
     * @return true if just pressed, false otherwise.
     */
    public static boolean isMiddleMouseButtonJustPressed() { return isMouseButtonJustPressed(-3); }
    
    /**
     * Checks if the left mouse button was just released in the current frame.
     * @return true if just released, false otherwise.
     */
    public static boolean isLeftMouseButtonReleased() { return isMouseButtonReleased(-1); }
    /**
     * Checks if the right mouse button was just released in the current frame.
     * @return true if just released, false otherwise.
     */
    public static boolean isRightMouseButtonReleased() { return isMouseButtonReleased(-2); }
    /**
     * Checks if the middle mouse button was just released in the current frame.
     * @return true if just released, false otherwise.
     */
    public static boolean isMiddleMouseButtonReleased() { return isMouseButtonReleased(-3); }
    
    // --- DIRECT KEY CHECKING METHODS (NOW PUBLIC) ---

    /**
     * Checks directly if a physical keyboard key is currently pressed.
     * @param keyCode The KeyEvent.VK_ code of the key.
     * @return true if the key is pressed, false otherwise.
     */
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return instance.keys[keyCode];
        }
        return false;
    }

    /**
     * Checks directly if a physical keyboard key was just pressed in the current frame.
     * @param keyCode The KeyEvent.VK_ code of the key.
     * @return true if the key was just pressed, false otherwise.
     */
    public static boolean isKeyJustPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return instance.keys[keyCode] && !instance.prevKeys[keyCode];
        }
        return false;
    }
    
    /**
     * Checks directly if a physical keyboard key was just released in the current frame.
     * @param keyCode The KeyEvent.VK_ code of the key.
     * @return true if the key was just released, false otherwise.
     */
    public static boolean isKeyReleased(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return !instance.keys[keyCode] && instance.prevKeys[keyCode];
        }
        return false;
    }
    
    /**
     * Helper method to check if a mouse button is currently pressed.
     * @param buttonCode The negative button code (-1 for left, -2 for right, -3 for middle).
     * @return true if pressed, false otherwise.
     */
    private static boolean isMouseButtonPressed(int buttonCode) {
        int buttonIndex = Math.abs(buttonCode);
        if (buttonIndex > 0 && buttonIndex < instance.mouseButtons.length) {
            return instance.mouseButtons[buttonIndex];
        }
        return false;
    }

    /**
     * Helper method to check if a mouse button was just pressed in the current frame.
     * @param buttonCode The negative button code.
     * @return true if just pressed, false otherwise.
     */
    private static boolean isMouseButtonJustPressed(int buttonCode) {
        int buttonIndex = Math.abs(buttonCode);
        if (buttonIndex > 0 && buttonIndex < instance.mouseButtons.length) {
            return instance.mouseButtons[buttonIndex] && !instance.prevMouseButtons[buttonIndex];
        }
        return false;
    }

    /**
     * Helper method to check if a mouse button was just released in the current frame.
     * @param buttonCode The negative button code.
     * @return true if just released, false otherwise.
     */
    private static boolean isMouseButtonReleased(int buttonCode) {
        int buttonIndex = Math.abs(buttonCode);
        if (buttonIndex > 0 && buttonIndex < instance.mouseButtons.length) {
            return !instance.mouseButtons[buttonIndex] && instance.prevMouseButtons[buttonIndex];
        }
        return false;
    }

    /**
     * Converts screen mouse coordinates to world coordinates.
     * @param mouseX The mouse X coordinate on screen.
     * @param mouseY The mouse Y coordinate on screen.
     * @return An array containing [worldX, worldY].
     */
    public static int[] covertMousePositionToWorld(int mouseX, int mouseY) {
    	
    	int worldX = (mouseX / Engine.getSCALE()) + Engine.camera.getX();
        int worldY = (mouseY / Engine.getSCALE()) + Engine.camera.getY();
    	
    	int[] numeros = {worldX,worldY};
    	return numeros ;
    }

    /**
     * Converts the current mouse position on screen to world coordinates.
     * @return An array containing [worldX, worldY].
     */
    public static int[] covertMousePositionToWorld() {
    	
    	int worldX = (getMouseX() / Engine.getSCALE()) + Engine.camera.getX();
        int worldY = (getMouseY() / Engine.getSCALE()) + Engine.camera.getY();
    	
    	int[] numeros = {worldX,worldY};
    	return numeros ;
    }

	@Override
	public void keyTyped(KeyEvent e) {}
}