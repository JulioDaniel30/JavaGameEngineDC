package com.JDStudio.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import org.json.JSONArray;
import org.json.JSONObject;

// Importações corretas dos pacotes da sua Engine
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.GameState;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.UIManager;
import com.JDStudio.Engine.Graphics.UI.UIText;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.World.Camera;
import com.JDStudio.Engine.World.IMapLoaderListener;
import com.JDStudio.Engine.World.Tile;
import com.JDStudio.Engine.World.World;

public class PlayingState extends GameState implements IMapLoaderListener {

    public static AssetManager assets;
    public static Player player;
    private World world;
    private UIManager uiManager; // A declaração está correta

    public PlayingState() {
        assets = new AssetManager();
        loadAssets();

        // Inicializa o UIManager
        uiManager = new UIManager();
        
        player = new Player(0, 0, 16, 16);
        world = new World("/map1.json", this); // Usa o World da engine
        player.setWorld(world);
        
        this.addGameObject(player); // Agora este método funciona, pois está na classe pai
        
        setupUI(); // Configura a UI
        Sound.loop("/music.wav");
        
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
        
        
        Sprite player_idle_frame1 = worldSheet.getSprite(32, 0, 16, 16);
        assets.registerSprite("player_idle", player_idle_frame1);
        
        Sprite player_walk_right_frame1 = worldSheet.getSprite(48, 0, 16, 16);
        Sprite player_walk_right_frame2 = worldSheet.getSprite(64, 0, 16, 16);
        Sprite player_walk_right_frame3 = worldSheet.getSprite(80, 0, 16, 16);
        assets.registerSprite("player_walk_right_1", player_walk_right_frame1);
        assets.registerSprite("player_walk_right_2", player_walk_right_frame2);
        assets.registerSprite("player_walk_right_3", player_walk_right_frame3);
        
        Sprite player_walk_left_frame1 = worldSheet.getSprite(48, 16, 16, 16);
        Sprite player_walk_left_frame2 = worldSheet.getSprite(64, 16, 16, 16);
        Sprite player_walk_left_frame3 = worldSheet.getSprite(80, 16, 16, 16);
        assets.registerSprite("player_walk_left_1", player_walk_left_frame1);
        assets.registerSprite("player_walk_left_2", player_walk_left_frame2);
        assets.registerSprite("player_walk_left_3", player_walk_left_frame3);
        
        
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
        if (InputManager.isKeyJustPressed(KeyEvent.VK_9)) { // Alterado para F9 para evitar conflitos
            Engine.isDebug = !Engine.isDebug;
            System.out.println(Engine.isDebug);
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
                	if ((obj1 instanceof Player && obj2 instanceof Lifepack) || 
                            (obj1 instanceof Lifepack && obj2 instanceof Player)) {
                            
                            // Use o novo método utilitário!
                            Player player = GameObject.getInstanceOf(Player.class, obj1, obj2);
                            Lifepack lifepack = GameObject.getInstanceOf(Lifepack.class, obj1, obj2);
                            gameObjects.remove(lifepack);
                            
                            break;
                	
                	}
                }
            }
        }
        
        if (InputManager.isKeyJustPressed(KeyEvent.VK_PLUS) || InputManager.isKeyJustPressed(KeyEvent.VK_ADD)) {
            float newVolume = Sound.getMusicVolume() + 0.01f;
            Sound.setMusicVolume(newVolume);
            System.out.println("Volume da Música: " + (int)(Sound.getMusicVolume() * 100) + "%");
        }
        
        // Diminui o volume da música
        if (InputManager.isKeyJustPressed(KeyEvent.VK_MINUS) || InputManager.isKeyJustPressed(KeyEvent.VK_SUBTRACT)) {
            float newVolume = Sound.getMusicVolume() - 0.01f;
            Sound.setMusicVolume(newVolume);
            System.out.println("Volume da Música: " + (int)(Sound.getMusicVolume() * 100) + "%");
        }
        
        updateCamera();
        
        InputManager.instance.update();
    }

  

    @Override
    public Tile onTileFound(int tileId, int x, int y) {
        // CORREÇÃO 1: Usar o ID correto para a parede (2), conforme o seu map1.json
    	System.out.println("ontilefound");
    	System.out.println("\n" + tileId);
        if (tileId == 2) {
        	System.out.println("id 2");
            return new WallTile(x, y, PlayingState.assets.getSprite("tile_wall"));
        } else if (tileId == 1) { // O ID 1 é o chão
        	System.out.println("id 1");
            return new FloorTile(x, y, PlayingState.assets.getSprite("tile_floor"));
        }
        return null;
    }

    @Override
    public void onObjectFound(String type, int x, int y, JSONObject properties) {
        String objectName = properties.getString("name");

        if (objectName.equals("player_start")) {
            player.setX(x);
            player.setY(y);
        } else if (objectName.equals("enemy")) {
            this.addGameObject(new Enemy(x, y, 16, 16, PlayingState.assets.getSprite("enemy")));
        } else if (objectName.equals("lifepack")) {
            this.addGameObject(new Lifepack(x, y, 16, 16, PlayingState.assets.getSprite("lifepack")));
        } else if (objectName.equals("weapon")) {
            this.addGameObject(new Weapon(x, y, 16, 16, PlayingState.assets.getSprite("weapon")));
        } else if (objectName.equals("bullet")) {
            this.addGameObject(new Bullet(x, y, 16, 16, PlayingState.assets.getSprite("bullet")));
        }
    }
    
    private void updateCamera() {
        int mapPixelWidth = world.WIDTH * World.TILE_SIZE;
        int mapPixelHeight = world.HEIGHT * World.TILE_SIZE;
        Camera.x = Camera.clamp(player.getX() - (Engine.WIDTH / 2), 0, mapPixelWidth - Engine.WIDTH);
        Camera.y = Camera.clamp(player.getY() - (Engine.HEIGHT / 2), 0, mapPixelHeight - Engine.HEIGHT);
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