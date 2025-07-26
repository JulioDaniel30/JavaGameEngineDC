## JD Studio Game Engine

Um motor de jogo 2D simples, modular e orientado a objetos, construído em Java puro, utilizando AWT e Swing para renderização. Este projeto serve como uma base sólida para a criação de jogos 2D, especialmente aqueles baseados em tiles, e inclui um jogo de exemplo completo para demonstrar seus recursos.

## Como Começar Rapidamente (Usando o .jar)

A maneira mais fácil de usar este motor em seu próprio projeto é importando o arquivo `engine.jar` pré-compilado.

1.  Encontre o arquivo no diretório: `Game_engine/sourceLib/engine.jar`.
2.  Adicione este `.jar` ao *build path* (caminho de compilação) do seu projeto Java na sua IDE de preferência (Eclipse, IntelliJ IDEA, etc.).
3.  Agora você pode importar e usar todas as classes do motor (como `GameState`, `GameObject`, `InputManager`) para construir seu próprio jogo.

## Principais Funcionalidades do Motor

  * **Game Loop de Timestep Fixo**: Garante que a lógica do jogo execute a uma taxa constante (padrão de 60 UPS) para um comportamento previsível, independentemente do FPS.
  * **Gerenciamento de Estado**: Arquitetura baseada em uma máquina de estados (`GameState`) que permite separar de forma limpa a lógica do menu, das fases, da tela de game over, etc.
  * **Renderização com BufferStrategy**: Utiliza double/triple buffering para uma renderização suave e sem *flickering*.
  * **Câmera Estática**: Uma câmera global (`Camera`) que permite criar mundos maiores que a tela, com funcionalidades para limitar sua posição aos limites do mapa.
  * **Mundo Baseado em Tiles**: Sistema para criar mundos com uma grade de tiles (`World`, `Tile`). É capaz de carregar mapas a partir de arquivos JSON do Tiled e delega a criação de tiles e objetos específicos para um listener (`IMapLoaderListener`).
  * **Sistema de Input Simples**: Gerenciador de input (`InputManager`) com suporte para "tecla pressionada" (`isKeyPressed`) e "tecla recém-pressionada" (`isKeyJustPressed`).
  * **Gerenciamento de Assets**: Carrega e armazena em cache sprites e spritesheets (`AssetManager`, `Sprite`, `Spritesheet`) para evitar o carregamento duplicado de recursos.
  * **Detecção de Colisão AABB**: Funções para verificar colisões entre `GameObject` e entre `GameObject` e tiles sólidos do `World`. Utiliza a técnica de AABB (Axis-Aligned Bounding Box) com máscaras de colisão customizáveis para cada objeto.
  * **Sistema de UI Básico**: Estrutura para elementos de interface, como texto estático e dinâmico (`UIElement`, `UIText`, `UIManager`).
  * **Modo de Debug**: Uma flag global (`Engine.isDebug`) que permite visualizar informações de debug, como as máscaras de colisão de `GameObject` e `Tile`.
  * **Sistema de Áudio**: Uma classe utilitária estática (`Sound`) para gerenciar o carregamento, cache e reprodução de efeitos sonoros (SFX) e músicas de fundo (em loop), com controle de volume independente.
  * **Sistema de Animação**: Inclui classes para gerenciar animações de sprites (`Animation`, `Animator`), permitindo definir sequências de quadros e controlar a velocidade. O `GameObject` agora pode utilizar um `Animator` para gerenciar suas animações.

## Arquitetura do Motor

O código está organizado em pacotes que separam as diferentes responsabilidades:

  * `com.JDStudio.Engine`
      * `Engine.java`: O coração do motor. Contém o game loop, inicializa a janela e gerencia o estado do jogo.
      * `GameState.java`: Classe abstrata para os diferentes estados do jogo (menu, nível, game over).
  * `com.JDStudio.Engine.Graphics`
      * `AssetManager.java`: Gerencia o carregamento e cache de sprites.
      * `Sprite.java`: Representa uma única imagem/sprite.
      * `Spritesheet.java`: Ferramenta para extrair sprites de uma folha de sprites.
  * `com.JDStudio.Engine.Graphics.Animations`
      * `Animation.java`: Define uma sequência de sprites para uma animação.
      * `Animator.java`: Gerencia múltiplas animações e qual está ativa para um `GameObject`.
  * `com.JDStudio.Engine.Graphics.UI`
      * `UIElement.java`: Classe base para todos os elementos de UI.
      * `UIText.java`: Elemento de UI para renderizar texto, estático ou dinâmico.
      * `UIManager.java`: Gerencia uma coleção de elementos de UI.
  * `com.JDStudio.Engine.Input`
      * `InputManager.java`: Singleton que gerencia todo o input do teclado.
  * `com.JDStudio.Engine.Object`
      * `GameObject.java`: Classe base para todos os objetos dinâmicos do jogo (jogador, inimigos, etc.), com suporte a colisão e animações.
  * `com.JDStudio.Engine.Sound`
      * `Sound.java`: Classe utilitária estática para reprodução e gerenciamento de áudio.
  * `com.JDStudio.Engine.World`
      * `World.java`: Gerencia a grade de tiles, carregamento de mapa e colisão com o cenário.
      * `Tile.java`: Representa um único tile no mapa.
      * `Camera.java`: Classe estática que controla a visão do mundo.
      * `IMapLoaderListener.java`: Interface para delegar a criação de tiles e objetos específicos do jogo durante o carregamento do mapa.

### Pré-requisitos

  * **JDK 11** ou superior.
  * Uma IDE Java de sua preferência (Eclipse, IntelliJ IDEA, VS Code com extensões Java).
  * Um programa para criar o map, recomendamos o [Tiled](https://www.mapeditor.org/)
  * Biblioteca externa `org.json` para parsear arquivos JSON (necessária para o carregamento de mapas do Tiled).

-----

## Jogo de Exemplo: Dungeon Crawler

Para ver o motor em ação, explore o jogo de exemplo incluído. Ele demonstra como os componentes da engine podem ser utilizados para criar um jogo 2D básico.

### Funcionalidades Demonstradas no Jogo de Exemplo

  * **Estrutura de Jogo por Estados**: O jogo principal é encapsulado na classe `PlayingState`, que estende `GameState` para gerenciar a lógica e renderização da cena do jogo.
  * **Carregamento de Nível com Tiled (JSON)**: O mapa é carregado a partir do arquivo `/map1.json` (exportado do Tiled). A classe `PlayingState` atua como `IMapLoaderListener` para interpretar os dados do JSON e instanciar os `Tile`s (como `FloorTile` e `WallTile`) e `GameObject`s (como `Player`, `Enemy`, `Lifepack`, `Weapon`, `Bullet`) nas posições corretas do mapa.
  * **Gerenciamento de Assets**: O `PlayingState` inicializa um `AssetManager` estático (`PlayingState.assets`) para carregar e registrar todos os sprites do jogo a partir de uma `Spritesheet`.
  * **Controle de Personagem e Colisão**: A classe `Player` processa a entrada do teclado via `InputManager` para movimentação. Antes de mover, o `Player` utiliza o método `isFree` do `World` para verificar colisões com tiles sólidos.
  * **Animações de `GameObject`**: O `Player` utiliza o `Animator` (herdado de `GameObject`) para gerenciar e reproduzir animações de "idle" e "caminhada" (direita e esquerda) baseadas na entrada do jogador.
  * **Colisão entre `GameObject`s**: A classe `PlayingState` contém um loop que verifica colisões entre `GameObject`s utilizando o método estático `GameObject.isColliding()`. Um exemplo prático é a colisão entre o `Player` e um `Lifepack`, onde o `Lifepack` é removido após a interação.
  * **Câmera Dinâmica**: A câmera do jogo (`Camera`) é atualizada no `PlayingState` para seguir o `Player`, mantendo-o centralizado na tela enquanto garante que a câmera não ultrapasse os limites do mapa.
  * **Sistema de UI**: A vida do jogador é exibida dinamicamente na tela usando um `UIText` gerenciado pelo `UIManager` dentro do `PlayingState`.
  * **Sistema de Áudio**: Uma música de fundo (`/music.wav`) é reproduzida em loop ao iniciar o `PlayingState`, e seu volume pode ser ajustado em tempo real usando as teclas `+` e `-` (NumPad).
  * **Modo de Debug**: A funcionalidade de debug da engine (`Engine.isDebug`) pode ser ativada ou desativada durante o jogo pressionando a tecla `F9`, permitindo visualizar as caixas de colisão dos objetos e tiles.

### Como Jogar

  * **Movimento**: Use as teclas **W, A, S, D** ou as **Setas Direcionais** para mover o personagem.
  * **Debug**: Pressione **F9** para visualizar as caixas de colisão dos tiles sólidos e objetos.
  * **Volume da Música**: Use **+** ou **-** (do NumPad) para aumentar ou diminuir o volume da música de fundo.

-----

## Guia para Desenvolvedores

Esta seção é para quem deseja modificar o motor ou o jogo de exemplo diretamente a partir do código-fonte.

### Pré-requisitos

  * **JDK 11** ou superior.
  * Uma IDE Java (Eclipse, IntelliJ IDEA, VS Code).
  * **Tiled Map Editor**: Para criar e exportar seus mapas no formato JSON.

### Configuração (A partir do Código-Fonte)

1.  Clone este repositório.
2.  Abra o projeto na sua IDE.
3.  Certifique-se de que a pasta contendo os recursos (spritesheets, arquivos de áudio, e *seus mapas `.json` do Tiled*) esteja no *Classpath* do projeto.
4.  Certifique-se de ter a biblioteca `org.json` adicionada às dependências do seu projeto.
5.  O ponto de entrada para executar o jogo de exemplo é o método `main` na classe `com.JDStudio.Game.Main`.

### Tutorial: Criando um Novo Jogo do Zero

1.  **Crie seu Mapa com Tiled**: Use o Tiled Map Editor para desenhar seu mapa. Exporte-o como um arquivo JSON. Certifique-se de que os Tilesets estejam configurados corretamente (IDs de tiles) e que as camadas de objetos tenham os nomes de tipo/nome (ex: "player\_start", "enemy") que você pretende usar na sua lógica.
2.  **Implemente `IMapLoaderListener`**: Crie uma classe no seu projeto do jogo que implemente `IMapLoaderListener`. Esta classe será responsável por criar as instâncias corretas de `Tile` e seus `GameObject`s baseadas nos IDs de tile e tipos de objeto definidos no Tiled.
    ```java
    import com.JDStudio.Engine.World.IMapLoaderListener;
    import com.JDStudio.Engine.World.Tile;
    import com.JDStudio.Engine.Graphics.Sprite.Sprite;
    import org.json.JSONObject;

    // Assumindo que você tem um AssetManager global (como em Main.assetManager)
    import static com.JDStudio.Game.Main.assetManager; // Importe seu AssetManager global

    public class MyGameMapLoader implements IMapLoaderListener {
        @Override
        public Tile onTileFound(int tileId, int x, int y) {
            // Exemplo: Mapeie IDs do Tiled para seus Tiles personalizados
            switch (tileId) {
                case 1: // ID do tile de chão no seu tileset (ex: Asset ID 1)
                    return new FloorTile(x, y, assetManager.getSprite("floor_sprite"));
                case 2: // ID do tile de parede no seu tileset (ex: Asset ID 2)
                    WallTile wall = new WallTile(x, y, assetManager.getSprite("wall_sprite"));
                    wall.isSolid = true; // Marque como sólido para colisão
                    return wall;
                default:
                    // Retorne um tile padrão ou nulo se o ID não for reconhecido
                    return new FloorTile(x, y, assetManager.getSprite("default_floor_sprite"));
            }
        }

        @Override
        public void onObjectFound(String type, int x, int y, JSONObject properties) {
            // Exemplo: Mapeie tipos de objetos do Tiled para seus GameObjects
            switch (type) {
                case "player_start":
                    // Você precisará de uma referência para o Player ou para a lista de GameObjects do seu GameState
                    // Exemplo: MyLevelState.player = new Player(x, y, assetManager.getSprite("player_idle"));
                    // MyLevelState.instance.addGameObject(MyLevelState.player);
                    break;
                case "enemy_spawn":
                    // Exemplo: MyLevelState.instance.addGameObject(new Enemy(x, y, 16, 16, assetManager.getSprite("enemy_sprite")));
                    break;
                // ... outros tipos de objetos definidos no Tiled
            }
        }
    }
    ```
3.  **Crie um `GameState`**: Estenda a classe `GameState` para seu nível ou tela. No construtor, você deve inicializar o `AssetManager` e carregar seus assets, depois instanciar o `World` passando seu `IMapLoaderListener`.
    ```java
    import com.JDStudio.Engine.Engine;
    import com.JDStudio.Engine.GameState;
    import com.JDStudio.Engine.World.World;
    import com.JDStudio.Engine.Input.InputManager;
    import com.JDStudio.Engine.Graphics.AssetManager;
    import com.JDStudio.Engine.Sound.Sound;
    import com.JDStudio.Engine.World.Camera;
    import java.awt.Graphics;

    public class MyLevelState extends GameState {
        private World gameWorld;
        public static Player player; // Exemplo de referência global ao player para a câmera e UI

        public MyLevelState() {
            // 1. Carregue assets: É crucial que os assets estejam carregados antes de carregar o mapa!
            //    Seu Main.java pode fazer isso, ou aqui se este é o primeiro estado.
            //    Ex: Main.assetManager = new AssetManager();
            //    Main.assetManager.loadSprite("player_idle", "/sprites/player_idle.png");
            //    Main.assetManager.loadSprite("floor_sprite", "/tiles/floor.png");
            //    Sound.loop("/music/level_music.wav");

            // 2. Crie o mundo usando seu IMapLoaderListener
            //    Passe 'this' se MyLevelState também implementa IMapLoaderListener,
            //    ou passe uma nova instância de MyGameMapLoader.
            this.gameWorld = new World("/maps/my_first_map.json", new MyGameMapLoader());

            // 3. Após o carregamento do mapa, seus GameObjects deverão ter sido criados pelo MyGameMapLoader.
            //    Se o player foi criado via mapa, você pode acessá-lo agora se o MyGameMapLoader o atribuiu
            //    a uma variável estática ou se você o recupera de alguma forma.
            //    Ex: this.addGameObject(player); // Certifique-se de que 'player' foi inicializado!

            // 4. Configure sua UI, se houver
            //    UIManager uiManager = new UIManager();
            //    uiManager.addElement(new UIText(10, 20, new Font("Arial", Font.PLAIN, 10), Color.BLACK, () -> "Player X: " + player.getX()));
            //    this.addUiElement(uiManager); // Você pode criar um método para adicionar UIManager
        }

        @Override
        public void tick() {
            // Atualize a lógica de todos os GameObjects
            for (int i = 0; i < gameObjects.size(); i++) {
                gameObjects.get(i).tick();
            }

            // Lógica de colisão entre GameObjects (se não for tratada dentro dos próprios objetos)
            // Ex: if (GameObject.isColliding(player, enemy)) { /* ... */ }

            // Atualize a câmera para seguir o player
            if (player != null) {
                int mapPixelWidth = gameWorld.WIDTH * World.TILE_SIZE;
                int mapPixelHeight = gameWorld.HEIGHT * World.TILE_SIZE;
                Camera.x = Camera.clamp(player.getX() - (Engine.WIDTH / 2), 0, mapPixelWidth - Engine.WIDTH);
                Camera.y = Camera.clamp(player.getY() - (Engine.HEIGHT / 2), 0, mapPixelHeight - Engine.HEIGHT);
            }

            // **IMPORTANTE**: Chame InputManager.instance.update() no final do tick do seu GameState ativo
            InputManager.instance.update();
        }

        @Override
        public void render(Graphics g) {
            // Renderize o mundo
            gameWorld.render(g);
            // Renderize todos os GameObjects
            for (GameObject go : gameObjects) {
                go.render(g);
            }
            // Renderize sua UI
            // uiManager.render(g);
        }
    }
    ```
4.  **Crie seus `GameObject`s**: Estenda `GameObject` para seu jogador, inimigos, itens, projéteis, etc. Lembre-se que `GameObject` já possui um `Animator` e métodos para máscara de colisão.
    ```java
    import com.JDStudio.Engine.Object.GameObject;
    import com.JDStudio.Engine.Graphics.Sprite.Sprite;
    import com.JDStudio.Engine.Input.InputManager;
    import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
    import java.awt.event.KeyEvent;

    // Assumindo Main.assetManager está disponível
    import static com.JDStudio.Game.Main.assetManager;

    public class Player extends GameObject {
        public double speed = 1.0;
        private World worldRef; // Referência ao mundo para colisões

        public Player(double x, double y, Sprite initialSprite) {
            super(x, y, 16, 16, initialSprite);
            setCollisionMask(3, 0, 10, 16); // Exemplo: máscara de 10px de largura, deslocada 3px da esquerda
            setupAnimations();
        }

        public void setWorldReference(World world) {
            this.worldRef = world;
        }

        private void setupAnimations() {
            animator.addAnimation("idle", new Animation(10, assetManager.getSprite("player_idle_frame1")));
            animator.addAnimation("walk_right", new Animation(8, assetManager.getSprite("player_walk_right_1"), assetManager.getSprite("player_walk_right_2")));
            // Adicione outras animações (walk_left, walk_up, walk_down, attack, etc.)
            animator.play("idle"); // Animação inicial
        }

        @Override
        public void tick() {
            super.tick(); // Atualiza o Animator

            boolean moving = false;
            double nextX = x, nextY = y;

            if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT) || InputManager.isKeyPressed(KeyEvent.VK_D)) {
                nextX += speed;
                moving = true;
                animator.play("walk_right");
            } else if (InputManager.isKeyPressed(KeyEvent.VK_LEFT) || InputManager.isKeyPressed(KeyEvent.VK_A)) {
                nextX -= speed;
                moving = true;
                animator.play("walk_left"); // Assumindo que você criou essa animação
            }

            if (InputManager.isKeyPressed(KeyEvent.VK_UP) || InputManager.isKeyPressed(KeyEvent.VK_W)) {
                nextY -= speed;
                moving = true;
                // animator.play("walk_up");
            } else if (InputManager.isKeyPressed(KeyEvent.VK_DOWN) || InputManager.isKeyPressed(KeyEvent.VK_S)) {
                nextY += speed;
                moving = true;
                // animator.play("walk_down");
            }

            // Verifique colisão com o mundo antes de aplicar o movimento
            if (worldRef != null && worldRef.isFree((int)nextX, (int)y, maskX, maskY, maskWidth, maskHeight)) {
                x = nextX;
            }
            if (worldRef != null && worldRef.isFree((int)x, (int)nextY, maskX, maskY, maskWidth, maskHeight)) {
                y = nextY;
            }

            if (!moving && !animator.getCurrentAnimationKey().equals("idle")) {
                animator.play("idle");
            }
        }
    }
    ```
5.  **Junte Tudo na sua classe `Main`**: No método `main` da sua aplicação, você inicializa a engine, seu `AssetManager`, carrega os assets, e então cria e define o estado inicial do jogo.
    ```java
    package com.JDStudio.Game;

    import com.JDStudio.Engine.Engine;
    import com.JDStudio.Engine.Graphics.AssetManager;
    import com.JDStudio.Engine.Sound.Sound;
    import com.JDStudio.Engine.Graphics.Sprite.Spritesheet; // Para carregar spritesheets

    public class Main {
        // Instância global do AssetManager (conveniente para acessar assets de qualquer lugar)
        public static AssetManager assetManager;

        public static void main(String[] args) {
            // 1. Inicialize a engine
            Engine engine = new Engine();

            // 2. Inicialize o AssetManager e carregue seus assets
            assetManager = new AssetManager();
            loadGameAssets(); // Chame um método para carregar todos os assets

            // 3. Opcional: Inicie a música de fundo
            Sound.loop("/music.wav");

            // 4. Crie e defina o estado inicial do jogo (PlayingState no exemplo)
            Engine.setGameState(new PlayingState());

            // 5. Inicie o game loop
            engine.start();
        }

        private static void loadGameAssets() {
            System.out.println("Carregando assets do jogo...");
            Spritesheet mainSheet = new Spritesheet("/spritesheet.png"); // Carregue sua spritesheet principal

            // Exemplo de registro de sprites da spritesheet
            assetManager.registerSprite("tile_floor", mainSheet.getSprite(0, 0, 16, 16));
            assetManager.registerSprite("tile_wall", mainSheet.getSprite(16, 0, 16, 16));
            // ... e todos os outros sprites necessários para o Player, Enemy, itens, etc.
            assetManager.registerSprite("player_idle_frame1", mainSheet.getSprite(32, 0, 16, 16));
            assetManager.registerSprite("player_walk_right_1", mainSheet.getSprite(48, 0, 16, 16));
            assetManager.registerSprite("player_walk_right_2", mainSheet.getSprite(64, 0, 16, 16));
            assetManager.registerSprite("player_walk_left_1", mainSheet.getSprite(48, 16, 16, 16));
            assetManager.registerSprite("player_walk_left_2", mainSheet.getSprite(64, 16, 16, 16));
            // ... Carregue os assets de áudio também, se não forem feitos no Sound.loadClip automaticamente
            // Sound.loadClip("explosion_sfx", "/sfx/explosion.wav");
            System.out.println("Assets carregados.");
        }
    }
    ```

## Próximos Passos (Roadmap)

  * [ ] **Sistema de Câmera Melhorado**: Mais opções como zoom, *shaking* (tremor) e transições suaves.
  * [ ] **Física**: Implementar gravidade e momento para movimentos mais complexos.
  * [ ] **Sistema de Eventos**: Para comunicação desacoplada entre diferentes partes do jogo.
  * [ ] **Suporte a Múltiplas Camadas de Tiles**: Para renderização de paralaxe e complexidade visual.
  * [ ] **Otimização de Renderização**: Para mundos muito grandes.

## Autor

  * **JD Studio**

## Licença

Este projeto está sob a licença MIT.

-----