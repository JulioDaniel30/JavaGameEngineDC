package com.JDStudio.Engine.Core;

import java.util.HashSet;
import java.util.Set;

/**
 * Um Singleton que gere o estado global do jogo através de "flags".
 * As flags são marcadores simples que indicam que um evento ocorreu (ex: uma quest foi aceite).
 */
public class GameStateManager {
    private static final GameStateManager instance = new GameStateManager();
    private Set<String> flags = new HashSet<>();

    private GameStateManager() {}
    public static GameStateManager getInstance() { return instance; }

    /** Adiciona uma flag ao estado do jogo. */
    public void setFlag(String flag) { flags.add(flag); }
    
    /** Remove uma flag do estado do jogo. */
    public void removeFlag(String flag) { flags.remove(flag); }
    
    /** Verifica se uma flag específica está ativa. */
    public boolean hasFlag(String flag) { return flags.contains(flag); }
    
    /** Limpa todas as flags. Útil para iniciar um novo jogo. */
    public void clearFlags() { flags.clear(); }
}