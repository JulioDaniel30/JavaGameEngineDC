// game
package com.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.Interactable;
@SuppressWarnings("unused")
public class Door extends GameObject implements Interactable {

    private boolean isOpen = false;
    private List<GameObject> allGameObjects;

    public Door(double x, double y, int width, int height, boolean startsOpen) {
        super(x, y, width, height);
        this.isOpen = startsOpen;
        
        setupAnimations();

        if (startsOpen) {
            animator.play("idleOpen");
            this.isSolid = false;
        } else {
            animator.play("idleClosed");
            this.isSolid = true;
        }
    }
    
    private void setupAnimations() {
        // Pega os frames que carregamos no PlayingState
        Sprite frame1 = PlayingState.assets.getSprite("door_frame_1"); // Fechada
        Sprite frame2 = PlayingState.assets.getSprite("door_frame_2"); // Meio-aberta
        Sprite frame3 = PlayingState.assets.getSprite("door_frame_3"); // Aberta

        // Cria as animações
        Animation idleClosed = new Animation(1, frame1); // Animação "parada fechada"
        Animation idleOpen = new Animation(1, frame3);   // Animação "parada aberta"
        
        // Animação de ABERTURA: toca uma vez e para (loop = false)
        Animation opening = new Animation(10, false, frame1, frame2, frame3);
        
        // Animação de FECHAMENTO: toca uma vez e para, na ordem inversa
        Animation closing = new Animation(10, false, frame3, frame2, frame1);

        // Adiciona ao animator
        animator.addAnimation("idleClosed", idleClosed);
        animator.addAnimation("idleOpen", idleOpen);
        animator.addAnimation("opening", opening);
        animator.addAnimation("closing", closing);
    }
    
    public void setGameObjects(List<GameObject> gameObjects) {
        this.allGameObjects = gameObjects;
    }

    @Override
    public void onInteract(GameObject source) {
        // Só permite interação se a porta estiver "parada" (aberta ou fechada)
        String currentKey = animator.getCurrentAnimationKey();
        if ("idleOpen".equals(currentKey)) {
            animator.play("closing");
            isOpen = false;
            // A solidez será atualizada no tick() quando a animação terminar
        } else if ("idleClosed".equals(currentKey)) {
            animator.play("opening");
            isOpen = true;
            // A solidez será atualizada no tick()
        }
    }
    
    /**
     * Verifica se a área da porta está ocupada por outro GameObject.
     */
    
    private boolean isObstructed() {
        if (allGameObjects == null) return false;

        Rectangle doorBounds = new Rectangle(
            this.getX() + this.getMaskX(),
            this.getY() + this.getMaskY(),
            this.getMaskWidth(),
            this.getMaskHeight()
        );

        for (GameObject other : allGameObjects) {
            if (other == this) continue; // Não verifica contra si mesmo

            Rectangle otherBounds = new Rectangle(
                other.getX() + other.getMaskX(),
                other.getY() + other.getMaskY(),
                other.getMaskWidth(),
                other.getMaskHeight()
            );

            if (doorBounds.intersects(otherBounds)) {
                return true; // Encontrou um objeto no caminho!
            }
        }
        return false; // Caminho livre
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza a animação

        Animation currentAnimation = animator.getCurrentAnimation();
        
        if (currentAnimation != null && currentAnimation.hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("opening".equals(currentKey)) {
                animator.play("idleOpen");
                this.isSolid = false;
            } 
            else if ("closing".equals(currentKey)) {
                // --- VERIFICAÇÃO NO MOMENTO CRÍTICO ---
                if (isObstructed()) {
                    // Obstruído! Reverte a animação para abrir novamente.
                    animator.play("opening"); 
                    isOpen = true; // Garante que o estado lógico seja "aberta"
                } else {
                    // Caminho livre! Pode fechar e se tornar sólida.
                    animator.play("idleClosed");
                    this.isSolid = true;
                }
            }
        }
    }
    
    // (O método render já funciona por herança de GameObject)
    @Override
    public int getInteractionRadius() { return 24; }

    @Override
    public void render(Graphics g) {
        super.render(g);
        renderDebugInteractionArea(g);
    }
}