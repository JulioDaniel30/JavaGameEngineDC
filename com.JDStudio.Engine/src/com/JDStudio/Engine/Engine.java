// engine
package com.JDStudio.Engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.JFrame;

import com.JDStudio.Engine.Dialogue.DialogueManager;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Graphics.Lighting.LightingManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.ProjectileManager;
import com.JDStudio.Engine.States.GameState;
import com.JDStudio.Engine.World.Camera;

public class Engine extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    public static JFrame frame;
    // --- Suas variáveis estáticas foram MANTIDAS ---
    public static int WIDTH;
    public static int HEIGHT;
    public static int SCALE;
    public static boolean isDebug = false;
    public static boolean showFPS = false;
    public static Camera camera;
    private static double FPS;
    private static double CURRENT_FPS = 0;

    private Thread thread;
    private boolean isRunning = true;
    private BufferedImage image;
    
    // --- O sistema agora usa APENAS a pilha de estados ---
    private static Stack<GameState> gameStates = new Stack<>();
    private static Class<? extends GameState> initialGameStateClass;
    
    private TransitionManager transitionManager;
    private static Engine instance;

    public Engine(int width, int height, int scale, boolean isResizable, String title, Double fps) {
    	Engine.FPS = fps;
    	Engine.WIDTH = width;
    	Engine.HEIGHT = height;
    	Engine.SCALE = scale;
    	instance = this;
    	this.transitionManager = new TransitionManager(this); 
    	this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame(title, isResizable);
        
        addKeyListener(InputManager.instance);
        addMouseListener(InputManager.instance);
        addMouseMotionListener(InputManager.instance);
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        camera = new Camera(0, 0);
    }
    
    public void initFrame(String title, boolean isResizable) {
        frame = new JFrame(title);
        frame.add(this);
        frame.setResizable(isResizable);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public synchronized void start() {
    	thread = new Thread(this);
        isRunning = true;
        if (initialGameStateClass != null && gameStates.isEmpty()) {
            try {
                // Usa pushState para adicionar o primeiro estado à pilha
                pushState(initialGameStateClass.getConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Falha ao criar o GameState inicial.");
            }
        }
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE GESTÃO DE ESTADOS UNIFICADOS ---

    public static void pushState(GameState state) {
        if (state != null) {
            gameStates.push(state);
            state.onEnter();
        }
    }

    public static void popState() {
        if (!gameStates.isEmpty()) {
            gameStates.pop().onExit();
        }
    }

    public static void setGameState(GameState state) {
        while (!gameStates.isEmpty()) {
            gameStates.pop().onExit();
        }
        pushState(state);
    }
    
    void setGameStateInternal(GameState state) {
        setGameState(state);
    }
    
    public static void setInitialGameState(Class<? extends GameState> stateClass) {
        initialGameStateClass = stateClass;
    }

    public static void transitionToState(GameState nextState, int speed, Color color) {
        if (instance != null) {
            instance.transitionManager.startTransition(nextState, speed, color);
        }
    }
    
    public static void transitionToState(GameState nextState) {
        transitionToState(nextState, 8, Color.BLACK);
    }
    
    public static void restartGame() {
        System.out.println("--- REINICIANDO O JOGO ---");
        resetManagers();
        try {
            transitionToState(initialGameStateClass.getConstructor().newInstance());
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public static void restartCurrentState() {
        if (!gameStates.isEmpty()) {
            GameState currentState = gameStates.peek();
            System.out.println("--- REINICIANDO ESTADO ATUAL ---");
            resetManagers();
            try {
                transitionToState(currentState.getClass().getConstructor().newInstance());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public static void restartPreviousState() {
        if (gameStates.size() > 1) {
            GameState previousState = gameStates.get(gameStates.size() - 2);
            System.out.println("--- REINICIANDO ESTADO ANTERIOR ---");
            resetManagers();
            try {
                transitionToState(previousState.getClass().getConstructor().newInstance());
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            System.err.println("AVISO: Nenhum estado anterior na pilha para reiniciar. Reiniciando o jogo todo.");
            restartGame();
        }
    }
    
    private static void resetManagers(){
        EventManager.getInstance().reset();
        ProjectileManager.getInstance().reset();
        LightingManager.getInstance().reset();
        DialogueManager.getInstance().reset();
    }

    // --- LÓGICA PRINCIPAL (TICK E RENDER) ---

    private void tick() {
        if (transitionManager.isTransitioning()) {
            transitionManager.update();
            return;
        }

        if (!gameStates.isEmpty()) {
            gameStates.peek().tick();
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) { this.createBufferStrategy(3); return; }
        
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (GameState state : gameStates) {
            state.render(g);
        }
        
        transitionManager.render(g);
        
        g.dispose();

        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = FPS;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus();
        
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            if (delta >= 1) {
            	// 1. Processa todos os inputs que aconteceram desde a última frame.
                InputManager.instance.pollEvents();
                
                // 2. Executa a lógica do jogo com um estado de input 100% fiável.
                tick();
                
                // 3. Renderiza o resultado.
                render();
                
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
            	if(showFPS) { System.out.println("FPS: " + frames); }
                CURRENT_FPS = frames;
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }

	public static int getWIDTH() {
		return WIDTH;
	}

	public static int getHEIGHT() {
		return HEIGHT;
	}

	public static int getSCALE() {
		return SCALE;
	}

	public static double getFPS() {
		return FPS;
	}
}