# Guia Avançado: Componentes de Movimento

Este guia detalha a arquitetura e o uso dos componentes de movimento da JDStudio Engine. Estes componentes foram projetados para desacoplar a complexa lógica de movimento e colisão dos `GameObject`s, tornando o seu código mais limpo, reutilizável e poderoso.

## A Base: `BaseMovementComponent`

Todos os componentes de movimento top-down herdam de `BaseMovementComponent`. Esta classe base contém a lógica fundamental que é partilhada entre o jogador e a IA:

* **Precisão de Sub-Pixel**: Utiliza acumuladores (`xRemainder`, `yRemainder`) para lidar com velocidades fracionadas (ex: `speed = 1.5`). Isto garante um movimento suave e preciso, sem perder "pedaços" de movimento entre os frames.
* **Lógica de Colisão (`isPathClear`)**: Antes de mover, o componente verifica se o próximo pixel no caminho está livre. Esta verificação é dupla:
    1.  Verifica contra tiles do tipo `SOLID` no `World`.
    2.  Verifica contra outros `GameObject`s que tenham um `CollisionType` do tipo `SOLID` ou `CHARACTER_SOLID`.
* **Auto-configuração via Eventos**: O componente inscreve-se automaticamente no evento `WORLD_LOADED` para obter as referências do `World` e da lista de `GameObject`s, simplificando a inicialização.

---

## 1. Movimento Top-Down para o Jogador (`MovementComponent`)

Este componente foi projetado para o controlo direto de um `GameObject` através do input do jogador.

**Como Funciona**: Ele não move o objeto diretamente. Em vez disso, você define a **direção desejada** (`dx`, `dy`) a cada frame, e o componente calcula o movimento, aplica a velocidade, normaliza as diagonais e trata todas as colisões.

### Exemplo Prático: Criando um `Player`

```java
// Na sua classe Player.java
import com.JDStudio.Engine.Components.Moviments.MovementComponent;
import com.JDStudio.Engine.Input.InputManager;

public class Player extends Character {

    public Player(JSONObject properties) {
        super(properties);
        // ...
        // Adiciona o componente de movimento com uma velocidade de 2.5
        this.addComponent(new MovementComponent(2.5));
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza todos os componentes
        handleMovementInput();
    }

    private void handleMovementInput() {
        // Pega a referência para o componente
        MovementComponent movement = getComponent(MovementComponent.class);
        if (movement == null) return;

        // Determina a direção com base no input do jogador
        double dx = 0, dy = 0;
        if (InputManager.isActionPressed("MOVE_LEFT"))  dx = -1;
        if (InputManager.isActionPressed("MOVE_RIGHT")) dx = 1;
        if (InputManager.isActionPressed("MOVE_UP"))    dy = -1;
        if (InputManager.isActionPressed("MOVE_DOWN"))  dy = 1;

        // Define a direção no componente. O resto é tratado automaticamente.
        movement.setDirection(dx, dy);
    }
}
```

---

## 2. Inteligência Artificial (`AIMovementComponent`)

Este componente dá vida a NPCs e inimigos, permitindo-lhes navegar pelo mundo para alcançar um alvo.

**Como Funciona**: Ele calcula continuamente a direção em direção a um `GameObject` ou a um ponto (`Point`) alvo e aplica o movimento, usando a mesma lógica de colisão do `BaseMovementComponent`.

### Funcionalidades Avançadas:

  * **`setTarget(GameObject target)`**: Define um `GameObject` que a IA deve seguir.
  * **`setTarget(int x, int y)`**: Define um ponto estático para onde a IA deve se mover.
  * **`targetAnchor`**: Um `enum` que permite definir qual parte do alvo a IA deve mirar (`CENTER`, `BOTTOM_CENTER`, `TOP_LEFT`). Útil para fazer com que inimigos mirem nos "pés" do jogador em vez do seu centro exato.
  * **`useAStarPathfinding`**: Se `true`, o componente usará o `Pathfinder` para calcular rotas complexas que desviam de obstáculos, em vez de apenas andar em linha reta. `Nota: o A* esta  avariado, não utilize`
  * **`avoidOtherActors`**: Se `true`, a IA tentará desviar de outros `Character`s que não sejam o seu alvo, evitando aglomerações.

### Exemplo Prático: Criando um Inimigo que Persegue o Jogador

```java
// Na sua classe Enemy.java
import com.JDStudio.Engine.Components.Moviments.AIMovementComponent;

public class Enemy extends Character {

    public Enemy(JSONObject properties, Player playerTarget) {
        super(properties);
        // ...
        
        // 1. Cria e adiciona o componente de IA
        AIMovementComponent aiMovement = new AIMovementComponent(1.2); // Velocidade 1.2
        this.addComponent(aiMovement);

        // 2. Configura o comportamento da IA
        aiMovement.setTarget(playerTarget); // Segue o jogador
        aiMovement.targetAnchor = AIMovementComponent.TargetAnchor.BOTTOM_CENTER; // Mira nos pés do jogador
        aiMovement.useAStarPathfinding = true; // Desvia de paredes
    }

    @Override
    public void tick() {
        super.tick(); // O tick() do GameObject já atualiza todos os componentes.
                      // O inimigo irá se mover automaticamente.
    }
}
```

---

## 3. Física de Plataforma (`PhysicsComponent`)

Este componente é **independente** dos outros e foi projetado para jogos de plataforma (side-scrollers). Ele simula gravidade, aceleração e forças, como o pulo.

**Como Funciona**: Em vez de controlar a direção, você manipula as **velocidades** (`velocityX`, `velocityY`) e aplica **forças**. O componente então aplica a gravidade e move o `GameObject`, resolvendo as colisões com o cenário de forma a simular um comportamento de plataforma.

### Funcionalidades Chave:

  * **`gravity`**: A força que puxa o objeto para baixo a cada frame. Pode ser ajustada.
  * **`onGround`**: Uma flag booleana que se torna `true` quando o componente deteta que o objeto está a tocar numa superfície sólida abaixo dele. Essencial para controlar o pulo.
  * **`addVerticalForce(double force)`**: Aplica um impulso vertical instantâneo. Use um valor negativo para pular (ex: `-12.0`).

### Exemplo Prático: Criando um Personagem de Plataforma

```java
// Numa nova classe PlatformerPlayer.java
import com.JDStudio.Engine.Components.PhysicsComponent;

public class PlatformerPlayer extends Character {

    private double moveSpeed = 2.5;
    private double jumpForce = -10.0;

    public PlatformerPlayer(JSONObject properties) {
        super(properties);
        // Adiciona o componente de física
        this.addComponent(new PhysicsComponent());
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza o componente de física

        PhysicsComponent physics = getComponent(PhysicsComponent.class);
        if (physics == null) return;

        // Controlo do movimento horizontal
        if (InputManager.isActionPressed("MOVE_LEFT")) {
            physics.velocityX = -moveSpeed;
        } else if (InputManager.isActionPressed("MOVE_RIGHT")) {
            physics.velocityX = moveSpeed;
        } else {
            physics.velocityX = 0; // Para o movimento se nenhuma tecla for pressionada
        }

        // Controlo do pulo
        if (InputManager.isActionJustPressed("JUMP") && physics.onGround) {
            physics.addVerticalForce(jumpForce);
        }
    }
}
```

---
[⬅️ Voltar para o Guias Avançados](./README.md)