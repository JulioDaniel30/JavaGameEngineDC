# JD Studio Game Engine

Um motor de jogo 2D simples, modular e orientado a objetos, construído em Java puro, utilizando AWT e Swing para renderização. Este projeto serve como uma base sólida para a criação de jogos 2D, especialmente aqueles baseados em tiles.

## Principais Funcionalidades

  * **Game Loop de Timestep Fixo**: Garante que a lógica do jogo execute a uma taxa constante (padrão de 60 UPS) para um comportamento previsível, independentemente do FPS.
  * **Gerenciamento de Estado**: Arquitetura baseada em uma máquina de estados (`GameState`) que permite separar de forma limpa a lógica do menu, das fases, da tela de game over, etc.
  * **Renderização com BufferStrategy**: Utiliza double/triple buffering para uma renderização suave e sem *flickering*.
  * **Câmera Estática**: Uma câmera global que permite criar mundos maiores que a tela.
  * **Mundo Baseado em Tiles**: Sistema para criar mundos com uma grade de tiles (`World`, `Tile`), incluindo suporte para tiles sólidos.
  * **Sistema de Input Simples**: Gerenciador de input (`InputManager`) com suporte para "tecla pressionada" e "tecla recém-pressionada".
  * **Gerenciamento de Assets**: Carrega e armazena em cache sprites e spritesheets (`AssetManager`, `Sprite`, `Spritesheet`) para evitar o carregamento duplicado de recursos.
  * **Detecção de Colisão AABB**: Funções para verificar colisões entre `GameObject` e entre `GameObject` e tiles sólidos.
  * **Sistema de UI Básico**: Estrutura para elementos de interface, como texto estático e dinâmico (`UIElement`, `UIText`).
  * **Modo de Debug**: Uma flag global (`Engine.isDebug`) que permite visualizar informações de debug, como as máscaras de colisão de objetos e tiles.

## Estrutura do Projeto

O código está organizado em pacotes que separam as diferentes responsabilidades do motor:

  * `com.JDStudio.Engine`
      * `Engine.java`: O coração do motor. Contém o game loop, inicializa a janela e gerencia o estado do jogo.
      * `GameState.java`: Classe abstrata para os diferentes estados do jogo.
  * `com.JDStudio.Engine.Graphics`
      * `AssetManager.java`: Gerencia o carregamento e cache de sprites.
      * `Sprite.java`: Representa uma única imagem/sprite.
      * `Spritesheet.java`: Ferramenta para extrair sprites de uma folha de sprites.
  * `com.JDStudio.Engine.Graphics.UI`
      * `UIElement.java`: Classe base para todos os elementos de UI.
      * `UIText.java`: Elemento de UI para renderizar texto.
  * `com.JDStudio.Engine.Input`
      * `InputManager.java`: Singleton que gerencia todo o input do teclado.
  * `com.JDStudio.Engine.Object`
      * `GameObject.java`: Classe base para todos os objetos dinâmicos do jogo (jogador, inimigos, etc.).
  * `com.JDStudio.Engine.World`
      * `World.java`: Gerencia a grade de tiles e a colisão com o cenário.
      * `Tile.java`: Representa um único tile no mapa.
      * `Camera.java`: Classe estática que controla a visão do mundo.

## Como Começar

### Pré-requisitos

  * **JDK 11** ou superior.
  * Uma IDE Java de sua preferência (Eclipse, IntelliJ IDEA, VS Code com extensões Java).

### Configuração

1.  Clone este repositório:
    ```bash
    git clone [URL_DO_SEU_REPOSITORIO]
    ```
2.  Abra o projeto na sua IDE. Ela deve reconhecer a estrutura do projeto automaticamente.
3.  O ponto de entrada da aplicação é o método `main` na classe `com.JDStudio.Engine.Engine`.

### Executando

Para rodar o motor, basta executar o arquivo `Engine.java`. Como ainda não há um `GameState` concreto, ele iniciará com uma tela preta. Veja a próxima seção para criar seu primeiro nível.

## Como Usar o Motor

Aqui está um guia rápido para criar uma cena básica.

### 1\. Crie um Estado de Jogo

Crie uma nova classe que estende `GameState`. Esta será a sua fase principal.

```java
// Em um novo arquivo, ex: LevelState.java
import com.JDStudio.Engine.GameState;
import com.JDStudio.Engine.World.World;

public class LevelState extends GameState {
    
    private World world;

    public LevelState() {
        super(); // Chama o construtor de GameState
        
        // Cria um mundo de 100x100 tiles
        world = new World(100, 100);
        // TODO: Carregar tiles e objetos aqui
    }

    @Override
    public void tick() {
        // Atualiza todos os GameObjects na lista
        for (GameObject go : gameObjects) {
            go.tick();
        }
    }

    @Override
    public void render(Graphics g) {
        world.render(g); // Renderiza o mundo primeiro
        
        // Renderiza todos os GameObjects
        for (GameObject go : gameObjects) {
            go.render(g);
        }
    }
}
```

### 2\. Crie um GameObject (Jogador)

Crie uma classe para o seu jogador que estende `GameObject`.

```java
// Em um novo arquivo, ex: Player.java
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Input.InputManager;
import java.awt.event.KeyEvent;

public class Player extends GameObject {

    public Player(double x, double y) {
        // Chama o construtor pai com a posição, tamanho e sprite
        super(x, y, 16, 16, null); // Passe o sprite do jogador aqui
    }

    @Override
    public void tick() {
        // Lógica de movimento simples
        if (InputManager.isKeyPressed(KeyEvent.VK_W)) {
            this.y--;
        }
        if (InputManager.isKeyPressed(KeyEvent.VK_S)) {
            this.y++;
        }
        if (InputManager.isKeyPressed(KeyEvent.VK_A)) {
            this.x--;
        }
        if (InputManager.isKeyPressed(KeyEvent.VK_D)) {
            this.x++;
        }
    }
}
```

### 3\. Junte Tudo

Modifique o método `main` na classe `Engine` para iniciar com o seu novo `LevelState`.

```java
// Dentro da classe Engine.java

public static void main(String[] args) {
    Engine engine = new Engine();
    
    // Define o estado inicial do jogo
    Engine.setGameState(new LevelState()); // <-- SUA MODIFICAÇÃO
    
    engine.start();
}
```

E no construtor de `LevelState`, adicione o jogador.

```java
// Dentro do construtor de LevelState.java

public LevelState() {
    super();
    world = new World(100, 100);
    
    // Adiciona o jogador ao estado de jogo
    Player player = new Player(50, 50);
    this.addGameObject(player);
}
```

Agora, ao executar o motor, seu `LevelState` será carregado e você poderá controlar o `Player`.

## Próximos Passos (Roadmap)

Este motor é uma base. Aqui estão algumas ideias para futuras melhorias:

  * [ ] **Motor de Áudio**: Para tocar músicas de fundo e efeitos sonoros.
  * [ ] **Sistema de Animação**: Para animar os sprites dos `GameObject`.
  * [ ] **Carregamento de Níveis**: Carregar o layout do `World` a partir de arquivos (ex: JSON, TMX do Tiled Editor).
  * [ ] **Física**: Implementar um sistema de física mais robusto com gravidade e momento.
  * [ ] **Câmera Dinâmica**: Fazer a câmera seguir o jogador ou outro objeto.

## Autor

  * **JD Studio**

## Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.