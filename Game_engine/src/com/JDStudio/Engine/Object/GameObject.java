package com.JDStudio.Engine.Object;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Objects;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.Moviments.BaseMovementComponent;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Class GameObject
 * @author JDStudio
 * @since 1.0
 */
public abstract class GameObject {

    /** A posição horizontal precisa (eixo X), usando double para movimento suave. */
    protected double x;

    /** A posição vertical precisa (eixo Y), usando double para movimento suave. */
    protected double y;

    /** A largura visual do objeto. */
    protected int width;

    /** A altura visual do objeto. */
    protected int height;

    /** O sprite (imagem) associado a este objeto. */
    protected Sprite sprite;

    //<editor-fold desc="Collision Mask">
    /** O deslocamento da máscara de colisão no eixo X em relação à posição do objeto. */
    protected int maskX;
    /** O deslocamento da máscara de colisão no eixo Y em relação à posição do objeto. */
    public int maskY;
    /** A largura da máscara de colisão. */
    protected int maskWidth;
    /** A altura da máscara de colisão. */
    protected int maskHeight;
    protected Animator animator;
    public BaseMovementComponent movement;
    public boolean isDestroyed = false;
    
    
    public String name = "";

    public enum CollisionType {
        NO_COLLISION, // Não colide com nada
        SOLID,        // Bloqueia o movimento
        TRIGGER       // Não bloqueia, mas detecta sobreposição
    }
    
    public CollisionType collisionType = CollisionType.TRIGGER;
    
    public GameObject(JSONObject properties) {
        // O construtor agora recebe o objeto de propriedades
        this.animator = new Animator();
        initialize(properties); // Chama o método de inicialização
        this.movement = new BaseMovementComponent(this, 0) {
            @Override
            public void tick() { /* Não faz nada */ }
        };
    }

    /**
     * Inicializa o GameObject a partir de um conjunto de propriedades do Tiled.
     * As subclasses devem chamar super.initialize(properties) primeiro.
     */
    public void initialize(JSONObject properties) {
        PropertiesReader reader = new PropertiesReader(properties);
        
        // Pega as propriedades básicas que todo GameObject tem
        this.x = reader.getInt("x", 0);
        this.width = reader.getInt("width", 16);
        this.height = reader.getInt("height", 16);
        this.y = reader.getInt("y", 0) - this.height; // Ajuste automático de Y
        this.name = reader.getString("name", "");
        
     // A máscara é inicializada com o tamanho total por padrão.
        // O setCollisionType cuidará de ajustá-la se necessário.
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = this.width;
        this.maskHeight = this.height;
        
    }
    
    /**
     * NOVO MÉTODO "ENGINE SIDE"
     * Define o tipo de colisão e ajusta a máscara de colisão de acordo.
     */
    public void setCollisionType(CollisionType type) {
        this.collisionType = type;
        
        if (type == CollisionType.NO_COLLISION) {
            // Se não há colisão, zera a máscara.
            setCollisionMask(0, 0, 0, 0);
        } else {
            // Para SOLID ou TRIGGER, a máscara física existe e tem o tamanho do objeto.
            setCollisionMask(0, 0, this.maskWidth, this.maskHeight);
        }
    }

    /**
     * Retorna o tipo de colisão atual do objeto.
     */
    public CollisionType getCollisionType() {
        return this.collisionType;
    }
    

    /**
     * Define uma máscara de colisão customizada para o objeto, diferente de suas dimensões visuais.
     *
     * @param maskX      O deslocamento X da máscara em relação à posição (x) do objeto.
     * @param maskY      O deslocamento Y da máscara em relação à posição (y) do objeto.
     * @param maskWidth  A largura da máscara de colisão.
     * @param maskHeight A altura da máscara de colisão.
     */
    public void setCollisionMask(int maskX, int maskY, int maskWidth, int maskHeight) {
        this.maskX = maskX;
        this.maskY = maskY;
        this.maskWidth = maskWidth;
        this.maskHeight = height;
    }

    
    /**
     * Chamado quando este objeto colide com outro.
     * Subclasses devem sobrescrever este método para implementar a lógica de colisão específica.
     * @param other O GameObject com o qual este objeto colidiu.
     */
    public void onCollision(GameObject other) {
        // Por padrão, não faz nada.
    }

    
    /**
     * Verifica se dois GameObjects estão colidindo.
     * <p>
     * Utiliza o método de interseção de retângulos (AABB - Axis-Aligned Bounding Box)
     * com base nas máscaras de colisão de cada objeto.
     *
     * @param obj1 O primeiro objeto a ser verificado.
     * @param obj2 O segundo objeto a ser verificado.
     * @return {@code true} se os objetos estiverem colidindo, {@code false} caso contrário.
     * 
     * 
     */
    public static boolean isColliding(GameObject obj1, GameObject obj2) {
        Objects.requireNonNull(obj1, "O objeto 1 não pode ser nulo.");
        Objects.requireNonNull(obj2, "O objeto 2 não pode ser nulo.");
        
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
     * Um método utilitário genérico para encontrar uma instância de um tipo específico
     * entre dois objetos. Útil para identificar os participantes de uma colisão.
     *
     * @param <T>   O tipo genérico da classe que estamos procurando.
     * @param clazz A classe do tipo que queremos encontrar (ex: {@code Player.class}).
     * @param obj1  O primeiro objeto da colisão.
     * @param obj2  O segundo objeto da colisão.
     * @return A instância do tipo procurado, ou {@code null} se nenhum dos objetos for desse tipo.
     */
    public static <T extends GameObject> T getInstanceOf(Class<T> clazz, GameObject obj1, GameObject obj2) {
        if (clazz.isInstance(obj1)) {
            return clazz.cast(obj1);
        } else if (clazz.isInstance(obj2)) {
            return clazz.cast(obj2);
        }
        return null; // Nenhum dos objetos é do tipo procurado
    }

    /**
     * Contém a lógica de atualização do objeto, como movimento, IA ou resposta a inputs.
     * <p>
     * Este método abstrato <b>deve</b> ser implementado por todas as subclasses e é chamado
     * pelo motor do jogo a cada quadro (frame).
     */
    public void tick() {
    	animator.tick();
    	//movement.tick(); // O GameObject agora delega a atualização do movimento
    }

    /**
     * Renderiza o objeto na tela.
     * <p>
     * Desenha o sprite do objeto na sua posição atual, ajustada pela câmera.
     * Se o modo de debug estiver ativo ({@code Engine.isDebug}), desenha também
     * um retângulo vermelho representando a máscara de colisão.
     *
     * @param g O contexto {@link Graphics} onde o objeto será desenhado.
     */
    public void render(Graphics g) {
        /*if (sprite != null) {
            g.drawImage(sprite.getImage(), this.getX() - Camera.x, this.getY() - Camera.y, null);
        }*/
        Sprite currentSprite = animator.getCurrentSprite();
        if (currentSprite != null) {
            // Desenha o frame atual da animação
            g.drawImage(currentSprite.getImage(), this.getX() - Engine.camera.getX(), this.getY() -Engine.camera.getY(), null);
        } else if (this.sprite != null) {
            // Se não houver animação, desenha o sprite estático padrão
            g.drawImage(this.sprite.getImage(), this.getX() - Engine.camera.getX(), this.getY() - Engine.camera.getY(), null);
        }

       renderDebug(g);
    }
    
    public void renderDebug(Graphics g) {
    	if (!Engine.isDebug) return;
    	
    	// Desenha a posição (origem) do objeto
    	g.setColor(Color.GREEN);
    	g.setFont(new Font("Arial", Font.PLAIN, 10));
    	String posString = "(" + getX() + ", " + getY() + ")";
    	g.drawString(
    			posString,
    			getX() - Engine.camera.getX(),
    			getY() - Engine.camera.getY() - 5 // Desenha um pouco acima do objeto
    	);
    	g.setColor(Color.CYAN); // Uma cor que se destaca
        // Desenha um pequeno quadrado de 3x3 pixels centrado na origem do objeto
        g.fillRect(
            this.getX() - Engine.camera.getX() - 1, 
            this.getY() - Engine.camera.getY() - 1, 
            3, 
            3
        );

       // Desenha a máscara de colisão
       g.setColor(Color.RED);
       g.drawRect(
           getX() - Engine.camera.getX() + maskX,
           getY() - Engine.camera.getY() + maskY,
           maskWidth,
           maskHeight
       );
    	
    }
    
    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
    }

    public void setX(double newX) { this.x = newX; }
    public void setY(double newY) { this.y = newY; }

    /** Retorna a posição X do objeto como um inteiro. */
    public int getX() { return (int)this.x; }
    /** Retorna a posição Y do objeto como um inteiro. */
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