# Pacote `com.JDStudio.Engine.Object`

Este pacote fornece a base para todas as entidades dinâmicas e interativas dentro do jogo.

## Resumo das Classes

### `GameObject.java`

É uma classe abstrata que representa qualquer "coisa" que existe no mundo do jogo e possui comportamento próprio. Exemplos incluem o jogador, inimigos, itens, projéteis, etc.

- **Atributos Principais:**
    - Posição (`x`, `y`), dimensões (`width`, `height`).
    - `Sprite` para representação visual e um `Animator` para animações.
    - `MovementComponent` para delegar a lógica de movimento.

- **Funcionalidades:**
    - **Ciclo de Vida:** Possui métodos `tick()` para lógica e `render(Graphics g)` para desenho, que são chamados pelo `GameState` ativo.
    - **Colisão:** Define uma "máscara de colisão" (`maskX`, `maskY`, etc.) que pode ser diferente das dimensões visuais. Fornece um método estático `isColliding()` para verificar a interseção entre dois objetos.
    - **Extensibilidade:** Deve ser estendida por classes concretas (como `Player` ou `Enemy`) que implementarão seus comportamentos específicos.