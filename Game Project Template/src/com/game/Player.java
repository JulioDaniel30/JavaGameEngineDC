package com.game;
//package com.meujogo;

import org.json.JSONObject;
import com.JDStudio.Engine.Components.Moviments.MovementComponent;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.Character;

public class Player extends Character {

	public Player(JSONObject properties) {
		super(properties);
	}

	@Override
	public void initialize(JSONObject properties) {
		super.initialize(properties);

		this.name = "Player";
		this.addComponent(new Animator());
		this.addComponent(new MovementComponent(1.5));

		// Configurações (podem ser movidas para o Tiled depois)
		setCollisionMask(2, 2, 12, 14);
		this.maxLife = 100;
		this.life = this.maxLife;
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