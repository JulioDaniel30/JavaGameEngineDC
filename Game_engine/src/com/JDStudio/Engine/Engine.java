package com.JDStudio.Engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import com.JDStudio.Engine.Input.InputManager;

/**
 * A classe principal do motor do jogo, responsável por inicializar a janela,
 * gerenciar o loop principal (game loop) e coordenar os estados de jogo.
 * <p>
 * Ela estende {@link Canvas} para ser o componente de desenho e implementa
 * {@link Runnable} para executar o game loop em uma thread separada.
 *
 * @author JDStudio
 * @since 1.0
 */
public class Engine extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    /** A janela principal (frame) do jogo. */
    public static JFrame frame;
    
    /** A largura interna (lógica) do jogo em pixels. */
    public static final int WIDTH = 240;
    
    /** A altura interna (lógica) do jogo em pixels. */
    public static final int HEIGHT = 160;
    
    /** O fator de escala para ampliar a resolução interna para o tamanho da janela. */
    public static final int SCALE = 3;
    
    /** Flag global para ativar/desativar a renderização de informações de debug. */
    public static boolean isDebug = false;
    
    /** A thread principal onde o game loop é executado. */
    private Thread thread;
    
    /** Controla se o game loop deve continuar executando. */
    private boolean isRunning = true;

    /** A imagem de back-buffer onde toda a renderização do jogo é feita antes de ser exibida na tela. */
    private BufferedImage image;
    
    /** O estado de jogo atual (ex: Menu, Jogo Principal, Game Over). */
    private static GameState currentGameState;

    /**
     * Construtor do motor do jogo.
     * Configura as dimensões do canvas, inicializa a janela, registra o listener
     * de input e cria o buffer de imagem para a renderização.
     */
    public Engine() {
        this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        initFrame();
        addKeyListener(InputManager.instance);
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Ponto de entrada principal da aplicação.
     * Cria e inicia uma nova instância do motor do jogo.
     */
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();
    }
    
    /**
     * Inicializa e configura a janela principal (JFrame) do jogo.
     */
    public void initFrame() {
        frame = new JFrame("Game Engine");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /**
     * Inicia a thread do jogo de forma segura.
     */
    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    /**
     * Para a thread do jogo de forma segura, aguardando sua finalização.
     */
    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Define o estado de jogo atual, trocando a lógica e a renderização ativas.
     * @param state O novo {@link GameState} a ser ativado.
     */
    public static void setGameState(GameState state) {
        currentGameState = state;
    }

    /**
     * Executa um passo da lógica do jogo (tick), delegando ao estado de jogo atual.
     */
    private void tick() {
        if (currentGameState != null) {
            currentGameState.tick();
        }
    }

    /**
     * Realiza a renderização de um quadro (frame) completo.
     * <p>
     * Limpa a tela, delega a renderização ao estado de jogo atual, e então
     * desenha a imagem final na tela usando um {@link BufferStrategy} para
     * evitar flickering (double buffering).
     */
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3); // 3 buffers para "triple buffering"
            return;
        }
        
        // Desenha tudo em uma imagem de back-buffer
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (currentGameState != null) {
            currentGameState.render(g);
        }
        g.dispose();

        // Mostra a imagem do back-buffer na tela
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
        bs.show();
    }

    /**
     * O coração do motor: o loop principal do jogo (game loop).
     * <p>
     * Implementa uma lógica de timestep fixo, garantindo que a lógica do jogo ({@code tick()})
     * execute a uma taxa constante (60 vezes por segundo), independentemente da
     * velocidade de renderização, resultando em um comportamento consistente.
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus(); // Garante que o canvas receba o foco para o input funcionar

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
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }
}