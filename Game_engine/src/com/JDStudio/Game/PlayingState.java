package com.JDStudio.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

// Importações corretas dos pacotes da sua Engine
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.GameState;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.UIManager;
import com.JDStudio.Engine.Graphics.UI.UIText;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.Camera;

public class PlayingState extends GameState {

    public static AssetManager assets;
    public static Player player;
    private World world;
    private UIManager uiManager; // A declaração está correta

    public PlayingState() {
        assets = new AssetManager();
        loadAssets();

        // Inicializa o UIManager
        uiManager = new UIManager();
        
        player = new Player(0, 0, 16, 16, assets.getSprite("player"));
        world = new World(this);
        
        player.setWorld(world);
        this.addGameObject(player); // Agora este método funciona, pois está na classe pai
        
        setupUI(); // Configura a UI
    }

    private void loadAssets() {
        System.out.println("Carregando assets da Spritesheet...");
        Spritesheet worldSheet = new Spritesheet("/spritesheet.png"); //

        assets.registerSprite("tile_floor", worldSheet.getSprite(0, 0, 16, 16));
        assets.registerSprite("tile_wall", worldSheet.getSprite(16, 0, 16, 16));
        assets.registerSprite("player", worldSheet.getSprite(32, 0, 16, 16));
        assets.registerSprite("enemy", worldSheet.getSprite(7 * 16, 16, 16, 16));
        assets.registerSprite("weapon", worldSheet.getSprite(7 * 16, 0, 16, 16));
        assets.registerSprite("lifepack", worldSheet.getSprite(6 * 16, 0, 16, 16));
        assets.registerSprite("bullet", worldSheet.getSprite(6 * 16, 16, 16, 16));
    }

    // 2. O método setupUI agora está preenchido e funcional
    private void setupUI() {
        UIText lifeText = new UIText(
            5, 15,
            new Font("Arial", Font.BOLD, 12),
            Color.WHITE,
            () -> "Vida: " + (int)player.life + "/" + (int)player.maxLife
        );
        uiManager.addElement(lifeText);
    }

    // O método addGameObject não é mais necessário aqui, pois foi movido para GameState
    // public void addGameObject(GameObject go) { ... }

    @Override
    public void tick() {
        if (InputManager.isKeyJustPressed(KeyEvent.VK_F9)) { // Alterado para F9 para evitar conflitos
            Engine.isDebug = !Engine.isDebug;
        }

        // A lógica de tick dos objetos, colisões, etc. continua aqui...
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).tick();
        }

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);
                if (GameObject.isColliding(obj1, obj2)) {
                    // Lógica de colisão (Player-Enemy, etc.)
                	if ((obj1 instanceof Player && obj2 instanceof Enemy) || 
                            (obj1 instanceof Enemy && obj2 instanceof Player)) {
                            
                            // Use o novo método utilitário!
                            Player player = GameObject.getInstanceOf(Player.class, obj1, obj2);
                            Enemy enemy = GameObject.getInstanceOf(Enemy.class, obj1, obj2);
                	
                	}
                }
            }
        }
        
        Camera.x = Camera.clamp(player.getX() - (Engine.WIDTH / 2), 0, world.WIDTH * 16 - Engine.WIDTH);
        Camera.y = Camera.clamp(player.getY() - (Engine.HEIGHT / 2), 0, world.HEIGHT * 16 - Engine.HEIGHT);
        
        InputManager.instance.update();
    }

    @Override
    public void render(Graphics g) {
        // Renderiza o mundo e os objetos
        world.render(g);
        for (GameObject go : gameObjects) {
            go.render(g);
        }

        // 3. RENDERIZA A UI POR ÚLTIMO
         uiManager.render(g);
    }
}