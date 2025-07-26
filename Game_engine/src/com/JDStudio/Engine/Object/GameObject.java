package com.JDStudio.Engine.Object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Objects;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Sprite;
import com.JDStudio.Engine.World.Camera;

/**
 * Verifica se dois GameObjects estão colidindo, respeitando suas máscaras de colisão.
 * <p>
 * Este método utiliza a técnica de <strong>AABB (Axis-Aligned Bounding Box)</strong>.
 * Ele cria um {@link java.awt.Rectangle} para a máscara de colisão de cada objeto
 * e então usa o método {@code intersects()} para determinar se há uma sobreposição.
 *
 * <p><b>Etapa 1: Detectando a Colisão</b>
 * <p>O primeiro passo é usar este método dentro de um loop para detectar se uma
 * colisão entre quaisquer dois objetos ocorreu.
 *
 * <pre>{@code
 * // Exemplo dentro do método tick() do seu GameState
 * if (GameObject.isColliding(obj1, obj2)) {
 * // Colisão detectada! Prossiga para a próxima etapa.
 * // ...
 * }
 * }</pre>
 *
 * <p><b>Etapa 2: Identificando os Objetos e Executando a Ação</b>
 * <p>Após detectar a colisão, use {@code instanceof} para determinar o tipo de
 * interação e execute a lógica específica para aquela colisão.
 *
 * <pre>{@code
 * // Continuação do código dentro do if(isColliding(...))
 *
 * // A interação é entre um Player e um Enemy?
 * if ((obj1 instanceof Player && obj2 instanceof Enemy) ||
 * (obj1 instanceof Enemy && obj2 instanceof Player)) {
 *
 * // Sim. Obtenha as instâncias para usar seus métodos.
 * Player player = GameObject.getInstanceOf(Player.class, obj1, obj2);
 * Enemy enemy = GameObject.getInstanceOf(Enemy.class, obj1, obj2);
 *
 * // Execute a lógica de dano.
 * player.takeDamage(10);
 * }
 * }</pre>
 *
 * @param obj1 O primeiro objeto a ser verificado na colisão.
 * @param obj2 O segundo objeto a ser verificado na colisão.
 * @return {@code true} se as máscaras de colisão dos objetos se sobrepõem,
 * {@code false} caso contrário.
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
    protected int maskY;
    /** A largura da máscara de colisão. */
    protected int maskWidth;
    /** A altura da máscara de colisão. */
    protected int maskHeight;
    //</editor-fold>

    /**
     * Construtor base para todos os GameObjects.
     *
     * @param x      A posição inicial no eixo X.
     * @param y      A posição inicial no eixo Y.
     * @param width  A largura do objeto.
     * @param height A altura do objeto.
     * @param sprite O sprite a ser renderizado para este objeto. Pode ser nulo.
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
     * Verifica se dois GameObjects estão colidindo.
     * <p>
     * Utiliza o método de interseção de retângulos (AABB - Axis-Aligned Bounding Box)
     * com base nas máscaras de colisão de cada objeto.
     *
     * @param obj1 O primeiro objeto a ser verificado.
     * @param obj2 O segundo objeto a ser verificado.
     * @return {@code true} se os objetos estiverem colidindo, {@code false} caso contrário.
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
    public abstract void tick();

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
        if (sprite != null) {
            g.drawImage(sprite.getImage(), this.getX() - Camera.x, this.getY() - Camera.y, null);
        }

        if (Engine.isDebug) {
            g.setColor(Color.RED);
            g.drawRect(
                this.getX() + this.maskX - Camera.x,
                this.getY() + this.maskY - Camera.y,
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