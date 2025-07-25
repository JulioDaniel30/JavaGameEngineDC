package com.JDStudio.Engine.Object;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.World.Camera;

/**
 * A classe base para todos os objetos renderizáveis e atualizáveis no jogo.
 * Cada objeto no jogo deve estender esta classe.
 *
 * <p><b>Exemplo de como criar uma nova entidade:</b>
 * {@snippet :
 * class Rocket extends GameObject {
 * public Rocket(double x, double y) {
 * super(x, y, 16, 16, null); // Chama o construtor pai
 * }
 *
 * @Override
 * public void tick() {
 * // Lógica de movimento do foguete
 * this.y -= 5;
 * }
 * }
 *}
 */

public abstract class GameObject {

    protected double x, y; // Double para movimento suave
    protected int width, height;
    protected BufferedImage sprite;
    
    // Máscara de colisão
    protected int maskX, maskY, maskWidth, maskHeight;

    public GameObject(double x, double y, int width, int height, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        
     // Por padrão, a máscara de colisão é do mesmo tamanho do objeto
        this.maskX = 0;
        this.maskY = 0;
        this.maskWidth = width;
        this.maskHeight = height;
    }
    
    /**
     * Define uma máscara de colisão customizada para o objeto.
     * @param maskX O deslocamento X da máscara em relação ao X do objeto.
     * @param maskY O deslocamento Y da máscara em relação ao Y do objeto.
     * @param maskWidth A largura da máscara.
     * @param maskHeight A altura da máscara.
     */
    public void setCollisionMask(int maskX, int maskY, int maskWidth, int maskHeight) {
        this.maskX = maskX;
        this.maskY = maskY;
        this.maskWidth = maskWidth;
        this.maskHeight = maskHeight;
    }

    /**
     * Verifica se este GameObject está colidindo com outro.
     * Utiliza o método de interseção de retângulos (AABB).
     *
     * @param obj1 O primeiro objeto.
     * @param obj2 O segundo objeto.
     * @return true se houver colisão, false caso contrário.
     */
    public static boolean isColliding(GameObject obj1, GameObject obj2) {
        // Cria os retângulos de colisão para cada objeto na sua posição atual
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

        // O método intersects faz toda a mágica da verificação
        return rect1.intersects(rect2);
    }

    /** este metodo fica toda a logica do gameObject,
     * ele é atualizado a cada frame
     * */
    public abstract void tick();

   

	public void render(Graphics g) {
        // Desenha o sprite normal
        g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);

        // Se o modo de debug estiver ativo, desenha a máscara de colisão
        if (Engine.isDebug) {
            g.setColor(Color.RED); // Define uma cor para a hitbox
            g.drawRect(
                this.getX() + this.maskX - Camera.x,
                this.getY() + this.maskY - Camera.y,
                this.maskWidth,
                this.maskHeight
            );
        }
    }
	
	/**
     * Um método utilitário genérico para encontrar uma instância de um tipo específico
     * entre dois objetos. Isso elimina a necessidade de usar o operador ternário
     * repetidamente no loop de colisão.
     *
     * @param <T> O tipo genérico da classe que estamos procurando.
     * @param clazz A classe do tipo que queremos encontrar (ex: Player.class).
     * @param obj1 O primeiro objeto da colisão.
     * @param obj2 O segundo objeto da colisão.
     * @return A instância do tipo procurado, ou null se nenhum dos objetos for desse tipo.
     */
    public static <T extends GameObject> T getInstanceOf(Class<T> clazz, GameObject obj1, GameObject obj2) {
        if (clazz.isInstance(obj1)) {
            return clazz.cast(obj1);
        } else if (clazz.isInstance(obj2)) {
            return clazz.cast(obj2);
        }
        return null; // Nenhum dos objetos é do tipo procurado
    }
	
    
    // Getters and Setters
    public void setX(double newX) { this.x = newX; }
    public void setY(double newY) { this.y = newY; }
    public int getX() { return (int)this.x; }
    public int getY() { return (int)this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getMaskX() {
		return maskX;
	}

	public void setMaskX(int maskX) {
		this.maskX = maskX;
	}

	public int getMaskY() {
		return maskY;
	}

	public void setMaskY(int maskY) {
		this.maskY = maskY;
	}

	public int getMaskWidth() {
		return maskWidth;
	}

	public void setMaskWidth(int maskWidth) {
		this.maskWidth = maskWidth;
	}

	public int getMaskHeight() {
		return maskHeight;
	}

	public void setMaskHeight(int maskHeight) {
		this.maskHeight = maskHeight;
	}
}