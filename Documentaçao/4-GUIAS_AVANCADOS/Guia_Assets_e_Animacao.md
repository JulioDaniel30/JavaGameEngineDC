# Guia Completo: Assets, Sprites e Animações

Este guia fornece um passo a passo completo sobre como carregar imagens (`Sprites`) e criar sequências animadas (`Animation`) usando a JDStudio Engine. Ele cobre todos os métodos disponíveis, desde o carregamento de uma única imagem até o fluxo de trabalho automatizado com **Aseprite**.

---

### Parte 1: Sprites - As Imagens do Jogo

Um `Sprite` é o objeto fundamental para qualquer elemento visual no jogo. É basicamente um contêiner para uma imagem. Existem três maneiras principais de carregar e registrar um `Sprite` para que ele possa ser usado no jogo. Em todos os casos, usamos um `AssetManager` para centralizar e armazenar os sprites em cache.

#### Método 1: Carregar um Sprite de um Arquivo Individual

Esta é a forma mais simples, ideal para imagens únicas como ícones, itens de inventário ou personagens que têm um único frame.

**Fluxo:** `Arquivo de Imagem (.png) -> AssetManager -> Sprite`

```java
// Em um método de carregamento, como loadAssets()
AssetManager assets = new AssetManager();

// Usa loadSprite() para carregar a imagem diretamente do sistema de arquivos
// e registrá-la no cache com a chave "icone_espada".
assets.loadSprite("icone_espada", "/sprites/itens/espada.png");
assets.loadSprite("avatar_heroi", "/sprites/ui/avatar_heroi.png");

// Para usar o sprite depois:
Sprite espadaSprite = assets.getSprite("icone_espada");
```

#### Método 2: Recortar Sprites de uma Spritesheet

Esta é a abordagem mais comum e eficiente para personagens e tiles. Você carrega uma única imagem grande (`Spritesheet`) que contém vários sprites e depois recorta cada um individualmente.

**Fluxo:** `Spritesheet (.png) -> Recortar Sprites -> Registrar no AssetManager`

```java
// Em um método de carregamento
AssetManager assets = new AssetManager();

// 1. Carrega a folha de sprites inteira na memória
Spritesheet personagemSheet = new Spritesheet("/sprites/personagem_sheet.png");

// 2. Recorta cada sprite individualmente usando coordenadas (x, y, largura, altura)
Sprite playerIdle = personagemSheet.getSprite(0, 0, 16, 16);
Sprite playerWalk1 = personagemSheet.getSprite(16, 0, 16, 16);
Sprite playerWalk2 = personagemSheet.getSprite(32, 0, 16, 16);
Sprite playerJump = personagemSheet.getSprite(48, 0, 16, 16);

// 3. Registra cada sprite recortado no AssetManager com uma chave única
assets.registerSprite("player_idle", playerIdle);
assets.registerSprite("player_walk_1", playerWalk1);
assets.registerSprite("player_walk_2", playerWalk2);
assets.registerSprite("player_jump", playerJump);

// Para usar depois:
Sprite frameAndar = assets.getSprite("player_walk_1");
```

#### Método 3: Carregar Sprites em Lote via JSON

Esta é a abordagem mais poderosa e organizada para carregar múltiplos sprites, combinando os dois métodos anteriores. A engine possui duas formas de fazer isso a partir de um arquivo de configuração JSON.

**A. Carregar Arquivos de Imagem Individuais via JSON**

Ideal para organizar o carregamento de muitos sprites que estão em arquivos separados.

1. **Crie o arquivo `sprites.json`:**
    ```json
    {
      "sprites": [
        { "key": "player_idle", "path": "/sprites/player.png" },
        { "key": "enemy_slime", "path": "/sprites/slime.png" }
      ]
    }
    ```

2. **Chame o método `loadSpritesFromJson`:**
    ```java
    // Carrega todos os sprites definidos no arquivo JSON de uma só vez
    assets.loadSpritesFromJson("/configs/sprites.json");
    ```

**B. Recortar Múltiplos Sprites de uma Spritesheet via JSON (Recomendado para Tilesets)**

Permite definir como recortar uma única `Spritesheet` diretamente no JSON.

1. **Crie o arquivo de definição (ex: `tileset_grass.json`).**
2. **Chame o método `loadSpritesFromSpritesheetJson`:**
    ```java
    // Esta única linha carrega a spritesheet e recorta todos os sprites definidos no JSON
    assets.loadSpritesFromSpritesheetJson("/configs/tileset_grass.json");
    ```

### O Formato do JSON de Definição de Spritesheet

O arquivo JSON tem uma estrutura principal:

- `"spritesheetPath"`: O caminho para o arquivo de imagem `.png`.
- `"definitions"`: Um array que pode conter múltiplos blocos de definição, dos tipos `grid` ou `manual`.

#### Definição do Tipo "manual"

Ideal para spritesheets "empacotadas", onde os sprites têm tamanhos e posições diferentes.

```json
{
  "spritesheetPath": "/sprites/player_sheet.png",
  "definitions": [
    {
      "type": "manual",
      "sprites": [
        { "key": "player_jump_fall", "x": 64, "y": 0, "w": 16, "h": 20 },
        { "key": "player_portrait", "x": 0, "y": 32, "w": 32, "h": 32 }
      ]
    }
  ]
}
```
Isto irá recortar e registrar dois sprites com tamanhos e nomes específicos.

#### Definição do Tipo "grid"

Ideal para tilesets uniformes. Ele percorre uma grelha e nomeia os sprites com um prefixo e um número sequencial. A engine suporta dois formatos para grid: um para múltiplas linhas (recomendado) e um para uma única linha (retrocompatibilidade).

**Formato Multi-Linha (Recomendado):**
```json
{
  "spritesheetPath": "/tilesets/grass_sheet.png",
  "definitions": [
    {
      "type": "grid",
      "prefix": "grass_v_",
      "spriteWidth": 16,
      "spriteHeight": 16,
      "numCols": 8,
      "numRows": 4
    }
  ]
}
```
Isto irá gerar 32 sprites, com as chaves `grass_v_1` a `grass_v_32`.

**Formato de Linha Única (Compatibilidade):**
Este formato é útil para recortar uma única linha de frames de uma spritesheet de animação.

```json
{
  "spritesheetPath": "/sprites/player_sheet.png",
  "definitions": [
    {
      "type": "grid",
      "prefix": "player_walk_down_",
      "spriteWidth": 16,
      "spriteHeight": 16,
      "count": 4
    }
  ]
}
```
Isto irá gerar 4 sprites, `player_walk_down_1` a `player_walk_down_4`, a partir da primeira linha (y=0) da spritesheet.

**Propriedades Opcionais para "grid" (`startX`, `startY`, `startIndex`):**

- `startX` e `startY`: Use estas propriedades se a sua grelha não começar no canto superior esquerdo (0,0) da imagem.
- `startIndex`: Define o número inicial para a chave do sprite. Se omitido, começará em 1.

**Exemplo Combinado:**
```json
{
  "spritesheetPath": "/tilesets/terreno_parte2.png",
  "definitions": [
    {
      "type": "grid",
      "prefix": "terreno_",
      "spriteWidth": 16,
      "spriteHeight": 16,
      "startX": 32,
      "startY": 16,
      "numCols": 8,
      "numRows": 2,
      "startIndex": 33
    }
  ]
}
```
Isto irá ler uma grelha de 8x2 a partir da posição (32, 16) da imagem e irá gerar os sprites `terreno_33` a `terreno_48`.

#### Definição do Tipo "full_grid" (Automático)

Este é o método mais rápido para recortar um tileset inteiro onde todos os tiles têm o mesmo tamanho. Você só precisa especificar o tamanho de um tile, e a engine calcula automaticamente quantas colunas e linhas existem na imagem.

**Uso:** Ideal para importar um tileset completo com uma única definição.

```json
{
  "spritesheetPath": "/tilesets/terreno_completo.png",
  "definitions": [
    {
      "type": "full_grid",
      "prefix": "terreno_",
      "spriteWidth": 16,
      "spriteHeight": 16,
      "startIndex": 1
    }
  ]
}
```

**Como Funciona:**  
O AssetManager irá pegar a largura total da imagem `terreno_completo.png` e dividi-la por `spriteWidth` (16) para descobrir o número de colunas. Fará o mesmo com a altura para descobrir o número de linhas.

**Resultado:**  
Se a imagem `terreno_completo.png` tiver 128x64 pixels, a engine irá calcular `numCols = 8` (128/16) e `numRows = 4` (64/16), gerando automaticamente os sprites

---

### Parte 2: Animações - Dando Vida aos Sprites

Uma `Animation` é uma sequência de `Sprites` que são exibidos ao longo do tempo. O `Animator` é um componente que gerencia e executa essas animações. Existem duas maneiras de criar e registrar uma `Animation`.

#### Método 1: Criar Animações Manualmente (Programaticamente)

Este método oferece controle total e é ideal para animações simples, efeitos visuais ou quando você não está a usar o Aseprite.

**Fluxo:** `Recortar Sprites -> Criar Objeto Animation -> Adicionar ao Animator`

```java
// Em um método de inicialização do seu GameObject (ex: no construtor)

// Pré-requisito: Os sprites já devem ter sido carregados e registrados no AssetManager
// (usando o Método 2 da seção de Sprites).
Sprite walk1 = assets.getSprite("player_walk_1");
Sprite walk2 = assets.getSprite("player_walk_2");

// 1. Cria a instância da Animação
// Parâmetros: (velocidade em ticks, é em loop?, frames...)
Animation walkAnim = new Animation(20, true, walk1, walk2);

// 2. Adiciona a animação criada a um componente Animator
Animator animator = new Animator();
animator.addAnimation("walk", walkAnim);

// 3. Adiciona o componente Animator ao GameObject
this.addComponent(animator);
```

#### Método 2: Carregar Animações do Aseprite (Método Recomendado)

Este é o fluxo de trabalho mais rápido e poderoso, integrando a engine diretamente com o Aseprite.

**Fluxo:** `Aseprite (com Tags) -> Exportar (PNG + JSON) -> AnimationLoader -> Map<String, Animation> -> Adicionar ao Animator`

1.  **No Aseprite**: Crie suas animações e, o mais importante, defina **tags de animação** para cada sequência (ex: "idle_right", "walk_right"). O nome da tag se tornará a chave da animação na engine.
2.  **Exporte**: Exporte a Spritesheet (`.png`) e os dados da animação (`.json` no formato "Array").
3.  **No Código**:

```java
// Em um método de carregamento de assets
Spritesheet playerSheet = new Spritesheet("/sprites/player_sheet.png");

// 1. O AnimationLoader faz todo o trabalho: lê o JSON, recorta a folha de sprites
// e cria todos os objetos Animation de uma só vez.
Map<String, Animation> playerAnims = AnimationLoader.loadFromAsepriteJson(
    "/anims/player_data.json",
    playerSheet,
    true // true para criar automaticamente versões "left" a partir das "right"
);

// No construtor do seu GameObject (ex: Player.java)
Animator animator = new Animator();

// 2. Adiciona TODAS as animações carregadas ao Animator de uma vez
animator.getAnimations().putAll(playerAnims);

// 3. Adiciona o componente ao GameObject e define a animação inicial
this.addComponent(animator);
animator.play("idle_right");
```

Com esta abordagem, você não precisa recortar sprites ou criar animações manualmente no código, tornando o processo muito mais rápido e menos propenso a erros.

#### Método 3: Carregar Animações de um JSON Genérico (Data-Driven)

Esta abordagem é uma alternativa flexível ao Aseprite. Ela permite definir animações em um arquivo JSON, fazendo referência a sprites que já foram carregados no `AssetManager`.

**Fluxo:** Registrar Sprites -> JSON de Animação -> AnimationLoader -> Animator

**Passo 1:** Carregue os Sprites dos Frames Primeiro  
Garanta que todos os sprites que serão usados nos frames da animação (ex: `"player_walk_1"`, `"player_walk_2"`) já estejam no `AssetManager`.

**Passo 2:** Crie o arquivo `animations.json`:

```json
{
  "animations": [
    {
      "key": "player_walk",
      "speed": 10,
      "loop": true,
      "frames": [
        "player_walk_1",
        "player_walk_2",
        "player_walk_3",
        "player_walk_4"
      ]
    },
    {
      "key": "explosion",
      "speed": 8,
      "loop": false,
      "frames": ["exp_1", "exp_2", "exp_3"]
    }
  ]
}
```

**Passo 3:** Chame o método `loadFromJson`:

```java
// 'assets' é a instância do AssetManager que já contém os sprites dos frames
Map<String, Animation> minhasAnims = AnimationLoader.loadFromJson("/configs/animations.json", assets);

// O resultado é um Map que pode ser usado para popular um Animator
animator.getAnimations().putAll(minhasAnims);
```

**Vantagens:**  
- Totalmente data-driven: fácil editar ou adicionar animações sem mexer no código Java  
- Reutiliza sprites já carregados  
- Ideal para animações simples, efeitos ou quando não se usa Aseprite

---
[⬅️ Voltar para o Guias Avançados](./README.md)