// engine
package com.JDStudio.Engine.World;

import java.util.Random;
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.Camera.CameraProfile;
import com.JDStudio.Engine.World.Camera.FollowStyle;

public class Camera {

	// --- ENUM PARA OS ESTILOS DE CÂMARA ---
    public enum FollowStyle {
    	/**A câmara fica parada.*/
        STATIC,
        /**A câmara segue o alvo perfeitamente, sem suavização.*/
        LOCK_ON_TARGET,
        /**A câmara segue o alvo suavemente.*/
        SMOOTH_FOLLOW
    }

    // --- "PERFIL" QUE GUARDA AS CONFIGURAÇÕES DA CÂMARA ---
    public record CameraProfile(FollowStyle style, double smoothSpeed, double zoom) {}
    /**Perfil padrão para jogabilidade normal*/
    public static  CameraProfile PROFILE_GAMEPLAY = new CameraProfile(FollowStyle.SMOOTH_FOLLOW, 0.1, 1.0);
    /**Perfil para um momento de "foco", com mais zoom e mais rápido*/
	public static CameraProfile PROFILE_FOCUS = new CameraProfile(FollowStyle.SMOOTH_FOLLOW, 0.2, 1.2);
    /**Perfil para mostrar uma área do mapa, sem seguir o jogador*/
	public static CameraProfile PROFILE_STATIC_VIEW = new CameraProfile(FollowStyle.STATIC, 0, 1.0);

    private double x, y;
    private double currentZoom; // Zoom atual, para transições suaves
    
    // Alvo e perfil atuais
    private GameObject target;
    private CameraProfile currentProfile;
    private CameraProfile targetProfile; // Perfil alvo, para transições
    private double transitionSpeed = 0.02; // Velocidade da transição entre perfis

    // Lógica de tremor (inalterada)
    private double shakeIntensity, shakeDuration, shakeOffsetX, shakeOffsetY;
    private Random random = new Random();

    public Camera(double x, double y) {
        this.x = x;
        this.y = y;
        // Define um perfil padrão inicial
        this.currentProfile = new CameraProfile(FollowStyle.LOCK_ON_TARGET, 1.0, 1.0);
        this.targetProfile = this.currentProfile;
        this.currentZoom = this.currentProfile.zoom();
    }
    
    /**
     * Define um novo perfil para a câmara, que irá transitar suavemente para as novas configurações.
     * @param newProfile O novo perfil a ser aplicado.
     * @param target O novo GameObject que a câmara deve seguir (pode ser null para STATIC).
     */
    public void applyProfile(CameraProfile newProfile, GameObject target) {
        this.targetProfile = newProfile;
        this.target = target;
    }

    /**
     * O método principal que atualiza a lógica da câmara a cada frame.
     * O 'target' já não é necessário aqui, pois a câmara já sabe quem seguir.
     * @param world O mundo para obter os limites do mapa.
     */
    public void update(World world) {
        // Transição suave entre perfis (smoothSpeed e zoom)
        if (currentProfile != targetProfile) {
            double newSmoothSpeed = lerp(currentProfile.smoothSpeed(), targetProfile.smoothSpeed(), transitionSpeed);
            double newZoom = lerp(currentZoom, targetProfile.zoom(), transitionSpeed);
            
            this.currentProfile = new CameraProfile(targetProfile.style(), newSmoothSpeed, targetProfile.zoom());
            this.currentZoom = newZoom;
            
            // Quando a transição estiver perto do fim, "trava" no perfil alvo
            if (Math.abs(currentZoom - targetProfile.zoom()) < 0.01) {
                currentProfile = targetProfile;
                currentZoom = targetProfile.zoom();
            }
        }
        
        // Atualiza a posição com base no estilo do perfil ATUAL
        updateFollow(world);
        updateShake();
    }
    
    private void updateFollow(World world) {
        if (target == null || currentProfile.style() == FollowStyle.STATIC) {
            // Se não houver alvo ou o estilo for estático, a câmara não se move.
            // A sua posição pode ser definida externamente (ex: para cutscenes) com setPosition().
        } else {
            // Calcula a posição alvo (centralizada no jogador)
            double targetX = target.getX() + target.getWidth()/2.0 - (Engine.getWIDTH() / 2.0);
            double targetY = target.getY() + target.getHeight()/2.0 - (Engine.getHEIGHT() / 2.0);
            
            double finalSmoothSpeed = currentProfile.smoothSpeed();
            if(currentProfile.style() == FollowStyle.LOCK_ON_TARGET){
                finalSmoothSpeed = 1.0; // Força o movimento instantâneo
            }

            // Interpola a posição da câmara em direção ao alvo
            this.x += (targetX - this.x) * finalSmoothSpeed;
            this.y += (targetY - this.y) * finalSmoothSpeed;
        }
        
        // Garante que a câmara não saia dos limites do mapa
        clampToWorldBounds(world);
    }
    
    private void clampToWorldBounds(World world) {
        if (world == null) return;
        int mapPixelWidth = world.WIDTH * world.tileWidth;
        int mapPixelHeight = world.HEIGHT * world.tileHeight;
        this.x = clamp(this.x, 0, mapPixelWidth - Engine.getWIDTH());
        this.y = clamp(this.y, 0, mapPixelHeight - Engine.getHEIGHT());
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

 // Função de Interpolação Linear para suavizar transições
    private double lerp(double start, double end, double amount) {
        return start + amount * (end - start);
    }

    // --- Getters e Setters ---

    public int getX() { return (int) (x + shakeOffsetX); }
    public int getY() { return (int) (y + shakeOffsetY); }
    public double getZoom() { return this.currentZoom; }

    public CameraProfile getTargetProfile() { return this.targetProfile; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void setZoom(double zoom) { this.currentZoom = Math.max(0.1, zoom); } // Evita zoom 0 ou negativo
}