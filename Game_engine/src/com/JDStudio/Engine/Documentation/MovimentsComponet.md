# Pacote: com.JdStudio.Engine.Components.Moviments

Implementa uma arquitetura de componentes para a lógica de movimento.

## Classe Abstrata `BaseMovementComponent`

A base para todos os componentes de movimento.
-   Mantém uma referência ao `GameObject` "dono".
-   Contém a lógica de colisão fundamental (`isPathClear`), que verifica contra tiles sólidos do `World` e outros `GameObject` sólidos.

## Classe `MovementComponent`

Um componente para movimento direto, controlado por input (ex: jogador).
-   Use o método `setDirection(dx, dy)` para definir para onde o objeto deve se mover no próximo `tick`.

## Classe `AIMovementComponent`

Um componente para movimento de IA, que segue um `GameObject` alvo.
-   Use `setTarget(gameObject)` para definir o alvo.
-   Pode ser configurado para desviar de outros `Character`.

### Exemplo de Uso

A ideia é anexar um componente a um `GameObject` para dar a ele um comportamento de movimento.

```java
// --- Exemplo 1: Configurando o Jogador ---
public class Player extends Character {
    public Player(double x, double y) {
        super(x, y, 16, 16);
        
        // Anexa um componente de movimento controlado por input
        this.movement = new MovementComponent(this, 2.5); // velocidade 2.5
    }

    @Override
    public void tick() {
        // ... (lógica de input para obter dx, dy) ...
        ((MovementComponent)this.movement).setDirection(dx, dy);
        
        super.tick(); // GameObject.tick() chama movement.tick()
    }
}

// --- Exemplo 2: Configurando um Inimigo ---
public class Slime extends Character {
    public Slime(double x, double y, GameObject playerTarget) {
        super(x, y, 16, 16);

        // Anexa um componente de movimento de IA
        AIMovementComponent aiMovement = new AIMovementComponent(this, 1.0); // velocidade 1.0
        aiMovement.setTarget(playerTarget); // Define o jogador como alvo
        this.movement = aiMovement;
    }
    
    // O tick do GameObject já cuida de chamar o tick do componente.
    // O Slime automaticamente seguirá o jogador.
}
```