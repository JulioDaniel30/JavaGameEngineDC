package com.jdstudio.engine.Object.PreBuildObjcts;

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

    public EngineDoor(JSONObject properties) {
        super(properties);
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

        // Adiciona a zona de interação manual
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);

        updateStateVisuals();
    }
    
    public void interact() {
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        if (isOpen) {
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
}