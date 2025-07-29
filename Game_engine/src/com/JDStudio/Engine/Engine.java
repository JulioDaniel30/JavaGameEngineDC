// engine
package com.JDStudio.Engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.States.GameState;
import com.JDStudio.Engine.World.Camera; // Importação da câmera

public class Engine extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    public static JFrame frame;
    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;
    public static boolean isDebug = false;

    // --- Câmera agora é uma instância ---
    public static Camera camera;

    private Thread thread;
    private boolean isRunning = true;
    private BufferedImage image;
    private static GameState currentGameState;

    public Engine() {
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
        addKeyListener(InputManager.instance);
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        // --- Inicializa a câmera ---
        camera = new Camera(0, 0);
    }

    // ... (main, initFrame, start, stop, setGameState, tick - permanecem iguais) ...
    public static void main(String[] args) {
        Engine engine = new Engine();
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

    public static void setGameState(GameState state) {
        currentGameState = state;
    }

    private void tick() {
        if (currentGameState != null) {
            currentGameState.tick();
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

        if (currentGameState != null) {
            currentGameState.render(g);
        }
        g.dispose();

        g = bs.getDrawGraphics();
        
        // --- Renderização final agora usa o ZOOM da câmera ---
        int finalWidth = (int)(WIDTH * SCALE * camera.getZoom());
        int finalHeight = (int)(HEIGHT * SCALE * camera.getZoom());
        g.drawImage(image, 0, 0, finalWidth, finalHeight, null);
        
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
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
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                if(isDebug) { System.out.println("FPS: " + frames); }
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }
}