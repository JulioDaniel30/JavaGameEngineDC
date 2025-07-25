package com.JDStudio.Engine.World;


public class World {

 protected Tile[] tiles;
 public int WIDTH, HEIGHT;

 public World(int width, int height) {
     this.WIDTH = width;
     this.HEIGHT = height;
     this.tiles = new Tile[width * height];
 }
 
 public Tile getTile(int x, int y) {
     if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
         // Retorna um tile sólido padrão para fora dos limites do mapa
         return new Tile(x, y, null) {{ isSolid = true; }}; 
     }
     return tiles[x + (y * WIDTH)];
 }

 /**
  * Verifica se uma determinada área está livre ou se contém um tile sólido.
  * Esta versão respeita a máscara de colisão da entidade.
  * @param x A coordenada X da entidade.
  * @param y A coordenada Y da entidade.
  * @param maskX O deslocamento X da máscara de colisão.
  * @param maskY O deslocamento Y da máscara de colisão.
  * @param maskWidth A largura da máscara.
  * @param maskHeight A altura da máscara.
  * @return true se a área estiver livre, false se houver um tile sólido.
  */
 public boolean isFree(int x, int y, int maskX, int maskY, int maskWidth, int maskHeight) {
     // Calcula as coordenadas absolutas da caixa de colisão
     int startX = x + maskX;
     int startY = y + maskY;

     // Calcula as coordenadas dos 4 cantos da máscara em relação à grade de tiles
     int x1 = startX / 16;
     int y1 = startY / 16;

     int x2 = (startX + maskWidth - 1) / 16;
     int y2 = (startY + maskHeight - 1) / 16;

     // Verifica os 4 cantos da máscara de colisão
     if (getTile(x1, y1).isSolid || 
         getTile(x2, y1).isSolid ||
         getTile(x1, y2).isSolid ||
         getTile(x2, y2).isSolid) {
         return false;
     }

     return true;
 }
}