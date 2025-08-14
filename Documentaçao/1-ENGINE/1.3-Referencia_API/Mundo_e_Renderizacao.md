# API: Mundo, Renderização e Efeitos

Esta página cobre como criar o mundo do seu jogo e como a engine o renderiza, incluindo câmera e efeitos visuais como iluminação e partículas.

## Construindo o Mundo com Tiled

A engine é projetada para carregar mapas criados no editor **Tiled** e exportados como arquivos `.json`.

### Fluxo de Trabalho Recomendado

1.  **Crie seu Tileset**: Importe a imagem do seu tileset no Tiled.
2.  **Crie as Camadas de Tiles**: Desenhe seu cenário usando as camadas de tiles. A engine recomenda nomear as camadas de forma lógica (ex: "Background", "Collision", "Foreground") para facilitar a identificação no código.
3.  **Crie uma Camada de Objetos**: Use uma camada de objetos (`Object Layer`) para posicionar entidades como o jogador, inimigos, NPCs e itens.
    * **Propriedade "Classe"**: Para cada objeto, defina sua **Classe** (ou "Type" no Tiled) com o nome exato da classe do seu jogo que você quer instanciar (ex: "Player", "Enemy_Slime", "NPC_Villager").
    * **Propriedades Customizadas**: Adicione propriedades customizadas aos seus objetos para configurar valores iniciais (ex: `maxHealth: 100`, `dialogueFile: "/dialogues/villager1.json"`).

### O `IMapLoaderListener`

A classe `World` da engine não sabe como criar um `Player` ou um `Enemy_Slime`. Para manter a engine desacoplada do seu jogo, ela usa a interface `IMapLoaderListener`.

Seu `GameState` de jogo (ex: `Level1State`) deve implementar esta interface. Quando a engine carrega o mapa, ela notifica seu `GameState` sobre cada tile e objeto encontrado, permitindo que você crie as instâncias específicas do seu jogo.

**Exemplo de implementação:**

```java
public class Level1State extends EnginePlayingState implements IMapLoaderListener {

    public Level1State() {
        // Passa 'this' como o listener para o construtor do World
        this.world = new World("/maps/level1.json", this); 
        // ...
    }

    @Override
    public Tile onTileFound(String layerName, int tileId, int x, int y) {
        // Lógica para criar seus tiles específicos com base no ID e nome da camada
        if (layerName.equals("Collision")) {
            return new WallTile(x, y, assets.getSprite("wall_" + tileId));
        }
        return new FloorTile(x, y, assets.getSprite("floor_" + tileId));
    }

    @Override
    public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
        // Lógica para criar seus GameObjects
        if (type.equals("Player")) {
            addGameObject(new Player(properties));
        } else if (type.equals("Slime")) {
            addGameObject(new Slime(properties));
        }
    }
    
    // ... onPathFound, etc.
}
```
Para ler as propriedades customizadas dos objetos do Tiled de forma fácil, use a classe `PropertiesReader`.

### O Sistema de Renderização
A engine usa um sistema de renderização em camadas para garantir que os elementos visuais sejam desenhados na ordem correta.

- `IRenderable`: Qualquer objeto que possa ser desenhado (`GameObject`, `UIElement`, `Tile`) deve implementar esta interface.

- `RenderLayer`: Define uma camada com um nome e uma profundidade (`depth`). Camadas com maior profundidade são desenhadas por cima.

- `Z-Order`: Dentro da mesma camada, os objetos são ordenados pelo seu Z-Order (`getZOrder()`). Objetos com Z-Order maior são desenhados por cima. Por padrão, a engine usa a posição `Y` do objeto como Z-Order, criando um efeito de profundidade 2.5D naturalmente.

- `RenderManager`: É o singleton que gerencia e executa todo o processo de renderização. Você não precisa chamá-lo diretamente; os `GameObject`s e `UIElement`s se registram e desregistram automaticamente.

#### Camadas Padrão (`StandardLayers`)
A engine fornece um conjunto de camadas padrão para os casos de uso mais comuns, já ordenadas corretamente:

- `PARALLAX_BACKGROUND` (depth 0): Fundos de parallax mais distantes.

- `WORLD_BACKGROUND` (depth 10): Tiles de chão e fundo do cenário.

- `GAMEPLAY_BELOW` (depth 20): Objetos que ficam atrás dos personagens (ex: tapetes).

- `CHARACTERS` (depth 30): O jogador, NPCs e inimigos.

- `GAMEPLAY_ABOVE` (depth 40): Objetos na frente dos personagens (ex: uma mesa pequena).

- `PROJECTILES` (depth 50): Projéteis.

- `PARTICLES` (depth 60): Efeitos de partículas.

- `WORLD_FOREGROUND` (depth 70): Tiles que cobrem os personagens (ex: copas de árvores).

- `LIGHTING` (depth 80): A máscara de iluminação.

- `POPUPS` (depth 90): Popups de dano e outros textos de mundo.

- `UI` (depth 100): A interface do usuário (menus, HUD, etc.).

### A Câmera
A classe `Camera` da engine é flexível e controlada por perfis.

- `Camera.FollowStyle`: Um `enum` com os estilos de seguimento:

- `STATIC`: A câmera fica parada.

- `LOCK_ON_TARGET`: Segue o alvo perfeitamente, travada no centro.

- `SMOOTH_FOLLOW`: Segue o alvo de forma suave (efeito de "lag").

- `CameraProfile`: Um objeto que agrupa um FollowStyle, uma velocidade de suavização (`smoothSpeed`) e um nível de `zoom`.

#### Como Usar:
```java
// Criando um perfil de câmera para gameplay normal
Camera.CameraProfile gameplayProfile = new Camera.CameraProfile(
    Camera.FollowStyle.SMOOTH_FOLLOW, // Estilo
    0.1, // Velocidade de suavização (valores menores são mais suaves)
    1.0  // Zoom normal
);

// Aplicando o perfil à câmera para seguir o jogador
Engine.camera.applyProfile(gameplayProfile, player);

// Para uma cutscene, você pode querer uma câmera estática
Camera.CameraProfile cutsceneProfile = new Camera.CameraProfile(Camera.FollowStyle.STATIC, 1.0, 1.0);
Engine.camera.applyProfile(cutsceneProfile, null); // null como alvo
Engine.camera.setPosition(100, 50); // Posição manual
```
#### Efeito de Tremor (`Shake`):
Para adicionar impacto a explosões ou golpes, use o método `shake`.
```java
// Causa um tremor de intensidade 5 por 30 frames
Engine.camera.shake(5.0, 30);
```
### Efeitos Visuais
#### Iluminação (`LightingManager`)
O `LightingManager` cria uma camada de escuridão sobre a tela e "recorta" áreas de luz.

#### Como Usar:
```java
// 1. Defina a cor e opacidade da escuridão ambiente
LightingManager.getInstance().setAmbientColor(new Color(0, 0, 10, 200)); // Escuridão azulada e forte

// 2. Adicione uma luz circular (ponto de luz)
Light playerLight = new Light(player.getX(), player.getY(), 60, new Color(255, 255, 200, 50));
LightingManager.getInstance().addLight(playerLight);

// 3. Adicione uma luz em formato de cone (lanterna)
// O sprite 'coneSprite' deve ser uma imagem em tons de cinza do formato do cone
Sprite coneSprite = assets.getSprite("flashlight_cone");
ConeLight flashlight = new ConeLight(player.getX(), player.getY(), 120, player.getAngle(), coneSprite, new Color(255, 255, 255, 70));
LightingManager.getInstance().addLight(flashlight);

// A renderização é automática se a camada LIGHTING estiver ativa no RenderManager.
```
### Partículas (`ParticleManager`)
O `ParticleManager` gerencia e reutiliza partículas para criar efeitos de forma eficiente.

#### Como Usar:
A engine fornece um emissor de exemplo, `createExplosion`, que é altamente customizável.

```java
// No momento em que um inimigo morre, crie uma explosão de sangue
ParticleManager.getInstance().createExplosion(
    enemy.getX(), enemy.getY(), // Posição
    50,                         // Quantidade de partículas
    Color.RED,                  // Cor inicial
    new Color(50, 0, 0, 0),     // Cor final (vermelho escuro e transparente)
    30, 60,                     // Duração mínima e máxima (frames)
    1.0, 3.0,                   // Velocidade mínima e máxima
    4.0f, 1.0f                  // Tamanho inicial e final
);

// O ParticleManager precisa ser atualizado e renderizado no seu GameState:
public void tick() {
    // ...
    ParticleManager.getInstance().update();
}

public void render(Graphics g) {
    // ...
    // A renderização das partículas deve ocorrer na sua própria camada para
    // ser desenhada corretamente em relação aos outros objetos.
    // O ideal é registrar um objeto anônimo no RenderManager para isso.
}
```
