# Pacote `com.JDStudio.Engine.Graphics.UI`

Este pacote contém um sistema flexível para criar e gerenciar elementos de Interface de Usuário (UI).

## Resumo das Classes

### `UIElement.java`

Uma classe `abstract` que serve como a base para todos os componentes de UI (botões, painéis, textos, etc.).
- **Estrutura Base:** Define propriedades comuns a todos os elementos, como posição (`x`, `y`), dimensões e um booleano `visible`.
- **Contrato:** Exige que todas as subclasses implementem o método `render(Graphics g)`.

### `UIManager.java`

Um contêiner que gerencia uma coleção de `UIElement`s.
- **Propósito:** Simplificar o gerenciamento da UI. Em vez de chamar o `render()` de cada elemento individualmente, você adiciona todos a um `UIManager` e chama apenas o `render()` do gerenciador, que por sua vez renderiza todos os elementos visíveis.

### `UIText.java`

Uma implementação concreta de `UIElement` para exibir texto na tela.
- **Flexibilidade:**
    - **Texto Estático:** Pode exibir uma string de texto fixa.
    - **Texto Dinâmico:** Sua principal característica é a capacidade de aceitar um `Supplier<String>`. Isso permite que o texto seja atualizado a cada frame, obtendo seu valor de uma função. É perfeito para exibir informações que mudam constantemente, como pontuação, vida do jogador ou um contador de FPS.