package com.jdstudio.engine.Graphics.WSUI.PersonalityElements;

import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * Um balão de emoção que exibe um ícone (Sprite) sobre um GameObject
 * por um período de tempo definido.
 * Ideal para reações de NPCs (alerta, ideia, etc.).
 */
public class UIEmotionBubble extends UIWorldAttached {

    private Sprite emotionSprite;
    private int lifeTime; // Duração em ticks (frames)

    /**
     * Cria um novo balão de emoção.
     * @param target O GameObject sobre o qual o balão irá aparecer.
     * @param emotionSprite O Sprite (ícone) a ser exibido.
     * @param lifeTime A duração em ticks que o balão permanecerá visível.
     */
    public UIEmotionBubble(GameObject target, Sprite emotionSprite, int lifeTime) {
        // O offset Y padrão pode ser ajustado para aparecer acima da cabeça do personagem
        super(target, -16); 
        this.emotionSprite = emotionSprite;
        this.lifeTime = lifeTime;

        if (emotionSprite != null) {
            this.width = emotionSprite.getWidth();
            this.height = emotionSprite.getHeight();
        }
    }

    @Override
    public void tick() {
        super.tick(); // A classe pai (UIWorldAttached) já trata de seguir o alvo.
        if (!visible) return;

        // Decrementa o tempo de vida
        lifeTime--;
        if (lifeTime <= 0) {
            // Quando o tempo acaba, o balão torna-se invisível e será eventualmente removido
            this.visible = false; 
            this.destroy(); // Usa o método destroy() para se remover do RenderManager
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible || target == null || emotionSprite == null) return;

        // As coordenadas 'this.x' e 'this.y' já são atualizadas pelo tick() da classe pai.
        // O cálculo centraliza o sprite sobre o alvo.
        int drawX = (this.x - (this.width / 2)) - Engine.camera.getX();
        int drawY = this.y - this.getHeight() - Engine.camera.getY(); // Desenha acima do ponto de offset

        g.drawImage(emotionSprite.getImage(), drawX, drawY, null);
    }
}