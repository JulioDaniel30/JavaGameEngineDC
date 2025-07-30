// engine
package com.JDStudio.Engine.Components;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import com.JDStudio.Engine.Object.GameObject;

/**
 * Um componente que permite a um GameObject seguir um caminho pré-definido.
 */
public class PathComponent {

    public enum PatrolMode {
        LOOP,       // Ao chegar no fim, volta para o primeiro ponto.
        PING_PONG   // Ao chegar no fim, inverte o caminho e volta.
    }

    private final List<Point> path;
    private final PatrolMode mode;
    private final GameObject owner;
    private int currentTargetIndex = 0;
    private int direction = 1; // 1 para frente, -1 para trás (usado no modo PING_PONG)

    // Distância para considerar que o alvo foi alcançado
    private final double arrivalThreshold = 5.0;

    public PathComponent(GameObject owner, List<Point> path, PatrolMode mode) {
        this.owner = owner;
        this.path = path;
        this.mode = mode;
    }

    /**
     * Atualiza a lógica do componente. Deve ser chamado a cada frame.
     */
    public void update() {
        if (path == null || path.isEmpty()) {
            return;
        }

        // Verifica se o dono do componente chegou perto do ponto alvo atual
        if (hasReachedTarget()) {
            // Se chegou, avança para o próximo ponto na rota
            advanceToNextPoint();
        }
    }

    /**
     * Retorna a coordenada do ponto alvo atual na rota de patrulha.
     * @return O objeto Point do alvo atual, ou null se não houver caminho.
     */
    public Point getTargetPosition() {
        if (path == null || path.isEmpty()) {
            return null;
        }
        return path.get(currentTargetIndex);
    }

    private boolean hasReachedTarget() {
        Point target = getTargetPosition();
        if (target == null) return false;

        // Calcula a distância euclidiana entre o dono e o alvo
        double dx = owner.getX() - target.x;
        double dy = owner.getY() - target.y;
        return Math.sqrt(dx * dx + dy * dy) < arrivalThreshold;
    }

    private void advanceToNextPoint() {
        currentTargetIndex += direction;

        if (mode == PatrolMode.LOOP) {
            if (currentTargetIndex >= path.size()) {
                currentTargetIndex = 0; // Volta para o início
            }
        }
        else if (mode == PatrolMode.PING_PONG) {
            if (currentTargetIndex >= path.size() || currentTargetIndex < 0) {
                direction *= -1; // Inverte a direção
                // Move o índice duas vezes para não ficar parado no mesmo ponto
                currentTargetIndex += direction;
                currentTargetIndex += direction;

                // Garante que o índice não saia dos limites após a inversão
                if (currentTargetIndex >= path.size()) {
                    currentTargetIndex = path.size() - 1;
                }
                if (currentTargetIndex < 0) {
                    currentTargetIndex = 0;
                }
            }
        }
    }

    public void reset() {
        this.currentTargetIndex = 0;
        this.direction = 1;
    }

    public List<Point> getPath() {
        return this.path;
    }
}