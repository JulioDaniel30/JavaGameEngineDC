package com.jdstudio.engine.Object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.Component;
import com.jdstudio.engine.Graphics.Layers.IRenderable;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * The abstract base class for all objects in the game world.
 * A GameObject represents any entity that exists in the game, such as players, enemies, items, or environmental elements.
 * It provides fundamental properties like position, size, collision, rendering, and a component system
 * for adding specific behaviors.
 * 
 * @author JDStudio
 * @since 1.0
 */
public abstract class GameObject implements IRenderable  {

    /** The x-coordinate of the object's top-left corner in world space. */
    protected double x;
    /** The y-coordinate of the object's top-left corner in world space. */
    protected double y;
    /** The width of the object in pixels. */
    protected int width;
    /** The height of the object in pixels. */
    protected int height;
    /** The primary sprite used for rendering this object. Can be overridden by an Animator component. */
    protected Sprite sprite;
    
    /** The x-offset of the collision mask relative to the object's top-left corner. */
    protected int maskX;
    /** The y-offset of the collision mask relative to the object's top-left corner. */
    public int maskY;
    /** The width of the collision mask. */
    protected int maskWidth;
    /** The height of the collision mask. */
    protected int maskHeight;
    
    /** The vertical velocity of the object. Used in physics calculations, especially for one-way platforms. */
    public double velocityY = 0;
    /** Flag indicating if the object is currently on the ground. */
    public boolean onGround = false;
    /** Flag indicating if the object has been marked for destruction. */
    public boolean isDestroyed = false;
    /** The name of the GameObject, often used for identification or debugging. */
    public String name = "";

    // --- ATTACHMENT SYSTEM ---

    /** The "parent" GameObject to which this object is attached. Null if not attached to anyone. */
    protected GameObject parent = null;

    /** The list of "child" GameObjects that are attached to this object. */
    protected List<GameObject> children = new ArrayList<>();

    /** The local position of the object relative to its parent (offset). */
    protected Point localPosition = new Point(0, 0);

    // --- END OF ATTACHMENT SYSTEM ---
    
    /** The rendering layer for this GameObject. */
    protected RenderLayer renderLayer = StandardLayers.GAMEPLAY_BELOW;

    /**
     * Defines the type of collision behavior for a GameObject.
     */
    public enum CollisionType {
        /** No collision of any type. */
        NO_COLLISION,
        /** Solid for everything (walls, closed doors). */
        SOLID,
        /** Passable, but can trigger events (items, player). */
        TRIGGER,
        /** Solid for other characters, but passable by projectiles. */
        CHARACTER_SOLID,
        /** Passable, but can trigger events for characters. */
        CHARACTER_TRIGGER,
        /** A trigger that specifically represents a source of damage (e.g., projectiles, attack areas). */
        DAMAGE_SOURCE,

    }
    
    /** The collision type of this GameObject. */
    public CollisionType collisionType = CollisionType.TRIGGER;
    
    /**
     * If true, this object will not be automatically removed by the cleanup loop
     * of the EnginePlayingState, even if 'isDestroyed' is true.
     * Ideal for the player, who should manage their own "game over" state.
     */
    public boolean isProtectedFromCleanup = false;
    
    // --- COMPONENT SYSTEM ---
    /** A map storing all components attached to this GameObject, keyed by their class type. */
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * Constructs a new GameObject from a set of properties.
     * It initializes basic properties and registers itself with the RenderManager.
     *
     * @param properties A JSONObject containing the initial properties of the GameObject.
     */
    public GameObject(JSONObject properties) {
        initialize(properties);
        RenderManager.getInstance().register(this);
    }

    /**
     * Initializes the GameObject's properties from a JSONObject.
     * This method is typically called by the constructor or when loading from a save file.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    public void initialize(JSONObject properties) {
        PropertiesReader reader = new PropertiesReader(properties);
        this.x = reader.getInt("x", 0);
        this.width = reader.getInt("width", 16);
        this.height = reader.getInt("height", 16);
        this.y = reader.getInt("y", 0) - this.height; // Adjust Y to be top-left of sprite
        this.name = reader.getString("name", "");
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = this.width;
        this.maskHeight = this.height;
        String layerName = reader.getString("renderLayer", "GAMEPLAY_BELOW");
        
        // 2. Ask the RenderManager to find the layer with that name.
        RenderLayer layer = RenderManager.getInstance().getLayerByName(layerName);
        
        // 3. Set the GameObject's rendering layer.
        if (layer != null) {
            this.setRenderLayer(layer);
        } else {
            // If the layer name in Tiled is invalid or not registered,
            // use the engine's default and warn in the console.
            System.err.println("Warning: RenderLayer '" + layerName + "' invalid or not registered for object '" + this.name + "'. Using default layer.");
            this.setRenderLayer(StandardLayers.GAMEPLAY_BELOW);
        }
    }
    
    /**
     * Adds a new component to this GameObject.
     * The component's owner is set to this GameObject, and its initialize method is called.
     * 
     * @param component The instance of the component to be added.
     * @return The GameObject itself, for method chaining (e.g., go.addComponent(...).addComponent(...)).
     */
    public <T extends Component> GameObject addComponent(T component) {
        components.put(component.getClass(), component);
        component.setOwner(this);
        component.initialize(this); 
        return this;
    }

    /**
     * Retrieves a component from this GameObject by its type (class).
     * 
     * @param componentClass The class of the desired component (e.g., Animator.class).
     * @return The component instance, or null if not found.
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        Component comp = components.get(componentClass);
        if (comp == null) {
            return null;
        }
        return componentClass.cast(comp);
    }

    /**
     * Checks if this GameObject has a specific component attached.
     * 
     * @param componentClass The class of the component to check for.
     * @return true if the component is present, false otherwise.
     */
    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }
    
    // --- ATTACHMENT METHODS ---

    /**
     * Attaches a "child" GameObject to this GameObject ("parent").
     * The child's position will be relative to the parent's position.
     * 
     * @param child    The object to be attached.
     * @param localX   The X position of the child relative to the parent's top-left corner.
     * @param localY   The Y position of the child relative to the parent's top-left corner.
     */
    public void attach(GameObject child, int localX, int localY) {
        if (child.parent != null) {
            child.parent.children.remove(child); // Remove from the old parent's children list
        }
        child.parent = this; // Set this object as the new parent
        child.localPosition.setLocation(localX, localY); // Set the relative position
        if (!this.children.contains(child)) {
            this.children.add(child); // Add to this object's children list
        }
        
        // Update the child's position immediately
        updateChildPosition(child);
    }

    /**
     * Detaches this GameObject from its current parent.
     * The object will retain its last world position but will no longer follow the parent.
     */
    public void detach() {
        if (this.parent != null) {
            this.parent.children.remove(this); // Remove itself from the parent's children list
            this.parent = null; // Remove the reference to the parent
        }
    }

    /**
     * Updates the world position of a single child based on the parent's position.
     * 
     * @param child The child to be updated.
     */
    private void updateChildPosition(GameObject child) {
        child.setX(this.x + child.localPosition.x);
        child.setY(this.y + child.localPosition.y);
    }

    /**
     * Updates the world position of all attached children.
     * This method is called automatically when the parent's position changes.
     */
    private void updateAllChildrenPositions() {
        for (GameObject child : this.children) {
            updateChildPosition(child);
        }
    }
    
    /**
     * Finds and returns a child attached to this GameObject by its name.
     * The name is the "name" property you define for the object in the Tiled Editor.
     *
     * @param name The name of the child GameObject to search for.
     * @return The child GameObject if found, otherwise returns null.
     */
    public GameObject getChildByName(String name) {
        // Ensure the name to search for is not null or empty to avoid errors.
        if (name == null || name.isEmpty()) {
            return null;
        }

        // Iterate through this object's children list.
        for (GameObject child : this.children) {
            // Compare the child's name with the name we are looking for.
            if (name.equals(child.name)) {
                return child; // Found! Return the child object.
            }
        }

        // If the loop finishes and no child with that name is found, return null.
        return null;
    }
    
    /**
     * Changes the relative position (offset) of a child object relative to its parent.
     * Use this method to move an object that is already attached.
     * 
     * @param localX The new X position relative to the parent's origin (top-left corner).
     * @param localY The new Y position relative to the parent's origin.
     */
    public void setLocalPosition(int localX, int localY) {
        this.localPosition.setLocation(localX, localY);
    }

    // --- END OF ATTACHMENT METHODS ---
    
    /**
     * Sets the collision type for this GameObject.
     * @param type The new CollisionType.
     */
    public void setCollisionType(CollisionType type) {
        this.collisionType = type;
    }

    /**
     * Gets the current collision type of this GameObject.
     * @return The CollisionType.
     */
    public CollisionType getCollisionType() {
        return this.collisionType;
    }

    /**
     * Sets the dimensions and offset of the collision mask.
     * @param maskX The x-offset of the mask.
     * @param maskY The y-offset of the mask.
     * @param maskWidth The width of the mask.
     * @param maskHeight The height of the mask.
     */
    public void setCollisionMask(int maskX, int maskY, int maskWidth, int maskHeight) {
        this.maskX = maskX;
        this.maskY = maskY;
        this.maskWidth = maskWidth;
        this.maskHeight = maskHeight;
    }
    
    /**
     * Called when this GameObject collides with another GameObject.
     * Subclasses can override this method to implement specific collision responses.
     * 
     * @param other The other GameObject involved in the collision.
     */
    public void onCollision(GameObject other) {
        // By default, does nothing.
    }

    /**
     * Checks if two GameObjects are colliding based on their collision masks.
     * 
     * @param obj1 The first GameObject.
     * @param obj2 The second GameObject.
     * @return true if the collision masks intersect, false otherwise.
     * @throws NullPointerException if either obj1 or obj2 is null.
     */
    public static boolean isColliding(GameObject obj1, GameObject obj2) {
        Objects.requireNonNull(obj1, "Object 1 cannot be null.");
        Objects.requireNonNull(obj2, "Object 2 cannot be null.");
        
        // If ANY of the objects has collision disabled, collision is impossible.
        if (obj1.getCollisionType() == CollisionType.NO_COLLISION || obj2.getCollisionType() == CollisionType.NO_COLLISION) {
            return false;
        }
        
        // If both have collision, proceed with normal check
        Rectangle rect1 = new Rectangle(
            obj1.getX() + obj1.maskX,
            obj1.getY() + obj1.maskY,
            obj1.maskWidth,
            obj1.maskHeight
        );

        Rectangle rect2 = new Rectangle(
            obj2.getX() + obj2.maskX,
            obj2.getY() + obj2.maskY,
            obj2.maskWidth,
            obj2.maskHeight
        );

        return rect1.intersects(rect2);
    }

    /**
     * Retrieves an instance of a specific GameObject subclass from two potential GameObjects.
     * Useful for type-safe casting in collision callbacks.
     * 
     * @param <T> The type of GameObject subclass to retrieve.
     * @param clazz The Class object representing the desired subclass.
     * @param obj1 The first GameObject to check.
     * @param obj2 The second GameObject to check.
     * @return An instance of the specified class if found, otherwise null.
     */
    public static <T extends GameObject> T getInstanceOf(Class<T> clazz, GameObject obj1, GameObject obj2) {
        if (clazz.isInstance(obj1)) {
            return clazz.cast(obj1);
        } else if (clazz.isInstance(obj2)) {
            return clazz.cast(obj2);
        }
        return null;
    }

    /**
     * Updates the GameObject's logic and its components.
     * This method also updates the positions of all attached children.
     */
    public void tick() {
    	if (isDestroyed) return;
        for (Component component : components.values()) {
            component.update();
        }
        updateAllChildrenPositions();
    }
    
    // Implement IRenderable interface methods
    @Override
    public RenderLayer getRenderLayer() { return this.renderLayer; }

    @Override
    public int getZOrder() {
        // Uses the Y position for Z-ordering: objects further down are drawn on top.
        return getY() + getHeight();
    }
    
    @Override
    public boolean isVisible() { return !isDestroyed; }
    
    /**
     * Sets the rendering layer for this GameObject.
     * It unregisters from the old layer and registers with the new one.
     * 
     * @param layer The new RenderLayer for this GameObject.
     */
    public void setRenderLayer(RenderLayer layer) {
        RenderManager.getInstance().unregister(this);
        this.renderLayer = layer;
        RenderManager.getInstance().register(this);
    }

    /**
     * Renders the GameObject.
     * It first attempts to render using an attached Animator component.
     * If no Animator is present or active, it falls back to rendering the base sprite.
     * Also renders debug information if Engine.isDebug is true.
     *
     * @param g The Graphics context to draw on.
     */
    public void render(Graphics g) {
        if (isDestroyed) return;
        
        Sprite spriteToRender = null;
        
        // 1. Try to get the sprite from the Animator component
        Animator animator = getComponent(Animator.class);
        if (animator != null) {
            spriteToRender = animator.getCurrentSprite();
        }
        
        // 2. If not successful (no animator or no animation playing), use the base sprite
        if (spriteToRender == null) {
            spriteToRender = this.sprite;
        }

        // 3. Draw the found sprite
        if (spriteToRender != null) {
             g.drawImage(spriteToRender.getImage(), getX() - Engine.camera.getX(), getY() - Engine.camera.getY(), null);
        }
        
        if (Engine.isDebug) {
            renderDebug(g);
        }
    }
    
    /**
     * Renders debug information for the GameObject, such as its position, and collision mask.
     * This method is only called if {@code Engine.isDebug} is true.
     *
     * @param g The Graphics context to draw on.
     */
    public void renderDebug(Graphics g) {
    	if (!Engine.isDebug) return;
    	g.setColor(Color.GREEN);
    	g.setFont(new Font("Arial", Font.PLAIN, 10));
    	String posString = "(" + getX() + ", " + getY() + ")";
    	g.drawString(posString, getX() - Engine.camera.getX(), getY() - Engine.camera.getY() - 5);
    	g.setColor(Color.CYAN);
        g.fillRect(this.getX() - Engine.camera.getX() - 1, this.getY() - Engine.camera.getY() - 1, 3, 3);
        g.setColor(Color.RED);
        g.drawRect(getX() - Engine.camera.getX() + maskX, getY() - Engine.camera.getY() + maskY, maskWidth, maskHeight);
     // Additionally, components can draw themselves in debug mode
        for (Component component : components.values()) {
            component.render(g);
        }
    }
    
    /**
     * Marks this GameObject for destruction.
     * It will be unregistered from the RenderManager and typically removed from the game world
     * in the next cleanup phase.
     */
    public void destroy() {
        this.isDestroyed = true;
        RenderManager.getInstance().unregister(this);
    }
    
    /**
     * Sets the primary sprite for this GameObject.
     * @param newSprite The new Sprite to use.
     */
    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
    }
    
    // --- UPDATED POSITION SETTERS ---
    /**
     * Sets the x-coordinate of the object and updates all attached children's positions.
     * @param newX The new x-coordinate.
     */
    public void setX(double newX) { 
        this.x = newX; 
        updateAllChildrenPositions();
    }
    
    /**
     * Sets the y-coordinate of the object and updates all attached children's positions.
     * @param newY The new y-coordinate.
     */
    public void setY(double newY) { 
        this.y = newY; 
        updateAllChildrenPositions();
    }
    
    /**
     * Gets the x-coordinate of the object (integer part).
     * @return The x-coordinate.
     */
    public int getX() { return (int)this.x; }
    /**
     * Gets the y-coordinate of the object (integer part).
     * @return The y-coordinate.
     */
    public int getY() { return (int)this.y; }
    /**
     * Gets the width of the object.
     * @return The width.
     */
    public int getWidth() { return this.width; }
    /**
     * Gets the height of the object.
     * @return The height.
     */
    public int getHeight() { return this.height; }
    
    /**
     * Gets the x-offset of the collision mask.
     * @return The mask x-offset.
     */
    public int getMaskX() { return maskX; }
    /**
     * Sets the x-offset of the collision mask.
     * @param maskX The new mask x-offset.
     */
    public void setMaskX(int maskX) { this.maskX = maskX; }
    /**
     * Gets the y-offset of the collision mask.
     * @return The mask y-offset.
     */
    public int getMaskY() { return maskY; }
    /**
     * Sets the y-offset of the collision mask.
     * @param maskY The new mask y-offset.
     */
    public void setMaskY(int maskY) { this.maskY = maskY; }
    /**
     * Gets the width of the collision mask.
     * @return The mask width.
     */
    public int getMaskWidth() { return maskWidth; }
    /**
     * Sets the width of the collision mask.
     * @param maskWidth The new mask width.
     */
    public void setMaskWidth(int maskWidth) { this.maskWidth = maskWidth; }
    /**
     * Gets the height of the collision mask.
     * @return The mask height.
     */
    public int getMaskHeight() { return maskHeight; }
    /**
     * Sets the height of the collision mask.
     * @param maskHeight The new mask height.
     */
    public void setMaskHeight(int maskHeight) { this.maskHeight = maskHeight; }
    
    /**
     * Returns the X coordinate of the object's center, based on its total dimensions (width).
     * @return The central X position of the object.
     */
    public double getCenterX() {
        return this.x + (this.width / 2.0);
    }

    /**
     * Returns the Y coordinate of the object's center, based on its total dimensions (height).
     * @return The central Y position of the object.
     */
    public double getCenterY() {
        return this.y + (this.height / 2.0);
    }

    /**
     * Returns the X coordinate of the center of the collision MASK.
     * Useful for precise collision and targeting calculations.
     * @return The central X position of the collision mask.
     */
    public double getMaskCenterX() {
        return this.x + this.maskX + (this.maskWidth / 2.0);
    }

    /**
     * Returns the Y coordinate of the center of the collision MASK.
     * Useful for precise collision and targeting calculations.
     * @return The central Y position of the collision mask.
     */
    public double getMaskCenterY() {
        return this.y + this.maskY + (this.maskHeight / 2.0);
    }
}
