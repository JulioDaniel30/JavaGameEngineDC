package com.game.gameObjects;

import org.json.JSONObject;

import com.JDStudio.Engine.Events.EventListener;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.ProjectileManager;
import com.JDStudio.Engine.Utils.PropertiesReader;
import com.game.States.PlayingState;
import com.game.manegers.GameEvent;

public class Weapon extends GameObject {
	private String spriteName = "";
	private Animator animator;
	
    // O listener agora é uma variável de instância, criado apenas uma vez.
	private EventListener playerFireEventListener;

	public Weapon(JSONObject properties) {
        super(properties);
    }
	
	@Override
	public void initialize(JSONObject properties) {
		super.initialize(properties);
		PropertiesReader reader = new PropertiesReader(properties);
        this.spriteName = reader.getString("SpriteName", "weapon_holder");
        this.sprite = PlayingState.assets.getSprite(spriteName);
        
        animator = new Animator();
        addComponent(animator);
        setupAnimations(properties);
		
        // --- A CORREÇÃO PRINCIPAL ESTÁ AQUI ---
        // A lógica de inscrição foi movida para o initialize().
        this.playerFireEventListener = (data) -> {
            // Só atira se a arma estiver anexada a quem disparou o evento
            if (this.parent != null && this.parent == data) {
            	
                shoot();
            }
        };

        EventManager.getInstance().subscribe(GameEvent.PLAYER_FIRE, this.playerFireEventListener);
	}
	
	private void setupAnimations(JSONObject properties) {
		
		Animation rightAnim = new Animation(10, PlayingState.assets.getSprite("pistol_right"));
		Animation leftAnim = new Animation(10, PlayingState.assets.getSprite("pistol_left"));
		Animation idleAnim = new Animation(10, PlayingState.assets.getSprite("pistol_right"));
		Animation holderAnimation = new Animation(10, PlayingState.assets.getSprite(spriteName));
		
		animator.addAnimation("idle", idleAnim);
		animator.addAnimation("right", rightAnim);
		animator.addAnimation("left", leftAnim);
		animator.addAnimation("holder", holderAnimation);
		
	}

    // O método shoot agora contém a lógica de criação do projétil
    private void shoot() {
        if (parent == null || !(parent instanceof Player)) return;
        Player player = (Player) parent;

        double dirX = player.lastDx;
        double dirY = 0;//player.lastDy;
        
        double startX = this.x + (this.width / 2.0);
        double startY = this.y + (this.height / 2.0) - 8;

        // Lógica simples para ajustar a posição da bala
        if (dirX > 0) startX += 8;
        if (dirX < 0) startX -= 8;
        
        ProjectileManager.getInstance().spawnProjectile(
            parent,
            startX, startY,
            dirX, dirY,
            4.0,
            10, // Dano como int
            120, // Tempo de vida aumentado
            PlayingState.assets.getSprite("bullet")
        );
        if(player.ammo >0) player.ammo--;
    }

	@Override
	public void tick() {
        super.tick(); // Atualiza os componentes (animator)

		if(parent == null) {
			animator.play("holder");
			return;
		}
        // Garante que a arma só reaja se o dono for o jogador
		if(!(parent instanceof Player)) return;
		
		Player player = (Player)parent;
		Animator playerAnimator = player.getComponent(Animator.class);
        if (playerAnimator == null) return;
		
		String lastAnimKey = playerAnimator.getCurrentAnimationKey();
		
		if (lastAnimKey != null && lastAnimKey.contains("right")) {
            animator.play("right");
        } else if (lastAnimKey != null && lastAnimKey.contains("left")) {
        	animator.play("left");
        } else {
        	animator.play("idle");
        }
	}
    
    
}


