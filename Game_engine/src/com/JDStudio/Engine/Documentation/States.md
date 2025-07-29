# Pacote: com.JdStudio.Engine.States

Define a arquitetura de máquina de estados para gerenciar diferentes telas e lógicas do jogo.

## Classe Abstrata `GameState`

A base para qualquer estado. Define o contrato (`tick`, `render`) que o `Engine` espera.

## Classe Abstrata `EnginePlayingState`

Uma especialização de `GameState` para fases onde o jogo acontece.
-   Gerencia automaticamente uma lista de `GameObject`.
-   Atualiza e renderiza todos os objetos da lista.
-   Remove objetos marcados como `isDestroyed`.

### Exemplo de Uso (`EnginePlayingState`)

Crie uma classe que herda de `EnginePlayingState` para representar uma fase do seu jogo.

```java
public class Level1State extends EnginePlayingState {

    private Player player;

    public Level1State() {
        super(); // Chama o construtor que cria a lista de gameObjects

        // 1. Carregue seus assets
        assets = new AssetManager();
        assets.loadSprite("player_sprite", "/sprites/player.png");

        // 2. Crie e adicione seus objetos
        player = new Player(100, 100); // Supondo que Player herda de Character/GameObject
        addGameObject(player);

        Enemy enemy = new Enemy(200, 100);
        addGameObject(enemy);
    }
    
    @Override
    public void tick() {
        super.tick(); // ESSENCIAL: Chama a lógica de atualização e remoção de objetos da classe pai
        // Lógica específica da fase aqui, se necessário...
    }

    @Override
    public void render(Graphics g) {
        // Opcional: desenhar um fundo
        // g.drawImage(background, 0, 0, null);
        
        super.render(g); // ESSENCIAL: Renderiza todos os GameObjects da lista
    }
}
```

## Classe Abstrata `EngineMenuState`

Especialização para menus e telas de UI.
-   Gerencia automaticamente um `UIManager`.

### Exemplo de Uso (`EngineMenuState`)

```java
public class MainMenuState extends EngineMenuState {

    public MainMenuState() {
        super(); // Cria o uiManager

        // Adiciona um título ao menu
        UIText title = new UIText(50, 50, new Font("Arial", Font.BOLD, 24), Color.WHITE, "Meu Jogo");
        uiManager.addElement(title);

        // Adiciona um texto de "Pressione Enter"
        UIText prompt = new UIText(60, 100, new Font("Arial", Font.PLAIN, 16), Color.YELLOW, "Pressione Enter para começar");
        uiManager.addElement(prompt);
    }
    
    @Override
    public void tick() {
        super.tick(); // Pode ser deixado vazio ou chamar super
        
        if (InputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) {
            // Muda para o estado de jogo
            Engine.setGameState(new Level1State());
        }

        InputManager.instance.update();
    }
    
    // O método render() já é implementado por EngineMenuState para desenhar o uiManager
}
```