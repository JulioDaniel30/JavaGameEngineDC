
# Guia Definitivo: Diálogos Interativos e Narrativa Dinâmica

Este guia é um mergulho profundo no sistema de diálogo da JDStudio Engine. Ele cobre tudo o que você precisa saber para criar desde conversas simples até narrativas complexas e dinâmicas, que reagem ao jogador e modificam o mundo do jogo.

## 1. Visão Geral da Arquitetura

O sistema foi projetado para ser **orientado a dados**, separando a escrita da programação. O fluxo de trabalho é o seguinte:

1.  **Ficheiro JSON**: Um designer de narrativa cria toda a conversa num ficheiro `.json`.
2.  **`DialogueParser`**: A engine lê o ficheiro `.json` e o transforma num objeto `Dialogue` que o código consegue entender.
3.  **`DialogableGameObject`**: Um `GameObject` no jogo (como um NPC ou item) é configurado no Tiled para usar um ficheiro de diálogo específico. Ele usa um `InteractionComponent` para detetar o jogador.
4.  **`PlayingState`**: Atua como o orquestrador. Ele "ouve" os eventos de proximidade e, quando o jogador pressiona a tecla de interação, ele comanda o `GameObject` para iniciar o diálogo.
5.  **`DialogueManager`**: É ativado e atua como o "cérebro" da conversa, sabendo sempre qual é a fala (`DialogueNode`) atual.
6.  **`DialogueBox`**: Este `UIElement` lê as informações do `DialogueManager` e as desenha no ecrã.
7.  **`ActionManager` & `ConditionManager`**: Quando uma escolha é feita, o diálogo comunica com estes sistemas centrais para ler ou alterar o estado do jogo.

---

## 2. O Formato JSON - A Espinha Dorsal da Narrativa

Dominar a estrutura do JSON é a chave para desbloquear todo o potencial do sistema.

### Exemplo Completo de um Ficheiro JSON

Este exemplo mostra quase todas as funcionalidades a trabalhar em conjunto: pontos de entrada, nós múltiplos, escolhas, ações e condições.
```json
{
  "entryPoints": [
    { "condition": "FERREIRO_MISSÃO_COMPLETA", "nodeId": 100 },
    { "condition": "FERREIRO_MISSÃO_ATIVA", "nodeId": 10 }
  ],
  "defaultEntryPoint": 0,
  "nodes": [
    {
      "id": 0, "speakerName": "Ferreiro", "text": "Olá! Tenho uma missão perigosa para ti.",
      "choices": [
        { "text": "Aceito!", "nextNodeId": 10, "action": "ACEITAR_MISSÃO_FERREIRO" }
      ]
    },
    {
      "id": 10, "speakerName": "Ferreiro", "text": "Já conseguiste as 3 Peles de Lobo?",
      "choices": [
        {
          "text": "(Entregar Peles)", "nextNodeId": 11,
          "action": "ENTREGAR_PELES", "condition": "TEM_3_PELES_DE_LOBO"
        },
        { "text": "Ainda não.", "nextNodeId": -1 }
      ]
    },
    { "id": 11, "speakerName": "Ferreiro", "text": "Excelente! Aqui está a tua recompensa." },
    { "id": 100, "speakerName": "Ferreiro", "text": "Olá novamente! Obrigado pela ajuda." }
  ]
}
```

### Detalhe das Propriedades

#### Estrutura Raiz

| Chave | Tipo | Descrição |
| :--- | :--- | :--- |
| **`entryPoints`** | `array` | **(Opcional)** Lista de pontos de entrada condicionais. Verificado de cima para baixo. |
| **`defaultEntryPoint`** | `int` | **(Obrigatório)** O `id` do nó a ser usado se nenhuma condição for satisfeita. |
| **`nodes`** | `array` | **(Obrigatório)** Lista de todos os `DialogueNode`s da conversa. |

### Estrutura de `DialogueNode`
| Chave                 | Tipo              | Descrição                                                                             |
| :---                  | :---              | :---                                                                                  |
| **`id`**, **`text`**  | `int`, `string`   | **(Obrigatório)** As informações básicas da fala.                                                       |
|**`speakerName`**      | `string`          | **(Obrigatório)** O nome de quem esta falando                                                           |
| **`choices`**         | `array`           | **(Opcional)** Array de objetos `DialogueChoice`. Se for omitido, o diálogo termina após esta fala.  |                                   |


#### Estrutura de `DialogueChoice`

| Chave | Tipo | Descrição |
| :--- | :--- | :--- |
| **`text`** | `string` | **(Obrigatório)** O texto da opção que aparece para o jogador. |
| **`nextNodeId`** | `int` |**(Obrigatório)** O `id` do próximo nó. Use `-1` para terminar a conversa. |
| **`action`** | `string` | **(Opcional)** A chave de uma ação a ser executada no `ActionManager`. |
| **`condition`**| `string` | **(Opcional)** A chave de uma condição que deve ser satisfeita para que esta escolha apareça. |

-----

## 3\. Receitas Práticas de Diálogo

### Receita A: Monólogo ou Fala Linear

Para uma sequência de falas onde o jogador apenas avança o texto, crie nós com uma única escolha "fantasma".

```json
//...
"nodes": [
    {
      "id": 0, "text": "As sentinelas do norte caíram...",
      "choices": [ { "text": "...", "nextNodeId": 1 } ]
    },
    { "id": 1, "text": "Precisamos de ti na linha da frente." }
]
//...
```

*A `DialogueBox` não mostrará a opção "..." e qualquer interação avançará para o nó 1. O nó 1, sem `choices`, terminará o diálogo.*

### Receita B: Diálogo de Tutorial de Uso Único (Versão Corrigida e Final)

Este padrão é ideal para um item colecionável onde você quer que um diálogo de tutorial apareça na **primeira vez** que o jogador interage com *qualquer* item desse tipo, mas não nas interações seguintes.

#### 1\. Estruturar o JSON com um Nó Silencioso

Crie um diálogo que tem um ponto de entrada condicional para a primeira vez e um `defaultEntryPoint` que aponta para um nó "silencioso" (sem texto e sem escolhas).

**`dialogo_item_tutorial.json`:**

```json
{
  "entryPoints": [
    { 
      "condition": "PRIMEIRA_VEZ_ITEM_TUTORIAL", 
      "nodeId": 0 
    }
  ],
  "defaultEntryPoint": 999,
  "nodes": [
    {
      "id": 0,
      "speakerName": "Voz Interior",
      "text": "Este é um Fragmento de Luz. Colete-os para restaurar a sua memória.",
      "choices": [
        { 
          "text": "Coletar Fragmento", 
          "nextNodeId": -1,
          "action": "COLETAR_FRAGMENTO" 
        }
      ]
    },
    { "id": 999, "speakerName": "", "text": "" }
  ]
}
```

*Note que a `action` foi simplificada para `"COLETAR_FRAGMENTO"`.*

#### 2\. Definir a Lógica no `PlayingState`

A lógica é dividida em três partes: a **condição** (o que verificar), a **ação** (o que fazer) e o **orquestrador** (quando fazer).

```java
// Em PlayingState.java

/**
 * Define as CONDIÇÕES que o diálogo pode verificar.
 */
private void setupDialogueConditions() {
    ConditionManager cm = ConditionManager.getInstance();
    GameStateManager gsm = GameStateManager.getInstance();
    
    // Define que a condição é satisfeita se a flag global AINDA NÃO existir.
    cm.registerCondition("PRIMEIRA_VEZ_ITEM_TUTORIAL", (player) -> 
        !gsm.hasFlag("FLAG_JOGADOR_VIU_ITEM_TUTORIAL")
    );
}

/**
 * Define as AÇÕES que o diálogo pode executar.
 */
private void setupDialogueActions() {
    ActionManager am = ActionManager.getInstance();
    GameStateManager gsm = GameStateManager.getInstance();
    
    // Cria uma ÚNICA ação para coletar o fragmento.
    // Esta ação será chamada tanto pelo diálogo quanto pela interação direta.
    am.registerAction("COLETAR_FRAGMENTO", (player, item) -> {
        // Garante que a flag seja definida (não faz mal chamar várias vezes)
        gsm.setFlag("FLAG_JOGADOR_VIU_ITEM_TUTORIAL");
        
        if (item instanceof FragmentOfLight && !item.isDestroyed) {
            PlayingState.countFragLight++;
            item.destroy();
            // Tocar som de coleta aqui...
        }
    });
}

/**
 * O ORQUESTRADOR: decide o que fazer quando o jogador interage.
 */
public void handleInput() {
    if (InputManager.isActionJustPressed("INTERACT") && interactableObjectInRange != null) {
        if (interactableObjectInRange instanceof FragmentOfLight) {
            FragmentOfLight fragment = (FragmentOfLight) interactableObjectInRange;
            
            // 1. Verifica a condição ANTES de qualquer ação.
            boolean isFirstTime = ConditionManager.getInstance().checkCondition("PRIMEIRA_VEZ_ITEM_TUTORIAL", player);
            
            if (isFirstTime) {
                // 2. Se for a primeira vez, a única responsabilidade é iniciar o diálogo.
                // A ação "COLETAR_FRAGMENTO" será chamada quando o jogador confirmar.
                fragment.startFilteredDialogue(player);
            } else {
                // 3. Se NÃO for a primeira vez, chama a ação de coleta diretamente.
                ActionManager.getInstance().executeAction("COLETAR_FRAGMENTO", player, fragment);
            }
        }
    }
}
```

Com esta estrutura, você garante que o diálogo de tutorial aconteça apenas uma vez para o tipo de item, e que todas as instâncias subsequentes sejam coletadas diretamente, criando uma experiência de jogo fluida e inteligente.

### C. Diálogo com Escolhas Condicionais

Permite que opções só apareçam se o jogador cumprir um requisito.

1.  **No JSON**, adicione a `condition` à escolha:
    ```json
    "choices": [
      { "text": "(Entregar Peles)", "action": "ENTREGAR_PELES", "condition": "TEM_3_PELES_DE_LOBO" }
    ]
    ```
2.  **No Jogo**, registe a condição:
    ```java
    // Em setupDialogueConditions()
    ConditionManager.getInstance().registerCondition("TEM_3_PELES_DE_LOBO", (player) -> 
        player.getComponent(InventoryComponent.class).inventory.getItemCount("pele_de_lobo") >= 3
    );
    ```
3.  **No seu `DialogableGameObject`**, use o método de filtro:
    ```java
    public void startFilteredDialogue(GameObject interactor) {
        Dialogue filteredDialogue = DialogueParser.parseDialogue(this.dialoguePath);
        for (DialogueNode node : filteredDialogue.getNodes().values()) {
            node.getChoices().removeIf(choice -> {
                String condition = choice.getCondition();
                if (condition == null) return false;
                boolean conditionMet = ConditionManager.getInstance().checkCondition(condition, interactor);
                return !conditionMet; // Remove se a condição NÃO for satisfeita
            });
        }
        DialogueManager.getInstance().startDialogue(filteredDialogue, this, interactor);
    }
    ```

-----

## 4\. A Interface (`DialogueBox`)

A `DialogueBox` é o `UIElement` que torna a conversa visível.

  * **Instanciação**: Geralmente é criada uma vez no construtor do seu `PlayingState`.
  * **Atualização**: O seu método `dialogueBox.tick()` deve ser chamado no `tick()` do `PlayingState` para processar o input do jogador e o efeito de "máquina de escrever".
  * **Renderização**: O seu método `dialogueBox.render(g)` deve ser chamado no `render()` do `PlayingState`.

A `DialogueBox` é "inteligente": ela só irá se desenhar se `DialogueManager.getInstance().isActive()` for verdadeiro.

---
[⬅️ Voltar para o Guias Avançados](./README.md)