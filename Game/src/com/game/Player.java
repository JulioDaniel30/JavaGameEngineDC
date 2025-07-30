// game
package com.game;

import java.awt.event.KeyEvent;

import org.json.JSONObject; // Importação necessária

import com.JDStudio.Engine.Components.Moviments.MovementComponent;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.Utils.PropertiesReader; // Importa o nosso leitor
import com.JDStudio.Engine.World.World;

@SuppressWarnings("unused")
public class Player extends Character {
	
	private World world;

		public Player(JSONObject properties) {
			super(properties);
		}

		@Override
		public void initialize(JSONObject properties) {
			super.initialize(properties);
			
			PropertiesReader reader = new PropertiesReader(properties);
			double speed = reader.getDouble("speed", 1.4);
			
			this.movement = new MovementComponent(this, speed);
			
			// A máscara de colisão também poderia ser lida das propriedades!
			setMaskWidth(9);
			setMaskHeight(15);
			setMaskY(1);
			setMaskX(3);
			
			// Chama o método para configurar as animações, passando as propriedades
			setupAnimations(properties);
		}

		/**
		 * O método setupAnimations agora lê os nomes dos sprites a partir das propriedades.
		 */
		private void setupAnimations(JSONObject properties) {
			PropertiesReader reader = new PropertiesReader(properties);
			
			// Lê o nome de cada sprite do Tiled, com um valor "padrão" caso não seja definido
			String idleSpriteName = reader.getString("sprite_idle", "player_idle");
			String walkR1Name = reader.getString("sprite_walk_right_1", "player_walk_right_1");
			String walkR2Name = reader.getString("sprite_walk_right_2", "player_walk_right_2");
			String walkR3Name = reader.getString("sprite_walk_right_3", "player_walk_right_3");
			String walkL1Name = reader.getString("sprite_walk_left_1", "player_walk_left_1");
			String walkL2Name = reader.getString("sprite_walk_left_2", "player_walk_left_2");
			String walkL3Name = reader.getString("sprite_walk_left_3", "player_walk_left_3");
			
			// Busca os sprites no AssetManager usando os nomes lidos
			Animation idleAnim = new Animation(10, PlayingState.assets.getSprite(idleSpriteName));
			Animation walkRightAnim = new Animation(10, 
					PlayingState.assets.getSprite(walkR1Name),
					PlayingState.assets.getSprite(walkR2Name),
					PlayingState.assets.getSprite(walkR3Name));
			Animation walkLeftAnim = new Animation(10,
					PlayingState.assets.getSprite(walkL1Name),
					PlayingState.assets.getSprite(walkL2Name),
					PlayingState.assets.getSprite(walkL3Name));

			animator.addAnimation("idle", idleAnim);
			animator.addAnimation("walk_right", walkRightAnim);
			animator.addAnimation("walk_left", walkLeftAnim);
		}

	public void setWorld(World world) {
		this.world = world;
		if (this.movement != null) {
			this.movement.setWorld(world);
			this.movement.setGameObjects(PlayingState.getGameObjects());
		}
	}

	@Override
	public void onCollision(GameObject other) {
		if (other instanceof Lifepack) {
			this.heal(25); // Usa o método heal() herdado de Character
			Sound.play("/hurt.wav", 0.5f);
			other.isDestroyed = true;
		}
		
		
		
	}

	@Override
	public void tick() {
		// A checagem de isDead e a atualização do animator são feitas no super.tick()
		super.tick(); 
		if (isDead) return;

		handleMovementInput();
        
        if (this.movement != null) {
            this.movement.tick();
        }
	}
	
	private void handleMovementInput() {
		MovementComponent playerMovement = (MovementComponent) this.movement;
		double dx = 0;
		double dy = 0;

		if (InputManager.isKeyPressed(KeyEvent.VK_LEFT) || InputManager.isKeyPressed(KeyEvent.VK_A)) {
			dx = -1;
		} else if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT) || InputManager.isKeyPressed(KeyEvent.VK_D)) {
			dx = 1;
		}
		if (InputManager.isKeyPressed(KeyEvent.VK_UP) || InputManager.isKeyPressed(KeyEvent.VK_W)) {
			dy = -1;
		} else if (InputManager.isKeyPressed(KeyEvent.VK_DOWN) || InputManager.isKeyPressed(KeyEvent.VK_S)) {
			dy = 1; 
		}
		playerMovement.setDirection(dx, dy);

		if (dx > 0) {
			animator.play("walk_right");
		} else if (dx < 0) {
			animator.play("walk_left");
		} else {
			animator.play("idle");
		}
	}
}