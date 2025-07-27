<<<<<<< Updated upstream
=======
Com certeza. Com as últimas melhorias no sistema de tiles e som, uma reescrita completa do `README.md` é a melhor forma de consolidar tudo e deixar a documentação clara, profissional e alinhada com a versão atual da sua engine.
    
Abaixo está a nova versão completa do seu `README.md`. Ele foi reestruturado para destacar as novas funcionalidades e refinar as explicações, especialmente no tutorial para desenvolvedores.

-----

>>>>>>> Stashed changes
## JD Studio Game Engine

Um motor de jogo 2D simples, modular e orientado a objetos, construído em Java puro (AWT/Swing). Este projeto serve como uma base robusta para a criação de jogos 2D, especialmente aqueles baseados em tiles, e inclui um jogo de exemplo completo para demonstrar seus recursos avançados.

## Principais Funcionalidades

  * **Game Loop de Timestep Fixo**: Garante que a lógica do jogo execute a uma taxa constante (padrão de 60 UPS) para um comportamento previsível, independentemente do FPS.
  * **Gerenciamento de Estado**: Arquitetura baseada em uma máquina de estados (`GameState`) que permite separar de forma limpa a lógica de diferentes telas do jogo (menu, fases, game over, etc.).
  * **Renderização com BufferStrategy**: Utiliza double/triple buffering para uma renderização suave e sem *flickering*.
  * **Sistema de Câmera Dinâmico**: Uma câmera baseada em objeto (`Camera`) que gerencia a visão do mundo, com funcionalidades avançadas:
      * **Transição Suave (Smooth Follow)**: Segue um alvo (como o jogador) de forma suave usando interpolação.
      * **Zoom**: Permite aproximar e afastar a visão da cena dinamicamente.
      * **Tremor (Shake)**: Suporta um efeito de tremor de tela, configurável em intensidade e duração.
  * **Mundo Baseado em Tiles (Tiled)**: Sistema robusto para criar e renderizar mundos a partir de uma grade de tiles.
      * **Carregamento de Mapas JSON**: É capaz de carregar mapas complexos criados no editor **Tiled** e exportados como `.json`.
      * **Tamanho de Tile Dinâmico**: O tamanho dos tiles (`tilewidth`, `tileheight`) é lido diretamente do arquivo do mapa, permitindo que cada mundo tenha seu próprio tamanho de tile.
      * **Criação de Tiles Hierárquica**: Utiliza um sistema de regras poderoso, onde o **nome da camada** no Tiled ('Floor', 'Walls') define a categoria do tile, e o **ID do tile** dentro dessa camada define a variação visual. Isso permite um fluxo de trabalho que combina prototipagem rápida com controle artístico detalhado.
  * **Sistema de Input**: Gerenciador de input (`InputManager`) com suporte para "tecla pressionada continuamente" (`isKeyPressed`) e "tecla recém-pressionada" (`isKeyJustPressed`).
  * **Sistema de Áudio Flexível**: Gerenciador de áudio (`Sound`) com funcionalidades essenciais para uma experiência imersiva.
      * **Reprodução Simultânea (Overlap)**: Permite que múltiplas instâncias do mesmo efeito sonoro toquem ao mesmo tempo sem se cortarem.
      * **Controle de Volume Individual**: Cada efeito sonoro pode ser tocado com um volume específico, que é multiplicado pelo volume mestre de SFX.
      * **Cache de Áudio**: Dados de áudio são mantidos em cache para performance, evitando leituras repetidas do disco.
  * **Gerenciamento de Assets**: Carrega e armazena em cache sprites e spritesheets (`AssetManager`, `Sprite`, `Spritesheet`).
  * **Sistema de Colisão**:
      * **Detecção AABB**: Funções para verificar colisões entre `GameObject` e entre `GameObject` e tiles sólidos do `World`.
      * **Resposta Orientada a Objetos**: A lógica de *reação* a uma colisão é encapsulada nos próprios `GameObject`s através do método `onCollision(GameObject other)`.
  * **Sistema de Animação**: Inclui classes para gerenciar animações de sprites (`Animation`, `Animator`), permitindo definir sequências de quadros e controlar a velocidade.
  * **Sistema de UI Básico**: Estrutura para elementos de interface, como texto estático e dinâmico (`UIElement`, `UIText`, `UIManager`).
  * **Modo de Debug**: Uma flag global (`Engine.isDebug`) que permite visualizar informações de debug, como as máscaras de colisão.

## Arquitetura do Motor

O código está organizado em pacotes que separam as diferentes responsabilidades:

  * `com.JDStudio.Engine`
      * `Engine.java`: O coração do motor. Contém o game loop, inicializa a janela, a câmera e gerencia o estado do jogo.
      * `GameState.java`: Classe abstrata para os diferentes estados do jogo.
  * `com.JDStudio.Engine.Components`
      * `MovementComponent.java`: Componente que gerencia o movimento de um `GameObject` e sua colisão com o cenário.
  * `com.JDStudio.Engine.Graphics`
      * `AssetManager.java`: Gerencia o carregamento e cache de sprites.
      * `Sprite.java`, `Spritesheet.java`: Para manipulação de imagens e folhas de sprites.
  * `com.JDStudio.Engine.Graphics.Animations`
      * `Animation.java`, `Animator.java`: Gerenciam sequências de animação para os `GameObject`s.
  * `com.JDStudio.Engine.Graphics.UI`
      * `UIElement.java`, `UIText.java`, `UIManager.java`: Estrutura básica para a interface do usuário.
  * `com.JDStudio.Engine.Input`
      * `InputManager.java`: Singleton que gerencia todo o input do teclado.
  * `com.JDStudio.Engine.Object`
      * `GameObject.java`: Classe base para todos os objetos dinâmicos do jogo, com suporte a colisão, animações e um método `onCollision()` para tratar interações.
  * `com.JDStudio.Engine.Sound`
      * `Sound.java`: Classe utilitária para reprodução de áudio.
  * `com.JDStudio.Engine.World`
      * `World.java`: Gerencia a grade de tiles, carregamento de mapa e colisão com o cenário.
      * `Tile.java`: Representa um único tile no mapa.
      * `Camera.java`: Classe que gerencia a posição, zoom, tremor e movimento suave da câmera.
      * `IMapLoaderListener.java`: Interface para delegar a criação de objetos durante o carregamento do mapa.

### Pré-requisitos

  * **JDK 11** ou superior.
  * Uma IDE Java de sua preferência (Eclipse, IntelliJ IDEA, etc.).
  * **Tiled Map Editor** ([https://www.mapeditor.org/](https://www.mapeditor.org/)).
  * Biblioteca externa **`org.json`** para parsear os mapas.

-----

## Jogo de Exemplo e Tutorial

O projeto inclui um jogo de exemplo (`com.game.Game`) que demonstra como usar a engine.

### Como Jogar o Exemplo

  * **Movimento**: **W, A, S, D** ou **Setas Direcionais**.
  * **Debug**: Pressione **9** para ativar/desativar a visualização de caixas de colisão.
  * **Câmera**: Pressione **I** para aproximar (zoom in), **O** para afastar (zoom out) e **Espaço** para testar o efeito de tremor.

### Guia Rápido para Desenvolvedores

Este guia mostra o fluxo de trabalho para criar um nível usando a engine.

**1. Crie seu Mapa no Tiled**

  * Crie camadas com nomes específicos (ex: "Floor", "Walls"). A engine usará esses nomes.
  * Pinte o layout do seu nível. Para áreas grandes, use um tile genérico. Para detalhes, use tiles específicos.
  * Anote os IDs dos seus tiles específicos (lembre-se que o ID no código é `ID do Tiled + 1`).
  * Exporte o mapa como um arquivo `.json`.

**2. Configure seu `GameState`**
Crie uma classe que estende `GameState` e implementa `IMapLoaderListener`. Esta será a classe principal do seu nível.

**3. Carregue seus Assets**
No construtor do seu `GameState`, carregue todos os sprites necessários usando o `AssetManager`. Dê nomes descritivos a eles.

```java
// Exemplo em seu PlayingState.java
private void loadAssets() {
    Spritesheet sheet = new Spritesheet("/spritesheet.png");
    // Sprites para o sistema hierárquico
    assets.registerSprite("floor_plain", sheet.getSprite(0, 0, 16, 16));
    assets.registerSprite("floor_cracked", sheet.getSprite(0, 16, 16, 16));
    assets.registerSprite("wall_plain", sheet.getSprite(16, 0, 16, 16));
    // ... outros assets
}
```

**4. Implemente a Lógica Hierárquica de Criação de Tiles**
Este é o coração do sistema. No método `onTileFound`, use a hierarquia `layerName` -\> `tileId` para determinar qual tile criar.

```java
// Exemplo em seu PlayingState.java
@Override
public Tile onTileFound(String layerName, int tileId, int x, int y) {
    // 1º Nível: Filtra pela Camada
    switch (layerName) {
        case "Floor":
            // 2º Nível: Filtra pelo ID do Tile dentro da camada "Floor"
            switch (tileId) {
                case 2: // ID específico para o chão rachado
                    return new FloorTile(x, y, assets.getSprite("floor_cracked"));
                default: // Para qualquer outro tile ID na layer "Floor", usa o padrão
                    return new FloorTile(x, y, assets.getSprite("floor_plain"));
            }

        case "Walls":
            // Lógica similar para a camada "Walls"
            switch (tileId) {
                // Adicione casos para tiles de parede específicos aqui
                default:
                    return new WallTile(x, y, assets.getSprite("wall_plain"));
            }
            
        default:
            return null; // Ignora outras camadas
    }
}
```

**5. Implemente a Criação de Objetos**
Use o `onObjectFound` para instanciar seu `Player`, inimigos e outros itens com base nos objetos que você colocou no Tiled.

```java
// Exemplo em seu PlayingState.java
@Override
public void onObjectFound(String type, int x, int y, JSONObject properties) {
    if ("player_start".equals(properties.getString("name"))) {
        player = new Player(x, y, 16, 16);
        player.setWorld(this.world);
        this.addGameObject(player);
    }
}
```

Com esses passos, você tem um fluxo de trabalho poderoso que combina a facilidade de organização por camadas com o controle detalhado de tiles individuais.

## Próximos Passos (Roadmap)

  * ✅ **Sistema de Câmera Melhorado**
  * ✅ **Sistema de Áudio Melhorado**
  * [ ] **Som Espacial 2D (Panning)**: Ajustar o balanço do som com base na sua posição na tela.
  * [ ] **Efeitos de Fade para Áudio**: Transições suaves de entrada e saída para músicas e sons.
  * [ ] **Sistema de Física**: Implementar gravidade, aceleração e momento para movimentos mais complexos.
  * [ ] **Sistema de Eventos**: Para comunicação desacoplada entre diferentes partes do jogo.
  * [ ] **Otimização de Renderização**: Para mundos muito grandes (ex: *culling* de *chunks*).

## Autor

  * **JD Studio**

## Licença

Este projeto está sob a licença MIT.
