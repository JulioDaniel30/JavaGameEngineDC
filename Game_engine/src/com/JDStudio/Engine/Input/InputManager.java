// Arquivo: InputManager.java
package com.JDStudio.Engine.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Gerencia todo o input do teclado de forma centralizada usando o padrão Singleton.
 * <p>
 * Para usar, obtenha a instância única através de {@code InputManager.instance} e
 * adicione-a como um {@link KeyListener} ao seu componente principal (ex: o Canvas do jogo).
 *
 * @author JDStudio
 * @since 1.0
 */
public class InputManager implements KeyListener {

    /** A instância única e global do gerenciador de input. */
    public static final InputManager instance = new InputManager();

    /** Array que armazena o estado atual das teclas (pressionada ou não). */
    private final boolean[] keys = new boolean[256];
    
    /** Array que armazena o estado das teclas no frame anterior. */
    private final boolean[] prevKeys = new boolean[256];

    /**
     * Construtor privado para garantir que apenas uma instância desta classe exista (padrão Singleton).
     */
    private InputManager() {}

    /**
     * Atualiza o estado das teclas para o próximo frame.
     * <p>
     * Este método copia o estado atual das teclas para o array de estado anterior.
     * <strong>Importante:</strong> Deve ser chamado no <strong>final</strong> de cada iteração
     * do loop principal do jogo para que a detecção de "toque único" funcione.
     */
    public void update() {
        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
    }

    /**
     * Verifica se uma tecla está atualmente pressionada (sendo segurada).
     *
     * @param keyCode O código da tecla a ser verificada (ex: {@link KeyEvent#VK_W}).
     * @return {@code true} se a tecla estiver pressionada, {@code false} caso contrário.
     */
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            return instance.keys[keyCode];
        }
        return false;
    }

    /**
     * Verifica se uma tecla acabou de ser pressionada neste exato frame.
     * <p>
     * Ideal para ações de toque único, como pular, atirar ou interagir com objetos.
     *
     * @param keyCode O código da tecla a ser verificada (ex: {@link KeyEvent#VK_SPACE}).
     * @return {@code true} se a tecla foi pressionada neste frame e não no anterior, {@code false} caso contrário.
     */
    public static boolean isKeyJustPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < instance.keys.length) {
            // A tecla está pressionada agora E não estava pressionada no frame anterior?
            return instance.keys[keyCode] && !instance.prevKeys[keyCode];
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Chamado pelo AWT quando uma tecla é pressionada. Este método não deve ser chamado diretamente.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < keys.length) {
            keys[keyCode] = true;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Chamado pelo AWT quando uma tecla é solta. Este método não deve ser chamado diretamente.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < keys.length) {
            keys[keyCode] = false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Não utilizado por este gerenciador.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Intencionalmente deixado em branco.
    }
}