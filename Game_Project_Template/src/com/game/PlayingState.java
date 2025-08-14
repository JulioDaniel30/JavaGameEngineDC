package com.game;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Events.EngineEvent;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Events.WorldLoadedEventData;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Layers.IRenderable;
import com.JDStudio.Engine.Graphics.Layers.RenderLayer;
import com.JDStudio.Engine.Graphics.Layers.RenderManager;
import com.JDStudio.Engine.Graphics.Layers.StandardLayers;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Graphics.UI.Managers.UIManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.States.EnginePlayingState;
import com.JDStudio.Engine.World.IMapLoaderListener;
import com.JDStudio.Engine.World.Tile;
import com.JDStudio.Engine.World.World;

@SuppressWarnings("static-access")
public class PlayingState extends EnginePlayingState implements IMapLoaderListener {

	// Referências estáticas para fácil acesso
	public static AssetManager assets;
	public static Player player;
	public static World world;

	// Managers específicos deste estado
	@SuppressWarnings("unused")
	private UIManager uiManager;
	// Adicione aqui outros managers que você usa, como ProjectileManager,
	// LightingManager, etc.

	public PlayingState() {
		super();

		// --- ORDEM DE INICIALIZAÇÃO CORRETA ---

		// 1. Limpa o RenderManager de qualquer lixo de um estado anterior
		RenderManager.getInstance().clear();

		// 2. Inicializa os managers
		assets = new AssetManager();
		uiManager = new UIManager();
		// ... inicialize outros managers aqui ...

		// 3. Carrega os recursos visuais e sonoros
		loadAssets();

		// 4. Carrega o mundo. Durante este processo, os métodos onObjectFound e
		// onTileFound serão chamados, criando e registando os objetos e tiles.
		// (Pode criar um "empty_map.json" para o template)
		world = new World("/maps/empty_map.json", this);
		
		EventManager.getInstance().trigger(EngineEvent.WORLD_LOADED, new WorldLoadedEventData(world,this.getGameObjects()));

		// 5. Regista os sistemas de renderização que não são GameObjects (fundo,
		// iluminação, etc.)
		registerRenderSystems();

		// 6. Configura a UI e os eventos
		setupUI();
		setupEventListeners();
	}

	private void loadAssets() {
		// Carregue aqui os sprites necessários para o seu jogo (jogador, tiles, etc.)
		Spritesheet spritesheet = new Spritesheet("spritesheet.png");
		assets.registerSprite("Player", spritesheet.getSprite(0, 0, 16, 16));
	}

	private void setupUI() {
		// Crie e adicione aqui os elementos da sua UI (ex: HUD de vida)
		// uiManager.addElement(...);
	}

	private void setupEventListeners() {
		// Inscreva-se aqui nos eventos do jogo
		// EventManager.getInstance().subscribe(...);
		EventManager.getInstance().subscribe(GameEvent.PLAYER_TAKE_DAMAGE, (data) -> {
			System.out.println("player damager");
		});
	}

	@Override
	public void onExit() {
		super.onExit();
		EventManager.getInstance().unsubscribe(GameEvent.PLAYER_TAKE_DAMAGE, null);
	}

	/**
	 * Regista os sistemas de renderização (como o mundo e os efeitos) no
	 * RenderManager.
	 */
	private void registerRenderSystems() {
		RenderManager renderManager = RenderManager.getInstance();

		// Regista o renderizador do mundo de tiles
		renderManager.register(new IRenderable() {
			public void render(Graphics g) {
				if (world != null) {
					// O método render do mundo agora só desenha os tiles
					// (O ideal seria que os tiles se registassem individualmente)
				}
			}

			public RenderLayer getRenderLayer() {
				return StandardLayers.WORLD_BACKGROUND;
			}

			public boolean isVisible() {
				return true;
			}
		});

		// Adicione aqui o registo de outros sistemas, como Partículas e Iluminação
	}

	@Override
	public void tick() {
		super.tick(); // Atualiza todos os GameObjects registados

		if (InputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
			Engine.transitionToState(new MenuState());
		}

		// Atualiza a câmara para seguir o jogador
		if (player != null && world != null) {
			Engine.camera.update(world); // A câmara já sabe quem seguir
		}
	}

	@Override
	public void render(Graphics g) {
		// A renderização agora é 100% controlada pelo RenderManager.
		// Ele irá desenhar tudo na ordem correta das camadas.
		RenderManager.getInstance().render(g);
	}

	// --- MÉTODOS DO IMapLoaderListener ---

	@Override
	public Tile onTileFound(String layerName, int tileId, int x, int y) {
		// Lógica para criar e registar os seus tiles
		Tile createdTile = new Tile(x, y, null); // Crie o seu tile aqui
		if (createdTile != null) {
			RenderManager.getInstance().register(createdTile);
		}
		return createdTile;
	}

	@Override
	public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
		// Lógica para criar e registar os seus GameObjects
		if ("player_start".equals(type)) {
			player = new Player(properties);
			this.addGameObject(player); // Adiciona à lista de tick
			// Os GameObjects já se registam no RenderManager nos seus construtores
		}
	}

	@Override
	public void onPathFound(String pathName, List<Point> pathPoints) {
		// Lógica para caminhos de patrulha
	}
}