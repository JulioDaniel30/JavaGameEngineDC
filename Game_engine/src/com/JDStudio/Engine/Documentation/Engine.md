# Pacote `com.JDStudio.Engine`

Este é o pacote central da engine, contendo as classes fundamentais que gerenciam o ciclo de vida do jogo, a janela e a transição entre os diferentes estados.

## Resumo das Classes

### `Engine.java`

É o coração da aplicação. Esta classe é responsável por:
- **Inicialização da Janela:** Cria e configura o `JFrame` principal do jogo.
- **Game Loop:** Implementa a interface `Runnable` para criar o loop principal que controla as atualizações de lógica (`tick`) e renderização (`render`) em uma taxa de quadros fixa (padrão de 60 FPS).
- **Gerenciamento de Estado:** Mantém uma referência ao `GameState` atual, delegando a ele toda a lógica e renderização.
- **Ponto de Entrada:** Contém o método `main`, que inicia a engine.
- **Componentes Globais:** Mantém instâncias estáticas de componentes essenciais, como a `Camera`.

### `GameState.java`

Uma classe abstrata que serve como base para todos os "estados" do jogo, seguindo o padrão de projeto **State**.
- **Propósito:** Permite que o comportamento do jogo mude drasticamente (ex: de um Menu para uma Fase) de forma encapsulada.
- **Funcionalidades:**
    - Cada estado gerencia sua própria lista de `GameObject`s.
    - Subclasses devem implementar os métodos `tick()` e `render(Graphics g)` para definir a lógica e a aparência específicas daquele estado.

## Como Funciona

A classe `Engine` cria a janela e inicia o game loop. Em qualquer momento, a engine tem um `currentGameState` ativo. A cada iteração do loop, a engine chama `currentGameState.tick()` e `currentGameState.render()`. Para mudar de uma tela para outra (ex: sair do menu e iniciar o jogo), basta chamar `Engine.setGameState(new LevelState())`.