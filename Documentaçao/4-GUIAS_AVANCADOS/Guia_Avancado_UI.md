# Guia Avançado: Criando Interfaces de Jogo Dinâmicas (HUDs)

Este guia vai além dos menus estáticos e foca em como criar uma Interface de Utilizador (UI) de jogo, também conhecida como HUD (Heads-Up Display), que seja dinâmica e reaja em tempo real ao estado do jogo. Vamos cobrir a criação de barras de vida com corações, contadores de itens, barras de XP e muito mais, usando os elementos de UI da JDStudio Engine.

## O Padrão de UI Dinâmica: O Segredo do `Supplier`

O coração da UI dinâmica da engine é a interface funcional `Supplier` do Java. Pense num `Supplier` como um "fornecedor" de dados. Em vez de dar um valor fixo a um elemento de UI, você dá a ele uma "função" que ele pode chamar a qualquer momento para obter o valor mais recente.

A engine usa este padrão em elementos como `UIText` e `UIProgressBar`. Você não precisa de atualizar o texto ou a barra manualmente a cada frame; o próprio elemento se encarrega de "perguntar" ao seu `Supplier` qual é o novo valor.

**Exemplo Simples:** Exibir as coordenadas do jogador na tela.
```java
// Supondo que 'player' é o seu objeto GameObject do jogador.
// A expressão '() -> ...' é um Supplier.
// A cada frame, o render() do UIText irá executar esta expressão para obter o texto mais recente.
UIText playerCoords = new UIText(10, 10, font, Color.WHITE, 
    () -> "X: " + player.getX() + " | Y: " + player.getY()
);
uiManager.addElement(playerCoords);
```

Com este conceito em mente, vamos construir uma HUD completa.

---

## Catálogo de Elementos de UI com Exemplos Práticos

A seguir, exemplos de como instanciar e usar cada elemento de UI disponível na engine. Este código geralmente fica no método `buildUI()` de um estado que herda de `EngineMenuState` (como `MenuState`, `PauseState`, etc.).

### `UIText`
**Uso**: Para exibir qualquer tipo de texto, estático ou dinâmico.
```java
// Texto estático para um título
uiManager.addElement(new UIText(10, 20, new Font("Serif", Font.BOLD, 14), Color.WHITE, "Inventário"));

// Texto dinâmico para exibir a quantidade de munição do jogador
// Supondo que 'player' tem um campo 'int ammo'.
uiManager.addElement(new UIText(250, 20, font, Color.YELLOW, () -> "Munição: " + player.ammo));
```

### `UIImage`
**Uso**: Para exibir qualquer imagem estática na tela, como ícones, fundos de menu ou retratos de personagens.
```java
// Adiciona um ícone estático no canto da tela
uiManager.addElement(new UIImage(10, 30, assets.getSprite("key_icon")));
```

### `UIButton`
**Uso**: Botões clicáveis para menus e ações na HUD. A ação é definida por um Runnable.
```java
// Botão "Opções" em um menu principal que abre a tela de opções
UIButton optionsButton = new UIButton(
    Engine.WIDTH / 2 - 40, 80, // Posição (x, y)
    "Opções",                 // Texto do botão
    new Font("Arial", Font.BOLD, 12),
    () -> Engine.pushState(new OptionsState()) // Ação a ser executada ao clicar
);
uiManager.addElement(optionsButton);
```

### `UIToggleButton`
**Uso**: Um interruptor On/Off, ideal para menus de opções. A ação é um Consumer<Boolean> que recebe o novo estado do botão (true para ligado, false para desligado).
```java
// Botão para ligar/desligar a exibição de FPS
UIToggleButton showFpsToggle = new UIToggleButton(
    50, 100,
    ThemeManager.getInstance().get(UISpriteKey.TOGGLE_OFF), // Sprite para o estado 'desligado'
    ThemeManager.getInstance().get(UISpriteKey.TOGGLE_ON),  // Sprite para o estado 'ligado'
    Engine.showFPS, // Estado inicial (lê o estado atual da engine)
    (isNowOn) -> { 
        // Ação que é executada quando o botão é clicado
        Engine.showFPS = isNowOn;
    }
);
uiManager.addElement(showFpsToggle);
```

### `UISlider`
**Uso**: Uma barra deslizante, perfeita para ajustar valores contínuos como volume ou brilho. A ação é um Consumer<Float> que recebe o novo valor.
```java
// Slider para controlar o volume dos efeitos sonoros (SFX)
UISlider sfxSlider = new UISlider(
    50, 120,
    0.0f, 1.0f, // Valor mínimo e máximo
    Sound.getChannelVolume(SoundChannel.SFX), // Pega o volume atual para a posição inicial
    (newValue) -> {
        // Ação executada sempre que o valor do slider muda
        Sound.setChannelVolume(SoundChannel.SFX, newValue);
    }
);
uiManager.addElement(sfxSlider);
```

### `UIInventoryView`
**Uso**: Um elemento complexo que renderiza uma grade de inventário completa. Ele lê diretamente de um objeto Inventory. É o elemento principal de um InventoryState.
```java
// Dentro do buildUI() de uma classe InventoryState.java
InventoryComponent playerInv = PlayingState.player.getComponent(InventoryComponent.class);

if (playerInv != null) {
    UIInventoryView inventoryView = new UIInventoryView(
        20, 40, // Posição x, y da janela do inventário
        assets.getSprite("inventory_background"), // Sprite de fundo (opcional)
        ThemeManager.getInstance().get(UISpriteKey.INVENTORY_BUTTON_NORMAL_30_2), // Sprite para cada slot
        PlayingState.player, // O dono do inventário (para usar os itens)
        playerInv.inventory, // O objeto de inventário com os dados
        3, 6, 32, 2 // 3 linhas, 6 colunas, slots de 32x32, 2 pixels de espaçamento
    );
    uiManager.addElement(inventoryView);
}
```

### `DialogueBox`
**Uso**: Renderiza as conversas do DialogueManager. É um elemento "automático"; você só precisa criá-lo e chamar seus métodos tick() e render() no seu GameState.
```java
// No construtor do PlayingState
dialogueBox = new DialogueBox(10, Engine.HEIGHT - 70, Engine.WIDTH - 20, 60);
dialogueBox.setColors(new Color(20, 20, 80, 230), Color.WHITE, Color.YELLOW, Color.CYAN);

// No tick() do PlayingState
dialogueBox.tick();

// No render() do PlayingState
// (O ideal é que a DialogueBox seja um IRenderable na camada UI)
dialogueBox.render(g);
```

### `UIDynamicImage`
**Uso**: Para imagens que precisam mudar com base numa condição do jogo (vida do jogador, item equipado, etc.). Usa um `Supplier<Sprite>` para decidir qual imagem mostrar a cada frame.

**Exemplo Prático**: Um retrato do jogador que muda de expressão conforme a sua vida.
```java
// Em PlayingState.java -> setupUI()

// 1. Defina a lógica que escolhe o sprite
Supplier<Sprite> portraitSupplier = () -> {
    float healthPercent = player.getComponent(HealthComponent.class).getHealthPercentage();
    if (healthPercent > 0.5f) {
        return assets.getSprite("portrait_normal");
    } else if (healthPercent > 0.0f) {
        return assets.getSprite("portrait_hurt");
    } else {
        return assets.getSprite("portrait_dead");
    }
};

// 2. Crie o elemento de UI com o fornecedor de sprites
UIDynamicImage playerPortrait = new UIDynamicImage(10, 10, portraitSupplier);
uiManager.addElement(playerPortrait);
```

### `UIAnimatedImage`
**Uso**: Para qualquer elemento da UI que precise de ter uma animação em loop ou de execução única, como um ícone de "a guardar", uma moeda girando ou um cursor animado.

**Exemplo Prático**: Um ícone de "a guardar" que pisca.
```java
// Em algum lugar no seu código de carregamento de assets

// 1. Crie a animação manualmente (ou carregue-a via Aseprite/JSON)
Sprite saveIcon1 = assets.getSprite("save_icon_on");
Sprite saveIcon2 = assets.getSprite("save_icon_off");
Animation savingAnimation = new Animation(30, true, saveIcon1, saveIcon2); // Pisca a cada 30 ticks

// No seu setupUI()
// 2. Crie o elemento de UI com a animação
UIAnimatedImage savingIcon = new UIAnimatedImage(Engine.getWIDTH() - 20, 10, savingAnimation);

// 3. Controle a visibilidade com base no estado do jogo
// Supondo que SaveManager tem um método isSaving()
savingIcon.setVisible(SaveManager.isSaving());

uiManager.addElement(savingIcon);
```

---

## Construindo uma HUD Completa (Receitas)

Os exemplos a seguir devem ser implementados no seu `PlayingState`, geralmente dentro de um método `setupUI()` que é chamado no construtor.

### Receita 1: Corações de Vida (Usando `UIImage` e Eventos)

Uma barra de vida baseada em corações não é um único elemento, mas sim uma *coleção* de `UIImage`s que são atualizados de forma inteligente.

**Pré-requisitos**:
Carregue três sprites de coração no seu `AssetManager` ou `ThemeManager`, correspondentes às chaves: `HEART_FULL`, `HEART_HALF`, `HEART_EMPTY`.

**Implementação**:

1.  **Declare a lista de corações no `PlayingState`**:

    ```java
    // Em PlayingState.java
    private List<UIImage> healthHearts;
    ```

2.  **Crie os corações no `setupUI()`**:

    ```java
    // Em PlayingState.java -> setupUI()
    this.healthHearts = new ArrayList<>();
    int maxHearts = (int) Math.ceil(player.maxLife / 20.0); // Ex: 1 coração para cada 20 de vida

    for (int i = 0; i < maxHearts; i++) {
        // Cria os ícones de coração, posicionando-os lado a lado
        UIImage heart = new UIImage(10 + (i * 18), 10, ThemeManager.getInstance().get(UISpriteKey.HEART_FULL));
        this.healthHearts.add(heart);
        this.uiManager.addElement(heart);
    }
    ```

3.  **Crie o método de atualização `updateHealthUI()`**:
    Este método é a lógica central. Ele percorre a vida do jogador e atualiza o sprite de cada coração.

    ```java
    // Em PlayingState.java
    private void updateHealthUI() {
        for (int i = 0; i < healthHearts.size(); i++) {
            int heartValue = (int) (player.life - (i * 20));
            Sprite newSprite;

            if (heartValue >= 20) {
                newSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_FULL);
            } else if (heartValue >= 10) {
                newSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_HALF);
            } else {
                newSprite = ThemeManager.getInstance().get(UISpriteKey.HEART_EMPTY);
            }
            healthHearts.get(i).setSprite(newSprite);
        }
    }
    ```

4.  **Chame a atualização com Eventos (A Forma Eficiente)**:
    Em vez de chamar `updateHealthUI()` a cada `tick`, o que seria um desperdício, inscreva-o nos eventos relevantes.

    ```java
    // Em PlayingState.java -> setupEventListeners()
    EventManager.getInstance().subscribe(GameEvent.PLAYER_TAKE_DAMAGE, (data) -> updateHealthUI());
    EventManager.getInstance().subscribe(GameEvent.PLAYER_HEALED, (data) -> updateHealthUI());
    // Chame-o uma vez no início para o estado inicial
    updateHealthUI();
    ```

### Receita 2: Contador de Colecionáveis (Moedas, Chaves)

Esta é uma combinação clássica de uma imagem estática e um texto dinâmico.

**Implementação**:

```java
// Em PlayingState.java -> setupUI()

// Supondo que 'player' tem um campo 'int coins'.
// 1. Adiciona o ícone da moeda (estático)
uiManager.addElement(new UIImage(10, 30, assets.getSprite("coin_icon")));

// 2. Adiciona o texto do contador (dinâmico)
uiManager.addElement(new UIText(30, 42, font, Color.YELLOW, () -> String.valueOf(player.coins)));
```

É tudo! A UI irá se atualizar "magicamente" sempre que o valor de `player.coins` mudar.

### Receita 3: Barra de XP, Energia ou Progresso

Este é o caso de uso perfeito para o `UIProgressBar`.

**Implementação**:

```java
// Em PlayingState.java -> setupUI()

// Supondo que 'player' tem os campos 'currentXp' e 'xpToNextLevel'.
UIProgressBar xpBar = new UIProgressBar(
    10, 50, // Posição (x, y)
    assets.getSprite("xp_bar_background"), // Sprite para o fundo da barra
    Color.CYAN, // Cor do preenchimento
    () -> player.currentXp, // Fornecedor do valor atual
    () -> player.xpToNextLevel  // Fornecedor do valor máximo
);
uiManager.addElement(xpBar);

// BÓNUS: Adicionar um marcador sobre a barra de progresso
// UIMarker é um UIImage que se posiciona dinamicamente sobre um UIProgressBar.
UIMarker xpMarker = new UIMarker(xpBar, assets.getSprite("xp_marker_icon"));
uiManager.addElement(xpMarker);
```

Este mesmo padrão pode ser usado para barras de energia/stamina, barras de vida de bosses, barras de progresso de *casting* de magias, etc.

### Receita 4: A Caixa de Diálogo (`DialogueBox`)

A `DialogueBox` é um elemento especial. Ela não é adicionada ao `UIManager`, pois sua lógica de `tick` e `render` é chamada diretamente pelo `GameState` para garantir a ordem correta de execução.

**Propósito:** Renderizar as conversas do `DialogueManager`, incluindo o texto com efeito de "máquina de escrever" e as escolhas do jogador.

**Implementação:**

```java
// Em PlayingState.java

// 1. Declare a DialogueBox como uma variável de instância
private DialogueBox dialogueBox;

// 2. No construtor (ou em setupUI), crie e personalize a caixa
public PlayingState() {
    super();
    // ...
    this.dialogueBox = new DialogueBox(
        10, Engine.HEIGHT - 70, // Posição (x, y)
        Engine.WIDTH - 20, 60   // Dimensões (largura, altura)
    );
    dialogueBox.setTypewriterSpeed(2); // Ajusta a velocidade do texto
    // ... outras personalizações
}

// 3. Chame o tick() da caixa de diálogo no tick() do seu estado
@Override
public void tick() {
    super.tick();
    // A caixa de diálogo precisa de ser atualizada para ouvir o input do jogador
    dialogueBox.tick();
}

// 4. Chame o render() da caixa de diálogo no render() do seu estado
@Override
public void render(Graphics g) {
    // A renderização normal é feita pelo RenderManager
    RenderManager.getInstance().render(g);
    
    // Mas a DialogueBox é desenhada por cima de tudo no final
    dialogueBox.render(g);
}
```

A `DialogueBox` é "inteligente": ela só irá se desenhar e processar o input se `DialogueManager.getInstance().isActive()` for verdadeiro. É seguro chamá-la a cada `tick` e `render` do seu estado.

---
[⬅️ Voltar para o Referencia API](./README.md)