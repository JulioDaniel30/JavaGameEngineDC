# Guia Avançado: World Space UI (Interfaces no Mundo do Jogo)

Este guia detalha como usar o sistema de "World Space UI" (WSUI) da JDStudio Engine. Diferente da UI de tela (HUD), que é fixa, a WSUI é composta por elementos que existem **dentro do mundo do jogo**, anexados a personagens e objetos. Eles movem-se com a câmara e são ideais para barras de vida, nomes de NPCs, ícones de missão e muito mais.

## 1. O Conceito: `UIWorldAttached`

A base de todo o sistema é a classe abstrata `UIWorldAttached`. Quando você cria um elemento que herda desta classe, ele ganha as seguintes propriedades automáticas:

* **Alvo (`target`)**: Ele precisa de um `GameObject` para seguir, que é passado no construtor.
* **Seguimento Automático**: O seu método `tick()` atualiza automaticamente a sua posição `(x, y)` para seguir o `target` no mundo do jogo.
* **Offset Vertical (`yOffset`)**: Permite ajustar a altura em que o elemento aparece em relação ao alvo, ideal para posicioná-lo "acima da cabeça" do personagem.

Todos os elementos WSUI devem ser adicionados ao `UIManager` para serem processados e renderizados.

---

## 2. Receitas Práticas para Cada Elemento WSUI

O local mais comum para criar estes elementos é no método `onObjectFound` do seu `PlayingState`, ou em resposta a eventos do jogo.

### Barra de Vida Contínua (`UIHealthBar`)

**Conceito**: Uma barra de vida que segue um `GameObject` e exibe a sua saúde atual de forma contínua.

**Receita**: Adicionar uma barra de vida sobre um inimigo.
1.  **Pré-requisito**: O `GameObject` inimigo deve ter um `HealthComponent` adicionado a ele.
2.  **Implementação**: No `PlayingState`, após criar o inimigo, crie e adicione a sua barra de vida.
    ```java
    // Em PlayingState.java -> onObjectFound()
    case "Enemy":
        Enemy enemy = new Enemy(player, properties);
        this.addGameObject(enemy);
        
        // Cria uma barra de vida e anexa-a ao inimigo
        // Parâmetros: (alvo, offset Y, largura da barra, altura da barra)
        UIHealthBar enemyHealthBar = new UIHealthBar(enemy, -8, 24, 4);
        
        // Adiciona a barra de vida ao gestor de UI
        this.uiManager.addElement(enemyHealthBar);
        break;
    ```

### Barra de Vida Segmentada (`UIHealthBarSegmented`)

**Conceito**: Exibe a saúde em segmentos discretos (como corações), em vez de uma barra contínua, ideal para um estilo mais retro.

**Receita**: Adicionar corações de vida sobre um boss.
1.  **Pré-requisitos**: O alvo deve ter um `HealthComponent`. Os sprites para os corações (`HEART_FULL`, `HEART_HALF`, `HEART_EMPTY`) devem estar disponíveis no `ThemeManager`, ou pode setar com o metodo `setSprites(HEART_FULL, HEART_HALF,HEART_EMPTY)`.
2.  **Implementação**: No `PlayingState`, após criar o boss, crie a barra segmentada.
    ```java
    // Em PlayingState.java -> onObjectFound()
    case "Boss":
        Boss boss = new Boss(player, properties);
        this.addGameObject(boss);

        // Cria uma barra de vida com corações. Cada coração representa 50 pontos de vida do boss.
        UIHealthBarSegmented bossHearts = new UIHealthBarSegmented(boss, -12, 50);
        this.uiManager.addElement(bossHearts);
        break;
    ```

### Barra de Mana (`UIManaBar`)

**Conceito**: Uma barra que segue um `GameObject` e exibe a sua mana atual, ideal para personagens que usam magia ou habilidades especiais.

**Receita**: Adicionar uma barra de mana ao jogador.
1.  **Pré-requisito**: O `GameObject` do jogador deve ter um `ManaComponent` adicionado a ele.
2.  **Implementação**: No `PlayingState`, após criar o jogador, crie e adicione a sua barra de mana.
    ```java
    // Em PlayingState.java -> após criar o objeto 'player'
    
    // Adiciona a barra de vida (exemplo)
    UIHealthBar playerHealthBar = new UIHealthBar(player, -8, 24, 4);
    this.uiManager.addElement(playerHealthBar);

    // Adiciona a nova barra de mana
    // Parâmetros: (alvo, offset Y, largura da barra, altura da barra)
    UIManaBar playerManaBar = new UIManaBar(player, -14, 24, 4);
    
    // Adiciona a barra de mana ao gestor de UI
    this.uiManager.addElement(playerManaBar);
    ```

### Placa de Nome (`UINameplate`)

**Conceito**: Exibe o nome de um `GameObject` (como um NPC) sobre a sua cabeça.

**Receita**: Exibir o nome do `Ferreiro`.
1.  **Pré-requisito**: O objeto no Tiled deve ter a propriedade `name` preenchida (ex: "Ferreiro").
2.  **Implementação**: No `PlayingState`, após criar o NPC, adicione o `UINameplate`.
    ```java
    // Em PlayingState.java -> onObjectFound()
    case "Ferreiro":
        Ferreiro ferreiro = new Ferreiro(properties);
        this.addGameObject(ferreiro);
        
        UINameplate nameplate = new UINameplate(
            ferreiro,                               // Alvo
            -6,                                     // Offset Y (um pouco acima da cabeça)
            new Font("Arial", Font.BOLD, 10),       // Fonte
            Color.WHITE                             // Cor
        );
        this.uiManager.addElement(nameplate);
        break;
    ```

### Prompt de Interação (`UIInteractionPrompt`)

**Conceito**: Um texto/ícone que aparece sobre um objeto quando o jogador está próximo, indicando que uma interação é possível (ex: “[E] Falar”).

**Receita**: Mostrar um prompt "[E] Falar" quando perto de um NPC.
1.  **No NPC**: Adicione um `InteractionPromptComponent` com o texto desejado.
    ```java
    // Em Ferreiro.java -> initialize()
    this.addComponent(new InteractionPromptComponent("[E] Falar"));
    ```
2.  **No `PlayingState`**: Crie um único `UIInteractionPrompt` e controle-o com os `EventListeners`.
    ```java
    // Em PlayingState.java
    private UIInteractionPrompt interactionPrompt;

    // No construtor ou em setupUI()
    this.interactionPrompt = new UIInteractionPrompt();
    this.uiManager.addElement(this.interactionPrompt);

    // Nos EventListeners...
    EventManager.getInstance().subscribe(GameEvent.TARGET_ENTERED_ZONE, (data) -> {
        // ...
        InteractionPromptComponent promptComp = interactableObjectInRange.getComponent(InteractionPromptComponent.class);
        if (promptComp != null) {
            interactionPrompt.setTarget(interactableObjectInRange, promptComp.promptText);
        }
    });
    EventManager.getInstance().subscribe(GameEvent.TARGET_EXITED_ZONE, (data) -> {
        // ...
        interactionPrompt.setTarget(null, "");
    });
    ```

### Balões de Emoção e Fala (`UIEmotionBubble` e `UIChatBubble`)

**Conceito**: Ícones ou textos temporários que aparecem sobre um personagem para dar feedback não-verbal ou "barks".

**Receita**: Fazer um inimigo mostrar um ícone de alerta (`❗`) ao detetar o jogador.
1.  **Carregue o Ícone**: No `AssetManager` do seu jogo, carregue o sprite para o marcador (ex: `"emotion_alert"`).
2.  **Crie o Balão em Resposta a um Evento**: No `EventListener` do inimigo, quando ele entra no estado de `CHASING`.
    ```java
    // Na sua classe Enemy.java, dentro do listener para TARGET_ENTERED_ZONE
    case InteractionZone.TYPE_AGGRO:
        if (self.currentState != AIState.CHASING) { // Apenas na primeira vez
            // ... (lógica para mudar de estado)
            Sprite alertSprite = PlayingState.assets.getSprite("emotion_alert");
            UIEmotionBubble alertBubble = new UIEmotionBubble(self, alertSprite, 90); // Dura 1.5 segundos
            PlayingState.uiManager.addElement(alertBubble);
        }
        break;
    ```

### Círculo de Progresso (`UIProgressCircle`)

**Conceito**: Um círculo que se preenche para indicar o progresso de uma ação que leva tempo (carregar feitiços, abrir baús).

**Receita**: Criar um baú que demora 2 segundos a abrir.
1.  **No Baú**: Adicione um `ChargeableComponent` para gerir a lógica de carregamento.
2.  **Crie a UI**: No `initialize` do baú, crie a `UIProgressCircle` e ligue os seus `suppliers` ao `ChargeableComponent`.
    ```java
    // Em LootChest.java -> initialize()
    Consumer<GameObject> openAction = (owner) -> this.giveLootTo(PlayingState.player);
    ChargeableComponent chargeable = new ChargeableComponent(120, openAction); // 120 ticks = 2s
    this.addComponent(chargeable);
    
    UIProgressCircle progressCircle = new UIProgressCircle(
        this, -16, 12, 3, Color.DARK_GRAY, Color.WHITE,
        () -> chargeable.getProgress(),   // Liga o progresso da UI à lógica
        () -> chargeable.isCharging()     // Liga a visibilidade da UI à lógica
    );
    PlayingState.uiManager.addElement(progressCircle);
    ```
3.  A interação com o baú agora simplesmente chama `chargeable.startCharging()`.

### Marcadores de Missão (`UIQuestMarker` e `UIDirectionArrow`)

**Conceito**: Ícones sobre a cabeça de NPCs (`UIQuestMarker`) ou do jogador (`UIDirectionArrow`) para guiar em missões.

**Receita**: Colocar um `!` sobre um NPC e uma seta sobre o jogador a apontar para o objetivo.
1.  **No NPC**: Adicione um `QuestGiverComponent` (do lado do jogo). A lógica de flags no `GameStateManager` irá controlar se o ícone é `!`, `?` ou se está escondido.
2.  **No Jogador**: Adicione um `QuestComponent` para guardar o `GameObject` alvo da missão.
3.  **No `PlayingState`**: Crie uma única `UIDirectionArrow` que segue o jogador. Crie um método `updateQuestIndicators()` que lê o alvo do `QuestComponent` do jogador e chama `questArrow.setPointTarget(...)`. Chame este método nas `action`s de diálogo que iniciam ou terminam missões.

---
[⬅️ Voltar para o Guias Avançados](./README.md)