# Pacote `com.JDStudio.Engine.Graphics.Sprite.Animations`

Este pacote oferece um sistema completo para criar, gerenciar e exibir animações baseadas em sprites.

## Resumo das Classes

### `Animation.java`

Representa uma única sequência de animação.
- **Estrutura:** Contém um array de `Sprite`s (os frames) e uma velocidade (`animationSpeed`) que determina quantos ticks do jogo cada frame deve durar.
- **Lógica:** O método `tick()` é responsável por avançar para o próximo frame da animação no tempo correto, reiniciando ao chegar no final para criar um loop.

### `Animator.java`

Atua como uma **máquina de estados** para as animações de um `GameObject`. É um componente que gerencia um conjunto de objetos `Animation`.
- **Funcionalidades:**
    - **Gerenciamento:** Armazena múltiplas animações associadas a uma chave de texto (ex: "walk_down", "idle", "attack").
    - **Controle:** O método `play(key)` define qual animação está ativa. Ele é inteligente e evita reiniciar uma animação que já está em execução.
    - **Atualização:** Seu método `tick()` chama o `tick()` da animação ativa.
    - **Saída:** O método `getCurrentSprite()` retorna o frame atual da animação ativa, pronto para ser renderizado pelo `GameObject`.

## Como Usar

1. Crie múltiplos objetos `Animation` para cada estado do seu personagem (andar, pular, etc.).
2. Adicione essas animações a uma instância de `Animator` usando `addAnimation(key, animation)`.
3. No `tick()` do seu `GameObject`, chame `animator.play(newState)` quando o estado do personagem mudar, e sempre chame `animator.tick()`.
4. No `render()` do seu `GameObject`, desenhe o sprite retornado por `animator.getCurrentSprite()`.