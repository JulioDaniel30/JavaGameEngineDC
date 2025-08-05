// engine
package com.JDStudio.Engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.JDStudio.Engine.Dialogue.DialogueManager;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Graphics.Lighting.LightingManager;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.ProjectileManager;
import com.JDStudio.Engine.States.GameState;
import com.JDStudio.Engine.World.Camera; // Importação da câmera

public class Engine extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    public static JFrame frame;
    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;
    public static boolean isDebug = false;
    public static boolean showFPS = false;
    private static double FPS;
    private static double CURRENT_FPS = 0;

    // --- Câmera agora é uma instância ---
    public static Camera camera;

    private Thread thread;
    private boolean isRunning = true;
    private BufferedImage image;
    private static GameState currentGameState;
    private static Class<? extends GameState> initialGameStateClass;
    private static GameState previousGameState;
    private TransitionManager transitionManager;
    private static Engine instance;

    public Engine(Double FPS) {
    	this.FPS = FPS;
    	instance = this;
    	this.transitionManager = new TransitionManager(this); 
    	this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
        
        // Adiciona listeners de input
        addKeyListener(InputManager.instance);
        addMouseListener(InputManager.instance);
        addMouseMotionListener(InputManager.instance);
        
        // Cria a imagem principal do jogo (o buffer)
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB); // Já corrigido para ARGB
        camera = new Camera(0, 0);
    }
    //nao precisa
    public static void main(String[] args) {
        Engine engine = new Engine(60.0);
        engine.start();
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

        // --- MUDANÇA AQUI: Cria a primeira instância do estado inicial ---
        if (initialGameStateClass != null && currentGameState == null) {
            try {
                setGameState(initialGameStateClass.getConstructor().newInstance());
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

    /**
     * O Jogo usa este método UMA VEZ para dizer à Engine qual é a sua cena inicial.
     * @param stateClass A classe do estado inicial (ex: MenuState.class).
     */
    public static void setInitialGameState(Class<? extends GameState> stateClass) {
        initialGameStateClass = stateClass;
    }
    
    /**
     * Troca o estado de jogo atual, guardando o anterior.
     */
    public static void setGameState(GameState state) {
        if (state != null) {
            if (currentGameState != null) {
                currentGameState.onExit();
                previousGameState = currentGameState; // Guarda o estado que está a sair
            }
            currentGameState = state;
            currentGameState.onEnter();
        }
    }
    
    void setGameStateInternal(GameState state) {
        if (state != null) {
            if (currentGameState != null) {
                currentGameState.onExit();
            }
            currentGameState = state;
            currentGameState.onEnter();
        }
    }

    /**
     * O NOVO método público para trocar de estado com parâmetros customizáveis.
     * @param nextState O próximo GameState.
     * @param speed A velocidade do fade (ex: 5).
     * @param color A cor do fade (ex: Color.BLACK).
     */
    public static void transitionToState(GameState nextState, int speed, Color color) {
        if (instance != null) {
            instance.transitionManager.startTransition(nextState, speed, color);
        }
    }
    
    // Sobrecarga para uma transição padrão (preto, velocidade 5)
    public static void transitionToState(GameState nextState) {
        transitionToState(nextState, 5, Color.BLACK);
    }
    
    /**
     * Reinicia o GameState ATUAL, criando uma nova instância dele.
     * Ideal para uma função "Reiniciar Fase" num menu de pausa.
     */
    public static void restartCurrentState() {
        if (currentGameState != null) {
            System.out.println("--- REINICIANDO ESTADO ATUAL ---");
            
            // 1. Reseta todos os managers para garantir um reinício limpo.
            EventManager.getInstance().reset();
            ProjectileManager.getInstance().reset();
            LightingManager.getInstance().reset();
            DialogueManager.getInstance().reset();
            // Adicione aqui o reset de qualquer outro manager que você criar.

            try {
                // 2. Usa a classe do estado ATUAL para criar uma nova instância.
                GameState newState = currentGameState.getClass().getConstructor().newInstance();
                
                // 3. Usa a transição suave para o novo estado.
                transitionToState(newState);
            } catch (Exception e) {
                System.err.println("ERRO CRÍTICO: Não foi possível reiniciar o estado atual.");
                e.printStackTrace();
            }
        } else {
            System.err.println("AVISO: Nenhum estado atual para reiniciar. Reiniciando o jogo todo.");
            restartGame(); // Como fallback, reinicia o jogo se não houver estado.
        }
    }
    
    /**
     * Reinicia o GameState que estava ativo antes do atual.
     * Ideal para um botão de "Tentar Novamente".
     */
    public static void restartPreviousState() {
        if (previousGameState != null) {
            System.out.println("--- REINICIANDO ESTADO ANTERIOR ---");
            
            // Limpa os managers para evitar lixo da "partida" anterior (opcional, mas recomendado)
            EventManager.getInstance().reset();
            ProjectileManager.getInstance().reset();
            LightingManager.getInstance().reset();
            DialogueManager.getInstance().reset();

            try {
                // Usa a classe do estado anterior para criar uma nova instância
                GameState newState = previousGameState.getClass().getConstructor().newInstance();
                transitionToState(newState); // Usa a transição suave
            } catch (Exception e) {
                System.err.println("ERRO CRÍTICO: Não foi possível reiniciar o estado anterior.");
                e.printStackTrace();
            }
        } else {
            System.err.println("AVISO: Nenhum estado anterior para reiniciar. Voltando ao início.");
            restartGame(); // Se não houver estado anterior, reinicia o jogo todo
        }
    }
    
    /**
     * Reinicia o jogo completamente.
     * Reseta todos os managers e carrega um novo estado inicial.
     */
    public static void restartGame() {
        System.out.println("--- REINICIANDO O JOGO ---");
        
        // 1. Reseta todos os "buffers" e sistemas globais da engine
        EventManager.getInstance().reset();
        ProjectileManager.getInstance().reset();
        LightingManager.getInstance().reset();
        DialogueManager.getInstance().reset();
        // Adicione aqui o reset de qualquer outro manager que você criar

        // 2. Cria uma nova instância do estado inicial
        try {
            setGameState(initialGameStateClass.getConstructor().newInstance());
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO: Não foi possível reiniciar o jogo. O estado inicial não pôde ser criado.");
            e.printStackTrace();
        }
    }
    
    private void tick() {
    	
    	 transitionManager.update(); // O transition manager cuida da lógica do fade

         // Só atualiza o jogo se não estiver em transição
         if (!transitionManager.isTransitioning() && currentGameState != null) {
                    currentGameState.tick();
                    if(InputManager.isActionJustPressed("TOGGLE_FPS_COUNTER")) showFPS = ! showFPS;
                }
        }
	

    public static double getCurrentFPS() {
		return CURRENT_FPS;
	}
    
    public static double getFPS() {
    	return FPS;
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

        if (currentGameState != null) {
            currentGameState.render(g);
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
       requestFocusInWindow();
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