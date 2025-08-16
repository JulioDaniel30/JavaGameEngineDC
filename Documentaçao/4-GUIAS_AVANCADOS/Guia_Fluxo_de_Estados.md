# Guia Avançado: Gerindo o Fluxo de Estados (Menus e Telas)

Este guia detalha como estruturar a navegação do seu jogo utilizando a arquitetura de máquina de estados da JDStudio Engine. Vamos cobrir a implementação de um fluxo completo, incluindo menu principal, tela de carregamento, menu de pausa, opções e tela de game over.

## O Conceito: A Pilha de Estados

A engine gerencia os `GameState`s em uma **pilha (stack)**. Isso é fundamental para entender como os menus funcionam:

- `Engine.setGameState(new MainMenuState())`: Limpa a pilha inteira e coloca o `MainMenuState` como o único estado. Usado para mudanças de contexto grandes (ex: ir do jogo para o menu principal).
- `Engine.pushState(new PauseState())`: Adiciona o `PauseState` **por cima** do estado atual (ex: `PlayingState`). O `PlayingState` fica "pausado" por baixo.
- `Engine.popState()`: Remove o estado do topo da pilha, retornando ao estado que estava embaixo. É assim que se "fecha" um menu de pausa ou de opções.

---

### 1. Tela de Carregamento (`LoadingState`)

**Propósito:** Carregar todos os assets pesados (spritesheets, sons, músicas) antes do jogo começar.

**Implementação:**
1. Crie uma classe `LoadingState` que herda de `GameState`.
2. No seu `Main.java`, defina como estado inicial: `Engine.setInitialGameState(LoadingState.class)`.
3. No método `tick()` do `LoadingState`, execute toda a lógica de carregamento.
4. Quando terminar, transite para o menu principal: `Engine.transitionToState(new MenuState())`.

```java
public class LoadingState extends GameState {
    private boolean loadingStarted = false;

    @Override
    public void tick() {
        if (!loadingStarted) {
            loadingStarted = true;
            // Carregue seus assets aqui
            Engine.transitionToState(new MenuState());
        }
    }
    // ... render() pode desenhar um texto "Loading..."
}
```

---

### 2. Menu Principal (`MenuState`)

**Propósito:** Permitir que o jogador inicie um novo jogo, acesse opções ou saia.

**Implementação:**
- Crie uma classe `MenuState` que herda de `EngineMenuState`.
- No método `buildUI()`, adicione seus botões.
- "Iniciar": `Engine.transitionToState(new PlayingState())`
- "Opções": `Engine.pushState(new OptionsState())`
- "Sair": `System.exit(0)`

---

### 3. Menu de Pausa (`PauseState`)

**Propósito:** Pausar o jogo e dar opções ao jogador.

**Implementação:**
- Crie uma classe `PauseState` que herda de `EngineMenuState`.
- No `PlayingState`, verifique se a ação "PAUSE" foi pressionada e chame `Engine.pushState(new PauseState())`.
- No `buildUI()` do `PauseState`, adicione botões:
  - "Continuar": `Engine.popState()`
  - "Opções": `Engine.pushState(new OptionsState(this))`
  - "Sair para o Menu": `Engine.setGameState(new MenuState())`
- No método `render()`, desenhe um retângulo semitransparente sobre a tela antes de chamar `super.render(g)` para escurecer o jogo por baixo.

---

### 4. Menu de Opções (`OptionsState`)

**Propósito:** Permitir ajustes como volume.

**Implementação:**
- Crie uma classe `OptionsState` que herda de `EngineMenuState`.
- No `buildUI()`, adicione sliders e botões.
- Botão "Voltar": `Engine.popState()`

---

### 5. Tela de Game Over (`GameOverState`)

**Propósito:** Informar que o jogo terminou e dar opções ao jogador.

**Implementação:**
- Crie uma classe `GameOverState` que herda de `EngineMenuState`.
- No código de jogo, ao detectar a morte do jogador, chame `Engine.transitionToState(new GameOverState())`.
- No `buildUI()` do `GameOverState`, adicione botões:
  - "Tentar Novamente": `Engine.restartCurrentState()` ou `Engine.restartGame()`
  - "Menu Principal": `Engine.setGameState(new MenuState())`

---

### 6. Tela de Inventário (`InventoryState`)

Assim como o menu de pausa, o inventário é uma tela de sobreposição que aparece sobre o jogo.

**Propósito:** Mostrar os itens do jogador e permitir que ele os utilize.

**Implementação:**
1. Crie uma classe `InventoryState` que herda de `EngineMenuState`.
2. No `PlayingState`, no método `tick()`, verifique se a ação "TOGGLE_INVENTORY" (ou outra que você defina) foi pressionada e, se sim, chame `Engine.pushState(new InventoryState())`.
3. No `buildUI()` do `InventoryState`:
    - Obtenha a referência para o jogador (ex: `PlayingState.player`).
    - Obtenha o `InventoryComponent` do jogador.
    - Crie uma instância de `UIInventoryView`, passando o inventário do jogador.
    - Adicione o `UIInventoryView` ao `uiManager`.
4. O `UIInventoryView` já lida com a maior parte da lógica, como navegação e uso de itens.
5. No `tick()` do `InventoryState`, permita fechar a janela com a mesma tecla "TOGGLE_INVENTORY" ou com "ESC", chamando `Engine.popState()`.

```java
public class InventoryState extends EngineMenuState {
    
    public InventoryState() {
        super();
        // O método buildUI() é chamado automaticamente pelo construtor pai.
    }

    @Override
    protected void buildUI() {
        // Pega a referência do inventário do jogador
        InventoryComponent playerInv = PlayingState.player.getComponent(InventoryComponent.class);
        
        if (playerInv != null) {
            UIInventoryView inventoryView = new UIInventoryView(
                20, 40, // Posição x, y
                null,   // Sprite de fundo da janela (opcional)
                ThemeManager.getInstance().get(UISpriteKey.INVENTORY_BUTTON_NORMAL_30_2), // Sprite do slot
                PlayingState.player,
                playerInv.inventory,
                3, 6, 32, 2 // Linhas, colunas, tamanho do slot, espaçamento
            );
            uiManager.addElement(inventoryView);
        }
    }

    @Override
    public void tick() {
        super.tick(); // Atualiza a lógica da UI (o UIInventoryView)
        
        // Permite fechar a janela com a mesma tecla ou com ESC
        if (InputManager.isActionJustPressed("TOGGLE_INVENTORY") || InputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            Engine.popState();
        }
    }
    
    @Override
    public void render(Graphics g) {
        // Desenha um fundo escuro semitransparente
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Engine.getWIDTH(), Engine.getHEIGHT());
        
        super.render(g); // Desenha a UI (o inventário) por cima
    }
}
```

---
[⬅️ Voltar para o Referencia API](./README.md)