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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Engine;

public class InputManager implements KeyListener, MouseListener, MouseMotionListener {

    public static final InputManager instance = new InputManager();

    // --- SISTEMA DE FILA DE INPUTS PARA 100% DE FIABILIDADE ---
    private record InputEvent(int code, boolean pressed) {}
    private final ConcurrentLinkedQueue<InputEvent> eventQueue = new ConcurrentLinkedQueue<>();

    // --- ESTADO INTERNO (CONTROLADO PELA FILA) ---
    private final boolean[] keys = new boolean[256];
    private final boolean[] prevKeys = new boolean[256];
    private boolean isCtrlDown, isShiftDown, isAltDown;

    private int mouseX, mouseY;
    private final boolean[] mouseButtons = new boolean[4]; // 0=N/A, 1=Left, 2=Middle, 3=Right
    private final boolean[] prevMouseButtons = new boolean[4];
    
    // --- SISTEMA DE MAPEAMENTO DE AÇÕES ---
    private static final String ENGINE_BINDINGS_PATH = "/Engine/engine_keybindings.json";
    private record KeyBinding(int keyCode, boolean ctrl, boolean shift, boolean alt) {}
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
     * Processa todos os eventos de input da fila.
     * Este método deve ser chamado NO INÍCIO de cada tick do jogo no game loop.
     */
    public void pollEvents() {
        // 1. Arquiva o estado anterior
        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
        System.arraycopy(mouseButtons, 0, prevMouseButtons, 0, mouseButtons.length);
        
        // 2. Processa todos os eventos que se acumularam na fila desde a última frame
        InputEvent event;
        while ((event = eventQueue.poll()) != null) {
            if (event.code >= 0 && event.code < keys.length) { // Evento de Teclado
                keys[event.code] = event.pressed;
                if (event.code == KeyEvent.VK_CONTROL) isCtrlDown = event.pressed;
                if (event.code == KeyEvent.VK_SHIFT) isShiftDown = event.pressed;
                if (event.code == KeyEvent.VK_ALT) isAltDown = event.pressed;
            } else if (event.code < 0) { // Evento de Rato
                int buttonIndex = Math.abs(event.code);
                if (buttonIndex < mouseButtons.length) {
                    mouseButtons[buttonIndex] = event.pressed;
                }
            }
        }
    }

    // --- LÓGICA DE BINDING ---
    private int stringToKeyCode(String keyString) throws Exception {
        keyString = keyString.toUpperCase();
        // Checagem especial para botões do mouse
        switch (keyString) {
            case "MOUSE1": return -1; // Usamos números negativos para diferenciar do teclado
            case "MOUSE2": return -2; // Right button
            case "MOUSE3": return -3; // Middle button
        }
        // Lógica de reflexão para o teclado
        String fieldName = "VK_" + keyString;
        Field field = KeyEvent.class.getField(fieldName);
        return (int) field.get(null);
    }

    // --- MÉTODOS DE AÇÃO ---
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

    // --- LISTENERS AGORA APENAS ADICIONAM EVENTOS À FILA ---
    @Override
    public void keyPressed(KeyEvent e) { eventQueue.add(new InputEvent(e.getKeyCode(), true)); }
    @Override
    public void keyReleased(KeyEvent e) { eventQueue.add(new InputEvent(e.getKeyCode(), false)); }
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) eventQueue.add(new InputEvent(-1, true));
        if (e.getButton() == MouseEvent.BUTTON2) eventQueue.add(new InputEvent(-3, true)); // Middle button é 3
        if (e.getButton() == MouseEvent.BUTTON3) eventQueue.add(new InputEvent(-2, true)); // Right button é 2
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
    
    // --- Getters Públicos para o Mouse ---
    public static int getMouseX() { return instance.mouseX; }
    public static int getMouseY() { return instance.mouseY; }
    public static boolean isLeftMouseButtonPressed() { return isMouseButtonPressed(-1); }
    public static boolean isRightMouseButtonPressed() { return isMouseButtonPressed(-2); }
    public static boolean isMiddleMouseButtonPressed() { return isMouseButtonPressed(-3); }
    public static boolean isLeftMouseButtonJustPressed() { return isMouseButtonJustPressed(-1); }
    public static boolean isRightMouseButtonJustPressed() { return isMouseButtonJustPressed(-2); }
    public static boolean isMiddleMouseButtonJustPressed() { return isMouseButtonJustPressed(-3); }
    public static boolean isLeftMouseButtonReleased() { return isMouseButtonReleased(-1); }
    public static boolean isRightMouseButtonReleased() { return isMouseButtonReleased(-2); }
    public static boolean isMiddleMouseButtonReleased() { return isMouseButtonReleased(-3); }
    
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
        int buttonIndex = Math.abs(buttonCode);
        if (buttonIndex > 0 && buttonIndex < instance.mouseButtons.length) {
            return instance.mouseButtons[buttonIndex];
        }
        return false;
    }
    private static boolean isMouseButtonJustPressed(int buttonCode) {
        int buttonIndex = Math.abs(buttonCode);
        if (buttonIndex > 0 && buttonIndex < instance.mouseButtons.length) {
            return instance.mouseButtons[buttonIndex] && !instance.prevMouseButtons[buttonIndex];
        }
        return false;
    }
    private static boolean isMouseButtonReleased(int buttonCode) {
        int buttonIndex = Math.abs(buttonCode);
        if (buttonIndex > 0 && buttonIndex < instance.mouseButtons.length) {
            return !instance.mouseButtons[buttonIndex] && instance.prevMouseButtons[buttonIndex];
        }
        return false;
    }

    /**
     * @return [int worldX , int worldY]
     * */
    public static int[] covertMousePositionToWorld(int mouseX, int mouseY) {
    	
    	int worldX = (mouseX / Engine.getSCALE()) + Engine.camera.getX();
        int worldY = (mouseY / Engine.getSCALE()) + Engine.camera.getY();
    	
    	int[] numeros = {worldX,worldY};
    	return numeros ;
    }
    /**
     * @return [int worldX , int worldY]
     * */
    public static int[] covertMousePositionToWorld() {
    	
    	int worldX = (getMouseX() / Engine.getSCALE()) + Engine.camera.getX();
        int worldY = (getMouseY() / Engine.getSCALE()) + Engine.camera.getY();
    	
    	int[] numeros = {worldX,worldY};
    	return numeros ;
    }

	@Override
	public void keyTyped(KeyEvent e) {}
}
