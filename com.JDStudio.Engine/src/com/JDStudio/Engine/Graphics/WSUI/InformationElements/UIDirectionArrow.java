package com.JDStudio.Engine.Graphics.WSUI.InformationElements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.WSUI.UIWorldAttached;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Um elemento de UI no mundo do jogo que exibe uma seta sobre um alvo,
 * podendo piscar e girar para apontar para outro GameObject.
 */
public class UIDirectionArrow extends UIWorldAttached {

    private final Sprite arrowSprite;
    private GameObject pointTarget; // O GameObject para o qual a seta deve apontar

    // Lógica para o efeito de piscar
    private boolean isBlinking = false;
    private int blinkTimer = 0;
    private int blinkSpeed = 15; // Pisca a cada 15 ticks

    private double currentAngle = 0.0; // Ângulo de rotação em radianos

    public UIDirectionArrow(GameObject followTarget, Sprite arrowSprite, boolean blinking) {
        // O offset Y padrão pode ser ajustado para aparecer acima da cabeça do personagem
        super(followTarget, -20);
        this.arrowSprite = arrowSprite;
        this.isBlinking = blinking;
        if (arrowSprite != null) {
            this.width = arrowSprite.getWidth();
            this.height = arrowSprite.getHeight();
        }
    }

    /**
     * Define o alvo para o qual a seta deve apontar.
     * Se for nulo, a seta aponta para cima.
     * @param pointTarget O GameObject alvo.
     */
    public void setPointTarget(GameObject pointTarget) {
        this.pointTarget = pointTarget;
    }

    @Override
    public void tick() {
        super.tick(); // A classe pai (UIWorldAttached) já trata de seguir o alvo.
        if (!visible) return;

        // Lógica de piscar
        if (isBlinking) {
            blinkTimer++;
            if (blinkTimer > blinkSpeed * 2) {
                blinkTimer = 0;
            }
        }
        
        // Lógica de rotação
        if (pointTarget != null && target != null) {
            double targetX = pointTarget.getCenterX();
            double targetY = pointTarget.getCenterY();
            
            double selfX = target.getCenterX();
            double selfY = target.getCenterY();

            // Calcula o ângulo entre o dono da seta e o alvo
            this.currentAngle = Math.atan2(targetY - selfY, targetX - selfX);
        } else {
            // Se não houver alvo para apontar, aponta para cima
            this.currentAngle = -Math.PI / 2; // -90 graus
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible || target == null || arrowSprite == null) return;

        // Se estiver a piscar, só desenha na "primeira metade" do ciclo
        if (isBlinking && blinkTimer > blinkSpeed) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        
        // As coordenadas 'this.x' e 'this.y' já são atualizadas pelo tick() da classe pai.
        int drawX = this.x - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // --- LÓGICA DE ROTAÇÃO ---
        AffineTransform oldTransform = g2d.getTransform();
        
        // Translada o ponto de rotação para o centro da imagem
        int centerX = drawX - width / 2;
        int centerY = drawY - height / 2;
        
        // Roda o contexto gráfico. Adicionamos +90 graus (PI/2) porque a maioria das setas é desenhada apontando para a direita.
        g2d.rotate(currentAngle + Math.PI / 2, centerX + width / 2.0, centerY + height / 2.0);

        g2d.drawImage(arrowSprite.getImage(), centerX, centerY, null);
        
        g2d.setTransform(oldTransform); // Restaura a transformação para não afetar outros desenhos
        g2d.dispose();
    }
}