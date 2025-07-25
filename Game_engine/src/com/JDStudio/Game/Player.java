package com.JDStudio.Game;

import java.awt.event.KeyEvent; // Importe para usar as constantes VK_*
import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;

public class Player extends GameObject {

 // REMOVA as variáveis boolean: right, left, up, down
 // public boolean right, left, up, down; 
 public double speed = 1.4;

 public Player(double x, double y, int width, int height, BufferedImage sprite) {
     super(x, y, width, height, sprite);
 }

 @Override
 public void tick() {
     // A lógica de movimento agora consulta o InputManager DIRETAMENTE
     if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT) || InputManager.isKeyPressed(KeyEvent.VK_D)) {
         x += speed;
     } else if (InputManager.isKeyPressed(KeyEvent.VK_LEFT) || InputManager.isKeyPressed(KeyEvent.VK_A)) {
         x -= speed;
     }

     if (InputManager.isKeyPressed(KeyEvent.VK_UP) || InputManager.isKeyPressed(KeyEvent.VK_W)) {
         y -= speed;
     } else if (InputManager.isKeyPressed(KeyEvent.VK_DOWN) || InputManager.isKeyPressed(KeyEvent.VK_S)) {
         y += speed;
     }
 }
}