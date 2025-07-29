# Pacote: com.JdStudio.Engine.Graphics.UI

Componentes para construir interfaces de usuário.

## Classe Abstrata `UIElement`

A base para todos os elementos de UI. Define posição e visibilidade.

## Classe `UIManager`

Um contêiner que gerencia e renderiza uma lista de `UIElement`.

## Classe `UIText`

Um `UIElement` que desenha texto na tela. Pode ser estático ou dinâmico (atualizado a cada frame via `Supplier`).

### Exemplo de Uso (`UIText`)

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

---

## Classe `DialogueBox`

Um `UIElement` especializado e pré-construído para renderizar conversas do sistema de `Dialogue`. Ele se conecta automaticamente ao `DialogueManager` para exibir o nó de diálogo atual, as escolhas do jogador e o nome do personagem que está falando.

### Visão Geral

-   **Automática**: Lê o estado diretamente do `DialogueManager.getInstance()`.
-   **Customizável**: Permite alterar fontes, cores, espaçamento e velocidade do efeito de "máquina de escrever".
-   **Controlada por `tick()`**: O método `tick()` da `DialogueBox` deve ser chamado no `tick()` do seu `GameState` para processar o input do jogador (navegar entre as escolhas, confirmar) e o efeito de texto.

### Exemplo de Uso (`DialogueBox`)

A `DialogueBox` não precisa ser adicionada a um `UIManager`, mas pode ser se você preferir. O mais importante é chamar seus métodos `tick()` e `render()` no seu `GameState`.

```java
public class MyGameState extends EnginePlayingState {
    
    private DialogueBox dialogueBox;

    public MyGameState() {
        super();
        // ... (criação de outros objetos)

        // Instancia a caixa de diálogo com posição (x, y) e dimensões (largura, altura)
        dialogueBox = new DialogueBox(10, Engine.HEIGHT - 70, Engine.WIDTH - 20, 60);

        // Customizações opcionais
        dialogueBox.setColors(new Color(20, 20, 80, 230), Color.WHITE, Color.YELLOW, Color.CYAN);
        dialogueBox.setTypewriterSpeed(1); // 1 = mais rápido, valores maiores = mais lento
        dialogueBox.setSelectionCursor("-> ");
    }
    
    @Override
    public void tick() {
        super.tick(); // Atualiza os GameObjects
        
        // Se a caixa de diálogo não estiver visível (ou seja, se o diálogo não estiver ativo),
        // seu tick() interno não fará nada. É seguro chamar sempre.
        dialogueBox.tick();

        InputManager.instance.update();
    }

    @Override
    public void render(Graphics g) {
        super.render(g); // Renderiza GameObjects
        
        // O método render() da DialogueBox só desenha algo se o diálogo estiver ativo.
        dialogueBox.render(g);
    }
}
```