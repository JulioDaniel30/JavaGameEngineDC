# JD Studio Engine - Guia Prático com Jogo de Exemplo

Bem-vindo à documentação do **JD Studio Engine**, um motor de jogos 2D construído em Java. Este guia prático utiliza um **jogo de exemplo completo** (um dungeon crawler top-down) para demonstrar como usar os recursos do motor para criar seus próprios jogos.

## Visão Geral do Jogo de Exemplo

O jogo incluído é um "dungeon crawler" onde o jogador controla um personagem em um mapa. O objetivo é navegar pelo cenário, que é carregado dinamicamente a partir de um arquivo de imagem. O jogo demonstra funcionalidades como:

  * Controle do jogador com animações de caminhada e idle.
  * Colisão com as paredes do cenário.
  * Uma câmera que segue o jogador.
  * Inimigos e itens (pacotes de vida, armas, munição) posicionados no mapa.
  * Uma interface de usuário (UI) que exibe a vida do jogador em tempo real.
  * Música de fundo e controle de volume.

## Recursos Necessários (Assets)

Para que o jogo de exemplo funcione, os seguintes arquivos devem estar em uma pasta de recursos (ex: `res/`) no Classpath do seu projeto:

  * `/spritesheet.png`: Uma folha de sprites contendo todos os gráficos do jogo (personagens, tiles, itens).
  * `/level1.png`: Uma imagem que define o layout do mapa.
  * `/music.wav`: O arquivo de música de fundo.

## Tutorial Passo a Passo: Construindo o Jogo com a Engine

Vamos desconstruir o jogo de exemplo para entender como ele usa o motor.

### 1\. O Ponto de Entrada (`Main.java`)

Tudo começa na classe `Main`. Sua única responsabilidade é inicializar o motor e definir o primeiro estado de jogo.

```java
// Em: com.JDStudio.Game.Main.java
public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();
        // Define a cena inicial para o nosso jogo
        Engine.setGameState(new PlayingState());
        engine.start();
    }
}
```

### 2\. O Coração do Jogo (`PlayingState.java`)

A classe `PlayingState` gerencia tudo o que acontece na tela principal do jogo. Ela estende `GameState` do motor.

#### a) Carregando Assets

No construtor, a primeira coisa a fazer é carregar todos os recursos gráficos. O `AssetManager` e a `Spritesheet` do motor tornam isso simples:

```java
// Dentro de PlayingState.java
private void loadAssets() {
    Spritesheet worldSheet = new Spritesheet("/spritesheet.png");

    // Registra sprites individuais com uma chave única
    assets.registerSprite("tile_wall", worldSheet.getSprite(16, 0, 16, 16));
    assets.registerSprite("player_idle", worldSheet.getSprite(32, 0, 16, 16));
    // ...e assim por diante para todos os outros sprites.
}
```

#### b) Configurando a UI

O `UIManager` do motor é usado para exibir a vida do jogador. Um `UIText` é criado com uma função lambda `() -> "Vida: ..."` , o que faz com que o texto se atualize automaticamente a cada frame.

```java
// Dentro de PlayingState.java
private void setupUI() {
    UIText lifeText = new UIText(
        5, 15, // Posição (x, y)
        new Font("Arial", Font.BOLD, 12),
        Color.WHITE,
        () -> "Vida: " + (int)player.life + "/" + (int)player.maxLife
    );
    uiManager.addElement(lifeText);
}
```

#### c) Lógica Principal (`tick()`)

O método `tick()` orquestra a lógica do jogo a cada frame:

1.  Atualiza todos os `GameObjects` (incluindo o `Player`).
2.  Verifica colisões entre objetos.
3.  Processa inputs globais (como volume e modo de debug).
4.  Posiciona a câmera para seguir o jogador.
5.  Atualiza o `InputManager`.

<!-- end list -->

```java
// Dentro de PlayingState.java
@Override
public void tick() {
    // ...
    for (int i = 0; i < gameObjects.size(); i++) {
        gameObjects.get(i).tick();
    }
    // ... (lógica de colisão) ...
    Camera.x = Camera.clamp(player.getX() - (Engine.WIDTH / 2), 0, world.WIDTH * 16 - Engine.WIDTH);
    // ...
    InputManager.instance.update();
}
```

### 3\. Criando o Mundo (`World.java`)

A classe `World` do jogo estende a classe `World` do motor e a especializa para carregar o mapa a partir de um arquivo de imagem (`level1.png`).

O design do nível é controlado pelas cores dos pixels na imagem. Cada cor corresponde a um tipo de tile ou objeto:

| Cor          | Código Hex  | Objeto Gerado                             |
|--------------|-------------|-------------------------------------------|
| **Branco** | `#FFFFFF`   | Parede (`WallTile`)                       |
| **Azul** | `#0026FF`   | Posição inicial do Jogador (`Player`)     |
| **Vermelho** | `#FF0000`   | Inimigo (`Enemy`)                         |
| **Laranja** | `#FF6A00`   | Arma (`Weapon`)                           |
| **Rosa** | `#FFFF7F7F` | Pacote de Vida (`Lifepack`)               |
| **Amarelo** | `#FFFFD800` | Munição (`Bullet`)                        |
| **Preto** | `#000000`   | Chão (`FloorTile`) - ou qualquer outra cor não listada |

```java
// Dentro do construtor de World.java
int pixelAtual = pixels[xx + (yy * map.getWidth())];
if (pixelAtual == 0xFFFFFFFF) { // Parede
    tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, PlayingState.assets.getSprite("tile_wall"));
} else if (pixelAtual == 0xFFFF0000) { // Inimigo
    state.addGameObject(new Enemy(xx * 16, yy * 16, 16, 16, PlayingState.assets.getSprite("enemy")));
}
// etc...
```

### 4\. Implementando o Jogador (`Player.java`)

O `Player` é um `GameObject` com lógica para movimento e animação.

#### a) Movimento e Colisão com o Mundo

No método `tick()`, o `InputManager` é usado para verificar as teclas pressionadas. Antes de mover o jogador, o método `world.isFree()` é chamado para garantir que a nova posição não seja dentro de um tile sólido.

```java
// Dentro de Player.java
if (InputManager.isKeyPressed(KeyEvent.VK_D)) {
    // Verifica se o caminho está livre ANTES de mover
    if (world.isFree((int)(x + speed), this.getY(), ...)) {
        x += speed;
        isMoving = true;
        animator.play("walk_right");
    }
}
```

#### b) Gerenciando Animações

O `Player` usa o componente `Animator` (herdado de `GameObject`) para gerenciar suas animações. Primeiro, as animações são criadas e adicionadas ao `Animator`.

```java
// Dentro de Player.java, no método setupAnimations()
Animation walkRightAnim = new Animation(10, 
    PlayingState.assets.getSprite("player_walk_right_1"),
    PlayingState.assets.getSprite("player_walk_right_2"),
    PlayingState.assets.getSprite("player_walk_right_3")
);
animator.addAnimation("walk_right", walkRightAnim);
animator.addAnimation("idle", idleAnim);
```

Depois, no método `tick()`, `animator.play()` é chamado para trocar a animação ativa com base nas ações do jogador.

```java
// Dentro de Player.java, no método tick()
if (isMoving) {
    animator.play("walk_right"); // ou walk_left, etc.
} else {
    animator.play("idle");
}
```

### 5\. Adicionando Som

Tocar música e efeitos sonoros é simples com a classe `Sound` do motor.

```java
// Em PlayingState.java, para tocar a música de fundo em loop
Sound.loop("/music.wav");

// Em outra classe, para tocar um efeito sonoro uma vez
// Sound.play("/sfx_jump.wav");
```

## Conclusão

Este jogo de exemplo demonstra como as classes modulares do **JD Studio Engine** (`GameState`, `GameObject`, `World`, `InputManager`, `AssetManager`, `Animator`, etc.) trabalham juntas para criar uma experiência de jogo completa e funcional. Use este projeto como base para experimentar, modificar e criar seus próprios jogos\!