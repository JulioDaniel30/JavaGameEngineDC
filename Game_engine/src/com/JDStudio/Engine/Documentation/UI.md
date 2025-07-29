# Pacote: com.JdStudio.Engine.Graphics.UI

Componentes para construir interfaces de usuário.

## Classe Abstrata `UIElement`

A base para todos os elementos de UI. Define posição e visibilidade.

## Classe `UIManager`

Um contêiner que gerencia e renderiza uma lista de `UIElement`.

## Classe `UIText`

Um `UIElement` que desenha texto na tela. Pode ser estático ou dinâmico (atualizado a cada frame via `Supplier`).

### Exemplo de Uso

O `EngineMenuState` já possui um `UIManager`. Para outros estados, você pode criar o seu.

```java
// Em um estado de jogo (ex: EnginePlayingState)
public class Level1State extends EnginePlayingState {
    
    private UIManager uiManager;
    private Player player;

    public Level1State() {
        super();
        this.uiManager = new UIManager();
        this.player = new Player(100, 100);
        addGameObject(player);

        // UI Estática
        UIText label = new UIText(5, 15, new Font("Arial", Font.BOLD, 12), Color.WHITE, "VIDA:");
        uiManager.addElement(label);

        // UI Dinâmica: exibe a vida atual do jogador
        // O texto é buscado a cada frame executando a lambda expression
        UIText lifeCounter = new UIText(45, 15, new Font("Arial", Font.PLAIN, 12), Color.GREEN,
            () -> String.valueOf(player.life)
        );
        uiManager.addElement(lifeCounter);
    }

    @Override
    public void render(Graphics g) {
        super.render(g); // Renderiza os GameObjects
        uiManager.render(g); // Renderiza a UI por cima
    }
}
```