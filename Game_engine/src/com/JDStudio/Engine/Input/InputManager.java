// engine
package com.JDStudio.Engine.Input;

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

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Engine;

public class InputManager implements  KeyListener, MouseListener, MouseMotionListener {

	public static final InputManager instance = new InputManager();

    // --- NOVA CONSTANTE PARA O ARQUIVO DA ENGINE ---
    private static final String ENGINE_BINDINGS_PATH = "/Engine/engine_keybindings.json";

 // --- Estrutura de dados interna para um Vínculo de Tecla ---
    private record KeyBinding(int keyCode, boolean ctrl, boolean shift, boolean alt) {}

 // --- ESTADO DO TECLADO ---
    private final boolean[] keys = new boolean[256];
    private final boolean[] prevKeys = new boolean[256];
    private boolean isCtrlDown, isShiftDown, isAltDown;

    // --- ESTADO DO MOUSE (movido do MouseManager) ---
    private int mouseX, mouseY;
    private boolean leftButtonPressed, rightButtonPressed, middleButtonPressed;
    private boolean prevLeftButtonPressed, prevRightButtonPressed, prevMiddleButtonPressed;
    
    // Mapa de Vínculos (inalterado)
    private final Map<String, List<KeyBinding>> keyBindings = new HashMap<>();

    private InputManager() {
        loadAndMergeBindings(ENGINE_BINDINGS_PATH);
    }

    public static InputManager getInstance() {
        return instance;
    }
    public void loadAndMergeBindings(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("Aviso: Arquivo de vínculos '" + resourcePath + "' não encontrado. Pulando.");
                return;
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);
            
            for (String action : json.keySet()) {
                keyBindings.put(action, new ArrayList<>()); // Limpa vínculos antigos para esta ação
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
                        System.err.println("Aviso: Tecla '" + keyString + "' no arquivo '" + resourcePath + "' é inválida.");
                    }
                }
            }
            System.out.println("Vínculos de teclas (com combinações) fundidos com sucesso de: " + resourcePath);
        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo de vínculos '" + resourcePath + "'.");
            e.printStackTrace();
        }
    }
    
    
    /**
     * Atualiza o estado de TODOS os inputs para o próximo quadro.
     */
    public void update() {
        // Teclado
        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
        // Mouse
        prevLeftButtonPressed = leftButtonPressed;
        prevRightButtonPressed = rightButtonPressed;
        prevMiddleButtonPressed = middleButtonPressed;
    }

    // --- LÓGICA DE BINDING ATUALIZADA ---

    private int stringToKeyCode(String keyString) throws Exception {
        keyString = keyString.toUpperCase();
        // Checagem especial para botões do mouse
        switch (keyString) {
            case "MOUSE1": return -1; // Usamos números negativos para diferenciar do teclado
            case "MOUSE2": return -2;
            case "MOUSE3": return -3;
        }
        // Lógica de reflexão para o teclado
        String fieldName = "VK_" + keyString;
        Field field = KeyEvent.class.getField(fieldName);
        return (int) field.get(null);
    }

    // --- MÉTODOS DE AÇÃO (ATUALIZADOS PARA SUPORTAR MOUSE) ---

    private static boolean checkBindingState(KeyBinding binding, String state) {
        int keyCode = binding.keyCode();
        boolean isCorrectState = false;

        // Verifica se é uma tecla de teclado ou de mouse
        if (keyCode >= 0) { // Teclado
            switch (state) {
                case "pressed": isCorrectState = isKeyPressed(keyCode); break;
                case "just_pressed": isCorrectState = isKeyJustPressed(keyCode); break;
                case "released": isCorrectState = isKeyReleased(keyCode); break;
            }
        } else { // Mouse (códigos negativos)
            switch (state) {
                case "pressed": isCorrectState = isMouseButtonPressed(keyCode); break;
                case "just_pressed": isCorrectState = isMouseButtonJustPressed(keyCode); break;
                case "released": isCorrectState = isMouseButtonReleased(keyCode); break;
            }
        }
        
        return isCorrectState && checkModifiers(binding);
    }


    public void bindKey(String action, int keyCode) { keyBindings.computeIfAbsent(action, k -> new ArrayList<>()).add(new KeyBinding(keyCode, false, false, false)); }
    // --- MÉTODOS DE VERIFICAÇÃO DE AÇÃO ---

    public static boolean isActionPressed(String action) {
        List<KeyBinding> bindings = instance.keyBindings.get(action);
        if (bindings == null) return false;
        for (KeyBinding binding : bindings) {
            if (checkBindingState(binding, "pressed")) return true;
        }
        return false;
    }

    public static boolean isActionJustPressed(String action) {
        List<KeyBinding> bindings = instance.keyBindings.get(action);
        if (bindings == null) return false;
        for (KeyBinding binding : bindings) {
            if (checkBindingState(binding, "just_pressed")) return true;
        }
        return false;
    }
    
    public static boolean isActionReleased(String action) {
        List<KeyBinding> bindings = instance.keyBindings.get(action);
        if (bindings == null) return false;
        for (KeyBinding binding : bindings) {
            if (checkBindingState(binding, "released")) return true;
        }
        return false;
    }
    
    /**
     * Método auxiliar que verifica se o estado atual dos modificadores
     * corresponde ao exigido por um KeyBinding.
     */
    private static boolean checkModifiers(KeyBinding binding) {
        return binding.ctrl == instance.isCtrlDown &&
               binding.shift == instance.isShiftDown &&
               binding.alt == instance.isAltDown;
    }

    
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) leftButtonPressed = true;
        if (e.getButton() == MouseEvent.BUTTON2) middleButtonPressed = true;
        if (e.getButton() == MouseEvent.BUTTON3) rightButtonPressed = true;
    }
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) leftButtonPressed = false;
        if (e.getButton() == MouseEvent.BUTTON2) middleButtonPressed = false;
        if (e.getButton() == MouseEvent.BUTTON3) rightButtonPressed = false;
    }
    public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
 // --- Getters Públicos para o Mouse ---
    public static int getMouseX() { return instance.mouseX; }
    public static int getMouseY() { return instance.mouseY; }
    public static boolean isLeftMouseButtonPressed() { return instance.leftButtonPressed; }
    public static boolean isRightMouseButtonPressed() { return instance.rightButtonPressed; }
    public static boolean isLeftMouseButtonJustPressed() { return instance.leftButtonPressed && !instance.prevLeftButtonPressed; }
    public static boolean isRightMouseButtonJustPressed() { return instance.rightButtonPressed && !instance.prevRightButtonPressed; }
    public static boolean isLeftMouseButtonReleased() { return !instance.leftButtonPressed && instance.prevLeftButtonPressed; }
    
    // --- MÉTODOS DE VERIFICAÇÃO DE TECLA DIRETA (AGORA PÚBLICOS) ---

    /**
     * Verifica diretamente se uma tecla física está pressionada.
     */
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return instance.keys[keyCode];
        }
        return false;
    }

    /**
     * Verifica diretamente se uma tecla física acabou de ser pressionada.
     */
    public static boolean isKeyJustPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return instance.keys[keyCode] && !instance.prevKeys[keyCode];
        }
        return false;
    }
    
    /**
     * Verifica diretamente se uma tecla física foi solta neste exato quadro.
     */
    public static boolean isKeyReleased(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return !instance.keys[keyCode] && instance.prevKeys[keyCode];
        }
        return false;
    }
    
    private static boolean isMouseButtonPressed(int buttonCode) {
        if (buttonCode == -1) return instance.leftButtonPressed;
        if (buttonCode == -2) return instance.rightButtonPressed;
        if (buttonCode == -3) return instance.middleButtonPressed;
        return false;
    }
    private static boolean isMouseButtonJustPressed(int buttonCode) {
        if (buttonCode == -1) return instance.leftButtonPressed && !instance.prevLeftButtonPressed;
        if (buttonCode == -2) return instance.rightButtonPressed && !instance.prevRightButtonPressed;
        if (buttonCode == -3) return instance.middleButtonPressed && !instance.prevMiddleButtonPressed;
        return false;
    }
    private static boolean isMouseButtonReleased(int buttonCode) {
        if (buttonCode == -1) return !instance.leftButtonPressed && instance.prevLeftButtonPressed;
        if (buttonCode == -2) return !instance.rightButtonPressed && instance.prevRightButtonPressed;
        if (buttonCode == -3) return !instance.middleButtonPressed && instance.prevMiddleButtonPressed;
        return false;
    }
    /**
     * @return [int worldX , int worldY]
     * */
    public static int[] covertMousePositionToWorld(int mouseX, int mouseY) {
    	
    	int worldX = (mouseX / Engine.SCALE) + Engine.camera.getX();
        int worldY = (mouseY / Engine.SCALE) + Engine.camera.getY();
    	
    	int[] numeros = {worldX,worldY};
    	return numeros ;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < keys.length) keys[keyCode] = true;

        // Atualiza o estado dos modificadores
        if (keyCode == KeyEvent.VK_CONTROL) isCtrlDown = true;
        if (keyCode == KeyEvent.VK_SHIFT) isShiftDown = true;
        if (keyCode == KeyEvent.VK_ALT) isAltDown = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < keys.length) keys[keyCode] = false;

        // Atualiza o estado dos modificadores
        if (keyCode == KeyEvent.VK_CONTROL) isCtrlDown = false;
        if (keyCode == KeyEvent.VK_SHIFT) isShiftDown = false;
        if (keyCode == KeyEvent.VK_ALT) isAltDown = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}