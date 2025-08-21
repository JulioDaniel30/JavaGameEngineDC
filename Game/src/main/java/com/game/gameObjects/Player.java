// game
package com.game.gameObjects;

import java.awt.event.KeyEvent;
import java.util.Map;

import org.json.JSONObject; // Importação necessária

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.InventoryComponent;
import com.jdstudio.engine.Components.ShadowComponent;
import com.jdstudio.engine.Components.Moviments.MovementComponent;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animation;
import com.jdstudio.engine.Graphics.Sprite.Animations.AnimationLoader;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Sound.Sound;
import com.jdstudio.engine.Sound.Sound.SoundChannel;
import com.jdstudio.engine.Utils.PropertiesReader; // Importa o nosso leitor
import com.game.manegers.GameEvent;

public class Player extends Character implements ISavable {
	
	//private World world;
	private int shootCooldown = 0;
	public float energy;
    public float maxEnergy = 100;
    public int ammo = 10;
    public int maxAmmo = 10;
    public double lastDx = 1;
	public double lastDy = 0;
	
	//MovementComponent movement;
	//Animator animator;

		public Player(JSONObject properties) {
			super(properties);
		}

		@Override
		public void initialize(JSONObject properties) {
			super.initialize(properties);
			this.isProtectedFromCleanup = true;
			PropertiesReader reader = new PropertiesReader(properties);
			double speed = reader.getDouble("speed", 1.4);
			
			//movement = new MovementComponent(speed);
			//animator = new Animator();
			System.out.println("player");
			//this.addComponent(animator);
	        //this.addComponent(movement);
			this.addComponent(new Animator());
			this.addComponent(new MovementComponent(speed));
			
			// A máscara de colisão também poderia ser lida das propriedades!
			setMaskWidth(9);
			setMaskHeight(15);
			setMaskY(1);
			setMaskX(3);
			
			this.maxLife = 120;
			this.life = maxLife;
			
			this.energy = maxEnergy;
			// Chama o método para configurar as animações, passando as propriedades
			setupAnimations(properties);
			this.addComponent(new InventoryComponent(18));
			this.addComponent(new ShadowComponent(12, 6, 0.7f, 0));
			
		}

		/**
		 * O método setupAnimations agora lê os nomes dos sprites a partir das propriedades.
		 */
		private void setupAnimations(JSONObject properties) {
			
			Animator animator = getComponent(Animator.class);
			if (animator == null) return;	
			Spritesheet playerSheet = new Spritesheet("/player_sheet.png"); // Use o caminho correto

	        // Carrega TODAS as animações de uma vez a partir do JSON!
	        // O 'true' no final diz para criar as versões "_left" automaticamente.
	        Map<String, Animation> playerAnims = AnimationLoader.loadFromAsepriteJson(
	            "/player_sheet.json", playerSheet, false);

	        // Adiciona as animações carregadas ao Animator do jogador
	        for (Map.Entry<String, Animation> entry : playerAnims.entrySet()) {
	            animator.addAnimation(entry.getKey(), entry.getValue());
	        }
	        animator.play("idle_right");

	        
		}

	@Override
	public void onCollision(GameObject other) {
		super.onCollision(other);
		if (other instanceof Lifepack) {
			this.heal(25); // Usa o método heal() herdado de Character
			Sound.play("/hurt.wav",SoundChannel.SFX, 0.5f);
			other.isDestroyed = true;
			// Passamos a quantidade de vida curada como dado do evento.
	        EventManager.getInstance().trigger(GameEvent.PLAYER_HEALED, 25.0);
		}
		if (other instanceof BulletPack) {
			if (ammo >= maxAmmo) {}else {
				this.ammo +=5;
				if (ammo > maxAmmo) ammo = maxAmmo;
				other.isDestroyed = true;
				// Passamos a quantidade de vida curada como dado do evento.
		        EventManager.getInstance().trigger(GameEvent.PLAYER_ADD_AMMO, 5);
			}
		}
		
		
		
	}
	
	public void takeDamage(double amount) {
	    if (this.life <= 0) return;

	    this.life -= amount;
	    
	    // --- DISPARE O EVENTO AQUI ---
	    EventManager.getInstance().trigger(GameEvent.PLAYER_TOOK_DAMAGE, amount);
	    Engine.camera.shake(4, 10);
	    System.out.println(life);
	    if (this.life <= 0) {
	        this.life = 0;
	        die();
	        EventManager.getInstance().trigger(GameEvent.PLAYER_DIED, true);
	    }
	}


	
	@Override
	public void tick() {
		// A checagem de isDead e a atualização do animator são feitas no super.tick()
		super.tick(); 
		if (isDead) return;
		/*if(getComponent(MovementComponent.class).getWorld() == null) {
			getComponent(MovementComponent.class).setWorld(PlayingState.world);
		}*/
		

		handleMovementInput();
		handleShootingInput();
		if (energy < maxEnergy) {
            energy += 0.1f;
        }
		
		if(this.getChildByName("pistol") !=null) {
			if (lastDx > 0) {
				this.getChildByName("pistol").setLocalPosition( 10, 0);
			} else if (lastDx < 0) {
				this.getChildByName("pistol").setLocalPosition( -10, 0);
			} else {
				this.getChildByName("pistol").setLocalPosition( 10, 0);
			}
		}
	}
	
	private void handleShootingInput() {
	    if (InputManager.isActionJustPressed("SHOOT") && shootCooldown <= 0) {
	        shootCooldown = 0; // Cooldown de 30 frames (meio segundo a 60 FPS)
	        if(ammo > 0) EventManager.getInstance().trigger(GameEvent.PLAYER_FIRE, (GameObject)this);
	        //Sound.play("/shoot.wav", 0.5f); // Você precisará de um som "shoot.wav"
	    }
	    if(InputManager.isKeyJustPressed(KeyEvent.VK_K) && this.getChildByName("pistol") !=null ) {
	    	this.getChildByName("pistol").detach();
	    }
	}
	
	private void handleMovementInput() {
		MovementComponent playerMovement = getComponent(MovementComponent.class);
		double dx = 0;
		double dy = 0;
		
		if(InputManager.isKeyJustPressed(KeyEvent.VK_I)) {
			System.out.println(playerMovement.getWorld());
		}

		if (InputManager.isActionPressed("MOVE_LEFT")) {
			dx = -1;
			this.lastDx = dx;
		} else if (InputManager.isActionPressed("MOVE_RIGHT")) {
			dx = 1;
			this.lastDx = dx;
		}
		if (InputManager.isActionPressed("MOVE_UP")) {
			dy = -1;
			this.lastDy = dy;
		} else if (InputManager.isActionPressed("MOVE_DOWN")) {
			dy = 1;
			this.lastDy = dy;
		}
		playerMovement.setDirection(dx, dy);
		Animator animator = getComponent(Animator.class);
		if (animator == null) return;
		if (dx > 0) {
			animator.play("walk_right");
		} else if (dx < 0) {
			animator.play("walk_left");
		} else {
			
			if(lastDx > 0) animator.play("idle_right");
			else if (lastDx < 0) animator.play("idle_left");
			else animator.play("idle_right");
			
		}
		
		
	}
	

	@Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name); // Salva o nome para identificar o objeto
        state.put("x", this.x);
        state.put("y", this.y);
        state.put("life", this.life);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.x = state.getDouble("x");
        this.y = state.getDouble("y");
        this.life = state.getDouble("life");
    }
}