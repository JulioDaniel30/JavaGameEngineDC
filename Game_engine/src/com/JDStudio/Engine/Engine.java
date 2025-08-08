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
    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;
    public static boolean isDebug = false;
    public static boolean showFPS = false;
    public static Camera camera;

    private Thread thread;
    private boolean isRunning = true;
    private BufferedImage image;
    
    // --- Sistema de Pilha de Estados ---
    private static Stack<GameState> gameStates = new Stack<>();
    private static Class<? extends GameState> initialGameStateClass;
    
    // --- Sistema de Transição ---
    private TransitionManager transitionManager;
    private static Engine instance;
    

    private static double FPS;
    private static double CURRENT_FPS = 0;

    public Engine(Double FPS) {
    	Engine.FPS = FPS;
    	instance = this;
    	this.transitionManager = new TransitionManager(this); 
    	this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
        
        addKeyListener(InputManager.instance);
        addMouseListener(InputManager.instance);
        addMouseMotionListener(InputManager.instance);
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        camera = new Camera(0, 0);
    }
    
    public void initFrame() {
        frame = new JFrame("Game Engine");
        frame.add(this);
        frame.setResizable(false);
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

    // --- MÉTODOS DE GESTÃO DE ESTADOS (AGORA COMPLETOS) ---

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
        // Limpa a pilha inteira antes de adicionar o novo estado
        while (!gameStates.isEmpty()) {
            gameStates.pop().onExit();
        }
        pushState(state);
    }
    
    // Método interno para o TransitionManager usar
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
        transitionToState(nextState, 8, Color.BLACK); // Velocidade padrão um pouco mais rápida
    }
    
    public static void restartGame() {
        System.out.println("--- REINICIANDO O JOGO ---");
        EventManager.getInstance().reset();
        ProjectileManager.getInstance().reset();
        LightingManager.getInstance().reset();
        DialogueManager.getInstance().reset();
        try {
            transitionToState(initialGameStateClass.getConstructor().newInstance());
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO: Não foi possível reiniciar o jogo.");
            e.printStackTrace();
        }
    }
    
    public static void restartCurrentState() {
        if (!gameStates.isEmpty()) {
            GameState currentState = gameStates.peek();
            System.out.println("--- REINICIANDO ESTADO ATUAL ---");
            EventManager.getInstance().reset();
            ProjectileManager.getInstance().reset();
            LightingManager.getInstance().reset();
            DialogueManager.getInstance().reset();
            try {
                transitionToState(currentState.getClass().getConstructor().newInstance());
            } catch (Exception e) {
                System.err.println("ERRO CRÍTICO: Não foi possível reiniciar o estado atual.");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Reinicia o GameState que estava ativo antes do estado de topo atual.
     * Ideal para um botão de "Tentar Novamente" numa tela de Game Over.
     */
    public static void restartPreviousState() {
        if (gameStates.size() > 1) {
            // Guarda o estado de topo (ex: GameOverState) para remover depois
            @SuppressWarnings("unused")
			GameState topState = gameStates.peek();
            // Pega o estado que está por baixo (ex: PlayingState)
            GameState previousState = gameStates.get(gameStates.size() - 2);

            System.out.println("--- REINICIANDO ESTADO ANTERIOR ---");
            EventManager.getInstance().reset();
            ProjectileManager.getInstance().reset();
            LightingManager.getInstance().reset();
            DialogueManager.getInstance().reset();
            
            try {
                // Cria uma nova instância do estado anterior
                GameState newState = previousState.getClass().getConstructor().newInstance();
                // Usa a transição para substituir a pilha inteira pelo novo estado
                transitionToState(newState);
            } catch (Exception e) {
                System.err.println("ERRO CRÍTICO: Não foi possível reiniciar o estado anterior.");
                e.printStackTrace();
            }
        } else {
            System.err.println("AVISO: Nenhum estado anterior na pilha para reiniciar. Reiniciando o jogo todo.");
            restartGame();
        }
    }

    public static double getCurrentFPS() {
		return CURRENT_FPS;
	}
    
    public static double getFPS() {
    	return FPS;
    }

    private void tick() {
        // 1. A transição tem prioridade. Se estiver a acontecer, o jogo não atualiza.
        if (transitionManager.isTransitioning()) {
            transitionManager.update();
            return; // Para a execução do tick aqui
        }

        // 2. Se não houver transição, atualiza o estado no topo da pilha.
        if (!gameStates.isEmpty()) {
            gameStates.peek().tick();
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Renderiza TODOS os estados na pilha (para o menu de pausa funcionar)
        for (GameState state : gameStates) {
            state.render(g);
        }
        
        // O transition manager desenha o fade por cima de tudo
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
                tick();
                render();
                InputManager.instance.update();
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
}