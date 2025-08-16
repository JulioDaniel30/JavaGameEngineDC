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

## Como Criar um Novo Jogo (Método Rápido com o Gerador)

A maneira mais fácil e recomendada de começar um novo projeto é usando a ferramenta **JDStudioGameProjectGenerator**, incluída neste repositório. Ela automatiza todo o processo de configuração.

### Passo 1: Baixar e Executar a Ferramenta

1. Navegue até a pasta `Executaveis/` deste repositório e baixe o arquivo **`JDStudioGameProjectGenerator.jar`** para o seu computador.
2. Dê um duplo clique no arquivo para iniciar o gerador.

### Passo 2: Descrição das Janelas do Assistente Gráfico

Durante o processo, o gerador apresenta uma sequência de janelas para configurar seu novo projeto:

#### Janela 1: Fonte dos Arquivos
- Escolha entre **clonar o essencial do repositório** (baixa automaticamente os arquivos necessários para uma pasta escolhida) ou **usar uma pasta local** (caso já tenha o repositório no computador).

#### Janela 2: Nome do Novo Projeto
- Digite o nome do seu jogo (exemplo: `MeuSuperJogo`). Este será o nome da pasta e do projeto no Eclipse.

#### Janela 3: Renomear Pacote
- O gerador pergunta se deseja **renomear o pacote padrão** (`com.game`).
  - **Sim**: Digite um nome de pacote personalizado (ex: `com.meuestudio.meusuperjogo`).
  - **Não**: O projeto será criado com o pacote padrão.

#### Janela 4: Diretório de Destino
- Escolha a pasta onde o projeto será criado (exemplo: `C:/MeusJogos`). O gerador criará uma nova pasta com o nome do projeto dentro desse diretório.

#### Janela 5: Opção da Engine
- Escolha como a engine será tratada:
  - **Copiar Engine (Autocontido)**: O projeto e a engine ficam juntos, tornando o projeto independente.
  - **Linkar com Engine (Workspace)**: O projeto do jogo é criado e vinculado à engine do workspace. Atualizações na engine são refletidas automaticamente no jogo.

#### Janela 6: Sucesso
- Ao final, uma janela informa que o **projeto foi gerado com sucesso**, mostrando o caminho onde ele foi criado.

### Passo 3: Importar o Projeto Gerado no Eclipse

1. Abra o Eclipse.
2. Vá em `File` > `Import...`.
3. Selecione **`Existing Projects into Workspace`**.
4. Clique em `Browse...` e selecione a pasta de destino escolhida.
5. Marque os projetos encontrados e clique em `Finish`.

Pronto! Seu novo projeto estará configurado e pronto para desenvolvimento.

## Documentação Completa

Consulte a [documentação](./Documentaçao/README.md) incluída para detalhes de cada sistema, exemplos e tutoriais.

## Autor

- **JD Studio**

## Licença

Este projeto está sob a licença MIT.
