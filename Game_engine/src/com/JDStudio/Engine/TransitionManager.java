package com.JDStudio.Engine;

import java.awt.Color;
import java.awt.Graphics;
import com.JDStudio.Engine.States.GameState;

public class TransitionManager {

    private enum State { IDLE, FADE_OUT, FADE_IN }
    private State currentState = State.IDLE;
    
    private GameState nextGameState;
    private int fadeAlpha = 0;
    private int fadeSpeed = 5;
    private Color fadeColor = Color.BLACK;

    private final Engine engine;

    public TransitionManager(Engine engine) {
        this.engine = engine;
    }

    public void startTransition(GameState nextState, int speed, Color color) {
        if (currentState == State.IDLE) {
            this.nextGameState = nextState;
            this.fadeSpeed = Math.max(1, speed);
            this.fadeColor = color;
            this.currentState = State.FADE_OUT;
        }
    }

    public void update() {
        switch (currentState) {
            case FADE_OUT:
                fadeAlpha += fadeSpeed;
                if (fadeAlpha >= 255) {
                    fadeAlpha = 255;
                    engine.setGameStateInternal(nextGameState); // Pede para a engine trocar o estado
                    nextGameState = null;
                    currentState = State.FADE_IN;
                }
                break;
            case FADE_IN:
                fadeAlpha -= fadeSpeed;
                if (fadeAlpha <= 0) {
                    fadeAlpha = 0;
                    currentState = State.IDLE;
                }
                break;
            case IDLE:
                break;
        }
    }

    public void render(Graphics g) {
        if (fadeAlpha > 0) {
            g.setColor(new Color(fadeColor.getRed(), fadeColor.getGreen(), fadeColor.getBlue(), fadeAlpha));
            g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT);
        }
    }

    public boolean isTransitioning() {
        return currentState != State.IDLE;
    }
}