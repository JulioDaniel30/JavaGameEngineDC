// game
package com.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.UIManager;
import com.JDStudio.Engine.Graphics.UI.UIText;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.Interactable;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.States.EnginePlayingState;
import com.JDStudio.Engine.World.IMapLoaderListener;
import com.JDStudio.Engine.World.Tile;
import com.JDStudio.Engine.World.World;
@SuppressWarnings("static-access")
public class PlayingState extends EnginePlayingState implements IMapLoaderListener {

    public static AssetManager assets;
    public static Player player;
    public static World world;
    private UIManager uiManager;

	public PlayingState() {
        assets = new AssetManager();
        loadAssets(); 

        uiManager = new UIManager();
        
        // 1. Cria o jogador
        player = new Player(0, 0, 16, 16);
        this.addGameObject(player);
        
        // 2. Cria o mundo (isso chamará onObjectFound e criará os outros objetos, como inimigos)
        world = new World("/map1.json", this); 
        
        // 3. Agora que TODOS os objetos existem, configure seus componentes
        for (GameObject go : this.gameObjects) {
            if (go.movement != null) {
                go.movement.setWorld(world);
                go.movement.setGameObjects(this.gameObjects);
            }
            if (go instanceof Door) {
                ((Door) go).setGameObjects(this.gameObjects);
            }
        }
        
        setupUI();
        Sound.loop("/music.wav");
        Sound.setMusicVolume(0.01f);
        Sound.setSfxVolume(0.02f);
    }
    private void loadAssets() {
        Spritesheet worldSheet = new Spritesheet("/spritesheet.png");
        

        // Registra os mesmos sprites com nomes para o modo BY_TILE_ID
        assets.registerSprite("floor_1", worldSheet.getSprite(0, 0, 16, 16));
        assets.registerSprite("floor_2", worldSheet.getSprite(0, 48, 16, 16));
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
		
		assets.registerSprite("door_closed", worldSheet.getSprite(32, 32, 16, 16)); // Exemplo de coordenada
		assets.registerSprite("door_open", worldSheet.getSprite(64, 32, 16, 16)); // Exemplo de coordenada
		
		assets.registerSprite("door_frame_1", worldSheet.getSprite(32, 32, 16, 16)); // Frame 1: Fechada
	    assets.registerSprite("door_frame_2", worldSheet.getSprite(48, 32, 16, 16)); // Frame 2: Meio-aberta
	    assets.registerSprite("door_frame_3", worldSheet.getSprite(64, 32, 16, 16)); // Frame 3: Aberta
    }
    
    private void setupUI() {
        uiManager.addElement(new UIText(5, 15, new Font("Arial", Font.BOLD, 12), Color.WHITE, () -> "Vida: " + (int)player.life + "/" + (int)player.maxLife));
    }

    @Override
    public void tick() {
    	keyboardEventsUpdate();
        for (int i = 0; i < gameObjects.size(); i++) { gameObjects.get(i).tick();        }

        collisionsUpdate();

        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            if (gameObjects.get(i).isDestroyed) {
                gameObjects.remove(i);
            }
        }
        
        Engine.camera.update(player, world);
        InputManager.instance.update();
    }
    
    private void collisionsUpdate() {
    	
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
    }
    
    private void keyboardEventsUpdate () {
    	if (InputManager.isKeyJustPressed(KeyEvent.VK_9)) {
            Engine.isDebug = !Engine.isDebug;
        }
        if (InputManager.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            Engine.camera.shake(4, 15);
        }
        
        if (InputManager.isKeyJustPressed(KeyEvent.VK_E)) {
		    for (GameObject go : gameObjects) {
		        if (go instanceof Interactable) {
		            Interactable interactableObject = (Interactable) go;
		            
		            // Pega o raio de interação específico deste objeto
		            int interactionRadius = interactableObject.getInteractionRadius();
		
		            int pCenterX = player.getX() + player.getWidth() / 2;
		            int pCenterY = player.getY() + player.getHeight() / 2;
		            int goCenterX = go.getX() + go.getWidth() / 2;
		            int goCenterY = go.getY() + go.getHeight() / 2;
		
		            double distance = Math.sqrt(Math.pow(pCenterX - goCenterX, 2) + Math.pow(pCenterY - goCenterY, 2));
		
		            if (distance <= interactionRadius) {
		                interactableObject.onInteract(player);
		                break;
		            }
		        }
		    }
        }
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
	public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
		String objectName = properties.getString("name");
		String objectClass = properties.getString("type");

		if (objectName.equals("player_start")) {
			player.setX(x);
			player.setY(y);
		} else if (objectName.equals("enemy")) {
			Enemy enemy = new Enemy(x, y, width, height, assets.getSprite("enemy"), this.player);
	        this.addGameObject(enemy);
		} else if (objectName.equals("lifepack")) {
			this.addGameObject(new Lifepack(x, y, width, height, PlayingState.assets.getSprite("lifepack")));
		} else if (objectName.equals("weapon")) {
			this.addGameObject(new Weapon(x, y, width, height, PlayingState.assets.getSprite("weapon")));
		} else if (objectName.equals("bullet")) {
			this.addGameObject(new Bullet(x, y, width, height, PlayingState.assets.getSprite("bullet")));
		}else if (objectClass.equals("door")) { // Adicione um objeto do tipo "door" no Tiled
			 boolean startsOpen = properties.has("startsOpen") ? properties.getBoolean("startsOpen") : false;
			    Door door = new Door(x, y, width, height, startsOpen);
			    
			    // --- NOVA LINHA ---
			    // Dê à porta acesso a todos os objetos para a verificação de obstrução
			    door.setGameObjects(this.gameObjects);
			    
			    this.addGameObject(door);
	    }
	}

    @Override
    public Tile onTileFound(String layerName, int tileId, int x, int y) {
        
        // 1º NÍVEL DA HIERARQUIA: Filtra pela LAYER
        switch (layerName) {
            
            case "CamadaDeChao":
                // 2º NÍVEL DA HIERARQUIA: Filtra pelo TILE ID dentro da layer "Floor"
                switch (tileId) {
                    case 31: // ID de teste
                        return new FloorTile(x, y, assets.getSprite("floor_2"));
                    
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
