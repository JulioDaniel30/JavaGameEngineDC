package com.JDStudio.Engine.Object;

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

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.Component;
import com.JDStudio.Engine.Graphics.Layers.IRenderable;
import com.JDStudio.Engine.Graphics.Layers.RenderLayer;
import com.JDStudio.Engine.Graphics.Layers.RenderManager;
import com.JDStudio.Engine.Graphics.Layers.StandardLayers;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Class GameObject
 * @author JDStudio
 * @since 1.0
 */
public abstract class GameObject implements IRenderable  {

    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected Sprite sprite;
    protected int maskX;
    public int maskY;
    protected int maskWidth;
    protected int maskHeight;
    public double velocityY = 0; // Para a lógica de ONE_WAY
    public boolean onGround = false;
    public boolean isDestroyed = false;
    public String name = "";

    // --- INÍCIO DO SISTEMA DE ATTACHMENT ---

    /** O GameObject "pai" ao qual este objeto está anexado. Nulo se não estiver anexado a ninguém. */
    protected GameObject parent = null;

    /** A lista de GameObjects "filhos" que estão anexados a este objeto. */
    protected List<GameObject> children = new ArrayList<>();

    /** A posição local do objeto em relação ao seu pai (deslocamento). */
    protected Point localPosition = new Point(0, 0);

    // --- FIM DO SISTEMA DE ATTACHMENT ---
    
    protected RenderLayer renderLayer = StandardLayers.GAMEPLAY_BELOW;

    public enum CollisionType {
        NO_COLLISION, // Não tem colisão de nenhum tipo.
        SOLID,        // Sólido para tudo (paredes, portas fechadas).
        TRIGGER,      // Atravessável, mas pode acionar eventos (itens, jogador).
        CHARACTER_SOLID, //Sólido para outros personagens, mas atravessável por projéteis.
        CHARACTER_TRIGGER
    }
    
    public CollisionType collisionType = CollisionType.TRIGGER;
    
    /**
     * Se verdadeiro, este objeto não será removido automaticamente pelo loop de limpeza
     * do EnginePlayingState, mesmo que 'isDestroyed' seja verdadeiro.
     * Ideal para o jogador, que deve gerir o seu próprio estado de "game over".
     */
    public boolean isProtectedFromCleanup = false;
    
 // --- O NOVO SISTEMA DE COMPONENTES ---
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public GameObject(JSONObject properties) {
        initialize(properties);
        RenderManager.getInstance().register(this);
    }

    public void initialize(JSONObject properties) {
        PropertiesReader reader = new PropertiesReader(properties);
        this.x = reader.getInt("x", 0);
        this.width = reader.getInt("width", 16);
        this.height = reader.getInt("height", 16);
        this.y = reader.getInt("y", 0) - this.height;
        this.name = reader.getString("name", "");
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = this.width;
        this.maskHeight = this.height;
        String layerName = reader.getString("renderLayer", "GAMEPLAY_BELOW");
        
        // 2. Pede ao RenderManager para encontrar a camada com esse nome.
        RenderLayer layer = RenderManager.getInstance().getLayerByName(layerName);
        
        // 3. Define a camada de renderização do GameObject.
        if (layer != null) {
            this.setRenderLayer(layer);
        } else {
            // Se o nome da camada no Tiled for inválido ou não estiver registado,
            // usa o padrão da engine e avisa no console.
            System.err.println("Aviso: RenderLayer '" + layerName + "' inválida ou não registada para o objeto '" + this.name + "'. Usando a camada padrão.");
            this.setRenderLayer(StandardLayers.GAMEPLAY_BELOW);
        }
    }
    
  
    
    /**
     * Adiciona um novo componente a este GameObject.
     * @param component A instância do componente a ser adicionado.
     * @return O próprio GameObject, para encadeamento de chamadas (ex: go.addComponent(...).addComponent(...)).
     */
    public <T extends Component> GameObject addComponent(T component) {
        components.put(component.getClass(), component);
        component.setOwner(this);
        
        // --- MUDANÇA AQUI ---
        // Passa o 'owner' diretamente para a inicialização do componente.
        component.initialize(this); 
        
        return this;
    }

    /**
     * Pega um componente deste GameObject pelo seu tipo (classe).
     * @param componentClass A classe do componente desejado (ex: Animator.class).
     * @return A instância do componente, ou null se não for encontrado.
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        Component comp = components.get(componentClass);
        if (comp == null) {
            return null;
        }
        return componentClass.cast(comp);
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }
    
    // --- MÉTODOS PARA GERENCIAR ATTACHMENTS ---

    /**
     * Anexa um GameObject "filho" a este GameObject ("pai").
     * @param child O objeto a ser anexado.
     * @param localX A posição X do filho em relação ao canto superior esquerdo do pai.
     * @param localY A posição Y do filho em relação ao canto superior esquerdo do pai.
     */
    public void attach(GameObject child, int localX, int localY) {
        if (child.parent != null) {
            child.parent.children.remove(child); // Remove da lista de filhos do pai antigo
        }
        child.parent = this; // Define este objeto como o novo pai
        child.localPosition.setLocation(localX, localY); // Define a posição relativa
        if (!this.children.contains(child)) {
            this.children.add(child); // Adiciona à lista de filhos deste objeto
        }
        
        // Atualiza a posição do filho imediatamente
        updateChildPosition(child);
    }

    /**
     * Desanexa este GameObject de seu pai atual.
     * O objeto manterá sua última posição no mundo, mas não seguirá mais o pai.
     */
    public void detach() {
        if (this.parent != null) {
            this.parent.children.remove(this); // Remove-se da lista de filhos do pai
            this.parent = null; // Remove a referência ao pai
        }
    }

    /**
     * Atualiza a posição no mundo de um único filho com base na posição do pai.
     * @param child O filho a ser atualizado.
     */
    private void updateChildPosition(GameObject child) {
        child.setX(this.x + child.localPosition.x);
        child.setY(this.y + child.localPosition.y);
    }

    /**
     * Atualiza a posição de todos os filhos anexados.
     */
    private void updateAllChildrenPositions() {
        for (GameObject child : this.children) {
            updateChildPosition(child);
        }
    }
    
    /**
     * Procura e retorna um filho anexado a este GameObject pelo seu nome.
     * O nome é a propriedade "name" que você define para o objeto no Tiled Editor.
     *
     * @param name O nome do GameObject filho a ser procurado.
     * @return O GameObject filho se encontrado, caso contrário, retorna null.
     */
    public GameObject getChildByName(String name) {
        // Garante que o nome a ser procurado não seja nulo ou vazio para evitar erros.
        if (name == null || name.isEmpty()) {
            return null;
        }

        // Percorre a lista de filhos ('children') deste objeto.
        for (GameObject child : this.children) {
            // Compara o nome do filho com o nome que estamos procurando.
            if (name.equals(child.name)) {
                return child; // Encontrou! Retorna o objeto filho.
            }
        }

        // Se o loop terminar e nenhum filho com esse nome for encontrado, retorna null.
        return null;
    }
    
    /**
     * Altera a posição relativa (offset) de um objeto filho em relação ao seu pai.
     * Use este método para mover um objeto que já está anexado.
     * @param localX A nova posição X relativa à origem (canto superior esquerdo) do pai.
     * @param localY A nova posição Y relativa à origem do pai.
     */
    public void setLocalPosition(int localX, int localY) {
        this.localPosition.setLocation(localX, localY);
    }

    // --- FIM DOS MÉTODOS DE ATTACHMENT ---
    
    public void setCollisionType(CollisionType type) {
        this.collisionType = type;
    }

    public CollisionType getCollisionType() {
        return this.collisionType;
    }

    public void setCollisionMask(int maskX, int maskY, int maskWidth, int maskHeight) {
        this.maskX = maskX;
        this.maskY = maskY;
        this.maskWidth = maskWidth;
        this.maskHeight = maskHeight;
    }
    
    public void onCollision(GameObject other) {
        // Por padrão, não faz nada.
    }

    public static boolean isColliding(GameObject obj1, GameObject obj2) {
        Objects.requireNonNull(obj1, "O objeto 1 não pode ser nulo.");
        Objects.requireNonNull(obj2, "O objeto 2 não pode ser nulo.");
        
        // --- LÓGICA ADICIONADA AQUI ---
        // Se QUALQUER um dos objetos tiver a colisão desativada, a colisão é impossível.
        if (obj1.getCollisionType() == CollisionType.NO_COLLISION || obj2.getCollisionType() == CollisionType.NO_COLLISION) {
            return false;
        }
        
        // Se ambos têm colisão, prossegue com a verificação normal
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

    public static <T extends GameObject> T getInstanceOf(Class<T> clazz, GameObject obj1, GameObject obj2) {
        if (clazz.isInstance(obj1)) {
            return clazz.cast(obj1);
        } else if (clazz.isInstance(obj2)) {
            return clazz.cast(obj2);
        }
        return null;
    }

    /**
     * O método tick agora também é responsável por atualizar os filhos.
     */
    public void tick() {
    	if (isDestroyed) return;
        for (Component component : components.values()) {
            component.update();
        }
        updateAllChildrenPositions();
    }
    
 // Implemente os métodos da interface
    @Override
    public RenderLayer getRenderLayer() { return this.renderLayer; }

    @Override
    public int getZOrder() {
        // Usa a posição Y para a ordenação Z: objetos mais abaixo são desenhados por cima.
        return getY() + getHeight();
    }
    
    @Override
    public boolean isVisible() { return !isDestroyed; }
    
    public void setRenderLayer(RenderLayer layer) {
        RenderManager.getInstance().unregister(this);
        this.renderLayer = layer;
        RenderManager.getInstance().register(this);
    }

    /**
     * O render do GameObject agora é mais robusto.
     * Ele primeiro tenta renderizar a partir do Animator. Se falhar,
     * ele usa a variável 'sprite' como um fallback.
     */
    public void render(Graphics g) {
        if (isDestroyed) return;
        
        Sprite spriteToRender = null;
        
        // 1. Tenta pegar o sprite do componente Animator
        Animator animator = getComponent(Animator.class);
        if (animator != null) {
            spriteToRender = animator.getCurrentSprite();
        }
        
        // 2. Se não conseguiu (sem animator ou sem animação tocando), usa o sprite base
        if (spriteToRender == null) {
            spriteToRender = this.sprite;
        }

        // 3. Desenha o sprite que foi encontrado
        if (spriteToRender != null) {
             g.drawImage(spriteToRender.getImage(), getX() - Engine.camera.getX(), getY() - Engine.camera.getY(), null);
        }
        
        if (Engine.isDebug) {
            renderDebug(g);
        }
    }
    
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
     // Adicionalmente, podemos fazer os componentes se desenharem no debug
        for (Component component : components.values()) {
            component.render(g);
        }
    }
    
    public void destroy() { // Um novo método para destruir objetos
        this.isDestroyed = true;
        RenderManager.getInstance().unregister(this);
    }
    
    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
    }
    
    // --- SETTERS DE POSIÇÃO ATUALIZADOS ---
    /**
     * Define a posição X e atualiza todos os filhos anexados.
     */
    public void setX(double newX) { 
        this.x = newX; 
        updateAllChildrenPositions();
    }
    
    /**
     * Define a posição Y e atualiza todos os filhos anexados.
     */
    public void setY(double newY) { 
        this.y = newY; 
        updateAllChildrenPositions();
    }
    
    public int getX() { return (int)this.x; }
    public int getY() { return (int)this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getMaskX() { return maskX; }
    public void setMaskX(int maskX) { this.maskX = maskX; }
    public int getMaskY() { return maskY; }
    public void setMaskY(int maskY) { this.maskY = maskY; }
    public int getMaskWidth() { return maskWidth; }
    public void setMaskWidth(int maskWidth) { this.maskWidth = maskWidth; }
    public int getMaskHeight() { return maskHeight; }
    public void setMaskHeight(int maskHeight) { this.maskHeight = maskHeight; }
}