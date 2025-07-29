package com.JDStudio.Engine.Graphics.Sprite.Animations;

import java.util.HashMap;
import java.util.Map;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Um componente que gerencia um conjunto de animações para um GameObject.
 * Atua como uma máquina de estados para as animações, controlando qual delas
 * está ativa e atualizando sua lógica.
 */
public class Animator {

    /** Mapa que armazena todas as animações disponíveis, associadas a uma chave de texto. */
    private Map<String, Animation> animations;
    
    /** A chave de texto da animação que está atualmente em execução. */
    private String currentAnimationKey;
    
    /** Referência direta à instância da animação ativa para acesso rápido. */
    private Animation currentAnimation;

    /**
     * Cria uma nova instância de Animator, inicializando o mapa de animações.
     */
    public Animator() {
        this.animations = new HashMap<>();
    }

    /**
     * Adiciona uma nova animação ao gerenciador.
     * <p>
     * Se esta for a primeira animação adicionada, ela será definida
     * como a animação ativa por padrão.
     *
     * @param key       O nome da animação (ex: "walk_right", "idle").
     * @param animation O objeto {@link Animation}.
     */
    public void addAnimation(String key, Animation animation) {
        animations.put(key, animation);
        if (currentAnimation == null) {
            play(key);
        }
    }

    /**
     * Define e inicia uma animação com base na sua chave.
     * <p>
     * A animação é reiniciada a partir do primeiro quadro. Se a animação solicitada
     * já estiver em execução, nada acontece para evitar reinícios desnecessários.
     *
     * @param key O nome da animação a ser executada.
     */
    public void play(String key) {
        // Otimização: Evita reiniciar a animação se ela já estiver tocando.
        if (key.equals(currentAnimationKey)) {
            return;
        }

        if (animations.containsKey(key)) {
            this.currentAnimationKey = key;
            this.currentAnimation = animations.get(key);
            this.currentAnimation.reset();
        } else {
            System.err.println("Erro: A animação '" + key + "' não foi encontrada no Animator.");
        }
    }

    /**
     * Atualiza a lógica da animação ativa, avançando seu quadro se necessário.
     * Deve ser chamado a cada tick do jogo.
     */
    public void tick() {
        if (currentAnimation != null) {
            currentAnimation.tick();
        }
    }

    /**
     * Obtém o sprite (frame) atual da animação ativa.
     *
     * @return O {@link Sprite} a ser renderizado, ou {@code null} se nenhuma animação estiver ativa.
     */
    public Sprite getCurrentSprite() {
        if (currentAnimation != null) {
            return currentAnimation.getCurrentFrame();
        }
        return null;
    }
    
    /**
     * Retorna a chave da animação que está atualmente em execução.
     * Útil para verificar o estado atual do Animator.
     *
     * @return A chave da animação ativa, ou {@code null} se nenhuma estiver ativa.
     */
    public String getCurrentAnimationKey() {
        return this.currentAnimationKey;
    }
    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }
}