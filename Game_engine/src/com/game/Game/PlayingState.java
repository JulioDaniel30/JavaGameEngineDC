// game
package com.game.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

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
import com.JDStudio.Engine.World.IMapLoaderListener;
import com.JDStudio.Engine.World.Tile;
import com.JDStudio.Engine.World.World;

public class PlayingState extends GameState implements IMapLoaderListener {

    public static AssetManager assets;
    public static Player player;
    private World world;
    private UIManager uiManager;
    private List<Sprite> floorSprites;
    private List<Sprite> wallSprites;
    private Random random;

    public PlayingState() {

        assets = new AssetManager();
        floorSprites = new ArrayList<>();
        wallSprites = new ArrayList<>();
        random = new Random();
        loadAssets(); 

        uiManager = new UIManager();
        player = new Player(0, 0, 16, 16);
        world = new World("/map1.json", this); 
        player.setWorld(world);
        this.addGameObject(player);
        setupUI();
        Sound.loop("/music.wav");
    }

    private void loadAssets() {
        Spritesheet worldSheet = new Spritesheet("/spritesheet.png");
        

        // Registra os mesmos sprites com nomes para o modo BY_TILE_ID
        assets.registerSprite("floor_1", worldSheet.getSprite(0, 0, 16, 16));
        //assets.registerSprite("floor_2", worldSheet.getSprite(0, 16, 16, 16));
        assets.registerSprite("wall_1", worldSheet.getSprite(16, 0, 16, 16));
        
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
    
    private void setupUI() {
        uiManager.addElement(new UIText(5, 15, new Font("Arial", Font.BOLD, 12), Color.WHITE, () -> "Vida: " + (int)player.life + "/" + (int)player.maxLife));
    }

    @Override
    public void tick() {
        if (InputManager.isKeyJustPressed(KeyEvent.VK_9)) {
            Engine.isDebug = !Engine.isDebug;
        }
        if (InputManager.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            Engine.camera.shake(4, 15);
        }

        for (int i = 0; i < gameObjects.size(); i++) { gameObjects.get(i).tick(); }

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);
                if (GameObject.isColliding(obj1, obj2)) {
                    obj1.onCollision(obj2);
                    obj2.onCollision(obj1);
                }
            }
        }

        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            if (gameObjects.get(i).isDestroyed) {
                gameObjects.remove(i);
            }
        }
        
        Engine.camera.update(player, world);
        InputManager.instance.update();
    }

    @Override
    public void render(Graphics g) {
        world.render(g);
        for (GameObject go : gameObjects) {
            go.render(g);
        }
        uiManager.render(g);
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

    @Override
    public Tile onTileFound(String layerName, int tileId, int x, int y) {
        
        // 1º NÍVEL DA HIERARQUIA: Filtra pela LAYER
        switch (layerName) {
            
            case "CamadaDeChao":
                // 2º NÍVEL DA HIERARQUIA: Filtra pelo TILE ID dentro da layer "Floor"
                switch (tileId) {
                    case 20: // ID de teste
                        return new FloorTile(x, y, assets.getSprite("wall_1"));
                    
                    // Adicione outros 'case' para mais variações de chão aqui...

                    default: // Para qualquer outro tile ID na layer "Floor", usa o padrão
                        return new FloorTile(x, y, assets.getSprite("floor_1"));
                }

            case "CamadaDeParedes":
                // 2º NÍVEL DA HIERARQUIA: Filtra pelo TILE ID dentro da layer "Walls"
                switch (tileId) {
                    case 18: // ID específico para a parede com tocha
                        return new WallTile(x, y, assets.getSprite("wall_1"));// preguiça de fazer uma nova sprite
                        
                    // Adicione outros 'case' para mais variações de parede aqui...

                    default: // Para qualquer outro tile ID na layer "Walls", usa o padrão
                        return new WallTile(x, y, assets.getSprite("wall_1"));
                }

            default:
                // Ignora qualquer outra camada que não seja "Floor" ou "Walls"
                return null;
        }
    }
}
