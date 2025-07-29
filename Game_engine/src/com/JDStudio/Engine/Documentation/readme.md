# Documentação do JdStudio Engine

Bem-vindo à documentação oficial do JdStudio Engine. Este documento serve como um ponto de partida e sumário para entender a arquitetura e os componentes do engine.

O objetivo deste engine é fornecer uma base robusta e extensível para a criação de jogos 2D em Java, com foco em uma arquitetura baseada em componentes e estados.

## Sumário da Documentação

Navegue pelos pacotes abaixo para encontrar detalhes sobre cada parte do engine.

* **Core e Inicialização**
    * [Engine](Engine.md) - O coração do engine, responsável pelo loop principal e gerenciamento da janela.
    * [Input](Input.md) - Gerenciador de entrada do teclado (Singleton).
    * [Sound](Sound.md) - Classe utilitária estática para tocar sons e músicas.

* **Estrutura do Jogo**
    * [States](States.md) - Classes para gerenciar diferentes estados do jogo (ex: menu, jogando).
    * [Object](Object.md) - As classes base para todos os objetos do jogo (`GameObject`, `Character`).

* **Mundo e Cenário**
    * [World](World.md) - Classes responsáveis por carregar e renderizar o mundo do jogo, incluindo tiles e câmera.

* **Gráficos e Renderização**
    * [Graphics](Graphics.md) - Gerenciador de recursos gráficos (`AssetManager`).
    * [Graphics.Sprite](Sprite.md) - Classes para representar e manipular imagens (`Sprite`, `Spritesheet`).
    * [Graphics.Sprite.Animations](Animations.md) - Sistema de animação baseado em frames.
    * [Graphics.UI](Graphics.UI.md) - Componentes para criar interfaces de usuário.

* **Componentes**
    * [Components.Moviments](MovimentsComponet.md) - Sistema de componentes para gerenciar a lógica de movimento.