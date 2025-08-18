
# Guia Avançado: Sistema de Tutoriais Interativos

Este guia detalha como usar o sistema de tutoriais da JDStudio Engine para criar dicas e guias interativos para os jogadores. O sistema é **orientado a dados**, o que significa que todos os tutoriais são definidos num arquivo JSON, tornando a sua criação e manutenção muito fácil.

## 1. O Formato JSON em Detalhe

Crie um arquivo na sua pasta de recursos (ex: `res/configs/tutorials.json`). Este arquivo irá conter um array de "passos" de tutorial. Cada passo é um objeto JSON com a seguinte estrutura:

* **`id`**: Um nome único para o tutorial (ex: "MOVE_TUTORIAL").
* **`text`**: O texto que será mostrado ao jogador.
* **`trigger`**: Um objeto que define **quando o tutorial deve aparecer**.
* **`completion`**: Um objeto que define **quando o tutorial deve desaparecer**.
* **`ui`**: (Opcional) Um objeto para configurar a aparência e posição da caixa de tutorial.

### Tipos de Gatilho (`trigger`)

| `type`        | Parâmetros Adicionais         | Descrição                                                                                     |
|---------------|-------------------------------|-----------------------------------------------------------------------------------------------|
| **`EVENT`**   | `eventName` (String)         | O tutorial é acionado quando um evento específico é disparado pelo `EventManager`. O `eventName` deve ser o nome da constante do seu enum (ex: `"GAME_STARTED"`). |
| **`ZONE_ENTER`** | `zoneType` (String)        | O tutorial é acionado quando o jogador entra numa `InteractionZone` com o `type` especificado. |

### Tipos de Conclusão (`completion`)

| `type`        | Parâmetros Adicionais         | Descrição                                                                                     |
|---------------|-------------------------------|-----------------------------------------------------------------------------------------------|
| **`KEY_PRESS`** | `actions` (Array de Strings) | O tutorial desaparece quando o jogador pressiona qualquer uma das "ações" de input listadas. |
| **`EVENT`**   | `eventName` (String)         | O tutorial desaparece quando um evento específico é disparado.                               |
| **`DURATION`** | `ticks` (int)                | O tutorial desaparece após um certo número de `ticks` (frames). 60 ticks = 1 segundo.       |
| **`ZONE_EXIT`**| `zoneType` (String)          | O tutorial desaparece quando o jogador sai de uma `InteractionZone` com o `type` especificado. |

```json
{
  "tutorials": [
    {
      "id": "MOVE_TUTORIAL",
      "text": "Use W, A, S, D para se mover.",
      "trigger": {
        "type": "EVENT",
        "eventName": "GAME_STARTED"
      },
      "completion": {
        "type": "KEY_PRESS",
        "actions": ["MOVE_UP", "MOVE_DOWN", "MOVE_LEFT", "MOVE_RIGHT"]
      },
      "ui": { "position": "BOTTOM_CENTER" }
    },
    {
      "id": "ATTACK_TUTORIAL",
      "text": "Pressione ESPAÇO para atacar!",
      "trigger": {
        "type": "EVENT",
        "eventName": "FIRST_ENEMY_SPAWNED"
      },
      "completion": {
        "type": "DURATION",
        "ticks": 300
      },
      "ui": { "position": "TOP_CENTER" }
    }
  ]
}
```

---

## 2. Implementação e Exemplos Práticos

A implementação no jogo é feita em três passos: configurar a UI, carregar os tutoriais e disparar os eventos de gatilho.

### Passo 1: Configurar a `TutorialBox` no `PlayingState`

No seu `PlayingState`, crie e adicione a `TutorialBox` ao seu `UIManager`.

```java
// Em PlayingState.java
private TutorialBox tutorialBox;

// No construtor ou em setupUI()
this.tutorialBox = new TutorialBox();
// Você pode customizar a aparência da caixa aqui, se ela tiver setters
// ex: this.tutorialBox.setFont(new Font(...));
this.uiManager.addElement(this.tutorialBox);
```

### Passo 2: Carregar os Tutoriais e Atualizar o Manager

No início do jogo, chame o `TutorialManager` para que ele carregue o seu arquivo JSON. Depois, chame o seu método `update()` a cada frame.

```java
// No construtor de PlayingState.java
TutorialManager.getInstance().loadTutorials("/configs/tutorials.json", this.tutorialBox);

// No tick() de PlayingState.java
@Override
public void tick() {
    super.tick();
    TutorialManager.getInstance().update();
    // ...
}
```

### Passo 3: Criar Tutoriais no JSON e Disparar Eventos

Agora, vamos ver exemplos práticos de como criar diferentes tipos de tutoriais.

#### Exemplo A: Tutorial de Movimento (Gatilho: Evento, Conclusão: Tecla)

Este tutorial aparece no início do jogo e desaparece assim que o jogador se move.

**JSON:**

```json
{
  "id": "MOVE_TUTORIAL",
  "text": "Use W, A, S, D para se mover.",
  "trigger": { "type": "EVENT", "eventName": "GAME_STARTED" },
  "completion": { "type": "KEY_PRESS", "actions": ["MOVE_UP", "MOVE_DOWN", "MOVE_LEFT", "MOVE_RIGHT"] }
}
```

**Código de Gatilho (no final do construtor de PlayingState.java):**

```java
// (Supondo que você adicionou GAME_STARTED ao seu enum GameEvent)
EventManager.getInstance().trigger(GameEvent.GAME_STARTED, null);
```

O `TutorialManager` irá ouvir o evento `GAME_STARTED`, ativar o tutorial, e depois o seu próprio método `update()` irá detectar o pressionar das teclas de movimento para o completar.

#### Exemplo B: Tutorial de Interação (Gatilho: Proximidade, Conclusão: Evento)

Este tutorial aparece quando o jogador se aproxima de um item interativo e desaparece quando o item é apanhado.

1. O Objeto no Tiled: Crie um objeto "Baú" no Tiled. Dê a ele um `InteractionComponent` com uma zona do tipo "LOOT_CHEST".

2. O JSON do Tutorial:

```json
{
  "id": "OPEN_CHEST_TUTORIAL",
  "text": "Pressione [E] para abrir o baú!",
  "trigger": {
    "type": "ZONE_ENTER",
    "zoneType": "LOOT_CHEST"
  },
  "completion": {
    "type": "EVENT",
    "eventName": "CHEST_OPENED"
  }
}
```

3. A Lógica no Jogo: O gatilho (`ZONE_ENTER`) é automático. Para a conclusão, a sua lógica de interação com o baú precisa de disparar o evento.

```java
// Em PlayingState.java -> tick(), dentro da verificação de input "INTERACT"

if (this.interactableObjectInRange instanceof Bau) {
    Bau bau = (Bau) this.interactableObjectInRange;
    bau.abrir(this.player); // O método abrir() do baú faz a sua lógica

    // Dispara o evento para completar o tutorial
    EventManager.getInstance().trigger(GameEvent.CHEST_OPENED, null);
}
```

O `TutorialManager` precisa ser atualizado para ouvir os eventos de zona. Certifique-se de que a sua classe `TutorialManager` está a ouvir os eventos `TARGET_ENTERED_ZONE` para que este gatilho funcione.

#### Exemplo C: Dica Temporária (Gatilho: Evento, Conclusão: Duração)

Este tutorial aparece quando um inimigo específico aparece e desaparece após 5 segundos.

**JSON:**

```json
{
  "id": "WEAK_SPOT_TUTORIAL",
  "text": "Este inimigo é fraco contra fogo!",
  "trigger": {
    "type": "EVENT",
    "eventName": "FIRE_SKELETON_SPAWNED"
  },
  "completion": {
    "type": "DURATION",
    "ticks": 300
  }
}
```

**Código de Gatilho (onde o inimigo é criado):**

```java
// ...código que cria o FireSkeleton...
EventManager.getInstance().trigger(GameEvent.FIRE_SKELETON_SPAWNED, null);
```

O `TutorialManager` irá mostrar a dica e o seu próprio método `update()` irá contar os ticks e escondê-la automaticamente.

---
[⬅️ Voltar para o Guias Avançados](./README.md)