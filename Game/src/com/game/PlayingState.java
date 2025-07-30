// game
package com.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.PathComponent.PatrolMode;
import com.JDStudio.Engine.Dialogue.ActionManager;
import com.JDStudio.Engine.Dialogue.DialogueManager;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.DialogueBox;
import com.JDStudio.Engine.Graphics.UI.UIManager;
import com.JDStudio.Engine.Graphics.UI.UIText;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.GameObject.CollisionType;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.Interactable;
import com.JDStudio.Engine.Object.TriggerZone;
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
    private DialogueBox dialogueBox;
    private Map<String, List<Point>> loadedPaths;
    private List<TriggerZone> triggerZones;
    private Map<String, Door> doors;

	public PlayingState() {
        assets = new AssetManager();
        loadAssets(); 

        uiManager = new UIManager();
        uiManager.addElement(createDialogueBox());
        registerDialogueActions();

        player = new Player(new JSONObject());
        this.addGameObject(player);
        this.loadedPaths = new HashMap<>();
        this.triggerZones = new ArrayList<>();
        this.doors = new HashMap<>();
        
        // A engine agora lida com a ordem de carregamento internamente.
        world = new World("/map1.json", this);
        
        // Configura os componentes de movimento DEPOIS que todos os objetos foram criados.
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
        
        assets.registerSprite("floor_1", worldSheet.getSprite(0, 0, 16, 16));
        assets.registerSprite("floor_2", worldSheet.getSprite(0, 48, 16, 16));
        assets.registerSprite("wall_1", worldSheet.getSprite(16, 0, 16, 16));
        
        assets.registerSprite("enemy", worldSheet.getSprite(7 * 16, 16, 16, 16));
        assets.registerSprite("lifepack", worldSheet.getSprite(6 * 16, 0, 16, 16));

        assets.registerSprite("player_idle", worldSheet.getSprite(32, 0, 16, 16));
        assets.registerSprite("player_walk_right_1", worldSheet.getSprite(48, 0, 16, 16));
        assets.registerSprite("player_walk_right_2", worldSheet.getSprite(64, 0, 16, 16));
        assets.registerSprite("player_walk_right_3", worldSheet.getSprite(80, 0, 16, 16));
        assets.registerSprite("player_walk_left_1", worldSheet.getSprite(48, 16, 16, 16));
        assets.registerSprite("player_walk_left_2", worldSheet.getSprite(64, 16, 16, 16));
        assets.registerSprite("player_walk_left_3", worldSheet.getSprite(80, 16, 16, 16));
        
	    assets.registerSprite("door_frame_1", worldSheet.getSprite(32, 32, 16, 16));
	    assets.registerSprite("door_frame_2", worldSheet.getSprite(48, 32, 16, 16));
	    assets.registerSprite("door_frame_3", worldSheet.getSprite(64, 32, 16, 16));
	    assets.registerSprite("npc_sprite", worldSheet.getSprite(112, 0, 16, 16));
    }
    
    private void setupUI() {
        uiManager.addElement(new UIText(5, 15, new Font("Arial", Font.BOLD, 12), Color.WHITE, () -> "Vida: " + (int)player.life + "/" + (int)player.maxLife));
    }
    
    private DialogueBox createDialogueBox() {
        dialogueBox = new DialogueBox(10, 85, Engine.WIDTH - 20, 70);
        // Configurações de aparência
        dialogueBox.setFonts(new Font("Courier New", Font.BOLD, 12), new Font("Courier New", Font.PLAIN, 10));
        dialogueBox.setColors(new Color(20, 20, 80, 230), Color.WHITE, Color.YELLOW, Color.CYAN);
        dialogueBox.setPadding(5);
        dialogueBox.setLineSpacing(12);
        dialogueBox.setSectionSpacing(8);
        dialogueBox.setTypewriterSpeed(2);
    	return dialogueBox;
    }
    
    private void registerDialogueActions() {
        ActionManager actionManager = ActionManager.getInstance();
        actionManager.registerAction("accept_quest_martelo", (player, npc) -> {
            System.out.println("AÇÃO: Missão 'Consiga o Martelo' iniciada!");
        });
        actionManager.registerAction("give_item_pocao", (player, npc) -> {
            System.out.println("AÇÃO: O jogador recebeu uma poção!");
        });
    }

    @Override
    public void tick() {
        if (DialogueManager.getInstance().isActive()) {
            dialogueBox.tick();
        } else {
            super.tick(); 
            guidanceUpdate();
            keyboardEventsUpdate();
            collisionsUpdate();
            if (player != null && world != null) {
                Engine.camera.update(player, world);
            }
        }
        InputManager.instance.update();
    }
    
    private void collisionsUpdate() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);
                // Esta verificação funciona para SÓLIDOS e GATILHOS.
                if (GameObject.isColliding(obj1, obj2)) {
                    obj1.onCollision(obj2); // O Player.onCollision(Lifepack) será chamado
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
		            int interactionRadius = interactableObject.getInteractionRadius();
		
		            int pCenterX = player.getX() + player.getWidth() / 2;
		            int pCenterY = player.getY() + player.getHeight() / 2;
		            int goCenterX = go.getX() + go.getWidth() / 2;
		            int goCenterY = go.getY() + go.getHeight() / 2;
		
		            double distance = Math.sqrt(Math.pow(pCenterX - goCenterX, 2) + Math.pow(pCenterY - goCenterY, 2));
		
		            if (distance <= interactionRadius) {
		                interactableObject.onInteract(player);
		                System.out.println("interagiu");
		                break;
		            }
		        }
		    }
        }
    }

    @Override
    public void render(Graphics g) {
        if (world != null) world.render(g);
        super.render(g); // Desenha todos os GameObjects
        if (uiManager != null) uiManager.render(g);
    }
    
    @Override
	public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
	    GameObject newObject = null;
	    
	    // O 'type' aqui é a "class" que você define no Tiled
	    switch (type) {
	        case "player_start":
	            player.initialize(properties);
	            return;
	
	        case "enemy":
	            newObject = new Enemy(this.player, properties);
	            break;
	            
	        case "NPC":
	             if ("ferreiro".equals(properties.getString("name"))) {
	                newObject = new Ferreiro(properties);
	             }
	            break;
	            
	        case "door":
	        	Door door = new Door(properties);
	            newObject = door;
	            
	            // Adiciona a porta ao mapa de portas usando o seu nome
	            
	            System.out.println("porta: " + door.name);
	            if (!door.name.isEmpty()) {
	            	System.out.println("o nome da porta nao esta vazia");
	                this.doors.put(door.name, door);
	            }
	            break;
	            
	        case "TriggerZone":
	        	// --- CORREÇÃO DE POSICIONAMENTO ---
	            // Para a TriggerZone, usamos a coordenada Y original do Tiled, não a ajustada.
	            int originalY = properties.getInt("y");
	            
	            // Cria a zona, mas ajusta manualmente a sua posição Y.
	            TriggerZone zone = new TriggerZone(properties);
	            zone.setY(originalY);
	            newObject = zone;
	            
	            // Adiciona a zona à lista para verificação
	            this.triggerZones.add(zone);
	        	break;
	            
	        case "lifepack":
	            newObject = new Lifepack(properties);
	            break;
	
	        case "weapon":
	            newObject = new Weapon(properties);
	            break;
	
	        case "bullet":
	            newObject = new Bullet(properties);
	            break;
	    }
	    
	    if (newObject != null) {
	        if (newObject instanceof Enemy) {
	            Enemy enemy = (Enemy) newObject;
	            String expectedPathName = "path_" + enemy.name;
	            if (loadedPaths.containsKey(expectedPathName)) {
	                enemy.setPath(loadedPaths.get(expectedPathName), PatrolMode.PING_PONG);
	            }
	        }
	        this.addGameObject(newObject);
	    }
	}

    @Override
    public Tile onTileFound(String layerName, int tileId, int x, int y) {
        switch (layerName) {
            case "CamadaDeChao":
                switch (tileId) {
                    case 31:
                        return new FloorTile(x, y, assets.getSprite("floor_2"));
                    default:
                        return new FloorTile(x, y, assets.getSprite("floor_1"));
                }

            case "CamadaDeParedes":
                return new WallTile(x, y, assets.getSprite("wall_1"));

            default:
                return null;
        }
    }

    @Override
    public void onPathFound(String pathName, List<Point> pathPoints) {
        this.loadedPaths.put(pathName, pathPoints);
    }
    
 // Adicione este novo método ao PlayingState
    private void guidanceUpdate() {
        // Itera sobre todos os GameObjects primeiro
        for (GameObject characterObject : this.gameObjects) {
            // Verifica se o objeto é um personagem
            if (characterObject instanceof Character) {
                
                // Agora, para este personagem específico, verifica todas as zonas de gatilho
                for (TriggerZone zone : triggerZones) {
                    Door targetDoor = doors.get(zone.targetName);

                    // Se a zona tem uma porta válida, a porta está aberta, E o personagem está a colidir com a zona...
                    if (targetDoor != null && targetDoor.getCollisionType() != CollisionType.SOLID && GameObject.isColliding(characterObject, zone)) {
                        
                    	if(!(characterObject instanceof Player)) return;
                    	
                        int doorCenterX = targetDoor.getX() + targetDoor.getWidth() / 2;
                        int doorCenterY = targetDoor.getY() + targetDoor.getHeight() / 2;
                        
                        if (characterObject.movement != null) {
                        	if(characterObject.getX() > targetDoor.getX() && characterObject.movement.getDx() ==+1) {
                        		return;
                        	}else if(characterObject.getX() < targetDoor.getX() && characterObject.movement.getDx() ==-1) {
                        		return;
                        	}
                        	if(characterObject.getY() > targetDoor.getY() && characterObject.movement.getDy() ==+1) {
                        		return;
                        	}else if(characterObject.getY() < targetDoor.getY() && characterObject.movement.getDy() ==-1) {
                        		return;
                        	}
                            characterObject.movement.applyGuidance(doorCenterX, doorCenterY, 0.1);
                        }
                        
                        // --- CORREÇÃO CRÍTICA AQUI ---
                        // Assim que encontramos uma zona que afeta o personagem,
                        // saímos do loop de zonas para evitar que outras zonas apliquem forças conflitantes.
                        break; 
                    }
                }
            }
           }
        }
       
    
}