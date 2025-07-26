package com.JDStudio.Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.World.Camera;
import com.JDStudio.Engine.World.Tile;

// A herança correta, sem o ".World" duplicado
public class World extends com.JDStudio.Engine.World.World {

    // O construtor agora só precisa do 'PlayingState' para acessar o AssetManager
    public World(PlayingState state) {
        // Chama o construtor da classe pai (engine.World)
        super(0, 0);

        try {
            // O mapa ainda pode ser usado para definir a estrutura do mundo
            BufferedImage map = ImageIO.read(getClass().getResource("/level1.png")); //
            
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

                    // **A MUDANÇA PRINCIPAL ESTÁ AQUI**
                    // Agora, pegamos os sprites do AssetManager usando a chave (key)
                    
                    // O chão é sempre colocado primeiro
                    tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, PlayingState.assets.getSprite("tile_floor"));

                    if (pixelAtual == 0xFFFFFFFF) { // Parede
                        tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, PlayingState.assets.getSprite("tile_wall"));
                    } else if (pixelAtual == 0xFF0026FF) { // Player
                        PlayingState.player.setX(xx * 16);
                        PlayingState.player.setY(yy * 16);
                    } else if (pixelAtual == 0xFFFF0000) { // Inimigo
                        state.addGameObject(new Enemy(xx * 16, yy * 16, 16, 16, PlayingState.assets.getSprite("enemy")));
                    } else if (pixelAtual == 0xFFFF6A00) { // Arma
                        state.addGameObject(new Weapon(xx * 16, yy * 16, 16, 16, PlayingState.assets.getSprite("weapon")));
                    } else if (pixelAtual == 0xFFFF7F7F) { // Vida
                        state.addGameObject(new Lifepack(xx * 16, yy * 16, 16, 16, PlayingState.assets.getSprite("lifepack")));
                    } else if (pixelAtual == 0xFFFFD800) { // Munição
                        state.addGameObject(new Bullet(xx * 16, yy * 16, 16, 16, PlayingState.assets.getSprite("bullet")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // O método render está correto e não precisa de mudanças.
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
                Tile tile = getTile(xx, yy); // Usando o getter da classe pai
                if (tile != null) {
                    tile.render(g);
                }
            }
        }
    }
}