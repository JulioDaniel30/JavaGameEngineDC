package com.JDStudio.Engine.Graphics.WSUI.BarElements;

import java.awt.Graphics;
import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.HealthComponent;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.UI.Managers.ThemeManager;
import com.JDStudio.Engine.Graphics.WSUI.UIWorldAttached;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Uma barra de vida que exibe a saúde em segmentos discretos (como corações),
 * em vez de uma barra contínua.
 */
public class UIHealthBarSegmented extends UIWorldAttached {

    private final HealthComponent healthComponent;
    private final int maxSegments;
    private final int healthPerSegment;

    // Sprites para os diferentes estados de um segmento
    private Sprite fullSegmentSprite;
    private Sprite halfSegmentSprite;
    private Sprite emptySegmentSprite;

    /**
     * Cria uma nova barra de vida segmentada.
     * @param target O GameObject cuja vida será exibida.
     * @param yOffset O deslocamento vertical em relação ao alvo.
     * @param healthPerSegment Quantos pontos de vida cada segmento representa.
     */
    public UIHealthBarSegmented(GameObject target, int yOffset, int healthPerSegment) {
        super(target, yOffset);
        this.healthComponent = target.getComponent(HealthComponent.class);
        this.healthPerSegment = Math.max(1, healthPerSegment); // Evita divisão por zero

        // A barra só é visível se o alvo tiver um HealthComponent
        if (this.healthComponent == null) {
            this.visible = false;
            System.err.println("Aviso: Tentativa de criar uma UIHealthBarSegmented para um objeto sem HealthComponent: " + target.name);
            this.maxSegments = 0;
        } else {
            // Calcula quantos segmentos são necessários para representar a vida máxima
            this.maxSegments = (int) Math.ceil((double) this.healthComponent.maxHealth / this.healthPerSegment);
        }

        // Carrega os sprites a partir do ThemeManager
        this.fullSegmentSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_FULL);
        this.halfSegmentSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_HALF);
        this.emptySegmentSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_EMPTY);
    }
    
    /**Setar os Sprites
     *
     *@param fullSegmentSpr Sprite do coraçao cheio.
     *@param halfSegmentSpr Sprite do coraçao pela metade.
     *@param emptySegmentSpr Sprite do coraçao vazio.
     */
    
    public void setSprites(Sprite fullSegmentSpr, Sprite halfSegmentSpr, Sprite emptySegmentSpr) {
    	this.fullSegmentSprite = fullSegmentSpr;
    	this.halfSegmentSprite = halfSegmentSpr;
    	this.emptySegmentSprite = emptySegmentSpr;
    	
    }
    
    @Override
    public void render(Graphics g) {
        if (!visible || healthComponent == null || fullSegmentSprite == null) return;
        
        int currentHealth = healthComponent.currentHealth;
        int segmentWidth = fullSegmentSprite.getWidth();
        int segmentPadding = 2; // Espaçamento entre os segmentos

        // Calcula a largura total de todos os segmentos para poder centralizar
        int totalBarWidth = (maxSegments * segmentWidth) + ((maxSegments - 1) * segmentPadding);
        
        // A posição X inicial é calculada para que o conjunto de corações fique centrado sobre o alvo
        int startDrawX = (this.x - totalBarWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // Itera sobre cada segmento que a barra de vida pode ter
        for (int i = 0; i < maxSegments; i++) {
            // Calcula o limiar de vida para este segmento estar "cheio"
            int healthThreshold = (i + 1) * healthPerSegment;
            Sprite spriteToDraw;

            if (currentHealth >= healthThreshold) {
                // Se a vida atual for maior ou igual ao limiar, o coração está cheio
                spriteToDraw = fullSegmentSprite;
            } else if (currentHealth >= healthThreshold - (healthPerSegment / 2.0)) {
                // Se a vida atual estiver na metade superior do segmento, o coração está meio cheio
                spriteToDraw = halfSegmentSprite;
            } else {
                // Caso contrário, o coração está vazio
                spriteToDraw = emptySegmentSprite;
            }

            // Calcula a posição X para este coração específico
            int currentDrawX = startDrawX + i * (segmentWidth + segmentPadding);
            
            if (spriteToDraw != null) {
                g.drawImage(spriteToDraw.getImage(), currentDrawX, drawY, null);
            }
        }
    }
}