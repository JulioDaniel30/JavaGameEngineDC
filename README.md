# JD Studio Game Engine

Uma engine de jogo 2D modular e orientada a componentes, construída em Java puro (AWT/Swing). Este projeto serve como uma base robusta para a criação de jogos 2D, com foco em design orientado a dados e um conjunto de ferramentas completo para acelerar o desenvolvimento.

O repositório inclui a **engine**, um **jogo de exemplo completo** para demonstrar os recursos, um **template de projeto** e uma **ferramenta geradora de projetos** para iniciar novos jogos instantaneamente.

## Filosofia da Engine

A arquitetura é construída sobre quatro pilares principais para garantir flexibilidade e escalabilidade:

1. **Arquitetura Baseada em Componentes (ECS-like)**: `GameObject`s são contêineres aos quais você anexa funcionalidades (`Component`s) como movimento, física, animação e vida.
2. **Design Orientado a Dados (Data-Driven)**: Níveis são construídos no **Tiled Editor**, animações são importadas de arquivos JSON do **Aseprite**, e diálogos são escritos em JSON, separando a lógica do conteúdo.
3. **Sistema de Renderização em Camadas**: Um `RenderManager` desenha tudo na ordem correta com base em camadas de renderização (`RenderLayer`) e profundidade Z, permitindo efeitos 2.5D.
4. **Mensageria por Eventos (Pub/Sub)**: Um `EventManager` central desacopla os sistemas, permitindo que áudio, UI e lógica reajam a eventos sem dependências diretas.

## Principais Funcionalidades

### Mundo e Renderização
- **Carregamento de Mapas do Tiled**: Importa níveis complexos (`.json`) do Tiled, incluindo camadas de tiles, objetos e polilinhas para caminhos de patrulha.
- **Renderização Otimizada**: Utiliza `BufferStrategy` para uma renderização suave e sem flickering.
- **Câmera Dinâmica**: Câmera com seguimento suave, zoom e efeito de tremor.
- **Sistema de Iluminação 2D**: Suporte a luz ambiente, luzes de ponto e luzes direcionais.
- **Sistema de Partículas**: Efeitos como explosões e fumaça com *object pooling*.
- **Efeito Parallax**: Fundos com múltiplas camadas para profundidade visual.

### Gameplay e Objetos
- **Sistema de Componentes**: Adicione `Component`s a qualquer `GameObject` para funcionalidades extras.
- **Animação via Aseprite**: `Animator` gerencia estados de animação, importando dados do Aseprite.
- **Física de Plataforma**: Gravidade, aceleração e pulos.
- **Movimento Top-Down**: Controle de jogador e IA, com pathfinding A*.
- **Sistema de Colisão**: Detecção AABB entre objetos e cenário, com tipos configuráveis.
- **Sistema de Anexos**: Hierarquias de objetos (ex: arma anexada ao jogador).

### UI, Interação e Áudio
- **Input com Mapeamento de Ações**: Abstração de teclado/mouse para ações configuráveis via JSON.
- **Sistema de UI com Temas**: Elementos como botões, sliders, barras de vida e inventário, com temas dinâmicos.
- **UI de Mundo**: Elementos de UI anexados a objetos do mundo.
- **Sistema de Diálogo Avançado**: Conversas ramificadas e escolhas em arquivos JSON.
- **Áudio com Canais e Som Espacial**: Gerenciamento de canais e panning 2D.

### Core da Engine
- **Máquina de Estados**: Gerenciamento de telas e sobreposições.
- **Gerenciamento de Assets**: Cache centralizado de sprites e spritesheets.
- **Sistema de Salvar/Carregar**: Interface `ISavable` e `SaveManager`.

## Arquitetura do Motor

O código está organizado em pacotes, cada um responsável por uma parte específica da engine. Isso facilita a manutenção, extensão e entendimento do projeto:

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

## Pré-requisitos
- **JDK 17** ou superior.
- **Eclipse IDE for Java Developers**.
- **Tiled Map Editor** ([https://www.mapeditor.org/](https://www.mapeditor.org/)).
- **(Opcional) Aseprite** para animações.

---

## Como Começar (Configuração do Ambiente Eclipse)

**1. Clonar o Repositório**
```bash
git clone https://github.com/JulioDaniel30/JavaGameEngineDC.git
```

**2. Importar os Projetos no Eclipse**

- Abra o Eclipse e vá em `File` > `Import...`.
- Expanda a opção `General` e selecione **`Existing Projects into Workspace`**. Clique em `Next`.
- Clique em `Browse...` e selecione a pasta principal que você acabou de clonar.
- O Eclipse deve detectar automaticamente todos os projetos (`com.JDStudio.Engine`, `Game_Project_Template`, etc.). Certifique-se de que **todos estejam marcados**.
- Clique em `Finish`.

**3. Configurar a Biblioteca JSON**

- No Eclipse, clique com o botão direito no projeto da engine > `Build Path` > `Configure Build Path...`.
- Vá para a aba `Libraries`.
- Clique em `Add JARs...`.
- Navegue até a pasta `lib` dentro do projeto da engine e selecione o arquivo `.jar` do JSON.
- Vá para a aba `Order and Export` e **marque a caixa** ao lado do JAR do JSON. Isso garante que os projetos de jogo também possam usá-lo.
- Clique em `Apply and Close`.

**4. Executar o Jogo de Exemplo**

- No "Package Explorer", dentro do projeto do jogo de exemplo (`Game`), encontre a classe `Main.java`.
- Clique com o botão direito sobre ela > `Run As` > `Java Application`.

---

## Como Criar um Novo Jogo (Método Rápido)

A forma mais fácil de começar é usando a ferramenta geradora de projetos incluída:

1. Após clonar o repositório, navegue até a pasta `JDStudioGameProjectGenerator/bin/`.
2. Dê um duplo clique no arquivo executável `JDStudioGameProjectGenerator.jar`.
3. Uma série de janelas irá aparecer, pedindo as informações para o seu novo projeto:
   - **Nome do novo projeto**: O nome que seu jogo terá (ex: `MeuSuperJogo`).
   - **Diretório de destino**: A pasta onde o projeto será criado (ex: `C:/MeusJogos`).
   - **Diretório fonte**: Aponte para a pasta principal clonada do Git, que contém as pastas `com.JDStudio.Engine` e `Game_Project_Template`.
   - **Opção da Engine**: Escolha entre linkar com a engine (recomendado para desenvolvimento) ou copiar a engine para o novo projeto.
4. Após o gerador terminar, importe o seu novo projeto para o Eclipse da mesma forma que fez no passo 2.

## Documentação Completa

Consulte a documentação incluída para detalhes de cada sistema, exemplos e tutoriais.

## Autor

- **JD Studio**

## Licença

Este projeto está sob a licença MIT.
