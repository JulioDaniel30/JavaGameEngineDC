# Pacote `com.JDStudio.Engine.World`

Este pacote é responsável por tudo relacionado à criação, representação e visualização do cenário do jogo.

## Resumo das Classes e Interfaces

### `World.java`

Gerencia a estrutura do mapa do jogo, que é composto por uma grade de tiles.
- **Carregamento de Mapa:** Lê e interpreta arquivos de mapa no formato JSON exportados pelo editor **Tiled**.
- **Estrutura:** Armazena todos os `Tile`s do cenário em um array.
- **Renderização Otimizada:** Renderiza apenas a porção do mapa que está visível na câmera, evitando processamento desnecessário.
- **Colisão com Cenário:** Oferece o método `isFree()` para verificar se uma determinada área retangular está livre ou se colide com um `Tile` sólido.

### `Tile.java`

Representa um único bloco estático na grade do mundo.
- **Propriedades:** Posição, `Sprite` para sua imagem e uma flag booleana `isSolid` para indicar se bloqueia ou não o movimento de `GameObject`s.

### `Camera.java`

Controla a "visão" do jogador dentro do mundo.
- **Funcionalidades Avançadas:**
    - **Seguir Alvo:** Move-se suavemente para seguir um `GameObject` (geralmente o jogador).
    - **Limites do Mundo:** Garante que a câmera não mostre áreas fora do mapa.
    - **Efeitos:** Implementa funcionalidades de **zoom** e **tremor de tela** (`shake`).

### `IMapLoaderListener.java`

Uma interface (contrato) que desacopla a engine do jogo.
- **Propósito:** O `World` sabe como ler o arquivo JSON, mas não sabe o que fazer com os dados. A classe do jogo que implementa esta interface é quem decide qual `Tile` criar para um ID específico ou qual `GameObject` instanciar quando um objeto é encontrado no mapa do Tiled.

## Como Funciona

O `World` é instanciado com o caminho para um mapa JSON e um `IMapLoaderListener`. Durante a criação, ele lê as camadas de tiles e objetos do arquivo. Para cada tile ou objeto encontrado, ele notifica o `listener` através dos métodos `onTileFound` e `onObjectFound`, permitindo que o jogo crie suas próprias instâncias de tiles e entidades.