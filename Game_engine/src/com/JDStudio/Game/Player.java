// package com.arcastudio.mygame;
package com.JDStudio.Game;
import java.awt.event.KeyEvent;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.World;

public class Player extends GameObject {

    public double speed = 1.4;
    private World world; // <-- ADICIONE O CAMPO PARA GUARDAR O MUNDO
    public double life = 100;    // <-- Vida atual
    public double maxLife = 100; // <-- Vida máxima
    public int ammo = 0;    

    public Player(double x, double y, int width, int height) {
        super(x, y, width, height);
        setMaskWidth(9);
        setMaskX((int) (x+3));
        setupAnimations();
    }
    
    /**
     * Configura as animações do jogador.
     */
    private void setupAnimations() {
        // Cria a animação de "idle"
        Animation idleAnim = new Animation(10, PlayingState.assets.getSprite("player_idle"));
        
        // Cria a animação de "andar para a direita"
        Animation walkRightAnim = new Animation(10, 
            PlayingState.assets.getSprite("player_walk_right_1"),
            PlayingState.assets.getSprite("player_walk_right_2"),
            PlayingState.assets.getSprite("player_walk_right_3")
        );
        
        Animation walkLeftAnimation = new Animation(10,
        		PlayingState.assets.getSprite("player_walk_left_1"),
        		PlayingState.assets.getSprite("player_walk_left_2"),
        		PlayingState.assets.getSprite("player_walk_left_3")
        		);
        
        // Adiciona as animações ao componente Animator herdado de GameObject
        animator.addAnimation("idle", idleAnim);
        animator.addAnimation("walk_right", walkRightAnim);
        animator.addAnimation("walk_left", walkLeftAnimation);
        // (Você adicionaria as outras direções aqui)
    }
    
    /**
     * Define a instância do mundo que o jogador usará para verificar colisões.
     * @param world A instância do mundo do jogo.
     */
    public void setWorld(World world) { // <-- ADICIONE ESTE MÉTODO
        this.world = world;
    }

    @Override
    public void tick() {
    	boolean isMoving = false;
        // Movimento Horizontal
        if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT) || InputManager.isKeyPressed(KeyEvent.VK_D)) {
            if (world != null && world.isFree((int)(x + speed), this.getY(), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                x += speed;
                isMoving = true;
                animator.play("walk_right");
            }
        } else if (InputManager.isKeyPressed(KeyEvent.VK_LEFT) || InputManager.isKeyPressed(KeyEvent.VK_A)) {
            if (world != null && world.isFree((int)(x - speed), this.getY(), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                x -= speed;
                isMoving = true;
                animator.play("walk_left");
            }
        }

        // Movimento Vertical
        if (InputManager.isKeyPressed(KeyEvent.VK_UP) || InputManager.isKeyPressed(KeyEvent.VK_W)) {
            if (world != null && world.isFree(this.getX(), (int)(y - speed), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                y -= speed;
            }
        } else if (InputManager.isKeyPressed(KeyEvent.VK_DOWN) || InputManager.isKeyPressed(KeyEvent.VK_S)) {
            if (world != null && world.isFree(this.getX(), (int)(y + speed), this.maskX, this.maskY, this.maskWidth, this.maskHeight)) {
                y += speed;
            }
        }
        if (!isMoving) {
            animator.play("idle"); // Se parado, toca a animação "idle"
        }
        super.tick();
    }
}