# Walkthrough do Jogo de Exemplo

Bem-vindo ao walkthrough do jogo de exemplo da JDStudio Engine. Este documento detalha como o jogo foi construído, servindo como um guia prático para entender como usar os recursos da engine para criar sua própria jogabilidade.

## 1. Inicialização e Estrutura

O jogo começa na classe `Main.java`, que realiza três tarefas cruciais antes de iniciar a engine:

1.  **Carrega Vínculos de Input Customizados**: `InputManager.instance.loadAndMergeBindings("/keybindings.json")` carrega as ações específicas do jogo (como "SHOOT", "PAUSE_GAME") e as funde com os padrões da engine.
2.  **Registra Camadas de Renderização Customizadas**: `RenderManager.getInstance().registerLayer(GameLayers.WATER_EFFECTS)` ensina ao `RenderManager` sobre uma nova camada de renderização, "WATER_EFFECTS", específica para este jogo.
3.  **Inicia a Engine**: Configura a resolução, escala e FPS, e define `MenuState.class` como o ponto de partida.

### Fluxo de Estados de Jogo

O jogo utiliza a máquina de estados da engine da seguinte forma:

-   **`MenuState`**: A tela principal. É um `EngineMenuState` que constrói os botões para Iniciar, Opções e Sair.
-   **`PlayingState`**: O estado principal do jogo. Carrega o mapa e gerencia toda a lógica de jogabilidade.
-   **Estados de Sobreposição (Overlay)**:
    -   `OptionsState`, `PauseState`, `InventoryState` e `GameOverState` são "empilhados" (`pushState`) sobre o `PlayingState` ou `MenuState`. Isso permite pausar o jogo ou abrir menus sem perder o estado anterior, ao qual se retorna com `popState()`.

## 2. Carregando o Mundo (`PlayingState`)

A classe `PlayingState` é o coração do jogo. Ela implementa a interface `IMapLoaderListener` para atuar como uma "fábrica" de objetos, traduzindo os dados do mapa Tiled para entidades vivas no jogo.

### `onTileFound`

Este método cria os tiles específicos do jogo. Ele lê o nome da camada (`layerName`) do Tiled para decidir se cria um `WallTile` (sólido), um `FloorTile` (passável) ou um `LightTile` (que também emite luz).

-   **Destaque (`LightTile`)**: Esta classe customizada é um excelente exemplo de extensão da engine. Em seu construtor, ela não apenas se define como um tile, mas também cria um objeto `Light` e o registra no `LightingManager` da engine, combinando cenário e iluminação.

### `onObjectFound`

Este método é o responsável por popular o mundo com `GameObject`s. Ele usa um `switch` na propriedade "Classe" (ou "Type") do objeto no Tiled para instanciar as classes corretas do jogo:

-   `player_start` -> Cria o `Player`.
-   `enemy` -> Cria um `Enemy`.
-   `door` -> Cria uma `Door` interativa.
-   `NPC` -> Cria um `Ferreiro`.
-   `weapon` -> Cria a `Weapon` (arma).

Para cada objeto criado, o `PlayingState` também pode adicionar **UI de Mundo**, como uma `UIHealthBar` para objetos com `HealthComponent` ou uma `UINameplate` para NPCs.

### `onPathFound`

O jogo usa polilinhas no Tiled para definir rotas de patrulha para os inimigos. Este método armazena esses caminhos, que são posteriormente atribuídos aos `Enemy` correspondentes em `onObjectFound`.

## 3. Lógica de Jogo e Entidades

### O Jogador (`Player.java`)

O `Player` é um `Character` que demonstra o uso pesado do sistema de componentes:
-   **`MovementComponent`**: Adicionado para controle de movimento top-down. O `tick()` do jogador lê o `InputManager` e chama `movement.setDirection()`.
-   **`Animator`**: Carrega suas animações de um JSON do Aseprite e as troca com base na direção do movimento (`animator.play("walk_right")`).
-   **`InventoryComponent`**: Carrega um inventário para o jogador.
-   **`onCollision`**: A lógica de colisão é implementada para interagir com itens como `Lifepack` e `BulletPack`, curando o jogador e destruindo o item.
-   **`takeDamage`**: Sobrescreve o método de `Character` para adicionar um efeito de `camera.shake()` e disparar o evento `GameEvent.PLAYER_TOOK_DAMAGE`.

### A Arma e o Tiro (`Weapon.java`)

A mecânica de tiro é um exemplo perfeito de **design desacoplado usando eventos**:
1.  O `Player` detecta a entrada da ação "SHOOT" e dispara um evento `GameEvent.PLAYER_FIRE`, passando a si mesmo (`this`) como dado do evento. Ele não sabe o que é uma arma nem como atirar.
2.  A `Weapon`, ao ser criada, se inscreve (`subscribe`) neste evento.
3.  Quando o evento é disparado, o listener da `Weapon` é ativado. Ele verifica se a arma está anexada ao `Player` que disparou o evento.
4.  Se sim, a `Weapon` chama seu método `shoot()`, que usa o `ProjectileManager` da engine para criar um projétil.

### A Inteligência Artificial (`Enemy.java`)

O `Enemy` usa uma **Máquina de Estados Finitos (FSM)** para controlar seu comportamento:
-   **Estados**: `IDLE`, `PATROLLING`, `CHASING`, `ATTACKING`.
-   **Componentes**: Usa `AIMovementComponent` para a lógica de movimento.
-   **Transições**: O estado muda com base na distância até o jogador, que é comparada com `visionRadius` e `attackRadius`.
-   **Lógica**:
    -   Em `PATROLLING`, ele segue o caminho definido pelo `PathComponent`.
    -   Em `CHASING`, ele define o `player` como alvo do `AIMovementComponent`.
    -   Em `ATTACKING`, ele para de se mover (`aiMovement.setTarget(null)`) e ataca o jogador.
    -   Ao morrer (`die()`), ele usa o `ParticleManager` para criar uma explosão de partículas.

## 4. HUD e Eventos de UI

O `PlayingState` também gerencia a UI do jogo.
-   **Criação da HUD**: No método `setupUI`, ele cria a `DialogueBox` e os ícones de vida (`UIImage` para os corações) e o texto de munição (`UIText`).
-   **Atualização via Eventos**: Em vez de verificar a vida do jogador a cada frame, o `PlayingState` se inscreve em eventos.
    -   Quando um evento `PLAYER_TOOK_DAMAGE` ou `PLAYER_HEALED` é disparado, o método `updateHealthUI()` é chamado. Isso atualiza os sprites dos corações para `HEART_FULL`, `HEART_HALF` ou `HEART_EMPTY`. Este é um método muito mais eficiente.
-   **Balões de Fala**: O `PlayingState` se inscreve no evento `EngineEvent.CHARACTER_SPOKE`. Quando um `Character` (como um `Enemy`) chama o método `say()`, o `PlayingState` reage criando um `UIChatBubble` e adicionando-o ao `UIManager`, mostrando como o jogo decide a *representação visual* de um evento da engine.

## 5. Salvando e Carregando o Jogo

As classes `Player`, `Enemy` e `Door` implementam a interface `ISavable`.
-   **`saveGame()`**: O `PlayingState` percorre sua lista de `GameObject`s. Se um objeto é `ISavable` e tem um nome, seu estado (`saveState()`) é adicionado a um `JSONArray`, que é salvo em um arquivo pelo `SaveManager`.
-   **`loadGame()`**: Carrega o JSON do arquivo. Para cada objeto salvo, ele procura o `GameObject` com o mesmo nome na lista atual do jogo e chama `loadState()` para restaurar sua posição, vida, etc.