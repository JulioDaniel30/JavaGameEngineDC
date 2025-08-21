package com.jdstudio.engine.Graphics.WSUI.InformationElements;

import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * Um marcador de UI no mundo do jogo que indica um objetivo de missão.
 * Ele flutua suavemente sobre um GameObject alvo para chamar a atenção.
 */
public class UIObjectiveMarker extends UIWorldAttached {

    private final Sprite markerSprite;

    // Lógica para a animação de flutuação (bobbing)
    private int bobbingTimer = 0;
    private int bobbingSpeed = 5; // Quão rápido ele flutua
    private int bobbingAmount = 4;  // Quantos pixels ele se move para cima e para baixo

    /**
     * Cria um novo marcador de objetivo.
     * @param target O GameObject que é o objetivo da missão.
     * @param markerSprite O Sprite a ser usado como ícone do marcador.
     */
    public UIObjectiveMarker(GameObject target, Sprite markerSprite) {
        // O offset Y posiciona o marcador acima do alvo
        super(target, -24); 
        this.markerSprite = markerSprite;
        this.visible = (target != null);

        if (markerSprite != null) {
            this.width = markerSprite.getWidth();
            this.height = markerSprite.getHeight();
        }
    }

    @Override
    public void tick() {
        // A visibilidade é controlada externamente, mas se o alvo for destruído, ele desaparece.
        if (target == null || target.isDestroyed) {
            this.visible = false;
        }
        if (!visible) return;

        // A classe pai (UIWorldAttached) já trata de seguir a posição base do alvo.
        super.tick(); 
        
        // Atualiza o timer para a animação de flutuação
        bobbingTimer++;
    }

    @Override
    public void render(Graphics g) {
        if (!visible || target == null || markerSprite == null) return;

        // Calcula o deslocamento vertical da flutuação usando uma função seno
        // para um movimento suave de sobe e desce.
        double bobbingOffset = Math.sin(bobbingTimer * (Math.PI / 180.0) * bobbingSpeed) * bobbingAmount;
        
        // As coordenadas 'this.x' e 'this.y' já seguem o alvo.
        // O cálculo centraliza o sprite e aplica o offset da câmera e da flutuação.
        int drawX = (this.x - (this.width / 2)) - Engine.camera.getX();
        int drawY = (int) (this.y - this.height + bobbingOffset) - Engine.camera.getY();

        g.drawImage(markerSprite.getImage(), drawX, drawY, null);
    }
    
    /**
     * Permite mudar o alvo do marcador dinamicamente.
     */
    public void setTarget(GameObject newTarget) {
        this.target = newTarget;
        this.visible = (newTarget != null);
    }
}