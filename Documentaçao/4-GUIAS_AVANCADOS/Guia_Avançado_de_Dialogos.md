# Guia Avançado: Diálogos Interativos e Narrativa Dinâmica

Este guia é um mergulho profundo no sistema de diálogo da JDStudio Engine. Ele cobre tudo o que você precisa saber para criar desde conversas simples até narrativas complexas e dinâmicas, que reagem ao jogador e modificam o mundo do jogo.

## 1. Visão Geral da Arquitetura

O sistema foi projetado para ser **orientado a dados**, separando a escrita da programação. O fluxo de trabalho é o seguinte:

1.  **Ficheiro JSON**: Um designer de narrativa ou escritor cria toda a conversa num ficheiro `.json`, definindo falas, escolhas, condições e ações.
2.  **`DialogueParser`**: A engine lê o ficheiro `.json` e o transforma num objeto `Dialogue` que o código consegue entender.
3.  **`DialogableGameObject`**: Um `GameObject` no jogo (como um NPC ou item) é configurado no Tiled para usar um ficheiro de diálogo específico. Ele usa um `InteractionComponent` para detetar o jogador.
4.  **`PlayingState`**: Atua como o orquestrador. Ele "ouve" os eventos de proximidade e, quando o jogador pressiona a tecla de interação, ele comanda o `GameObject` para iniciar o diálogo.
5.  **`DialogueManager`**: É ativado e atua como o "cérebro" da conversa, sabendo sempre qual é a fala (`DialogueNode`) atual.
6.  **`DialogueBox`**: Este `UIElement` lê as informações do `DialogueManager` e as desenha no ecrã.
7.  **`ActionManager` & `ConditionManager`**: Quando uma escolha é feita, o `DialogueManager` pode usar estes sistemas para executar lógica de jogo (`ActionManager`) ou verificar se uma condição é válida (`ConditionManager`).

---

## 2. O Formato JSON - A Espinha Dorsal da Narrativa

Dominar a estrutura do JSON é a chave para desbloquear todo o potencial do sistema.

### Estrutura Principal do JSON
| Chave                     | Tipo      | Descrição                                                                                 |
| :---                      | :---      | :---                                                                                      |
| **`entryPoints`**         | `array`   | **(Opcional)** Lista de pontos de entrada condicionais. Verificado de cima para baixo.    |
| **`defaultEntryPoint`**   | `int`     | **(Obrigatório)** O `id` do nó de diálogo a ser usado se nenhuma condição for satisfeita. |
| **`nodes`**               | `array`   | Lista de todos os `DialogueNode`s (as falas) possíveis na conversa.                       |

### Estrutura de `DialogueNode`
| Chave                 | Tipo              | Descrição                                                                             |
| :---                  | :---              | :---                                                                                  |
| **`id`**, **`text`**  | `int`, `string`   | As informações básicas da fala.                                                       |
|**`speakerName`**      | `string`          | O nome de quem esta falando                                                           |
| **`choices`**         | `array`           | Array de objetos `DialogueChoice`. Se for omitido, o diálogo termina após esta fala.  |                                   |


### Estrutura de `DialogueChoice`
| Chave             | Tipo      | Descrição                                                                                     |
| :---              | :---      | :---                                                                                          |
| **`text`**        | `string`  | O texto da opção que aparece para o jogador.                                                  |
| **`nextNodeId`**  | `int`     | O `id` do próximo nó. Use `-1` para terminar a conversa.                                      |
| **`action`**      | `string`  | **(Opcional)** A chave de uma ação a ser executada no `ActionManager`.                        |
| **`condition`**   | `string`  | **(Opcional)** A chave de uma condição que deve ser satisfeita para que esta escolha apareça. |

---

## 3. Guia Prático: Criar um NPC com Diálogo Evolutivo

Vamos criar um NPC do zero que tem uma fala na primeira interação, uma fala diferente enquanto uma missão está ativa, e uma terceira fala após a missão ser concluída.

### Passo 1: Escrever o Ficheiro `ferreiro.json`
```json
{
  "entryPoints": [
    { "condition": "PRIMEIRA_VEZ_FERREIRO", "nodeId": 0 },
    { "condition": "FERREIRO_MISSÃO_ATIVA", "nodeId": 10 }
  ],
  "defaultEntryPoint": 100,
  "nodes": [
    {
      "id": 0, "speakerName": "Ferreiro", "text": "Olá, forasteiro! Tenho uma missão perigosa para ti.",
      "choices": [
        { "text": "Aceito!", "nextNodeId": 10, "action": "ACEITAR_MISSÃO_FERREIRO" }
      ]
    },
    {
      "id": 10, "speakerName": "Ferreiro", "text": "Já conseguiste as Peles de Lobo?",
      "choices": [
        { "text": "(Entregar Peles)", "nextNodeId": 11, "action": "ENTREGAR_PELES", "condition": "TEM_PELES_DE_LOBO" },
        { "text": "Ainda não.", "nextNodeId": -1 }
      ]
    },
    { "id": 11, "speakerName": "Ferreiro", "text": "Excelente! Aqui está a tua recompensa.", "choices": [ { "text": "Obrigado!", "nextNodeId": 100 } ] },
    { "id": 100, "speakerName": "Ferreiro", "text": "Olá novamente! Obrigado pela ajuda." }
  ]
}
```

### Passo 2: Criar a Classe `Ferreiro.java`

Esta classe herda de uma base como `DialogableGameObject` ou `EngineNPC`, configura o seu `InteractionComponent` e implementa o método de filtro.

```java
public class Ferreiro extends DialogableGameObject {
    public Ferreiro(JSONObject properties) {
        super(properties);
        // Configura a zona para interação manual
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);
    }
}
```

### Passo 3: Registar as Ações e Condições

No `PlayingState`, ensine à engine o que as `actions` e `conditions` do seu JSON significam.

```java
// Em PlayingState.java

private void setupDialogueConditions() {
    ConditionManager cm = ConditionManager.getInstance();
    GameStateManager gsm = GameStateManager.getInstance();
    
    cm.registerCondition("PRIMEIRA_VEZ_FERREIRO", (p) -> !gsm.hasFlag("FALOU_COM_FERREIRO"));
    cm.registerCondition("FERREIRO_MISSÃO_ATIVA", (p) -> gsm.hasFlag("FERREIRO_MISSÃO_ATIVA"));
    cm.registerCondition("TEM_PELES_DE_LOBO", (p) -> p.getComponent(InventoryComponent.class).inventory.getItemCount("pele_de_lobo") >= 3);
}

private void setupDialogueActions() {
    ActionManager am = ActionManager.getInstance();
    GameStateManager gsm = GameStateManager.getInstance();
    
    am.registerAction("ACEITAR_MISSÃO_FERREIRO", (player, npc) -> {
        gsm.setFlag("FALOU_COM_FERREIRO");
        gsm.setFlag("FERREIRO_MISSÃO_ATIVA");
        System.out.println("Missão Aceite!");
    });
    
    am.registerAction("ENTREGAR_PELES", (player, npc) -> {
        player.getComponent(InventoryComponent.class).inventory.removeItem("pele_de_lobo", 3);
        gsm.removeFlag("FERREIRO_MISSÃO_ATIVA");
        gsm.setFlag("FERREIRO_MISSÃO_COMPLETA");
        System.out.println("Missão Completa!");
    });
}
```

### Passo 4: Orquestrar a Interação

Finalmente, o `PlayingState` usa os seus `EventListener`s e o `tick()` para juntar tudo.

```java
// Em PlayingState.java

// 1. A variável de estado
private GameObject interactableObjectInRange = null;

// 2. Os EventListeners (em setupEventListeners)
EventManager.getInstance().subscribe(GameEvent.TARGET_ENTERED_ZONE, (data) -> {
    InteractionEventData event = (InteractionEventData) data;
    if (event.zone().type.equals(InteractionZone.TYPE_DIALOGUE)) {
        this.interactableObjectInRange = event.zoneOwner();
    }
});
EventManager.getInstance().subscribe(GameEvent.TARGET_EXITED_ZONE, (data) -> {
    InteractionEventData event = (InteractionEventData) data;
    if (event.zoneOwner() == this.interactableObjectInRange) {
        this.interactableObjectInRange = null;
    }
});

// 3. A lógica de input no tick()
public void tick() {
    // ...
    if (InputManager.isActionJustPressed("INTERACT") && this.interactableObjectInRange != null) {
        if (this.interactableObjectInRange instanceof DialogableGameObject) {
             // Chama o método de filtro do objeto interativo
            ((DialogableGameObject) this.interactableObjectInRange).startFilteredDialogue(player);
        }
    }
    // ...
}
```

Com estes passos, você tem um sistema completo onde a narrativa, definida em JSON, está perfeitamente sincronizada com a lógica e o estado do seu jogo.

---
[⬅️ Voltar para o Guias Avançados](./README.md)