package com.JDStudio.Engine.Input;

//Arquivo: InputManager.java

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
* Gerencia todo o input do teclado de forma centralizada.
* Permite que qualquer objeto do jogo consulte o estado de uma tecla
* a qualquer momento.
* * @author Seu Nome
* @version 1.0
*/
public class InputManager implements KeyListener {

 // Array para guardar o estado de todas as teclas (256 para cobrir os códigos ASCII e VK)
 private boolean[] keys = new boolean[256];

 // Instância única (Singleton pattern) para acesso global
 public static InputManager instance = new InputManager();
 
 // Construtor privado para garantir que apenas uma instância exista
 private InputManager() {}

 /**
  * Verifica se uma tecla específica está pressionada no momento.
  * * @param keyCode O código da tecla a ser verificada (ex: KeyEvent.VK_W).
  * @return true se a tecla estiver pressionada, false caso contrário.
  */
 public static boolean isKeyPressed(int keyCode) {
     // Validação para evitar erros de array index out of bounds
     if (keyCode >= 0 && keyCode < instance.keys.length) {
         return instance.keys[keyCode];
     }
     return false;
 }
 
 @Override
 public void keyTyped(KeyEvent e) {
     // Não utilizado no momento
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
}