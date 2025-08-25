package com.jdstudio.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;

import com.jdstudio.engine.Dialogue.DialogueManager;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Lighting.LightingManager;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Object.ProjectileManager;
import com.jdstudio.engine.States.GameState;
import com.jdstudio.engine.World.Camera;

/**
 * The core of the game engine.
 * This class is responsible for creating the game window, managing the main game loop,
 * handling game states, and providing central access to engine functionalities.
 * It extends {@link Canvas} to draw graphics and implements {@link Runnable} to run the game loop on a separate thread.
 *
 * @author JDStudio
 */
public class Engine extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    /** The main window (frame) of the game. */
    public static JFrame frame;
    /** The native width of the game in pixels, before scaling. */
    public static int WIDTH;
    /** The native height of the game in pixels, before scaling. */
    public static int HEIGHT;
    /** The multiplier used to scale the game window. */
    public static int SCALE;
    /** Global flag to enable or disable debug rendering and information. */
    public static boolean isDebug = false;
    /** Global flag to show or hide the current FPS in the console. */
    public static boolean showFPS = false;
    /** The main camera used to control the viewport of the game world. */
    public static Camera camera;
    
    private static double FPS;
    private static double CURRENT_FPS = 0;

    private Thread thread;
    private boolean isRunning = true;
    private BufferedImage image;
    
    private static Stack<GameState> gameStates = new Stack<>();
    private static Class<? extends GameState> initialGameStateClass;
    
    private TransitionManager transitionManager;
    private static Engine instance;

    // --- Scheduled Task System (Wait) ---
    private static final List<DelayedTask> delayedTasks = new CopyOnWriteArrayList<>();

    /** Represents a task to be executed after a delay. */
    private static class DelayedTask {
        long executionTime;
        Runnable action;

        DelayedTask(long delayMs, Runnable action) {
            this.executionTime = System.currentTimeMillis() + delayMs;
            this.action = action;
        }
    }

    /**
     * Constructs the game engine.
     * @param width The native width of the game.
     * @param height The native height of the game.
     * @param scale The scaling factor for the window.
     * @param isResizable If the window should be resizable by the user.
     * @param title The title of the game window.
     * @param fps The target frames per second for the game loop.
     */
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
    
    /**
     * Initializes the main game window (JFrame).
     * @param title The title for the window.
     * @param isResizable Whether the window can be resized.
     */
    public void initFrame(String title, boolean isResizable) {
        frame = new JFrame(title);
        frame.add(this);
        frame.setResizable(isResizable);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /**
     * Starts the main game loop in a new thread.
     * If an initial game state has been set, it will be pushed to the stack.
     */
    public synchronized void start() {
    	thread = new Thread(this);
        isRunning = true;
        if (initialGameStateClass != null && gameStates.isEmpty()) {
            try {
                pushState(initialGameStateClass.getConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create the initial GameState.");
            }
        }
        thread.start();
    }

    /**
     * Stops the game loop and waits for the thread to terminate.
     */
    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // --- STATE MANAGEMENT METHODS ---

    /**
     * Pushes a new state onto the game state stack.
     * The new state becomes the active state.
     * @param state The GameState to add.
     */
    public static void pushState(GameState state) {
        if (state != null) {
            gameStates.push(state);
            state.onEnter();
        }
    }

    /**
     * Pops the current state from the game state stack.
     * The previous state in the stack becomes the active one.
     */
    public static void popState() {
        if (!gameStates.isEmpty()) {
            gameStates.pop().onExit();
        }
    }

    /**
     * Clears the entire game state stack and pushes a new state.
     * @param state The new GameState to set as the only one.
     */
    public static void setGameState(GameState state) {
        while (!gameStates.isEmpty()) {
            gameStates.pop().onExit();
        }
        pushState(state);
    }
    
    /** For internal use by the TransitionManager. */
    void setGameStateInternal(GameState state) {
        setGameState(state);
    }
    
    /**
     * Sets the class of the initial game state to be loaded when the engine starts.
     * @param stateClass The class of the initial GameState.
     */
    public static void setInitialGameState(Class<? extends GameState> stateClass) {
        initialGameStateClass = stateClass;
    }

    /**
     * Initiates a smooth transition (e.g., fade-out, fade-in) to a new game state.
     * @param nextState The target GameState for the transition.
     * @param speed The speed of the transition effect.
     * @param color The color of the transition effect (e.g., black for a fade-to-black).
     */
    public static void transitionToState(GameState nextState, int speed, Color color) {
        if (instance != null) {
            instance.transitionManager.startTransition(nextState, speed, color);
        }
    }
    
    /**
     * Initiates a transition to a new game state with default speed and color (black).
     * @param nextState The target GameState for the transition.
     */
    public static void transitionToState(GameState nextState) {
        transitionToState(nextState, 8, Color.BLACK);
    }
    
    /**
     * Restarts the entire game by transitioning to the initial game state and resetting all managers.
     */
    public static void restartGame() {
        System.out.println("--- RESTARTING GAME ---");
        resetManagers();
        try {
            transitionToState(initialGameStateClass.getConstructor().newInstance());
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /**
     * Restarts the currently active game state.
     */
    public static void restartCurrentState() {
        if (!gameStates.isEmpty()) {
            GameState currentState = gameStates.peek();
            System.out.println("--- RESTARTING CURRENT STATE ---");
            resetManagers();
            try {
                transitionToState(currentState.getClass().getConstructor().newInstance());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    /**
     * Restarts the previous game state if one exists in the stack.
     * If not, restarts the entire game.
     */
    public static void restartPreviousState() {
        if (gameStates.size() > 1) {
            GameState previousState = gameStates.get(gameStates.size() - 2);
            System.out.println("--- RESTARTING PREVIOUS STATE ---");
            resetManagers();
            try {
                transitionToState(previousState.getClass().getConstructor().newInstance());
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            System.err.println("WARNING: No previous state in stack to restart. Restarting the whole game.");
            restartGame();
        }
    }
    
    /** Resets all core managers to their default state. */
    private static void resetManagers(){
        EventManager.getInstance().reset();
        ProjectileManager.getInstance().reset();
        LightingManager.getInstance().reset();
        DialogueManager.getInstance().reset();
    }

    // --- CORE LOGIC (TICK AND RENDER) ---

    /**
     * The main logic update method.
     * It processes scheduled tasks and updates the current game state.
     * Called by the game loop at a fixed rate (FPS).
     */
    private void tick() {
        updateDelayedTasks(); // Process scheduled tasks

        if (transitionManager.isTransitioning()) {
            transitionManager.update();
            return;
        }

        if (!gameStates.isEmpty()) {
            gameStates.peek().tick();
        }
    }

    /**
     * The main rendering method.
     * It draws all visible game states and transition effects to the screen.
     */
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

    /**
     * The main game loop, which drives the entire engine.
     * It ensures that the tick() and render() methods are called at the target FPS.
     */
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
                InputManager.instance.pollEvents();
                tick();
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

    // --- PUBLIC UTILITY METHODS ---

    /**
     * Schedules an action to be executed after a specified delay, without blocking the game thread.
     * @param milliseconds The delay time in milliseconds.
     * @param action The action (code) to execute. Use a lambda expression: () -> yourFunction()
     */
    public static void wait(int milliseconds, Runnable action) {
        if (milliseconds <= 0 || action == null) {
            if (action != null) action.run(); // Execute immediately if time is zero or negative
            return;
        }
        delayedTasks.add(new DelayedTask(milliseconds, action));
    }

    /** Processes the list of delayed tasks, executing any that are due. */
    private void updateDelayedTasks() {
        if (delayedTasks.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        for (DelayedTask task : delayedTasks) {
            if (currentTime >= task.executionTime) {
                task.action.run();
                delayedTasks.remove(task);
            }
        }
    }

    /** @return The native width of the game. */
	public static int getWIDTH() {
		return WIDTH;
	}

    /** @return The native height of the game. */
	public static int getHEIGHT() {
		return HEIGHT;
	}

    /** @return The scale factor of the game window. */
	public static int getSCALE() {
		return SCALE;
	}

    /** @return The target FPS of the game. */
	public static double getFPS() {
		return FPS;
	}

    /** @return The number of frames rendered in the last second. */
    public static double getCurrentFPS() {
        return CURRENT_FPS;
    }
}