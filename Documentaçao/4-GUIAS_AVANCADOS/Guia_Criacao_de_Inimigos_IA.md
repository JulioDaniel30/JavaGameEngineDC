# Guia Avançado: Criando Inimigos com IA

Este guia detalha o sistema de Inteligência Artificial (IA) da JDStudio Engine, focado na classe `Enemy`. A arquitetura foi projetada para ser **orientada a dados** e **reativa a eventos**, permitindo que você crie uma grande variedade de comportamentos de inimigos diretamente do editor de mapas Tiled, com o mínimo de programação.

## 1. Configuração no Tiled (O "Corpo" do Inimigo)

Tudo começa no Tiled Editor. Cada inimigo no seu jogo é um objeto numa camada de objetos. A sua classe `Enemy.java` é capaz de ler uma série de **Propriedades Customizadas** para definir o seu comportamento.

1. **Crie o Objeto**: Numa camada de objetos, crie um objeto retangular para representar o seu inimigo.
2. **Defina a Classe**: No painel de propriedades do objeto, defina a sua **Classe** (ou "Type") como `Enemy`. É assim que o `PlayingState` saberá que deve criar uma instância desta classe.
3. **Adicione Propriedades Customizadas**: Adicione as seguintes propriedades para moldar o comportamento do inimigo:

| Propriedade              | Tipo    | Descrição                                                                                                  | Exemplo       |
| ------------------------- | ------- | ---------------------------------------------------------------------------------------------------------- | ------------- |
| `name`                    | `string`  | O nome único do inimigo. Essencial para rotas de patrulha e para o sistema de save/load.                   | `Goblin_Guard_1`  |
| `speed`                   | `float`   | A velocidade de movimento do inimigo em pixels por tick.                                                   | `0.8`         |
| `life` / `maxLife`        | `int`     | A quantidade de vida do inimigo.                                                                           | `50`          |
| `visionRadius`            | `int`     | **(Opcional)** O raio da zona de "AGGRO". Se o jogador entrar nesta área, o inimigo começa a persegui-lo.    | `80`          |
| `attackRadius`            | `int`     | O raio da zona de "ATTACK". Se o jogador entrar nesta área, o inimigo para de perseguir e começa a atacar. | `16`          |
| `attackSpeed`             | `float`   | O tempo (em ticks) entre cada ataque do inimigo. `60` equivale a um ataque por segundo (a 60 FPS).        | `90`          |
| `patrolArrivalThreshold`  | `float`   | A que distância do ponto de patrulha o inimigo o considera "alcançado" e avança para o próximo.          | `8.0`         |
| `useAStar`                | `bool`    | Se `true`, o inimigo usará pathfinding A* para desviar de obstáculos ao perseguir o jogador.              | `true`        |

### Criando Rotas de Patrulha

Para fazer um inimigo patrulhar, use a ferramenta **"Inserir Polilinha"** no Tiled para desenhar um caminho. Em seguida, dê a este objeto de polilinha um **Nome** que corresponda exatamente ao **Nome** do inimigo que deve seguir essa rota (ex: `Goblin_Guard_1`). O `PlayingState` irá ligar automaticamente o caminho ao inimigo durante o carregamento do mapa.

---

## 2. A Arquitetura do Inimigo (O "Cérebro")

A classe `Enemy.java` é um `Character` autocontido que combina vários sistemas da engine para funcionar.

### A Máquina de Estados Finitos (FSM)

O comportamento do inimigo é controlado por uma FSM simples com quatro estados:

* **`IDLE`**: O inimigo está parado e não tem alvo.
* **`PATROLLING`**: O inimigo está a seguir a sua rota de patrulha, movendo-se de ponto em ponto.
* **`CHASING`**: O inimigo detetou o jogador e está a persegui-lo ativamente.
* **`ATTACKING`**: O inimigo está suficientemente perto do jogador para parar de se mover e iniciar a sua rotina de ataque.

### O Sistema Sensorial (`InteractionComponent`)

Em vez de calcular distâncias a cada frame, o inimigo usa o `InteractionComponent` para detectar o jogador. No método `initialize`, as propriedades `visionRadius` e `attackRadius` do Tiled são usadas para criar duas `InteractionZone`s:

1. **Zona "AGGRO"**: Um círculo grande com o raio de `visionRadius`.
2. **Zona "ATTACK"**: Um círculo menor e interno com o raio de `attackRadius`.

### O Sistema Nervoso (EventListeners)

As **transições entre os estados** da FSM não acontecem no `tick()`. Elas são acionadas por **eventos**. A própria classe `Enemy` inscreve-se nos eventos `TARGET_ENTERED_ZONE` e `TARGET_EXITED_ZONE` para reagir às suas próprias zonas.

* **Quando o jogador entra na zona "AGGRO"**: O `EventListener` muda o estado do inimigo para `CHASING` e define o jogador como o alvo do `AIMovementComponent`.
* **Quando o jogador entra na zona "ATTACK"**: O `EventListener` muda o estado para `ATTACKING` e remove o alvo do `AIMovementComponent` para que o inimigo pare de se mover.
* **Quando o jogador sai da zona "ATTACK"**: O `EventListener` verifica se o inimigo tem uma zona de "AGGRO". Se tiver, volta para o estado `CHASING`. Se não, volta para `PATROLLING`/`IDLE`.
* **Quando o jogador sai da zona "AGGRO"**: O `EventListener` muda o estado de volta para `PATROLLING` (se tiver um caminho) ou `IDLE` (se não tiver).

### A Execução da Ação (`tick()`)

Com a lógica de transição entregue aos eventos, o método `tick()` do inimigo torna-se muito mais simples. A sua única responsabilidade é **executar** o comportamento do estado atual.

```java
// Em Enemy.java
@Override
public void tick() {
    // ...
    // Pede ao InteractionComponent para verificar as zonas e disparar eventos.
    interaction.checkInteractions(Collections.singletonList(player));

    // O 'switch' agora apenas EXECUTA o comportamento do estado atual.
    switch (currentState) {
        case PATROLLING:
            // Comportamento: Seguir o caminho de patrulha.
            pathComponent.update();
            Point targetPoint = pathComponent.getTargetPosition();
            if (targetPoint != null) aiMovement.setTarget(targetPoint.x, targetPoint.y);
            break;
        case ATTACKING:
            // Comportamento: Executar a lógica de ataque.
            if (attackCooldown <= 0) {
                player.takeDamage(10);
                attackCooldown = attackSpeed;
            }
            break;
        // Os outros estados não precisam de lógica contínua no tick,
        // pois o seu comportamento (perseguir ou parar) já foi definido pelo EventListener.
    }
    // ...
}
```

---

## 3. Tipos de Comportamento (Receitas de Tiled)

Graças a esta arquitetura, você pode criar diferentes tipos de inimigos apenas mudando as suas propriedades no Tiled, sem escrever uma única linha de código nova.

  * **Inimigo Patrulhador e Agressivo**:
      * Defina um `visionRadius` > 0.
      * Crie uma polilinha de patrulha com o mesmo `name` do inimigo.
      * *Comportamento*: Patrulha → Persegue → Ataca.

  * **Inimigo Sentinela Agressivo**:
      * Defina um `visionRadius` > 0.
      * **Não** crie uma rota de patrulha.
      * *Comportamento*: Fica parado → Persegue → Ataca.

  * **Inimigo "Torre" (Apenas Ataca)**:
      * Defina `visionRadius` como `0` ou simplesmente não adicione a propriedade.
      * Defina um `attackRadius` > 0.
      * *Comportamento*: Fica parado → Ataca (quando o jogador se aproxima muito) → Fica parado.

  * **Inimigo Passivo**:
      * Defina `visionRadius` e `attackRadius` como `0`.
      * *Comportamento*: Fica parado ou patrulha, mas nunca reage ao jogador.

Este sistema data-driven e reativo a eventos fornece uma base poderosa e flexível para a IA dos seus jogos.


---
 
[⬅️ Voltar para o Guias Avançados](./README.md)