package com.JDStudio.Engine.Input;

//Arquivo: InputManager.java

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputManager implements KeyListener {

 private boolean[] keys = new boolean[256];
 private boolean[] prevKeys = new boolean[256]; // <-- ADICIONE: Array para o estado anterior

 public static InputManager instance = new InputManager();
 private InputManager() {}
 
 /**
  * Este método deve ser chamado uma vez por frame, no início do tick principal.
  * Ele atualiza o estado anterior das teclas.
  */
 public void update() { // <-- ADICIONE ESTE MÉTODO
     // Copia o estado atual das teclas para o estado anterior
     System.arraycopy(keys, 0, prevKeys, 0, keys.length);
 }

 public static boolean isKeyPressed(int keyCode) {
     if (keyCode >= 0 && keyCode < instance.keys.length) {
         return instance.keys[keyCode];
     }
     return false;
 }
 
 /**
  * Verifica se uma tecla acabou de ser pressionada neste frame.
  * Ótimo para ações de toque único, como pular ou ativar/desativar algo.
  * @param keyCode O código da tecla a ser verificada.
  * @return true se a tecla foi pressionada neste frame, false caso contrário.
  */
 public static boolean isKeyJustPressed(int keyCode) { // <-- ADICIONE ESTE MÉTODO
     if (keyCode >= 0 && keyCode < instance.keys.length) {
         // A tecla está pressionada agora E não estava pressionada no frame anterior?
         return instance.keys[keyCode] && !instance.prevKeys[keyCode];
     }
     return false;
 }

 @Override
 public void keyPressed(KeyEvent e) {
     int keyCode = e.getKeyCode();
     if (keyCode >= 0 && keyCode < keys.length) {
         keys[keyCode] = true;
     }
 }

 @Override
 public void keyReleased(KeyEvent e) {
     int keyCode = e.getKeyCode();
     if (keyCode >= 0 && keyCode < keys.length) {
         keys[keyCode] = false;
     }
 }

 @Override
 public void keyTyped(KeyEvent e) {
     // Não utilizado
 }
}