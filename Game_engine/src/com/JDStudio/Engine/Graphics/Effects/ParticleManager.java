package com.JDStudio.Engine.Graphics.Effects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.JDStudio.Engine.Engine;

public class ParticleManager {

    private static final ParticleManager instance = new ParticleManager();
    private final List<Particle> particlePool = new ArrayList<>();
    private final Random random = new Random();

    private ParticleManager() {}

    public static ParticleManager getInstance() {
        return instance;
    }

    private Particle getInactiveParticle() {
        for (Particle p : particlePool) {
            if (!p.isActive) {
                return p;
            }
        }
        // Se não houver inativas, cria uma nova
        Particle newParticle = new Particle();
        particlePool.add(newParticle);
        return newParticle;
    }

    /**
     * Atualiza todas as partículas ativas.
     */
    public void update() {
        for (Particle p : particlePool) {
            if (p.isActive) {
                p.update();
            }
        }
    }
    
    /**
     * Renderiza todas as partículas ativas.
     */
    public void render(Graphics g) {
        int camX = Engine.camera.getX();
        int camY = Engine.camera.getY();
        for (Particle p : particlePool) {
            // Renderiza apenas se estiver ativa
            p.render(g, camX, camY);
        }
    }

    // --- MÉTODOS DE EMISSÃO (EXEMPLOS) ---

    /**
     * Cria um efeito de explosão.
     * @param x Posição X inicial
     * @param y Posição Y inicial
     * @param count Quantidade de partículas
     * @param startColor Cor inicial (ex: Color.YELLOW)
     * @param endColor Cor final (ex: new Color(100, 0, 0, 0) - vermelho escuro transparente)
     * @param minLife Mínimo de tempo de vida (frames)
     * @param maxLife Máximo de tempo de vida (frames)
     * @param minSpeed Velocidade mínima
     * @param maxSpeed Velocidade máxima
     * @param startSize Tamanho inicial
     * @param endSize Tamanho final
     */
    public void createExplosion(double x, double y, int count, Color startColor, Color endColor,
                                int minLife, int maxLife, double minSpeed, double maxSpeed,
                                float startSize, float endSize) {
        for (int i = 0; i < count; i++) {
            Particle p = getInactiveParticle();
            
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
            
            double velX = Math.cos(angle) * speed;
            double velY = Math.sin(angle) * speed;
            
            int life = minLife + random.nextInt(maxLife - minLife + 1);
            
            p.init(x, y, velX, velY, startSize, endSize, startColor, endColor, life);
        }
    }
}