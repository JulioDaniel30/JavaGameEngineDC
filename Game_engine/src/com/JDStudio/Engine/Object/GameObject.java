package com.JDStudio.Engine.Object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Objects;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.MovementComponent;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.World.Camera;

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
    public MovementComponent movement;
    public boolean isDestroyed = false;

    /**
     * Construtor base para todos os GameObjects.
     *
     * @param x      A posição inicial no eixo X.
     * @param y      A posição inicial no eixo Y.
     * @param width  A largura do objeto.
     * @param height A altura do objeto.
     * @param sprite A sprite do objeto
     */
    public GameObject(double x, double y, int width, int height, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        // Por padrão, a máscara de colisão ocupa todo o espaço do objeto.
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = width;
        this.maskHeight = height;
        
        this.animator = new Animator();
        // Inicializa o componente de movimento com velocidade 0 por padrão.
        // A velocidade real será definida pelas subclasses (como o Player).
        this.movement = new MovementComponent(this, 0);
        
    }
    
    /**
     * Construtor base para todos os GameObjects.
     *
     * @param x      A posição inicial no eixo X.
     * @param y      A posição inicial no eixo Y.
     * @param width  A largura do objeto.
     * @param height A altura do objeto.
     */
    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Por padrão, a máscara de colisão ocupa todo o espaço do objeto.
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = width;
        this.maskHeight = height;
        
        this.animator = new Animator();
        
     // Inicializa o componente de movimento com velocidade 0 por padrão.
        // A velocidade real será definida pelas subclasses (como o Player).
        this.movement = new MovementComponent(this, 0);
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
    	movement.tick(); // O GameObject agora delega a atualização do movimento
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

        if (Engine.isDebug) {
            g.setColor(Color.RED);
            g.drawRect(
                this.getX() + this.maskX - Engine.camera.getX(),
                this.getY() + this.maskY - Engine.camera.getY(),
                this.maskWidth,
                this.maskHeight
            );
        }
    }

    //<editor-fold desc="Getters and Setters">
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
    //</editor-fold>
}