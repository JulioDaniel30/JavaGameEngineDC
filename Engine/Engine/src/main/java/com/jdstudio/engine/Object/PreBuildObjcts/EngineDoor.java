package com.jdstudio.engine.Object.PreBuildObjcts;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.awt.Rectangle;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;



/**
 * Uma classe base "engine-side" para qualquer tipo de porta.
 * Contém toda a lógica de estado (aberta/fechada), animação e colisão.
 * A subclasse do jogo é responsável por fornecer as animações específicas.
 */
public abstract class EngineDoor extends GameObject implements ISavable {

    protected boolean isOpen = false;
    protected Animator animator;
    protected double interactionRadius = 35.0f; // Raio de interação padrão
    protected GameObject target; // Lista de objetos do jogo para interação
    private List<GameObject> allGameObjects;

    /**
     * Construtor padrão que inicializa a porta com propriedades JSON.
     * @param properties Propriedades da porta em formato JSON.
     */

    public EngineDoor(JSONObject properties, GameObject target) {
        super(properties);
        this.target = target;
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.isOpen = reader.getBoolean("startsOpen", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Chama o método abstrato que a classe do JOGO irá implementar
        setupAnimations(this.animator); 

        interactionRadius = reader.getDouble("interactionRadius", 24.0f);

        // Adiciona a zona de interação manual
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_TRIGGER, interactionRadius));
        this.addComponent(interaction);

        updateStateVisuals();
    }
    
    public void interact() {
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        if (isOpen) {
            // cancela se estiver obstruída
            if (isObstructed()) return;
            animator.play("closing");
            setCollisionType(CollisionType.SOLID);
            isOpen = false;
        } else {
            animator.play("opening");
            isOpen = true;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (animator == null) return;
        this.getComponent(InteractionComponent.class).checkInteractions(Collections.singletonList(target));




        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            if ("opening".equals(animator.getCurrentAnimationKey())) {
                animator.play("idleOpen");
                setCollisionType(CollisionType.TRIGGER);
            } else if ("closing".equals(animator.getCurrentAnimationKey())) {
                animator.play("idleClosed");
            }
        }
    }
    
    private void updateStateVisuals() {
        if (isOpen) {
            animator.play("idleOpen");
            setCollisionType(CollisionType.TRIGGER);
        } else {
            animator.play("idleClosed");
            setCollisionType(CollisionType.SOLID);
        }
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
        updateStateVisuals();
    }
    
    /**
     * MÉTODO ABSTRATO: A classe do jogo DEVE implementar este método
     * para fornecer as animações específicas desta porta.
     * @param animator O componente Animator a ser configurado.
     */
    protected abstract void setupAnimations(Animator animator);
    public void setInteractionRadius(float radius){
        this.interactionRadius = radius;
    }
    public double getInteractionRadius() {
        return interactionRadius;
    }

    public boolean isObstructed() {
        if (allGameObjects == null) return false;
        Rectangle Bounds = new Rectangle(
            this.getX() + this.getMaskX(), 
            this.getY() + this.getMaskY(),
            this.getWidth() + this.getMaskWidth(), 
            this.getHeight() + this.getMaskHeight()
        );

        
        for (GameObject obj : allGameObjects) {
            if (obj == this) continue;
           Rectangle otherBounds = new Rectangle(
                obj.getX() + obj.getMaskX(),
                obj.getY() + obj.getMaskY(),
                obj.getWidth() + obj.getMaskWidth(),
                obj.getHeight() + obj.getMaskHeight()
            );
            if (Bounds.intersects(otherBounds)) {
                return true;
            }
        }
        return false;
    }

    public void setGameObjects(List<GameObject> gameObjects) {
        this.allGameObjects = gameObjects;
    }
}