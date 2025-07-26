# JD Studio Game Engine

Um motor de jogo 2D simples, modular e orientado a objetos, construído em Java puro, utilizando AWT e Swing para renderização. Este projeto serve como uma base sólida para a criação de jogos 2D, especialmente aqueles baseados em tiles, e inclui um jogo de exemplo completo para demonstrar seus recursos.

## Como Começar Rapidamente (Usando o .jar)

A maneira mais fácil de usar este motor em seu próprio projeto é importando o arquivo `engine.jar` pré-compilado.

1.  Encontre o arquivo no diretório: `Game_engine/bin/lib/engine.jar`.
2.  Adicione este `.jar` ao *build path* (caminho de compilação) do seu projeto Java na sua IDE de preferência (Eclipse, IntelliJ IDEA, etc.).
3.  Agora você pode importar e usar todas as classes do motor (como `GameState`, `GameObject`, `InputManager`) para construir seu próprio jogo.

## Principais Funcionalidades do Motor

  * **Game Loop de Timestep Fixo**: Garante que a lógica do jogo execute a uma taxa constante (padrão de 60 UPS) para um comportamento previsível, independentemente do FPS.
  * **Gerenciamento de Estado**: Arquitetura baseada em uma máquina de estados (`GameState`) que permite separar de forma limpa a lógica do menu, das fases, da tela de game over, etc.
  * **Renderização com BufferStrategy**: Utiliza double/triple buffering para uma renderização suave e sem *flickering*.
  * **Câmera Estática**: Uma câmera global que permite criar mundos maiores que a tela.
  * **Mundo Baseado em Tiles**: Sistema para criar mundos com uma grade de tiles (`World`, `Tile`), incluindo suporte para tiles sólidos.
  * **Sistema de Input Simples**: Gerenciador de input (`InputManager`) com suporte para "tecla pressionada" e "tecla recém-pressionada".
  * **Gerenciamento de Assets**: Carrega e armazena em cache sprites e spritesheets (`AssetManager`, `Sprite`, `Spritesheet`) para evitar o carregamento duplicado de recursos.
  * **Detecção de Colisão AABB**: Funções para verificar colisões entre `GameObject` e entre `GameObject` e tiles sólidos.
  * **Sistema de UI Básico**: Estrutura para elementos de interface, como texto estático e dinâmico (`UIElement`, `UIText`).
  * **Modo de Debug**: Uma flag global (`Engine.isDebug`) que permite visualizar informações de debug, como as máscaras de colisão.

## Arquitetura do Motor

O código está organizado em pacotes que separam as diferentes responsabilidades:

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

### Pré-requisitos

  * **JDK 11** ou superior.
  * Uma IDE Java de sua preferência (Eclipse, IntelliJ IDEA, VS Code com extensões Java).

  -----

## Jogo de Exemplo: Dungeon Crawler

Para ver o motor em ação, explore o jogo de exemplo incluído. Nele, você controla um personagem em um mapa, com o objetivo de navegar, desviar de inimigos e coletar itens.

### Funcionalidades Demonstradas no Jogo

  * **Carregamento de Nível por Imagem**: O layout completo do mapa (`paredes`, `inimigos`, `itens` e a `posição inicial do jogador`) é carregado a partir dos pixels de um único arquivo de imagem (`level1.png`).
  * **Controle de Personagem e Colisão**: O `Player` pode se mover pelo mapa, e a colisão com tiles sólidos é gerenciada pelo motor.
  * **Câmera Dinâmica**: A câmera segue o `Player` pelo mundo.
  * **UI Dinâmica**: A interface exibe a vida do jogador em tempo real.

### Como Jogar

  * **Movimento**: Use as teclas **W, A, S, D** ou as **Setas Direcionais** para mover o personagem.
  * **Debug**: Pressione **F9** para visualizar as caixas de colisão dos tiles sólidos.

### Design de Nível via Imagem (`level1.png`)

O design do nível é controlado pelas cores dos pixels na imagem `level1.png`. Cada cor corresponde a um tipo de tile ou objeto:

| Cor | Código Hex | Objeto Gerado |
| :--- | :--- | :--- |
| **Branco** | `#FFFFFF` | Parede (`WallTile`) |
| **Azul** | `#0026FF` | Posição inicial do Jogador (`Player`) |
| **Vermelho** | `#FF0000` | Inimigo (`Enemy`) |
| **Laranja** | `#FF6A00` | Arma (`Weapon`) |
| **Rosa** | `#FFFF7F7F` | Pacote de Vida (`Lifepack`) |
| **Amarelo** | `#FFFFD800` | Munição (`Bullet`) |
| **Preto** | `#000000` | Chão (`FloorTile`) - ou qualquer outra cor não listada |

-----

## Guia para Desenvolvedores

Esta seção é para quem deseja modificar o motor ou o jogo de exemplo diretamente a partir do código-fonte.

### Pré-requisitos

  * **JDK 11** ou superior.
  * Uma IDE Java (Eclipse, IntelliJ IDEA, VS Code).

### Configuração (A partir do Código-Fonte)

1.  Clone este repositório.
2.  Abra o projeto na sua IDE.
3.  Certifique-se de que a pasta contendo os recursos (`level1.png`, `spritesheet.png`) esteja no *Classpath* do projeto.
4.  O ponto de entrada para executar o jogo de exemplo é o método `main` na classe `com.JDStudio.Game.Main`.

### Tutorial: Criando um Novo Jogo do Zero

1.  **Crie um `GameState`**: Estenda a classe `GameState` para seu nível.
    ```java
    public class LevelState extends GameState { /*...*/ }
    ```
2.  **Crie um `GameObject`**: Estenda `GameObject` para seu jogador ou inimigos.
    ```java
    public class Player extends GameObject {
        public Player(double x, double y) { super(x, y, 16, 16, null); }
        @Override public void tick() { /* Lógica de movimento */ }
    }
    ```
3.  **Junte Tudo**: Adicione seus objetos ao `GameState` e defina-o como o estado inicial no `main` da sua aplicação.
    ```java
    // No construtor de LevelState.java
    this.addGameObject(new Player(50, 50));

    // No método main
    Engine.setGameState(new LevelState());
    ```

## Próximos Passos (Roadmap)

  * [ ] **Motor de Áudio**: Para tocar músicas e efeitos sonoros.
  * [ ] **Sistema de Animação**: Para animar os sprites.
  * [ ] **Física**: Implementar gravidade e momento.
  * [ ] **Câmera Dinâmica**: Mais opções como zoom e transições suaves.

## Autor

  * **JD Studio**

## Licença

Este projeto está sob a licença MIT.