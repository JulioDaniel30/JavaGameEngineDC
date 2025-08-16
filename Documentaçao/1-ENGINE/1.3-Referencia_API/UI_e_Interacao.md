# API: UI, Diálogo e Input

Esta página detalha os sistemas que formam a ponte entre o jogador e o jogo: o `InputManager` para capturar comandos, o sistema de UI para feedback visual e o sistema de Diálogo para narrativa.

## Sistema de Input (`InputManager`)

O `InputManager` é um singleton que unifica a entrada de teclado e mouse em um sistema de "ações" abstratas. Em vez de verificar se a tecla "W" foi pressionada, você verifica se a ação "MOVE_UP" foi acionada. Isso permite que os jogadores remapeiem os controles facilmente.

### Mapeamento de Ações via JSON

Os vínculos são definidos em arquivos `.json`. A engine carrega um arquivo padrão (`/Engine/engine_keybindings.json`) e você pode fornecer o seu próprio para o jogo, que será fundido com o da engine.

**Exemplo de `meujogo_keybindings.json`:**
```json
{
  "MOVE_UP": [
    { "key": "W" }
  ],
  "MOVE_DOWN": [
    { "key": "S" }
  ],
  "ATTACK": [
    { "key": "SPACE" },
    { "key": "MOUSE1" } 
  ],
  "SPECIAL_ATTACK": [
    { "key": "Q", "ctrl": true }
  ]
}
```
- **Múltiplos Vínculos**: Uma ação pode ser vinculada a várias teclas/botões (ex: `ATTACK`).

- **Mouse**: Use `"MOUSE1"`, `"MOUSE2"`, `"MOUSE3"`.

- **Modificadores**: Adicione `"ctrl": true`, `"shift": true`, ou `"alt": true` para combinações.

#### Verificando Ações no Jogo
```java
// Verifica se a ação está sendo pressionada continuamente
if (InputManager.isActionPressed("MOVE_UP")) {
    // ...
}

// Verifica se a ação acabou de ser pressionada neste exato frame
if (InputManager.isActionJustPressed("ATTACK")) {
    player.attack();
}

// Verifica se a ação foi solta neste exato frame
if (InputManager.isActionReleased("AIM")) {
    // ...
}
```
##### Acesso Direto ao Mouse
- `InputManager.getMouseX()` / `getMouseY()`: Retorna as coordenadas do mouse na janela.

- `InputManager.covertMousePositionToWorld()`: Converte as coordenadas do mouse para as coordenadas do mundo do jogo, considerando a câmera e a escala.

### Sistema de UI
A engine possui um sistema de UI robusto com dois conceitos principais e um gerenciador de temas.

- **UI de Tela**: Elementos fixos na tela, como HUDs e menus. São gerenciados pelo UIManager dentro de um EngineMenuState ou um HUDState.

- **UI de Mundo**: Elementos que "flutuam" sobre GameObjects no mundo, como barras de vida e nomes. Herdam de UIWorldAttached.

- `ThemeManager`: Permite trocar a aparência de toda a UI. Ele carrega sprites de uma estrutura de pastas padronizada: `/Engine/UI/{theme_name}/{sprite_key_name}.png`. O tema ativo é definido com `ThemeManager.getInstance().setTheme(UITheme.MEDIEVAL)`.

#### Catálogo de Elementos de UI

Todos os elementos de UI herdam de `UIElement` e são adicionados a um `UIManager` com `uiManager.addElement(meuElemento)`.

- `UIButton`: Um botão clicável 
    ```java
    // Usa sprites do tema atual
    UIButton startButton = new UIButton(100, 50, "Iniciar", new Font    ("Arial", 12), () -> {
        Engine.transitionToState(new Level1State());
    });
    uiManager.addElement(startButton);
    ```
- ``UIText``: Exibe texto estático ou dinâmico.
    ```java
    // Texto estático
    uiManager.addElement(new UIText(10, 20, font, Color.WHITE, "Menu Principal"));
    // Texto dinâmico (ex: FPS)
    uiManager.addElement(new UIText(10, 40, font, Color.YELLOW, () -> "FPS: " + Engine.getFPS())
    ```
- `UISlider`: Um controle deslizante, ideal para menus de opções.
    ```java
    // Cria um slider para o volume da música
    UISlider musicSlider = new UISlider(100, 80, 0.0f, 1.0f, Sound.getMusicVolume(), (newValue) -> {
        Sound.setMusicVolume(newValue);
    });
    uiManager.addElement(musicSlider);
    ```
- `UIToggleButton`: Um interruptor On/Off
    ```java
    UIToggleButton vsyncToggle = new UIToggleButton(
        100, 120,
        ThemeManager.getInstance().get(UISpriteKey.TOGGLE_OFF), // Sprite desligado
        ThemeManager.getInstance().get(UISpriteKey.TOGGLE_ON),  // Sprite ligado
        true, // Estado inicial
        (isNowOn) -> { GameSettings.setVsync(isNowOn); } // Ação
    );
    uiManager.addElement(vsyncToggle);
    ```
- `UIProgressBar`: Uma barra de progresso que se atualiza automaticamente.
    ```java
    // Barra de vida do jogador no HUD
    UIProgressBar healthBar = new UIProgressBar(
        20, 20,
        assets.getSprite("healthbar_background"),
        Color.RED,
        () -> (float) player.life, // Supplier para o valor atual
        () -> (float) player.maxLife // Supplier para o valor máximo
    );
    uiManager.addElement(healthBar);
    ```
- `UIInventoryView`: Uma grade de inventário completa.
    ```java
    // Cria a visualização para o inventário do jogador
    UIInventoryView inventoryView = new UIInventoryView(
        50, 50,
        assets.getSprite("inventory_background"),
        assets.getSprite("inventory_slot_background"),
        player, // Dono do inventário
        player.getComponent(InventoryComponent.class).inventory,
        3, 5, // 3 linhas, 5 colunas
        32, // Tamanho de cada slot
        4 // Espaçamento entre slots
    );
    uiManager.addElement(inventoryView);
    ```


### Sistema de Diálogo e Ações
A engine permite criar diálogos complexos com escolhas e ações que afetam o jogo.

#### Estrutura do JSON de Diálogo
Crie arquivos `.json` para suas conversas com a seguinte estrutura:
```json
{
  "startNodeId": 0,
  "nodes": [
    {
      "id": 0,
      "speakerName": "Ferreiro",
      "text": "Olá, aventureiro! Precisa de uma espada nova?",
      "choices": [
        { "text": "Sim, por favor!", "nextNodeId": 1 },
        { "text": "Não, obrigado.", "nextNodeId": 2 }
      ]
    },
    {
      "id": 1,
      "speakerName": "Ferreiro",
      "text": "Excelente! Te custará 10 moedas.",
      "choices": [
        { "text": "Pagar 10 moedas.", "nextNodeId": 3, "action": "buy_sword" },
        { "text": "É muito caro.", "nextNodeId": 2 }
      ]
    },
    {
      "id": 2,
      "speakerName": "Ferreiro",
      "text": "Entendo. Volte quando precisar!",
      "choices": [
        { "text": "Adeus.", "nextNodeId": -1 }
      ]
    },
    {
      "id": 3,
      "speakerName": "Ferreiro",
      "text": "Aqui está! Que ela lhe sirva bem.",
      "choices": [
        { "text": "Obrigado!", "nextNodeId": -1 }
      ]
    }
  ]
}
```
- `nextNodeId: -1`: Termina o diálogo.

- `"action": "key"`: Define uma chave de ação a ser executada quando a escolha é selecionada.

##### Fluxo de Execução
1. `Tiled`: Um `EngineNPC` no mapa tem a propriedade customizada `dialogueFile` apontando para o caminho do JSON (ex: `/dialogues/ferreiro.json`).

2. `DialogueParser`: A engine usa esta classe para ler o JSON e criar os objetos de diálogo.

3. **Interação**: Quando o jogador interage com o NPC, o `DialogueManager.getInstance().startDialogue(...)` é chamado.

4. **UI**: Uma `DialogueBox` (adicionada ao seu `UIManager`) lê o estado do `DialogueManager` e desenha a conversa, as escolhas e o efeito de "máquina de escrever".

5. **Ação**: Quando o jogador escolhe uma opção com uma chave de `"action"`, o `DialogueManager` chama o `ActionManager`.

#### O `ActionManager`
Este sistema desacopla a lógica do jogo do sistema de diálogo.

##### Como Usar:
No seu código de inicialização do jogo (ex: no LoadingState), você registra a lógica para cada chave de ação.
```java
// Registra a lógica para a ação "buy_sword"
ActionManager.getInstance().registerAction("buy_sword", (interactor, source) -> {
    // 'interactor' é o GameObject que interagiu (o Player)
    // 'source' é a fonte do diálogo (o NPC Ferreiro)
    
    InventoryComponent playerInventory = interactor.getComponent(InventoryComponent.class);
    PlayerWallet playerWallet = interactor.getComponent(PlayerWallet.class); // Componente hipotético

    if (playerWallet.getMoney() >= 10) {
        playerWallet.removeMoney(10);
        playerInventory.inventory.addItem(new SwordItem(), 1);
        System.out.println("Jogador comprou a espada!");
    } else {
        // O diálogo poderia ter um nó para "dinheiro insuficiente"
        System.out.println("Dinheiro insuficiente!");
    }
});
```
Com isso, o sistema de diálogo permanece genérico, e toda a lógica específica do seu jogo fica centralizada no seu próprio código.
---
[⬅️ Voltar para o Referencia API](./README.md)