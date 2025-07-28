# Pacote `com.JDStudio.Engine.Input`

Fornece um sistema centralizado para gerenciar todo o input do teclado.

## Resumo das Classes

### `InputManager.java`

É um gerenciador de teclado que utiliza o padrão **Singleton** para garantir que exista apenas uma instância global.

- **Design:**
    - A instância única é acessada estaticamente através de `InputManager.instance`.
    - Deve ser adicionado como `KeyListener` ao `Canvas` principal da engine.

- **Funcionalidades Chave:**
    - `isKeyPressed(keyCode)`: Retorna `true` enquanto a tecla estiver sendo pressionada. Ideal para ações contínuas, como movimento.
    - `isKeyJustPressed(keyCode)`: Retorna `true` apenas no exato frame em que a tecla foi pressionada. Ideal para ações de toque único, como pular, atirar ou interagir.

- **Uso Obrigatório:**
    - O método `update()` do `InputManager` **deve ser chamado** uma vez por frame (no final do método `tick()` da engine) para que a lógica de `isKeyJustPressed` funcione corretamente.