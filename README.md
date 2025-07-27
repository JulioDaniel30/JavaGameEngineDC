## JD Studio Game Engine

Um motor de jogo 2D simples, modular e orientado a objetos, construído em Java puro, utilizando AWT e Swing para renderização. Este projeto serve como uma base sólida para a criação de jogos 2D, especialmente aqueles baseados em tiles, e inclui um jogo de exemplo completo para demonstrar seus recursos.

## Principais Funcionalidades do Motor

* **Game Loop de Timestep Fixo**: Garante que a lógica do jogo execute a uma taxa constante (padrão de 60 UPS) para um comportamento previsível, independentemente do FPS.
* **Gerenciamento de Estado**: Arquitetura baseada em uma máquina de estados (`GameState`) que permite separar de forma limpa a lógica do menu, das fases, da tela de game over, etc.
* **Renderização com BufferStrategy**: Utiliza double/triple buffering para uma renderização suave e sem *flickering*.
* **Sistema de Câmera Dinâmico**: Uma câmera baseada em objeto (`Camera`) que gerencia a visão do mundo, com funcionalidades avançadas:
    * **Transição Suave (Smooth Follow)**: Segue um alvo (como o jogador) de forma suave usando interpolação, em vez de se prender instantaneamente a ele.
    * **Zoom**: Permite aproximar e afastar a visão da cena dinamicamente.
    * **Tremor (Shake)**: Suporta um efeito de tremor de tela, configurável em intensidade e duração, ideal para dar impacto a eventos do jogo.
* **Mundo Baseado em Tiles**: Sistema para criar mundos com uma grade de tiles (`World`, `Tile`).
    * **Carregamento de Mapas (Tiled)**: É capaz de carregar mapas a partir de arquivos JSON do editor Tiled.
    * **Tamanho de Tile Dinâmico**: O tamanho dos tiles (`tilewidth`, `tileheight`) é lido diretamente do arquivo do mapa, permitindo que cada mundo tenha seu próprio tamanho de tile.
    * **Criação Delegada**: Delega a criação de tiles e objetos específicos do jogo para um listener (`IMapLoaderListener`).
* **Sistema de Input Simples**: Gerenciador de input (`InputManager`) com suporte para "tecla pressionada" (`isKeyPressed`) e "tecla recém-pressionada" (`isKeyJustPressed`).
* **Gerenciamento de Assets**: Carrega e armazena em cache sprites e spritesheets (`AssetManager`, `Sprite`, `Spritesheet`) para evitar o carregamento duplicado de recursos.
* **Sistema de Colisão**:
    * **Detecção AABB**: Funções para verificar colisões entre `GameObject` e entre `GameObject` e tiles sólidos do `World` (Axis-Aligned Bounding Box).
    * **Resposta Orientada a Objetos**: A lógica de *reação* a uma colisão é encapsulada dentro dos próprios `GameObject`s através do método `onCollision(GameObject other)`, tornando o código mais limpo e escalável.
* **Sistema de UI Básico**: Estrutura para elementos de interface, como texto estático e dinâmico (`UIElement`, `UIText`, `UIManager`).
* **Modo de Debug**: Uma flag global (`Engine.isDebug`) que permite visualizar informações de debug, como as máscaras de colisão de `GameObject` e `Tile`.
* **Sistema de Áudio**: Uma classe utilitária estática (`Sound`) para gerenciar o carregamento, cache e reprodução de efeitos sonoros (SFX) e músicas de fundo (em loop), com controle de volume independente.
* **Sistema de Animação**: Inclui classes para gerenciar animações de sprites (`Animation`, `Animator`), permitindo definir sequências de quadros e controlar a velocidade.

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
* Uma IDE Java de sua preferência (Eclipse, IntelliJ IDEA, VS Code com extensões Java).
* Um programa para criar o mapa, recomendamos o [Tiled](https://www.mapeditor.org/)
* Biblioteca externa `org.json` para parsear arquivos JSON.

-----

## Jogo de Exemplo: Dungeon Crawler

Para ver o motor em ação, explore o jogo de exemplo incluído.

### Funcionalidades Demonstradas no Jogo de Exemplo

* **Estrutura de Jogo por Estados**: O jogo é encapsulado na classe `PlayingState`, que gerencia a lógica e renderização da cena principal.
* **Carregamento de Nível com Tiled (JSON)**: O mapa é carregado a partir do arquivo `/map1.json`. A classe `PlayingState` atua como `IMapLoaderListener` para instanciar os `Tile`s e `GameObject`s corretos.
* **Controle de Personagem**: A classe `Player` utiliza um `MovementComponent` para se mover pelo cenário, processando a entrada do `InputManager`.
* **Animações e Colisão**: O `Player` utiliza o `Animator` para suas animações. A colisão entre objetos é detectada no `PlayingState`, que então notifica os objetos envolvidos chamando seus respectivos métodos `onCollision`, onde a lógica de interação (ex: coletar um `Lifepack`) é executada.
* **Câmera Dinâmica e Efeitos**: A câmera do jogo (`Engine.camera`) é atualizada para seguir o `Player` suavemente. O jogo de exemplo inclui controles para testar as funcionalidades de zoom e tremor da câmera.
* **Sistema de UI**: A vida do jogador é exibida dinamicamente na tela usando um `UIText`.
* **Áudio e Debug**: Uma música de fundo toca em loop e seu volume pode ser ajustado. O modo de debug pode ser ativado para visualizar as caixas de colisão.

### Como Jogar

* **Movimento**: Use as teclas **W, A, S, D** ou as **Setas Direcionais** para mover o personagem.
* **Debug**: Pressione **9** para visualizar as caixas de colisão.
* **Volume da Música**: Use **+** ou **-** (do NumPad) para ajustar o volume.
* **Câmera**: Pressione **I** para aproximar (zoom in), **O** para afastar (zoom out) e **Espaço** para testar o efeito de tremor.

-----

*(O resto do guia para desenvolvedores e tutorial permanecem relevantes e não precisam de alterações imediatas)*

## Próximos Passos (Roadmap)

* ✅ **Sistema de Câmera Melhorado**: Mais opções como zoom, *shaking* (tremor) e transições suaves.
* [ ] **Física**: Implementar gravidade e momento para movimentos mais complexos.
* [ ] **Sistema de Eventos**: Para comunicação desacoplada entre diferentes partes do jogo.
* [ ] **Suporte a Múltiplas Camadas de Tiles**: Para renderização de paralaxe e complexidade visual.
* [ ] **Otimização de Renderização**: Para mundos muito grandes.

## Autor

* **JD Studio**

## Licença

Este projeto está sob a licença MIT.

---
