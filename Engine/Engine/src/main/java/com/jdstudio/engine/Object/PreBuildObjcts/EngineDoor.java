package com.jdstudio.engine.Object.PreBuildObjcts;

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
 * An abstract base class for any type of door in the game.
 * It contains all the logic for state (open/closed), animation, and collision.
 * Game-specific subclasses are responsible for providing the specific animations.
 * This class also implements {@link ISavable} to allow its state to be persisted.
 * 
 * @author JDStudio
 */
public abstract class EngineDoor extends GameObject implements ISavable {

    protected boolean isOpen = false;
    protected Animator animator;
    protected double interactionRadius = 35.0f; // Default interaction radius
    protected GameObject target; // The GameObject (e.g., player) for interaction checks
    private List<GameObject> allGameObjects; // Reference to the main game object list for obstruction checks

    /**
     * Constructs a new EngineDoor with the given properties.
     * 
     * @param properties Properties of the door in JSON format.
     * @param target The GameObject (e.g., player) that will interact with this door.
     */
    public EngineDoor(JSONObject properties, GameObject target) {
        super(properties);
        this.target = target;
    }

    /**
     * Initializes the EngineDoor's properties from a JSONObject.
     * It sets up the initial open state, animator, and interaction component.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.isOpen = reader.getBoolean("startsOpen", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Call the abstract method that the GAME class will implement
        setupAnimations(this.animator); 

        interactionRadius = reader.getDouble("interactionRadius", 24.0f);

        // Add the interaction zone
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_TRIGGER, interactionRadius));
        this.addComponent(interaction);

        updateStateVisuals();
    }



    
    /**
     * Handles interaction with the door based on the logic from the reference code.
     * The door will not close if any object is obstructing it.
     */
    public void interact() {
        // Only interact if the door is idle
        if (animator == null || !animator.getCurrentAnimationKey().startsWith("idle")) {
            return;
        }

        if (isOpen) {
            // Check for any obstruction before closing
            if (isObstructed()) {
                // Optional: play a "blocked" sound here for feedback
                return;
            }
            // Become solid immediately, then play the animation
            setCollisionType(CollisionType.SOLID);
            animator.play("closing");
            isOpen = false;
        } else {
            // When opening, remain solid during the animation for safety
            animator.play("opening");
            isOpen = true;
        }
    }

    /**
     * Updates the door's logic, handling animation completion.
     */
    @Override
    public void tick() {
        super.tick();

        if (animator == null) return;

        // Check for interactions with the target (e.g., player)
        if (target != null && this.getComponent(InteractionComponent.class) != null) {
            this.getComponent(InteractionComponent.class).checkInteractions(Collections.singletonList(target));
        }

        // Update state after an animation finishes
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();

            if ("opening".equals(currentKey)) {
                // Only become passable after the opening animation is complete
                setCollisionType(CollisionType.TRIGGER);
                animator.play("idleOpen");
            } else if ("closing".equals(currentKey)) {
                animator.play("idleClosed");
            }
        }
    }
    
    /**
     * Updates the visual state of the door based on its open/closed status.
     * It plays different idle animations (e.g., idleOpen, idleClosed) and sets collision type.
     */
    private void updateStateVisuals() {
        if (isOpen) {
            animator.play("idleOpen");
            setCollisionType(CollisionType.TRIGGER);
        } else {
            animator.play("idleClosed");
            setCollisionType(CollisionType.SOLID);
        }
    }

    /**
     * Saves the current state of the door to a JSONObject.
     * 
     * @return A JSONObject containing the door's savable state.
     */
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOpen", this.isOpen);
        return state;
    }

    /**
     * Loads the state of the door from a JSONObject.
     * 
     * @param state The JSONObject containing the saved data.
     */
    @Override
    public void loadState(JSONObject state) {
        this.isOpen = state.getBoolean("isOpen");
        updateStateVisuals();
    }
    
    /**
     * ABSTRACT METHOD: The game class MUST implement this method
     * to provide the specific animations for this door.
     * @param animator The Animator component to be configured.
     */
    protected abstract void setupAnimations(Animator animator);

    /**
     * Sets the interaction radius for this door.
     * @param radius The new interaction radius.
     */
    public void setInteractionRadius(float radius){
        this.interactionRadius = radius;
    }

    /**
     * Gets the interaction radius of this door.
     * @return The interaction radius.
     */
    public double getInteractionRadius() {
        return interactionRadius;
    }

    /**
     * Checks if the door's path is obstructed by any other GameObject.
     * This is used to prevent closing the door if an object is in the way.
     * 
     * @return true if the door is obstructed, false otherwise.
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

    /**
     * Sets the list of all GameObjects in the game.
     * This is required for the {@code isObstructed} method to work correctly.
     * 
     * @param gameObjects The list of all GameObjects.
     */
    public void setGameObjects(List<GameObject> gameObjects) {
        this.allGameObjects = gameObjects;
    }
}