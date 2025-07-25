package com.JDStudio.Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.World.Camera;
import com.JDStudio.Engine.World.Tile;

//A classe do seu jogo estende a classe da engine
public class World extends com.JDStudio.Engine.World.World {

 // Este construtor agora corresponde à chamada em PlayingState.java
 public World(String path, PlayingState state) {
     // Chame o construtor da classe pai (engine.World)
     super(0, 0); 
     
     try {
         BufferedImage map = ImageIO.read(getClass().getResource(path));
         // Define as variáveis da classe pai
         this.WIDTH = map.getWidth();
         this.HEIGHT = map.getHeight();
         this.tiles = new Tile[map.getWidth() * map.getHeight()];
         
         int[] pixels = new int[map.getWidth() * map.getHeight()];
         map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

         // Loop para carregar o mapa
         for (int xx = 0; xx < map.getWidth(); xx++) {
             for (int yy = 0; yy < map.getHeight(); yy++) {
                 int pixelAtual = pixels[xx + (yy * map.getWidth())];
                 tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, PlayingState.TILE_FLOOR);

                 if (pixelAtual == 0xFFFFFFFF) { // Parede
                     tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, PlayingState.TILE_WALL);
                 } else if (pixelAtual == 0xFF0026FF) { // Player
                     PlayingState.player.setX(xx * 16);
                     PlayingState.player.setY(yy * 16);
                 } else if (pixelAtual == 0xFFFF0000) { // Inimigo
                     // Usa a referência 'state' para adicionar o inimigo à lista de objetos do jogo
                     state.addGameObject(new Enemy(xx * 16, yy * 16, 16, 16, PlayingState.ENEMY_EN));
                 } else if (pixelAtual == 0xFFFF6A00) { // Arma
                      state.addGameObject(new Weapon(xx * 16, yy * 16, 16, 16, PlayingState.WEAPON_EN));
                 } else if (pixelAtual == 0xFFFF7F7F) { // Vida
                      state.addGameObject(new Lifepack(xx * 16, yy * 16, 16, 16, PlayingState.LIFEPACK_EN));
                 } else if (pixelAtual == 0xFFFFD800) { // Munição
                      state.addGameObject(new Bullet(xx * 16, yy * 16, 16, 16, PlayingState.BULLET_EN));
                 }
             }
         }

     } catch (IOException e) {
         e.printStackTrace();
     }
 }

 // O método render agora deve ser adicionado aqui para desenhar os tiles
 public void render(Graphics g) {
     int xstart = Camera.x >> 4;
     int ystart = Camera.y >> 4;
     int xfinal = xstart + (Engine.WIDTH >> 4);
     int yfinal = ystart + (Engine.HEIGHT >> 4);

     for (int xx = xstart; xx <= xfinal; xx++) {
         for (int yy = ystart; yy <= yfinal; yy++) {
             if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
                 continue;
             }
             Tile tile = tiles[xx + (yy * WIDTH)];
             tile.render(g);
         }
     }
 }
}