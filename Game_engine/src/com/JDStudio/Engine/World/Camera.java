// engine
package com.JDStudio.Engine.World;

import java.util.Random;
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Object.GameObject;

public class Camera {

    private double x;
    private double y;

    // --- Novos Atributos ---
    
    // Para a transição suave
    private double smoothSpeed = 0.1; // Valor entre 0.0 (sem movimento) e 1.0 (instantâneo)

    // Para o Zoom
    private double zoom = 1.0; // 1.0 = normal, < 1.0 = zoom out, > 1.0 = zoom in

    // Para o Tremor (Shake)
    private double shakeIntensity = 0;
    private double shakeDuration = 0;
    private double shakeOffsetX = 0;
    private double shakeOffsetY = 0;
    private Random random = new Random();

    public Camera(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * O método principal que atualiza a lógica da câmera a cada frame.
     * @param target O GameObject que a câmera deve seguir.
     * @param world O mundo para obter os limites do mapa.
     */
    public void update(GameObject target, World world) {
        updateSmoothFollow(target, world);
        updateShake();
    }
    
    private void updateSmoothFollow(GameObject target, World world) {
        // Calcula a posição alvo (centralizada no jogador)
        double targetX = target.getX() - (Engine.WIDTH / 2.0);
        double targetY = target.getY() - (Engine.HEIGHT / 2.0);

        // Interpola suavemente a posição atual da câmera em direção ao alvo
        this.x += (targetX - this.x) * smoothSpeed;
        this.y += (targetY - this.y) * smoothSpeed;
        
        // Garante que a câmera não saia dos limites do mapa
        int mapPixelWidth = world.WIDTH * world.tileWidth;   // CORRIGIDO
        int mapPixelHeight = world.HEIGHT * world.tileHeight; // CORRIGIDO
        this.x = clamp(this.x, 0, mapPixelWidth - Engine.WIDTH);
        this.y = clamp(this.y, 0, mapPixelHeight - Engine.HEIGHT);
    }
    
    private void updateShake() {
        if (shakeDuration > 0) {
            shakeDuration--;
            // Decai a intensidade para o tremor parar suavemente
            double currentIntensity = shakeIntensity * (shakeDuration / 10.0); // Exemplo de decaimento
            
            shakeOffsetX = (random.nextDouble() * 2 - 1) * currentIntensity;
            shakeOffsetY = (random.nextDouble() * 2 - 1) * currentIntensity;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    /**
     * Inicia um efeito de tremor na câmera.
     * @param intensity A força do tremor (em pixels).
     * @param duration A duração do tremor (em frames).
     */
    public void shake(double intensity, double duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }
    
    // Método clamp não é mais estático
    public double clamp(double atual, double min, double max) {
        if (atual < min) {
            atual = min;
        }
        if (atual > max) {
            atual = max;
        }
        return atual;
    }

    // --- Getters e Setters ---

    public int getX() { return (int) (x + shakeOffsetX); }
    public int getY() { return (int) (y + shakeOffsetY); }
    
    public double getZoom() { return zoom; }
    public void setZoom(double zoom) { this.zoom = Math.max(0.1, zoom); } // Evita zoom 0 ou negativo
}