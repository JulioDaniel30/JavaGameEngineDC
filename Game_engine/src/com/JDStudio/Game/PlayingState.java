package com.JDStudio.Game;



import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.GameState;
import com.JDStudio.Engine.Graphics.Spritesheet;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.Camera;

public class PlayingState extends GameState {

    public static Spritesheet spritesheet;
    public static Player player;
    private List<GameObject> gameObjects;
    private World world;

    // Sprites estáticos para entidades
    public static BufferedImage TILE_FLOOR;
    public static BufferedImage TILE_WALL;
    public static BufferedImage LIFEPACK_EN;
    public static BufferedImage WEAPON_EN;
    public static BufferedImage BULLET_EN;
    public static BufferedImage ENEMY_EN;
    public static BufferedImage PLAYER_SPRITE;

    public PlayingState() {
        gameObjects = new ArrayList<>();
        
        // Carregar todos os recursos gráficos aqui
        spritesheet = new Spritesheet("/spritesheet.png");
        TILE_FLOOR = spritesheet.getSprite(0, 0, 16, 16);
        TILE_WALL = spritesheet.getSprite(16, 0, 16, 16);
        PLAYER_SPRITE = spritesheet.getSprite(32, 0, 16, 16);
        LIFEPACK_EN = spritesheet.getSprite(6 * 16, 0, 16, 16);
        WEAPON_EN = spritesheet.getSprite(7 * 16, 0, 16, 16);
        BULLET_EN = spritesheet.getSprite(6 * 16, 16, 16, 16);
        ENEMY_EN = spritesheet.getSprite(7 * 16, 16, 16, 16);

        // Criar objetos essenciais
        player = new Player(0, 0, 16, 16, PLAYER_SPRITE);
        this.addGameObject(player);

        world = new World("/level1.png", this);
    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).tick();
        }
        
        // Lógica da Câmera
        Camera.x = Camera.clamp(player.getX() - (Engine.WIDTH / 2), 0, world.WIDTH * 16 - Engine.WIDTH);
        Camera.y = Camera.clamp(player.getY() - (Engine.HEIGHT / 2), 0, world.HEIGHT * 16 - Engine.HEIGHT);
    }

    @Override
    public void render(Graphics g) {
        world.render(g);
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).render(g);
        }
    }

    
}