# Pacote: com.JdStudio.Engine.Input

Fornece gerenciamento de entrada centralizado.

## Classe `InputManager`

É um Singleton que atua como `KeyListener` para o engine, rastreando o estado de todas as teclas.

### Visão Geral

-   **Singleton**: Acesso global através de `InputManager.instance`.
-   **Dois Níveis de Detecção**:
    -   `isKeyPressed(keyCode)`: Verifica se uma tecla está sendo segurada.
    -   `isKeyJustPressed(keyCode)`: Verifica se a tecla foi pressionada *neste exato frame*.
-   **Método `update()`**: Essencial para o funcionamento do `isKeyJustPressed`. Deve ser chamado no final do `tick()` do seu `GameState`.

### Exemplo de Uso

Primeiro, adicione o listener ao `Engine`. Depois, use os métodos estáticos para verificar as teclas.

```java
// No construtor da sua classe Engine
public Engine() {
    // ...
    addKeyListener(InputManager.instance); // Registro do listener
    // ...
}

// Em um método tick() de um GameObject (ex: Player)
@Override
public void tick() {
    // Para movimento contínuo
    if (InputManager.isKeyPressed(KeyEvent.VK_D)) {
        // mover para a direita
        this.x += speed;
    }

    // Para ações de toque único (pulo, tiro)
    if (InputManager.isKeyJustPressed(KeyEvent.VK_SPACE)) {
        // pular ou atirar
        jump();
    }
}

// No tick() do seu GameState
@Override
public void tick() {
    // ... atualiza todos os objetos ...

    // ESSENCIAL: Chamar no final do tick do estado
    InputManager.instance.update();
}
```