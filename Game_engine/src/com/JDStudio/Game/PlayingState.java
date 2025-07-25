package com.JDStudio.Game;



import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.GameState;
import com.JDStudio.Engine.Graphics.Spritesheet;
import com.JDStudio.Engine.Input.InputManager;
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
        
        // A classe World do seu jogo agora estende a da engine
        world = new com.JDStudio.Game.World("/level1.png",this);

        // --- A CORREÇÃO CRUCIAL ESTÁ AQUI ---
        player.setWorld(world); // Atribui a instância do mundo ao jogador
        // ------------------------------------

        this.addGameObject(player);    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
    }
    
    @Override
    public void tick() {
    	
    	// Atualiza o estado do InputManager no início de cada frame
        
        
        // Verifica o toque único para o debug
        if (InputManager.isKeyJustPressed(KeyEvent.VK_9)) {
            Engine.isDebug = !Engine.isDebug;
            System.out.println(Engine.isDebug);
        }
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).tick();
        }
        
     // 2. VERIFICA AS COLISÕES ENTRE AS ENTIDADES
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);

                if (GameObject.isColliding(obj1, obj2)) {
                    // HOUVE COLISÃO! Agora, vamos ver que tipo de colisão foi.
                    
                	 // Colisão entre Player e Inimigo (em qualquer ordem)
                    if ((obj1 instanceof Player && obj2 instanceof Enemy) || 
                        (obj1 instanceof Enemy && obj2 instanceof Player)) {

                        // Identifica quem é o Player e quem é o Enemy
                    	Player player = GameObject.getInstanceOf(Player.class, obj1, obj2);
                        Enemy enemy = GameObject.getInstanceOf(Enemy.class, obj1, obj2);

                        // Agora, execute a lógica de colisão UMA VEZ
                        System.out.println("Colisão entre " + player.getClass().getSimpleName() + " e " + enemy.getClass().getSimpleName());
                        // Ex: player.sofrerDano(enemy.getForcaAtaque());
                    }

                    // Colisão entre Player e Lifepack (em qualquer ordem)
                    if ((obj1 instanceof Player && obj2 instanceof Lifepack) ||
                        (obj1 instanceof Lifepack && obj2 instanceof Player)) {
                            
                    	Player player = GameObject.getInstanceOf(Player.class, obj1, obj2);
                        Lifepack lifepack = GameObject.getInstanceOf(Lifepack.class, obj1, obj2);

                        System.out.println("Jogador pegou um Lifepack!");
                        // Ex: player.curar(lifepack.getValorCura());

                        // Remove o item do jogo
                        gameObjects.remove(lifepack);
                        j--; // Ajusta o índice para não pular o próximo item
                    }
                    if((obj1 instanceof Player && obj2 instanceof Weapon) ||
                    	(obj1 instanceof Weapon && obj2 instanceof Player)){
                    	
                    	Player player = GameObject.getInstanceOf(Player.class, obj1, obj2);
                    	Weapon weapon = GameObject.getInstanceOf(Weapon.class, obj1, obj2);
                    	
                    	System.out.println("Colisão entre " + player.getClass().getSimpleName() + " e " + weapon.getClass().getSimpleName());
                    	
                    }
                }
            }
            }
        
        // Lógica da Câmera
        Camera.x = Camera.clamp(player.getX() - (Engine.WIDTH / 2), 0, world.WIDTH * 16 - Engine.WIDTH);
        Camera.y = Camera.clamp(player.getY() - (Engine.HEIGHT / 2), 0, world.HEIGHT * 16 - Engine.HEIGHT);
        InputManager.instance.update();
    }

    @Override
    public void render(Graphics g) {
        world.render(g);
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).render(g);
        }
    }

    
}