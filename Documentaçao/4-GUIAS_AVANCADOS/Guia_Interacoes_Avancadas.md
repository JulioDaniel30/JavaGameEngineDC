# Guia Avançado: Interações com Múltiplas Zonas

Este guia detalha como usar o `InteractionComponent` para criar `GameObject`s com múltiplas áreas de interação e diferentes formatos geométricos. Isto permite criar comportamentos de IA mais complexos e interações com o mundo mais ricas, cobrindo tanto interações automáticas quanto manuais.

## O Conceito

O sistema de interação é baseado em:

1.  **`InteractionZone`**: Um objeto que representa uma única área, definida por um **formato** (círculo ou retângulo) e um **tipo** (uma `String` como "AGGRO" ou "DIALOGUE").
2.  **`InteractionComponent`**: Um componente que você adiciona a um `GameObject` para gerir uma **lista de `InteractionZone`s**. Ele dispara **eventos** (`TARGET_ENTERED_ZONE`, `TARGET_EXITED_ZONE`) quando um alvo (como o jogador) entra ou sai de uma das suas zonas.

Existem dois padrões principais para usar este sistema:

---

### Padrão 1: Interação Automática (Baseada em Proximidade)

**Uso Ideal**: Zonas de aggro de inimigos, áreas que causam dano contínuo (lava), gatilhos que iniciam cutscenes automaticamente.

Neste padrão, a lógica é executada **imediatamente** quando o evento de entrada/saída acontece.

#### Exemplo: Inimigo com Raio de Perseguição

1.  **Configurar o Inimigo**: Adicione um `InteractionComponent` com uma zona do tipo "AGGRO".
    ```java
    // Na classe Enemy.java
    interaction = new InteractionComponent();
    interaction.addZone(new InteractionZone(this, "AGGRO", 80.0)); // Raio de 80 pixels
    //OU 
    //interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_AGGRO, 80.0));
    this.addComponent(interaction);
    ```

2.  **Chamar a Verificação**: No `tick()` do inimigo, chame `interaction.checkInteractions(...)`.

3.  **Reagir aos Eventos**: No `PlayingState`, crie `EventListener`s que reagem diretamente.
    ```java
    // Em PlayingState.java -> setupEventListeners()
    
    // Quando o jogador ENTRA na zona "AGGRO"
    EventManager.getInstance().subscribe(GameEvent.TARGET_ENTERED_ZONE, (data) -> {
        InteractionEventData event = (InteractionEventData) data;
        if (event.zoneOwner() instanceof Enemy && event.zone().type.equals(InteractionZone.TYPE_DIALOGUE)) {
            // AÇÃO IMEDIATA: Inimigo começa a perseguir o jogador
            Enemy enemy = (Enemy) event.zoneOwner();
            enemy.getComponent(AIMovementComponent.class).setTarget(event.target());
        }
    });

    // Quando o jogador SAI da zona "AGGRO"
    EventManager.getInstance().subscribe(GameEvent.TARGET_EXITED_ZONE, (data) -> {
        InteractionEventData event = (InteractionEventData) data;
        if (event.zoneOwner() instanceof Enemy && event.zone().type.equals(InteractionZone.TYPE_DIALOGUE)) {
            // AÇÃO IMEDIATA: Inimigo para de perseguir
            Enemy enemy = (Enemy) event.zoneOwner();
            enemy.getComponent(AIMovementComponent.class).setTarget(null);
        }
    });
    ```

---

### Padrão 2: Interação Manual (Proximidade + Tecla de Ação)

**Uso Ideal**: Falar com NPCs, abrir baús, ler placas, apanhar itens que requerem confirmação.

Neste padrão, os eventos de zona são usados apenas para **manter o estado** de quem está próximo. A ação só é executada quando o jogador pressiona a tecla "INTERACT".

#### Passo 1: Declarar uma Variável de Estado no `PlayingState`

Esta variável irá "lembrar" qual objeto está atualmente ao alcance.

```java
// Em PlayingState.java
private GameObject interactableObjectInRange = null;
```

#### Passo 2: Usar os Eventos para Gerir o Estado

Modifique os seus `EventListener`s. A responsabilidade deles agora é apenas atualizar a variável `interactableObjectInRange`.

```java
// Em PlayingState.java -> setupEventListeners()

EventManager.getInstance().subscribe(GameEvent.TARGET_ENTERED_ZONE, (data) -> {
    InteractionEventData event = (InteractionEventData) data;
    // Verifica se a zona é de um tipo interativo manual (ex: "DIALOGUE")
    if (event.zone().type.equals("DIALOGUE")) {
        // Apenas guarda a referência do objeto. Nenhuma ação é executada.
        this.interactableObjectInRange = event.zoneOwner();
        // Opcional: Mostrar uma dica na UI, como um "[E]" a piscar.
    }
});

EventManager.getInstance().subscribe(GameEvent.TARGET_EXITED_ZONE, (data) -> {
    InteractionEventData event = (InteractionEventData) data;
    // Se estamos a sair da zona do objeto guardado, limpamos a referência.
    if (event.zoneOwner() == this.interactableObjectInRange) {
        this.interactableObjectInRange = null;
        // Opcional: Esconder a dica da UI.
    }
});
```

#### Passo 3: Executar a Ação no `tick()` com a Tecla "INTERACT"

Agora, o seu loop de input no `tick()` do `PlayingState` fica limpo e eficiente. Ele substitui o antigo loop `for` que percorria todos os objetos.

```java
// Em PlayingState.java -> tick()

if (InputManager.isActionJustPressed("INTERACT")) {
    
    // Se a nossa variável de estado não for nula, o jogador está perto de algo E pressionou a tecla.
    if (this.interactableObjectInRange != null) {
        
        // Verificamos que tipo de objeto é para decidir o que fazer.
        
        if (this.interactableObjectInRange instanceof Ferreiro) {
            Ferreiro ferreiro = (Ferreiro) this.interactableObjectInRange;
            
            // Apenas aqui, finalmente, executamos a lógica do diálogo.
            Dialogue dialogue = ferreiro.getDialogue(); 
            if (dialogue != null && !DialogueManager.getInstance().isActive()) {
                DialogueManager.getInstance().startDialogue(dialogue, ferreiro, this.player);
            }
        }
        // else if (this.interactableObjectInRange instanceof Bau) { ... }
    }
}
```

### Conclusão

Ao combinar estes dois padrões, você pode criar praticamente qualquer tipo de interação:

  * Use a **Interação Automática** para eventos de ambiente e IA reativa.
  * Use a **Interação Manual** para ações que devem ser iniciadas pelo jogador, resultando num código mais performante, organizado e fácil de manter.

---
 
[⬅️ Voltar para o Guias Avançados](./README.md)