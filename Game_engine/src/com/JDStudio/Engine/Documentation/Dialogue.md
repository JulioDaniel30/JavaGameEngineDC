# Pacote: com.JdStudio.Engine.Dialogue

Este pacote fornece um sistema completo para criar, analisar e gerenciar diálogos interativos baseados em JSON.

## Visão Geral do Sistema

O sistema de diálogo é composto por vários Singletons e classes de dados que trabalham juntos:

1.  **`DialogueParser`**: Lê um arquivo `.json` e o transforma em um objeto `Dialogue`.
2.  **`Dialogue`**: Um contêiner que armazena todos os `DialogueNode`s de uma conversa.
3.  **`DialogueNode`**: Representa um único balão de fala, contendo o nome do personagem, o texto e uma lista de `DialogueChoice`.
4.  **`DialogueChoice`**: Representa uma opção que o jogador pode escolher. Ela leva a um próximo `DialogueNode` e pode opcionalmente acionar uma `DialogueAction`.
5.  **`ActionManager`**: Um Singleton onde o seu jogo registra ações customizadas (ex: "dar_item", "iniciar_missao").
6.  **`DialogueManager`**: O Singleton central que gerencia o estado da conversa ativa. A UI (`DialogueBox`) lê dele para saber o que exibir.
7.  **`DialogueAction`**: Uma interface funcional que define o código a ser executado quando uma ação é acionada por uma escolha.

## Formato do JSON

Para usar o sistema, crie arquivos JSON com a seguinte estrutura:

```json
{
  "startNodeId": 0,
  "nodes": [
    {
      "id": 0,
      "speakerName": "Ferreiro",
      "text": "Olá, aventureiro! Posso ver que sua espada está em mau estado. Gostaria de consertá-la por 10 moedas de ouro?",
      "choices": [
        {
          "text": "Sim, por favor. (Pagar 10g)",
          "nextNodeId": 1,
          "action": "repair_sword"
        },
        {
          "text": "Não, obrigado.",
          "nextNodeId": 2
        }
      ]
    },
    {
      "id": 1,
      "speakerName": "Ferreiro",
      "text": "Prontinho! Afiada como nova. Tome cuidado por aí.",
      "choices": [
        { "text": "Obrigado!", "nextNodeId": -1 }
      ]
    },
    {
      "id": 2,
      "speakerName": "Ferreiro",
      "text": "Como quiser. Se mudar de ideia, sabe onde me encontrar.",
      "choices": [
        { "text": "Até mais.", "nextNodeId": -1 }
      ]
    }
  ]
}
```

-   `startNodeId`: O `id` do nó que inicia a conversa.
-   `nextNodeId: -1`: Sinaliza o fim do diálogo.
-   `action`: (Opcional) A chave da ação a ser executada, que deve ser registrada no `ActionManager`.

## Exemplo de Uso Completo

Este exemplo mostra como registrar uma ação, carregar um diálogo para um NPC e iniciar a conversa.

```java
// --- 1. No início do seu jogo, registre as ações customizadas ---
public void initializeGame() {
    ActionManager.getInstance().registerAction("repair_sword", (player, npc) -> {
        // 'player' e 'npc' são os GameObjects passados pelo DialogueManager
        if (player instanceof Player) {
            Player p = (Player) player;
            if (p.getGold() >= 10) {
                p.removeGold(10);
                p.repairWeapon();
                System.out.println("Ação: Espada do jogador foi reparada!");
            } else {
                // Se não tiver ouro, talvez você queira um diálogo diferente,
                // mas para este exemplo, a ação simplesmente falha.
                System.out.println("Ação: Jogador não tem ouro suficiente.");
            }
        }
    });
}


// --- 2. Crie uma classe de NPC que pode interagir ---
public class Blacksmith extends Character implements Interactable {
    
    private Dialogue dialogue;

    public Blacksmith(double x, double y) {
        super(x, y, 16, 32); // Posição e tamanho
        // Carrega o diálogo para este NPC usando o parser
        this.dialogue = DialogueParser.parseDialogue("/dialogues/blacksmith.json");
    }

    @Override
    public void onInteract(GameObject source) {
        // Quando o jogador interage, inicia o diálogo.
        // 'source' é o jogador que interagiu. 'this' é o próprio ferreiro.
        if (dialogue != null) {
            DialogueManager.getInstance().startDialogue(dialogue, this, source);
        }
    }

    @Override
    public int getInteractionRadius() {
        return 20; // Raio de interação
    }
}


// --- 3. No seu GameState, crie a DialogueBox e chame seu tick ---
public class Level1State extends EnginePlayingState {
    private DialogueBox dialogueBox;

    public Level1State() {
        super();
        
        // ... (criação de player, npc, etc) ...
        addGameObject(new Blacksmith(150, 100));

        // Cria a caixa de diálogo
        this.dialogueBox = new DialogueBox(10, Engine.HEIGHT - 60, Engine.WIDTH - 20, 50);
        // Você pode customizar a caixa com dialogueBox.setColors(...), etc.
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza todos os GameObjects

        // Atualiza a lógica da caixa de diálogo (input do jogador, efeito de texto)
        dialogueBox.tick();

        InputManager.instance.update();
    }

    @Override
    public void render(Graphics g) {
        super.render(g); // Renderiza o mundo e os GameObjects

        // Renderiza a caixa de diálogo por cima de tudo
        dialogueBox.render(g);
    }
}
```