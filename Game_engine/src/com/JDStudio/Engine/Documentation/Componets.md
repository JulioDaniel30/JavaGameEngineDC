# Pacote `com.JDStudio.Engine.Components`

Este pacote foi projetado para seguir a abordagem de **Arquitetura Baseada em Componentes**, onde comportamentos específicos e reutilizáveis são encapsulados em classes de componentes que podem ser "anexadas" a um `GameObject`.

## Resumo das Classes

### `MovementComponent.java`

Um componente que gerencia de forma robusta todo o movimento e a colisão de um `GameObject` com o mundo (cenário de tiles).

- **Design:**
    - **Desacoplamento:** Remove a complexa lógica de movimento e colisão de dentro da classe `GameObject`, tornando o código mais limpo e reutilizável.
    - **Referência:** Mantém uma referência ao seu `GameObject` "dono" (`owner`) para poder alterar sua posição.

- **Funcionalidades Chave:**
    - **Movimento Diagonal Normalizado:** Garante que a entidade não se mova mais rápido na diagonal, normalizando o vetor de movimento.
    - **Precisão de Sub-Pixel:** Utiliza acumuladores (`xRemainder`, `yRemainder`) para lidar com velocidades fracionadas (ex: `speed = 1.5`). Isso garante um movimento suave e preciso sem perder "pedaços" de movimento entre os frames.
    - **Colisão Pixel-por-Pixel:** Em vez de mover o objeto inteiro de uma vez, ele o move um pixel de cada vez no eixo X e depois no eixo Y, verificando por colisões a cada passo. Isso impede que o objeto "entre" em paredes ou fique preso.