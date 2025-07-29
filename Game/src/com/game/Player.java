// package com.arcastudio.mygame;
package com.game;

import java.awt.event.KeyEvent;

import com.JDStudio.Engine.Components.Moviments.MovementComponent;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.World.World;
@SuppressWarnings("unused")
public class Player extends Character{
	public double speed;
	
	private World world; // <-- ADICIONE O CAMPO PARA GUARDAR O MUNDO

	public Player(double x, double y, int width, int height) {
		super(x, y, width, height);
		this.speed = 1.4;
		this.movement = new MovementComponent(this, this.speed);
		this.maxLife = 100;
        this.life = this.maxLife;
		setMaskWidth(9);
		setMaskHeight(15);
		setMaskY(1);
		setMaskX(3); //
		setupAnimations();

	}

	/**
	 * Configura as animações do jogador.
	 */
	private void setupAnimations() {
		// Cria a animação de "idle"
		Animation idleAnim = new Animation(10, PlayingState.assets.getSprite("player_idle"));

		// Cria a animação de "andar para a direita"
		Animation walkRightAnim = new Animation(10, PlayingState.assets.getSprite("player_walk_right_1"),
				PlayingState.assets.getSprite("player_walk_right_2"),
				PlayingState.assets.getSprite("player_walk_right_3"));

		Animation walkLeftAnimation = new Animation(10, PlayingState.assets.getSprite("player_walk_left_1"),
				PlayingState.assets.getSprite("player_walk_left_2"),
				PlayingState.assets.getSprite("player_walk_left_3"));

		// Adiciona as animações ao componente Animator herdado de GameObject
		animator.addAnimation("idle", idleAnim);
		animator.addAnimation("walk_right", walkRightAnim);
		animator.addAnimation("walk_left", walkLeftAnimation);
		// (Você adicionaria as outras direções aqui)
	}

	/**
	 * Define a instância do mundo que o jogador usará para verificar colisões.
	 * 
	 * @param world A instância do mundo do jogo.
	 */
	public void setWorld(World world) { // <-- ADICIONE ESTE MÉTODO
		this.world = world;
		this.movement.setWorld(world);
		this.movement.setGameObjects(PlayingState.getGameObjects());
	}

	@Override
	public void onCollision(GameObject other) {
		super.onCollision(other);
		if (other instanceof Lifepack) {
			// Aumenta a vida do jogador, sem ultrapassar o máximo
			this.life += 25; // Exemplo: cura 25 de vida
			if (this.life > this.maxLife) {
				this.life = this.maxLife;
			}
			Sound.play("/hurt.wav", 0.5f);
			// Sinaliza que o Lifepack deve ser destruído
			other.isDestroyed = true;

			// Opcional: tocar um som de coleta
			// Sound.play("/sounds/pickup.wav");
		}
	}

	@Override
	public void tick() {
		
		MovementComponent playerMovement = (MovementComponent) this.movement;
		double dx = 0;
		double dy = 0;
		// Movimento Horizontal

		// Movimento Horizontal
		if (InputManager.isKeyPressed(KeyEvent.VK_LEFT) || InputManager.isKeyPressed(KeyEvent.VK_A)) {
			dx = -1;
		} else if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT) || InputManager.isKeyPressed(KeyEvent.VK_D)) {
			dx = 1;
		}

		// Movimento Vertical
		if (InputManager.isKeyPressed(KeyEvent.VK_UP) || InputManager.isKeyPressed(KeyEvent.VK_W)) {
			dy = -1;

		} else if (InputManager.isKeyPressed(KeyEvent.VK_DOWN) || InputManager.isKeyPressed(KeyEvent.VK_S)) {
			dy = 1;

		}
		playerMovement.setDirection(dx, dy);

		// 4. Atualiza a animação com base no input
		if (dx > 0) {
			animator.play("walk_right");
		} else if (dx < 0) {
			animator.play("walk_left");
		} else if (dy != 0) {
			// Supondo que você tenha animações para cima/baixo
			// animator.play("walk_up");
		} else {
			animator.play("idle");
		}
		super.tick(); 
        this.movement.tick(); // <-- ADICIONE ESTA LINHA

	}
}