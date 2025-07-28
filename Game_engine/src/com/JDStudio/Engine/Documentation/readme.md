# Documentação da JDStudio Engine

Bem-vindo à documentação oficial da JDStudio Engine. Este documento serve como um guia central e ponto de partida para entender a arquitetura e os componentes da engine.

Utilize a lista abaixo para navegar até a documentação detalhada de cada módulo.

## Módulos da Engine

Aqui está a descrição de cada módulo documentado e o link para seus detalhes. Os arquivos estão listados na imagem fornecida.

* ###  core-engine
    -   **[Engine.md](Engine.md)**: Detalha o núcleo da engine, incluindo o game loop, gerenciamento de janela e o sistema de estados de jogo (`GameState`).

* ### objetos-e-componentes
    -   **[Object.md](Object.md)**: Explica a classe base `GameObject`, o pilar para todas as entidades dinâmicas do jogo.
    -   **[Componets.md](Componets.md)**: Descreve a arquitetura de componentes, focando no `MovementComponent` para movimento e colisão.

* ### mundo-e-camera
    -   **[World.md](World.md)**: Cobre o carregamento de mapas do Tiled, o sistema de tiles e a verificação de colisões com o cenário.

* ### graficos-e-animacao
    -   **[Graphics.md](Graphics.md)**: Apresenta o `AssetManager` para gerenciamento e cache de recursos visuais.
    -   **[Sprite.md](Sprite.md)**: Define as classes para manipulação de `Sprite` e `Spritesheet`.
    -   **[Animations.md](Animations.md)**: Explica o sistema de animação, incluindo as classes `Animation` e `Animator`.
    -   **[UI.md](UI.md)**: Descreve o sistema para criação e gerenciamento de Interface de Usuário (`UIElement`, `UIManager`, etc.).

* ### sistemas-auxiliares
    -   **[Input.md](Input.md)**: Documenta o `InputManager` para um controle centralizado do teclado.
    -   **[Sound.md](Sound.md)**: Detalha a classe utilitária para tocar sons e músicas.