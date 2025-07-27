package com.JDStudio.Engine.Components;

import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.World;

/**
 * Gerencia o movimento e a colisão de um GameObject com o cenário.
 * Utiliza uma abordagem de acumulador de sub-pixel para movimento preciso e simétrico.
 */
public class MovementComponent {

    private GameObject owner;
    private World world;
    public double speed;

    private double dx = 0, dy = 0;

    // Acumuladores para movimento de sub-pixel. Guardam a parte fracionária do movimento entre frames.
    private double xRemainder = 0.0;
    private double yRemainder = 0.0;

    public MovementComponent(GameObject owner, double speed) {
        this.owner = owner;
        this.speed = speed;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setDirection(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Atualiza a posição do GameObject a cada frame, tratando colisões com precisão de sub-pixel.
     */
    public void tick() {
        if (dx == 0 && dy == 0) {
            return; // Nenhum movimento
        }

        // --- 1. PREPARAÇÃO DO VETOR DE MOVIMENTO ---
        double moveX = dx;
        double moveY = dy;

        // Normaliza o vetor para movimento diagonal para manter a velocidade constante.
        if (moveX != 0 && moveY != 0) {
            double length = Math.sqrt(moveX * moveX + moveY * moveY);
            moveX = (moveX / length);
            moveY = (moveY / length);
        }

        // Aplica a velocidade ao vetor de movimento
        moveX *= speed;
        moveY *= speed;

        // --- 2. LÓGICA DO ACUMULADOR DE SUB-PIXEL ---

        // Adiciona o resto do frame anterior ao movimento deste frame
        xRemainder += moveX;
        yRemainder += moveY;

        // Determina quantos pixels INTEIROS devemos nos mover neste frame
        int xToMove = (int) Math.round(xRemainder);
        int yToMove = (int) Math.round(yRemainder);

        // Subtrai os pixels que vamos tentar mover, guardando o novo resto para o próximo frame
        xRemainder -= xToMove;
        yRemainder -= yToMove;

        // --- 3. MOVIMENTO E COLISÃO PIXEL POR PIXEL ---
        int signX = Integer.signum(xToMove); // 1, -1 ou 0
        while (xToMove != 0) {
            if (world.isFree((int)(owner.getX() + signX), (int)owner.getY(), owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getHeight())) {
                owner.setX(owner.getX() + signX);
                xToMove -= signX;
            } else {
                break; // Colisão, para o movimento em X
            }
        }

        int signY = Integer.signum(yToMove); // 1, -1 ou 0
        while (yToMove != 0) {
            // A verificação em Y usa a posição X já atualizada
            if (world.isFree((int)owner.getX(), (int)(owner.getY() + signY), owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getHeight())) {
                owner.setY(owner.getY() + signY);
                yToMove -= signY;
            } else {
                break; // Colisão, para o movimento em Y
            }
        }
    }
}