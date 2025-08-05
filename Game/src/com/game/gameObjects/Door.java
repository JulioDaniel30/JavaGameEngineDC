// game
package com.game.gameObjects;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import org.json.JSONObject;

import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.Interactable;
import com.JDStudio.Engine.Utils.PropertiesReader;
import com.game.States.PlayingState;
@SuppressWarnings("unused")
public class Door extends GameObject implements Interactable, ISavable {

    private boolean isOpen = false;
    private List<GameObject> allGameObjects;

	Animator animator;

    public Door(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        
        PropertiesReader reader = new PropertiesReader(properties);
        this.isOpen = reader.getBoolean("startsOpen", false);

		animator = new Animator();
		System.out.println("door");
		this.addComponent(animator);
        
        setupAnimations();
        setCollisionType(CollisionType.SOLID);
        if (isOpen) {
            animator.play("idleOpen");
            
        } else {
            animator.play("idleClosed");
            setCollisionType(CollisionType.SOLID);
        }
    }

    
    private void setupAnimations() {
        Animation idleClosed = new Animation(1, PlayingState.assets.getSprite("door_frame_1"));
        Animation idleOpen = new Animation(1, PlayingState.assets.getSprite("door_frame_3"));
        Animation opening = new Animation(20, false, 
            PlayingState.assets.getSprite("door_frame_1"), 
            PlayingState.assets.getSprite("door_frame_2"), 
            PlayingState.assets.getSprite("door_frame_3"));
        Animation closing = new Animation(20, false, 
            PlayingState.assets.getSprite("door_frame_3"), 
            PlayingState.assets.getSprite("door_frame_2"), 
            PlayingState.assets.getSprite("door_frame_1"));

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
        String currentKey = animator.getCurrentAnimationKey();
        
        if ("idleOpen".equals(currentKey)) {
            if (isObstructed()) {
                return;
            }
            // Torna-se sólida IMEDIATAMENTE. O novo método já cuida da máscara.
            this.collisionType = CollisionType.SOLID;
            animator.play("closing");
            isOpen = false;

        } else if ("idleClosed".equals(currentKey)) {
            // Ao abrir, ela continua sólida durante a animação
            animator.play("opening");
            isOpen = true;
            
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        Animation currentAnimation = animator.getCurrentAnimation();
        
        if (currentAnimation != null && currentAnimation.hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("opening".equals(currentKey)) {
                animator.play("idleOpen");
                setCollisionType(CollisionType.TRIGGER);
                isOpen = true;
            } else if ("closing".equals(currentKey)) {
                animator.play("idleClosed");
                isOpen = false;
                // A porta já foi definida como sólida no onInteract
            }
        }
    }

    /**
     * Implementação completa do método de verificação de obstrução.
     */
    private boolean isObstructed() {
        // Este método precisa da lista de GameObjects. Vamos garantir que ele a tenha.
        if (allGameObjects == null) return false;

        Rectangle doorBounds = new Rectangle(
            this.getX() + this.getMaskX(),
            this.getY() + this.getMaskY(),
            this.getMaskWidth(),
            this.getMaskHeight()
        );

        for (GameObject other : allGameObjects) {
            if (other == this) continue;

            // Verifica colisão com qualquer outro objeto, não apenas o jogador
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
        return false;
    }
    
 
    
    @Override
    public int getInteractionRadius() { return 24; }

    @Override
    public void render(Graphics g) {
        super.render(g);
        renderDebugInteractionArea(g);
    }
    
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOpen", this.isOpen);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.isOpen = state.getBoolean("isOpen");
        // Atualiza a aparência e colisão da porta com base no estado carregado
        if (isOpen) {
            animator.play("idleOpen");
            setCollisionType(CollisionType.TRIGGER);
        } else {
            animator.play("idleClosed");
            setCollisionType(CollisionType.SOLID);
        }
    }
    
}