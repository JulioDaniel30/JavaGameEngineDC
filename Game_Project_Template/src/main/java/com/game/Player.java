package com.game;
//package com.meujogo;

import java.util.Map;

import org.json.JSONObject;
import com.jdstudio.engine.Components.Moviments.MovementComponent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animation;
import com.jdstudio.engine.Graphics.Sprite.Animations.AnimationLoader;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Utils.PropertiesReader;

public class Player extends Character {

	public Player(JSONObject properties) {
		super(properties);
	}

	@Override
	public void initialize(JSONObject properties) {
		super.initialize(properties);
		PropertiesReader reader = new PropertiesReader(properties);
		//pega o nome diretamente das propriedates/atributos do Tiled
		this.name = reader.getString("name", "Player");
		
		this.addComponent(new Animator());
		this.addComponent(new MovementComponent(1.5));

		// Configurações (podem ser movidas para o Tiled depois)
		setCollisionMask(2, 2, 12, 14);
		this.maxLife = 100;
		this.life = this.maxLife;
		//setupAnimations(properties);
	}
	
	/**
	 * O método setupAnimations agora lê os nomes dos sprites a partir das propriedades.
	 */
	@SuppressWarnings("unused")
	private void setupAnimations(JSONObject properties) {
		
		Animator animator = getComponent(Animator.class);
		if (animator == null) return;	
		Spritesheet playerSheet = new Spritesheet("/player_sheet.png"); // Use o caminho correto

        // Carrega TODAS as animações de uma vez a partir do JSON!
        // O 'true' no final diz para criar as versões "_left" automaticamente.
        Map<String, Animation> playerAnims = AnimationLoader.loadFromAsepriteJson(
            "/player_sheet.json", playerSheet, true);

        // Adiciona as animações carregadas ao Animator do jogador
        for (Map.Entry<String, Animation> entry : playerAnims.entrySet()) {
            animator.addAnimation(entry.getKey(), entry.getValue());
        }
        animator.play("idle_right");

        
	}

	@Override
	public void tick() {
		super.tick(); // Atualiza todos os componentes
		handleMovementInput();
	}

	@Override
	public void takeDamage(double amount) {
		// TODO Auto-generated method stub
		super.takeDamage(amount);
		EventManager.getInstance().trigger(GameEvent.PLAYER_TAKE_DAMAGE, amount);
	}

	private void handleMovementInput() {
		MovementComponent playerMovement = getComponent(MovementComponent.class);
		if (playerMovement == null)
			return;

		double dx = 0, dy = 0;
		if (InputManager.isActionPressed("MOVE_LEFT")) {
			dx = -1;
		} else if (InputManager.isActionPressed("MOVE_RIGHT")) {
			dx = 1;
		}
		if (InputManager.isActionPressed("MOVE_UP")) {
			dy = -1;
		} else if (InputManager.isActionPressed("MOVE_DOWN")) {
			dy = 1;
		}

		playerMovement.setDirection(dx, dy);
	}
}