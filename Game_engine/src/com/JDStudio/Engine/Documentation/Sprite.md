# Pacote `com.JDStudio.Engine.Graphics.Sprite`

Este pacote fornece as classes fundamentais para representar e manipular os recursos visuais básicos do jogo: imagens individuais (sprites) e folhas de sprites (spritesheets).

## Resumo das Classes

### `Sprite.java`

Representa uma única imagem ou "sprite" que pode ser desenhada na tela. Essencialmente, atua como um wrapper para a classe `BufferedImage` do Java.
- **Propósito:** Encapsular a imagem permite futuras extensões sem a necessidade de alterar todas as partes da engine que renderizam imagens.
- **Funcionalidades:** Fornece acesso à imagem (`getImage()`) e às suas dimensões (`getWidth()`, `getHeight()`).

### `Spritesheet.java`

Uma classe utilitária projetada para carregar um único arquivo de imagem grande (uma "folha de sprites") que contém múltiplos sprites menores organizados em uma grade.
- **Funcionalidades:**
    - **Carregamento:** Carrega a imagem da folha de sprites a partir de um caminho no classpath.
    - **Extração:** Possui o método `getSprite(x, y, width, height)` que "recorta" e retorna um `Sprite` individual de uma área específica da folha.

## Como Funciona

Tipicamente, você carrega uma `Spritesheet` uma vez. Em seguida, percorre a folha e extrai todos os `Sprite`s individuais usando `getSprite()`. Esses sprites podem então ser registrados em um `AssetManager` para fácil acesso ou usados para criar objetos `Animation`.