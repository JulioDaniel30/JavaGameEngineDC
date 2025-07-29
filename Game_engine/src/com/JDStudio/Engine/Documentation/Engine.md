# Pacote: com.JdStudio.Engine

Este pacote contém a classe principal que inicializa e executa o engine.

## Classe `Engine`

É a classe central do projeto. Ela cria a janela do jogo, gerencia o game loop (tick e render) e mantém referências estáticas importantes, como a `camera`.

### Visão Geral

-   **Canvas & Runnable**: Herda de `Canvas` para ser o componente de desenho e implementa `Runnable` para ter seu próprio `Thread`.
-   **Game Loop**: Possui um game loop de taxa de atualização fixa (60 FPS por padrão).
-   **Gerenciador de Estado**: Chama os métodos `tick()` e `render()` do `GameState` atual.

### Exemplo de Uso

A classe `Engine` é o ponto de entrada da sua aplicação.

```java
// Em algum lugar no seu código de inicialização do jogo
public class Main {
    public static void main(String[] args) {
        // Cria uma instância do engine
        Engine engine = new Engine();

        // Inicializa os seus GameStates (menu, fase 1, etc)
        GameState menuState = new MyMenuState(); // Supondo que você criou essa classe
        
        // Define o estado inicial do jogo
        Engine.setGameState(menuState);

        // Inicia o game loop em uma nova thread
        engine.start();
    }
}
```