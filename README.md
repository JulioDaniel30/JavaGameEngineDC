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



## Como Começar com Gradle

Este projeto utiliza uma estrutura de múltiplos projetos Gradle para gerenciar a engine e os jogos. A configuração é simples e não requer um IDE específico.

### Estrutura de Múltiplos Projetos

O repositório está organizado da seguinte forma:

-   `Engine/`: Contém o código-fonte da engine.
-   `Game/`: Um jogo de exemplo completo que demonstra os recursos da engine.
-   `Game_Project_Template/`: Um template básico para iniciar um novo jogo.

### Pré-requisitos

-   **JDK 17** ou superior.
-   **Gradle 8.5** ou superior (o projeto inclui um Gradle Wrapper, então não é necessário instalar o Gradle manualmente).

### Construindo o Projeto

Para construir a engine e o jogo de exemplo, execute o seguinte comando na raiz do repositório:

```bash
./gradlew build
```

Este comando irá compilar todo o código, executar os testes e montar os artefatos necessários.

### Executando o Jogo de Exemplo

Para executar o jogo de exemplo, utilize o seguinte comando:

```bash
./gradlew :Game:run
```

### Publicando a Engine em um Repositório Maven Local

Se você deseja usar a engine em um projeto separado, pode publicá-la em seu repositório Maven local. Isso permite que você adicione a engine como uma dependência em outros projetos Gradle.

Para publicar a engine, execute o seguinte comando:

```bash
./gradlew :Engine:publishToMavenLocal
```

Após a publicação, você pode adicionar a seguinte dependência ao arquivo `build.gradle` do seu jogo:

```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.JDStudio:Engine:1.0.0'
}
```

### Criando um Novo Jogo

A maneira recomendada de criar um novo jogo é copiar o projeto `Game_Project_Template`.

1.  Copie a pasta `Game_Project_Template` para um novo local e renomeie-a para o nome do seu jogo (por exemplo, `MeuSuperJogo`).
2.  Abra o arquivo `settings.gradle` na raiz do repositório e adicione o seu novo projeto:

```groovy
include 'Engine', 'Game', 'Game_Project_Template', 'MeuSuperJogo'
```

3.  Agora você pode construir e executar seu novo jogo usando os mesmos comandos Gradle, substituindo `Game` pelo nome do seu projeto:

```bash
./gradlew :MeuSuperJogo:build
./gradlew :MeuSuperJogo:run
```

### Criando um Novo Jogo com a Engine Local (Maven)

Se você publicou a engine em seu repositório Maven local, pode criar um novo projeto Gradle em qualquer lugar do seu sistema de arquivos e usar a engine como uma dependência.

**1. Crie um novo projeto Gradle**

Crie uma nova pasta para o seu jogo e, dentro dela, crie um arquivo `build.gradle` com o seguinte conteúdo:

```groovy
plugins {
    id 'java'
    id 'application'
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation 'com.JDStudio:Engine:1.0.0'
}

application {
    mainClass = 'com.meujogo.Main'
}
```

**2. Crie a estrutura de pastas**

Crie a seguinte estrutura de pastas dentro do seu projeto:


## Documentação Completa

Consulte a [documentação](./Documentaçao/README.md) incluída para detalhes de cada sistema, exemplos e tutoriais.

## Autor

- **JD Studio**

## Licença

Este projeto está sob a licença MIT.
