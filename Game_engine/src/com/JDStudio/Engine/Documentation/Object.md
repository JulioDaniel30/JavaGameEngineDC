# Pacote: com.JdStudio.Engine.Object

Contém as classes fundamentais para representar qualquer entidade dentro do jogo.

## Classe Abstrata `GameObject`

A classe base para todos os objetos no mundo do jogo (jogadores, inimigos, itens, projéteis, etc.).

### Visão Geral

-   **Atributos Fundamentais**: Posição (`x`, `y`), dimensões (`width`, `height`), `sprite`.
-   **Máscara de Colisão**: Permite definir uma hitbox diferente das dimensões visuais.
-   **Componente de Movimento**: Delega a lógica de movimento para um `BaseMovementComponent`.
-   **Animator**: Possui um `Animator` para gerenciar animações.
-   **Ciclo de Vida**: Pode ser marcado como `isDestroyed` para ser removido do jogo pelo `EnginePlayingState`.

## Classe Abstrata `Character`

Herda de `GameObject` e adiciona atributos de "personagem", como vida.

### Visão Geral

-   **Atributos**: `life`, `maxLife`, `isDead`.
-   **Métodos**: `takeDamage(amount)`, `heal(amount)`, e `die()`.

## Interface `Interactable`

Define um contrato para objetos com os quais se pode interagir.

### Exemplo de Uso

Crie uma classe `Player` que herda de `Character`.

```java
public class Player extends Character {

    public Player(double x, double y) {
        super(x, y, 16, 16); // Posição e tamanho
        this.life = 100;
        this.maxLife = 100;

        // Anexando um componente de movimento
        this.movement = new MovementComponent(this, 2.0); // Velocidade 2.0
    }

    @Override
    public void tick() {
        // Lógica de input
        double dx = 0, dy = 0;
        if (InputManager.isKeyPressed(KeyEvent.VK_W)) dy = -1;
        if (InputManager.isKeyPressed(KeyEvent.VK_S)) dy = 1;
        if (InputManager.isKeyPressed(KeyEvent.VK_A)) dx = -1;
        if (InputManager.isKeyPressed(KeyEvent.VK_D)) dx = 1;

        // Passa a direção para o componente de movimento
        ((MovementComponent)this.movement).setDirection(dx, dy);

        // Atualiza a lógica de movimento e animação
        super.tick(); // Chama o tick do GameObject, que chama o tick do movement e do animator
    }
    
    @Override
    protected void die() {
        super.die(); // Marca isDestroyed = true
        System.out.println("O jogador morreu!");
        // Poderia tocar um som de morte ou mudar para uma tela de Game Over aqui.
    }
}
```