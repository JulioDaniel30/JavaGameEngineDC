// game
package com.game.States;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.HealthComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Components.InventoryComponent;
import com.jdstudio.engine.Components.PathComponent.PatrolMode;
import com.jdstudio.engine.Components.Moviments.BaseMovementComponent;
import com.jdstudio.engine.Components.Moviments.MovementComponent;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Core.SaveManager;
import com.jdstudio.engine.Dialogue.ActionManager;
import com.jdstudio.engine.Dialogue.Dialogue;
import com.jdstudio.engine.Dialogue.DialogueManager;
import com.jdstudio.engine.Events.CharacterSpokeEventData;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventListener;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.InteractionEventData;
import com.jdstudio.engine.Events.WorldLoadedEventData;
import com.jdstudio.engine.Graphics.AssetManager;
import com.jdstudio.engine.Graphics.Effects.ParticleManager;
import com.jdstudio.engine.Graphics.Layers.IRenderable;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;
import com.jdstudio.engine.Graphics.Lighting.ConeLight;
import com.jdstudio.engine.Graphics.Lighting.Light;
import com.jdstudio.engine.Graphics.Lighting.LightingManager;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;
import com.jdstudio.engine.Graphics.UI.DialogueBox;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Graphics.UI.UITheme;
import com.jdstudio.engine.Graphics.UI.Elements.UIImage;
import com.jdstudio.engine.Graphics.UI.Elements.UIText;
import com.jdstudio.engine.Graphics.UI.Managers.PopupManager;
import com.jdstudio.engine.Graphics.UI.Managers.ThemeManager;
import com.jdstudio.engine.Graphics.UI.Managers.UIManager;
import com.jdstudio.engine.Graphics.WSUI.BarElements.UIHealthBar;
import com.jdstudio.engine.Graphics.WSUI.InformationElements.UINameplate;
import com.jdstudio.engine.Graphics.WSUI.PersonalityElements.UIChatBubble;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Object.EngineNPC;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Object.GameObject.CollisionType;
import com.jdstudio.engine.Object.ProjectileManager;
import com.jdstudio.engine.Object.TriggerZone;
import com.jdstudio.engine.Sound.Sound;
import com.jdstudio.engine.States.EnginePlayingState;
import com.jdstudio.engine.Utils.ImageUtils;
import com.jdstudio.engine.World.Camera;
import com.jdstudio.engine.World.IMapLoaderListener;
import com.jdstudio.engine.World.Tile;
import com.jdstudio.engine.World.World;
import com.game.Items.HealthPotion;
import com.game.Tiles.FloorTile;
import com.game.Tiles.LightTile;
import com.game.Tiles.WallTile;
import com.game.gameObjects.BulletPack;
import com.game.gameObjects.Door;
import com.game.gameObjects.Enemy;
import com.game.gameObjects.Ferreiro;
import com.game.gameObjects.Lifepack;
import com.game.gameObjects.Player;
import com.game.gameObjects.Weapon;
import com.game.manegers.GameEvent;
import com.game.manegers.Projectile;

@SuppressWarnings("static-access")
public class PlayingState extends EnginePlayingState implements IMapLoaderListener {

	public static AssetManager assets;
	public static Player player;
	public static World world;

	private final String SAVE_FILE = "savegame1.json";

	private UIManager uiManager;
	private DialogueBox dialogueBox;
	private Map<String, List<Point>> loadedPaths;
	private List<TriggerZone> triggerZones;
	private Map<String, Door> doors;
	private ProjectileManager projectileManager;
	private ParticleManager particleManager;
	private LightingManager lightingManager;
	private PopupManager popupManager;
	// private Light playerLight;
	private ConeLight playerFlashlight;
	private UIText ammoUiText; // <-- Guarde uma referência ao texto da vida
	private List<UIImage> heartIcons = new ArrayList<>();
	private EventListener playerHealListener; // <-- Guarde uma referência ao listener
	private EventListener playerDiedListener;
	private EventListener characterSpokeListener;
	
	
    private GameObject interactableObjectInRange = null;
	public PlayingState() {
		
		RenderManager.getInstance().clear();
		
		assets = new AssetManager();
		loadAssets();

		uiManager = new UIManager();
		registerDialogueActions();
		projectileManager = ProjectileManager.getInstance();
		
		particleManager = ParticleManager.getInstance();

		particleManager = ParticleManager.getInstance();
		lightingManager = LightingManager.getInstance();
		popupManager = PopupManager.getInstance();
		ThemeManager.getInstance().setTheme(UITheme.MEDIEVAL);
		

		this.loadedPaths = new HashMap<>();
		this.triggerZones = new ArrayList<>();
		this.doors = new HashMap<>();
		 
		// A engine agora lida com a ordem de carregamento internamente.
		world = new World("/map1.json", this);
		Engine.camera.applyProfile(Camera.PROFILE_GAMEPLAY, player);
		projectileManager.init(() -> new Projectile(),world,gameObjects);
		registerRenderSystems();
		
		InventoryComponent inv = player.getComponent(InventoryComponent.class);
        if (inv != null) {
            inv.inventory.addItem(new HealthPotion(), 5);
        }
		
		System.out.println("Disparando evento WORLD_LOADED...");
		EventManager.getInstance().trigger(EngineEvent.WORLD_LOADED,
				new WorldLoadedEventData(world, this.getGameObjects()));

		setupDependencies();
		setupUI();
		setupEventListeners();

		Sound.loop("/music.wav");
		Sound.setMusicVolume(0.01f);
		Sound.setSfxVolume(0.02f);
		// Configura as dependências DEPOIS que todos os objetos foram criados.
		
	}

	/**
	 * Configura a UI que depende de objetos do jogo (como o Player).
	 */
	private void setupUI() {
		createDialogueBox();
		uiManager.addElement(dialogueBox);

		int maxHearts = 3;
		for (int i = 0; i < maxHearts; i++) {
			UIImage heart = new UIImage(5 + (i * 18), 5, ThemeManager.getInstance().get(UISpriteKey.HEART_FULL));
			heartIcons.add(heart);
			uiManager.addElement(heart);
		}

		ammoUiText = new UIText(150, 15, new Font("Arial", Font.BOLD, 12), Color.WHITE,
				() -> "ammo: " + (int) player.ammo + "/" + (int) player.maxAmmo);

		uiManager.addElement(ammoUiText);

	}

	/**
	 * Configura as dependências (mundo, lista de objetos) nos componentes.
	 */
	private void setupDependencies() {
		// Cria e adiciona a luz do jogador AGORA que o jogador existe
		Color flashlightColor = new Color(255, 220, 150, 70);

		playerFlashlight = new ConeLight(player.getX(), player.getY(), 90.0, 0.0, assets.getSprite("cone_light_sprite"),
				flashlightColor);
		lightingManager.addLight(playerFlashlight);

		for (GameObject go : this.gameObjects) {

			if (go instanceof Door) {
				((Door) go).setGameObjects(this.gameObjects);
			}
		}
	}

	/**
	 * Inscreve todos os listeners de eventos.
	 */
	private void setupEventListeners() {
		playerDiedListener = (data) -> {
			//Engine.setGameState(new GameOverState());
			Engine.pushState(new GameOverState());
		};
		EventManager.getInstance().subscribe(GameEvent.PLAYER_DIED, playerDiedListener);

		// Listener para a UI de vida
		EventListener playerTookDamageListener = (data) ->{
			updateHealthUI();
			if (data instanceof Double) { // Supondo que o dano seja um inteiro
                double damageAmount = (Double) data;
                
                // Cria um popup de texto vermelho sobre o jogador
                PopupManager.getInstance().createPopup(
                    player,                              // Alvo
                    String.valueOf(damageAmount),        // Texto
                    new Font("Arial", Font.BOLD, 12),    // Fonte
                    Color.RED,                           // Cor
                    60                                   // Duração (60 frames = 1 segundo)
                );
            }
			};
			
			EventListener playerHealListener = (data) ->{
				updateHealthUI();
				if (data instanceof Double) { // Supondo que o dano seja um inteiro
	                double damageAmount = (Double) data;
	                
	                // Cria um popup de texto vermelho sobre o jogador
	                PopupManager.getInstance().createPopup(
	                    player,                              // Alvo
	                    String.valueOf(damageAmount),        // Texto
	                    new Font("Arial", Font.BOLD, 12),    // Fonte
	                    Color.BLUE,                           // Cor
	                    60                                   // Duração (60 frames = 1 segundo)
	                );
	            }
				};
			
			
		EventManager.getInstance().subscribe(GameEvent.PLAYER_HEALED, playerHealListener);
		EventManager.getInstance().subscribe(GameEvent.PLAYER_TOOK_DAMAGE, playerTookDamageListener);

		// --- LISTENER PARA O BALÃO DE FALA ---
        characterSpokeListener = (data) -> {
            if (data instanceof CharacterSpokeEventData) {
                CharacterSpokeEventData eventData = (CharacterSpokeEventData) data;
                
                // O Jogo é quem decide como renderizar o evento: criando um UIChatBubble.
                int durationInFrames = (int) (eventData.durationInSeconds() * 60);
                UIChatBubble bubble = new UIChatBubble(eventData.speaker(), eventData.message(), durationInFrames);
                
                // Adiciona o balão criado à UI
                uiManager.addElement(bubble);
            }
        };
        
        EventManager.getInstance().subscribe(EngineEvent.CHARACTER_SPOKE, characterSpokeListener);
		
        EventManager.getInstance().subscribe(EngineEvent.TARGET_ENTERED_ZONE, (data) -> {
            InteractionEventData event = (InteractionEventData) data;
            
            if (event.zone().type.equals(InteractionZone.TYPE_DIALOGUE)) {
            	this.interactableObjectInRange = event.zoneOwner();
            }
        
        });
        
     // Ouve o evento para quando o jogador SAI de uma zona
        EventManager.getInstance().subscribe(EngineEvent.TARGET_EXITED_ZONE, (data) -> {
            InteractionEventData event = (InteractionEventData) data;

            // Se o objeto de que estamos a sair for o mesmo que está guardado, limpamos a referência
            if (event.zoneOwner() == this.interactableObjectInRange) {
                this.interactableObjectInRange = null;

                // Opcional: Esconder a dica da UI
                // showInteractPrompt(false);
            }
        });
        
		updateHealthUI(); // Chama uma vez para o estado inicial
	}
	
	/**
     * Regista todos os sistemas de renderização que não são GameObjects ou UIElements
     * no RenderManager central. Cada sistema é envolvido num "IRenderable" anónimo
     * que o associa à sua camada de renderização correta.
     */
    private void registerRenderSystems() {
        RenderManager renderManager = RenderManager.getInstance();

        
        // 3. Regista o renderizador de partículas
        renderManager.register(new IRenderable() {
            @Override
            public void render(Graphics g) {
                particleManager.render(g);
            }
            @Override
            public RenderLayer getRenderLayer() {
                return StandardLayers.PARTICLES;
            }
            @Override
            public boolean isVisible() { return true; }
        });

        // 4. Regista o renderizador de iluminação
        renderManager.register(new IRenderable() {
            @Override
            public void render(Graphics g) {
                lightingManager.render(g);
            }
            @Override
            public RenderLayer getRenderLayer() {
                return StandardLayers.LIGHTING;
            }
            @Override
            public boolean isVisible() { return true; }
        });
        renderManager.register(new IRenderable() {
            @Override
            public void render(Graphics g) {
                popupManager.render(g);
            }
            @Override
            public RenderLayer getRenderLayer() {
                // Usa a nova camada dedicada aos popups
                return StandardLayers.POPUPS;
            }
            @Override
            public boolean isVisible() { return true; }
        });
    }

	private void loadAssets() {
		Spritesheet worldSheet = new Spritesheet("/spritesheet.png");

		assets.registerSprite("floor_1", worldSheet.getSprite(0, 0, 16, 16));
		assets.registerSprite("floor_2", worldSheet.getSprite(0, 48, 16, 16));
		assets.registerSprite("wall_1", worldSheet.getSprite(16, 0, 16, 16));

		assets.registerSprite("enemy", worldSheet.getSprite(7 * 16, 16, 16, 16));
		assets.registerSprite("lifepack", worldSheet.getSprite(6 * 16, 0, 16, 16));
		assets.registerSprite("bullet", worldSheet.getSprite(5 * 16, 16 * 2, 16, 16));
		assets.registerSprite("bullet_pack", worldSheet.getSprite(6 * 16, 16, 16, 16));

		assets.registerSprite("weapon_holder", worldSheet.getSprite(112, 0, 16, 16));
		assets.registerSprite("pistol_right", worldSheet.getSprite(128, 0, 16, 16));
		// assets.registerSprite("pistol_left", worldSheet.getSprite(144, 0, 16, 16));

		BufferedImage imagemOriginal = assets.getSprite("pistol_right").getImage();

		// 3. Usa o ImageUtils para criar a imagem invertida.
		BufferedImage imagemInvertida = ImageUtils.flipHorizontal(imagemOriginal);

		assets.registerSprite("pistol_left", new Sprite(imagemInvertida));

		assets.registerSprite("door_frame_1", worldSheet.getSprite(32, 32, 16, 16));
		assets.registerSprite("door_frame_2", worldSheet.getSprite(48, 32, 16, 16));
		assets.registerSprite("door_frame_3", worldSheet.getSprite(64, 32, 16, 16));
		assets.registerSprite("npc_sprite", worldSheet.getSprite(112, 0, 16, 16));
		assets.loadSprite("button_normal", "/Engine/UI/button_normal.png");
		assets.loadSprite("button_hover", "/Engine/UI/button_hover.png");
		assets.loadSprite("button_pressed", "/Engine/UI/button_pressed.png");
		assets.loadSprite("cone_light_sprite", "/Engine/cone_light.png");
		assets.registerSprite("grass_torch", worldSheet.getSprite(0, 64, 16, 16));

		Spritesheet heartsSpritesheet = new Spritesheet("/Engine/UI/Pixel_Heart_Spritesheet.png");
		assets.registerSprite("heart_full", heartsSpritesheet.getSprite(0, 0, 16, 16));
		assets.registerSprite("heart_half", heartsSpritesheet.getSprite(16, 0, 16, 16));
		assets.registerSprite("heart_empty", heartsSpritesheet.getSprite(32, 0, 16, 16));

		assets.loadSprite("slider_handle", "/Engine/UI/medieval/slider_handle.png");
		assets.loadSprite("slider_track", "/Engine/UI/medieval/slider_track.png");
	}

	public void updateHealthUI() {
		// Supondo que cada coração representa 40 de vida (maxLife = 120)
		// Você pode ajustar essa lógica para o seu jogo
		double healthPerHeart = player.maxLife / heartIcons.size();

		for (int i = 0; i < heartIcons.size(); i++) {
			UIImage heart = heartIcons.get(i);
			double heartHealthThreshold = (i + 1) * healthPerHeart;

			if (player.life >= heartHealthThreshold) {
				heart.setSprite(ThemeManager.getInstance().get(UISpriteKey.HEART_FULL));
			} else if (player.life >= heartHealthThreshold - (healthPerHeart / 2)) {
				heart.setSprite(ThemeManager.getInstance().get(UISpriteKey.HEART_HALF));
			} else {
				heart.setSprite(ThemeManager.getInstance().get(UISpriteKey.HEART_EMPTY));
			}
		}
	}

	public void onExit() { // Adicione este método ao seu GameState se quiser
		EventManager.getInstance().unsubscribe(GameEvent.PLAYER_HEALED, playerHealListener);
		EventManager.getInstance().unsubscribe(GameEvent.PLAYER_DIED, playerDiedListener);
		EventManager.getInstance().unsubscribe(EngineEvent.CHARACTER_SPOKE, characterSpokeListener);
	}

	private void createDialogueBox() {
		dialogueBox = new DialogueBox(10, 85, Engine.getWIDTH() - 20, 70);
		// Configurações de aparência
		dialogueBox.setFonts(new Font("Courier New", Font.BOLD, 12), new Font("Courier New", Font.PLAIN, 10));
		dialogueBox.setColors(new Color(20, 20, 80, 230), Color.WHITE, Color.YELLOW, Color.CYAN);
		dialogueBox.setPadding(5);
		dialogueBox.setLineSpacing(12);
		dialogueBox.setSectionSpacing(8);
		dialogueBox.setTypewriterSpeed(2);
		// return dialogueBox;
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
		uiManager.tick();
		if (!DialogueManager.getInstance().isActive()) {

			if(dialogueBox.inputConsumedThisFrame) return;
			super.tick();
			guidanceUpdate();
			// eventsActionsUpdate();
			keyboardEventsUpdate();
			particleManager.update();
			projectileManager.update();
			popupManager.update();
			collisionsUpdate();

			/*
			 * // Faz a luz seguir o centro do jogador playerLight.x = player.getX() +
			 * player.getWidth() / 2.0; playerLight.y = player.getY() + player.getHeight() /
			 * 2.0;
			 */

			// Atualiza a posição da lanterna para seguir o centro do jogador
			playerFlashlight.x = player.getX() + player.getWidth() / 2.0;
			playerFlashlight.y = player.getY() + player.getHeight() / 2.0;
			// --- LÓGICA DE ÂNGULO ATUALIZADA PARA 8 DIREÇÕES ---

			MovementComponent playerMovement = player.getComponent(MovementComponent.class);
			double dx = playerMovement.getDx();
			double dy = playerMovement.getDy();

			// Apenas atualizamos o ângulo SE o jogador estiver se movendo.
			// Isso faz com que a lanterna "lembre" da última direção quando o jogador para.
			if (dx != 0 || dy != 0) {
				playerFlashlight.angle = Math.atan2(dy, dx);
			}

			if (player != null && world != null) {
				Engine.camera.update(world);
			}
		}
		// InputManager.instance.update();
	}

	private void collisionsUpdate() {
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject obj1 = gameObjects.get(i);
			// Pula objetos destruídos
			if (obj1.isDestroyed)
				continue;

			for (int j = i + 1; j < gameObjects.size(); j++) {
				GameObject obj2 = gameObjects.get(j);
				// Pula objetos destruídos
				if (obj2.isDestroyed)
					continue;

				if (GameObject.isColliding(obj1, obj2)) {
					obj1.onCollision(obj2);
					obj2.onCollision(obj1);
				}
			}
		}
	}

	private void keyboardEventsUpdate() {
		if (InputManager.isActionJustPressed("TOGGLE_DEBUG")) {
			Engine.isDebug = !Engine.isDebug;
		}
		if (InputManager.isKeyJustPressed(KeyEvent.VK_SPACE)) {
			Engine.camera.shake(4, 15);
		}

		if (InputManager.isActionJustPressed("INTERACT")) {
			
			// A verificação agora é uma única e simples condição:
		    // Se a nossa variável de estado não for nula, significa que o jogador
		    // está perto de algo e pressionou a tecla.
		    if (this.interactableObjectInRange != null) {
		        
		        // Agora, aqui dentro, colocamos a lógica que antes estava no EventListener.
		        // Podemos verificar que tipo de objeto é para decidir o que fazer.
		        
		        if (this.interactableObjectInRange instanceof Ferreiro) {
		            Ferreiro ferreiro = (Ferreiro) this.interactableObjectInRange;
		            GameObject interactor = this.player; // O jogador
		            
		            Dialogue dialogue = ferreiro.getDialogue(); 
		            if (dialogue != null && !DialogueManager.getInstance().isActive()) {
		                DialogueManager.getInstance().startDialogue(dialogue, ferreiro, interactor);
		            }
		        }
		        
		        // Você poderia adicionar outros tipos de interação aqui
		        // else if (this.interactableObjectInRange instanceof Bau) {
		        //     ((Bau) this.interactableObjectInRange).abrir(this.player);
		        // }
		    }
			
			for (GameObject go : gameObjects) {
				
				if (go instanceof Weapon) {
					if (player.isColliding(player, go)) {
						player.attach(go, 0, 0);
					}
				}
			}
		}
		
		

		if (InputManager.isKeyJustPressed(KeyEvent.VK_P)) {
			ParticleManager.getInstance().createExplosion(player.getX() + player.getWidth() / 2.0,
					player.getY() + player.getHeight() / 2.0, 100, // 100 partículas
					Color.ORANGE, // Cor inicial laranja
					new Color(150, 0, 0, 0), // Cor final vermelho escuro e transparente
					30, 60, // Vida entre 30 e 60 frames
					0.5, 2.5, // Velocidade entre 0.5 e 2.5 pixels/frame
					8, 0 // Tamanho começa em 8 e termina em 0
			);
		}

		if (InputManager.isActionJustPressed("SAVE_GAME")) {
			saveGame();
		}
		if (InputManager.isActionJustPressed("LOAD_GAME")) {
			loadGame();
		}

		if (InputManager.isKeyJustPressed(KeyEvent.VK_R)) {
			Engine.restartCurrentState();
		}

		if (InputManager.isKeyJustPressed(KeyEvent.VK_M)) {
			Engine.restartGame();
			Engine.setGameState(new MenuState());
		}
		
		if(InputManager.isKeyJustPressed(KeyEvent.VK_L)) {
			Engine.pushState(new GameOverState());
		}
		if (InputManager.isActionJustPressed("PAUSE_GAME")) {
            // "Empilha" o estado de pausa por cima do estado de jogo
            Engine.pushState(new PauseState());
        }
		
		 if (InputManager.isActionJustPressed("TOGGLE_INVENTORY")) {
	            // "Empilha" o estado de inventário por cima do jogo
	            Engine.pushState(new InventoryState());
	        }

		if (InputManager.isLeftMouseButtonJustPressed()) {
			// Posição do mouse na JANELA
			/*int mouseX = InputManager.getMouseX();
			int mouseY = InputManager.getMouseY();*/

			// IMPORTANTE: Converte a coordenada da tela para a coordenada do MUNDO,
			// somando a posição da câmera e ajustando pela escala.
			/*
			 * int worldX = (mouseX / Engine.SCALE) + Engine.camera.getX();
			 * int worldY = (mouseY / Engine.SCALE) + Engine.camera.getY();
			 */

			int[] mouseWorldPos = InputManager.covertMousePositionToWorld();
			int worldX = mouseWorldPos[0];
			int worldY = mouseWorldPos[1];
			// System.out.println("Clique do mouse no mundo em: " + worldX + ", " + worldY);

			// Dispara um efeito de partículas na posição do clique
			ParticleManager.getInstance().createExplosion(worldX, worldY, 50, Color.CYAN, new Color(0, 50, 150, 0), 20,
					40, 0.5, 2.0, 5, 0);
		}

	}

	@Override
	public void render(Graphics g) {
		
		RenderManager.getInstance().render(g);
		/*if (world != null)
			world.render(g);
		super.render(g); // Desenha todos os GameObjects
		particleManager.render(g);
		projectileManager.render(g);
		lightingManager.render(g);
		if (uiManager != null)
			uiManager.render(g);*/
	}

	@Override
	public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
		GameObject newObject = null;

		// O 'type' aqui é a "class" que você define no Tiled
		switch (type) {
		case "player_start":
			// 1. CRIA a instância única do jogador AQUI.
			Player newPlayer = new Player(properties);

			// 2. ARMAZENA a referência estática para acesso global.
			PlayingState.player = newPlayer;

			// 3. Define o objeto a ser adicionado na lista principal do jogo.
			newObject = newPlayer;
			break;

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

			if (!door.name.isEmpty()) {

				this.doors.put(door.name, door);
			}
			break;

		case "TriggerZone":
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

		case "bullet_pack":
			newObject = new BulletPack(properties);
			break;
		}

		if (newObject != null) {
			// logica dos Paths
			if (newObject instanceof Enemy) {
				Enemy enemy = (Enemy) newObject;
				String expectedPathName = "path_" + enemy.name;
				if (loadedPaths.containsKey(expectedPathName)) {
					enemy.setPath(loadedPaths.get(expectedPathName), PatrolMode.PING_PONG);
				}
			}
			this.addGameObject(newObject);
			if (newObject.hasComponent(HealthComponent.class)) {
                UIHealthBar healthBar = new UIHealthBar(newObject, -8, 20, 4); // yOffset=-8 (acima), 20px de largura, 4px de altura
                uiManager.addElement(healthBar);
            }
			 if (newObject instanceof EngineNPC) {
	                // Cria uma placa de identificação para este NPC e a adiciona à UI
	                UINameplate nameplate = new UINameplate(newObject, -5, new Font("Arial", Font.BOLD, 10), Color.CYAN);
	                uiManager.addElement(nameplate);
	            }
		}
	}

	@Override
	public Tile onTileFound(String layerName, int tileId, int x, int y) {
		Tile createdTile = null;
		switch (layerName) {
		case "CamadaDeChao":
			switch (tileId) {
			case 31:
				createdTile = new FloorTile(x, y, assets.getSprite("floor_2"));
				break;
			default:
				createdTile = new FloorTile(x, y, assets.getSprite("floor_1"));
				break;
			}
			break;

		case "CamadaDeParedes":
			createdTile = new WallTile(x, y, assets.getSprite("wall_1"));
			break;
			
		case "CamadaDeLuz":
			Sprite torchSprite = assets.getSprite("grass_torch");

			// 2. Defina as propriedades da luz que este tile vai emitir
			// (posição x e y são temporárias, o LightTile vai corrigir)
			Light torchLight = new Light(0, 0, 48, new Color(255, 0, 0, 150));

			// 3. Crie o LightTile. Ele cuidará de adicionar a luz ao manager.
			createdTile = new LightTile(x, y, torchSprite, torchLight);
			break;

		default:
			createdTile = null;
			break;
		}
		
		if (createdTile != null) {
            // ...nós o registamos no RenderManager para que ele seja desenhado.
            RenderManager.getInstance().register(createdTile);
        }
        
        // Retorna o tile para ser adicionado ao array 'tiles' do World (para colisões)
        return createdTile;
	}

	@Override
	public void onPathFound(String pathName, List<Point> pathPoints) {
		this.loadedPaths.put(pathName, pathPoints);
	}

	private void guidanceUpdate() {
		// Itera sobre todos os GameObjects
		for (GameObject characterObject : this.gameObjects) {
			// Verifica se o objeto é um personagem e se ele tem um componente de movimento
			if (characterObject instanceof Character && characterObject.hasComponent(BaseMovementComponent.class)) {

				// --- CORREÇÃO: Guia apenas o jogador ---
				// Se o objetivo é guiar apenas o player, a verificação deve ser feita aqui.
				if (!(characterObject instanceof Player)) {
					continue; // Pula para o próximo objeto da lista
				}

				BaseMovementComponent movement = characterObject.getComponent(BaseMovementComponent.class);

				// Para este personagem, verifica todas as zonas de gatilho
				for (TriggerZone zone : triggerZones) {
					Door targetDoor = doors.get(zone.targetName);

					if (targetDoor != null && targetDoor.getCollisionType() != CollisionType.SOLID
							&& GameObject.isColliding(characterObject, zone)) {

						// --- CORREÇÃO: Lógica de segurança agora usa 'break' ---
						// Impede que a guia puxe o jogador de volta
						boolean alreadyPastTheDoor = (characterObject.getX() > targetDoor.getX()
								&& movement.getDx() == 1)
								|| (characterObject.getX() < targetDoor.getX() && movement.getDx() == -1)
								|| (characterObject.getY() > targetDoor.getY() && movement.getDy() == 1)
								|| (characterObject.getY() < targetDoor.getY() && movement.getDy() == -1);

						if (alreadyPastTheDoor) {
							break; // Sai do loop de zonas, mas continua o loop de personagens
						}

						// Aplica a orientação
						int doorCenterX = targetDoor.getX() + targetDoor.getWidth() / 2;
						int doorCenterY = targetDoor.getY() + targetDoor.getHeight() / 2;
						movement.applyGuidance(doorCenterX, doorCenterY, 0.1);

						// Sai do loop de zonas, pois já encontramos uma que afeta o personagem.
						break;
					}
				}
			}
		}
	}

	public void saveGame() {
		System.out.println("Iniciando salvamento...");
		JSONObject saveState = new JSONObject();
		JSONArray gameObjectsState = new JSONArray();

		for (GameObject go : this.gameObjects) {
			// Se o objeto sabe se salvar, pedimos seu estado
			if (go instanceof ISavable) {
				// Apenas salvamos objetos com nome, para podermos encontrá-los depois
				if (go.name != null && !go.name.isEmpty()) {
					gameObjectsState.put(((ISavable) go).saveState());
				}
			}
		}

		saveState.put("gameObjects", gameObjectsState);
		SaveManager.saveToFile(saveState, SAVE_FILE);
	}

	public void loadGame() {
		System.out.println("Iniciando carregamento...");
		JSONObject saveState = SaveManager.loadFromFile(SAVE_FILE);
		if (saveState == null)
			return;

		JSONArray gameObjectsState = saveState.getJSONArray("gameObjects");

		// Percorre os estados salvos
		for (int i = 0; i < gameObjectsState.length(); i++) {
			JSONObject objectState = gameObjectsState.getJSONObject(i);
			String objectName = objectState.getString("name");

			// Procura o objeto correspondente no jogo atual
			for (GameObject go : this.gameObjects) {
				if (go.name != null && go.name.equals(objectName)) {
					if (go instanceof ISavable) {
						((ISavable) go).loadState(objectState);
						break; // Encontrou e carregou, vai para o próximo estado salvo
					}
				}
			}
		}

		// Precisamos tratar os Lifepacks coletados (que foram marcados como
		// isDestroyed)
		for (GameObject go : this.gameObjects) {
			if (go instanceof Lifepack) {
				// Se um Lifepack salvo está marcado como destruído, removemos ele do jogo
				// (Esta é uma forma simples, uma mais avançada salvaria o estado de todos os
				// itens)
			}
		}
	}

}