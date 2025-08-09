# Guia: Como Criar um Personagem de Plataforma

Este guia explica como usar o novo `PhysicsComponent` para criar um personagem com movimento de plataforma (gravidade e pulo), sem afetar os `GameObjects` do seu jogo top-down existente.

## O Conceito

A nossa engine agora suporta dois tipos de movimento:

1.  **`MovementComponent`**: Para movimento top-down, controlado diretamente pela direção (cima, baixo, esquerda, direita).
2.  **`PhysicsComponent`**: Para movimento de plataforma, controlado por forças (velocidade, aceleração, gravidade).

Um `GameObject` pode ter **um ou outro**, mas não ambos. O seu `Player` e `Enemy` atuais continuarão a usar o `MovementComponent`. Para o nosso teste de plataforma, criaremos um novo personagem.

---

### Passo 1: Criar a Classe `PlatformerCharacter`

Crie uma nova classe no seu projeto do **jogo**. Esta será a nossa cobaia para testar a física.

```java
// game
package com.game;

import org.json.JSONObject;
import com.JDStudio.Engine.Components.PhysicsComponent;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Object.Character;

public class PlatformerCharacter extends Character {

    public PlatformerCharacter(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        
        // Adiciona um Animator (para o visual) e o novo PhysicsComponent.
        this.addComponent(new Animator());
        this.addComponent(new PhysicsComponent());
        
        // Define uma máscara de colisão
        setCollisionMask(2, 0, 12, 16);
        
        // Configura as animações
        Animator animator = getComponent(Animator.class);
        animator.addAnimation("idle", new com.JDStudio.Engine.Graphics.Sprite.Animations.Animation(10, PlayingState.assets.getSprite("player_idle"))); // Reutilize um sprite
        animator.play("idle");
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza todos os componentes, incluindo a física

        // Pega o componente de física para controlá-lo
        PhysicsComponent physics = getComponent(PhysicsComponent.class);
        if (physics == null) return;

        // --- LÓGICA DE CONTROLO DE PLATAFORMA ---

        // Movimento Horizontal
        if (InputManager.isActionPressed("MOVE_LEFT")) {
            physics.velocityX = -2; // Define a velocidade horizontal diretamente
        } else if (InputManager.isActionPressed("MOVE_RIGHT")) {
            physics.velocityX = 2;
        } else {
            physics.velocityX = 0; // Para quando nenhuma tecla é pressionada
        }

        // Pulo
        if (InputManager.isActionJustPressed("JUMP_ACTION") && physics.onGround) {
            physics.addVerticalForce(-10); // Aplica uma força para cima (Y negativo)
            // (Você precisará de adicionar a ação "JUMP_ACTION" ao seu Main.java, ex: para a Barra de Espaço)
        }
    }
}