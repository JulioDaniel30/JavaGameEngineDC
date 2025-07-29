# Pacote: com.JdStudio.Engine.World

Classes responsáveis pela representação e gerenciamento do ambiente do jogo.

## Classe `World`

Carrega e gerencia um mapa baseado em tiles a partir de um arquivo JSON (exportado pelo editor Tiled). Renderiza o cenário e verifica colisões contra ele.

## Interface `IMapLoaderListener`

Um contrato que você deve implementar para ensinar ao `World` como construir os objetos específicos do *seu jogo* a partir dos dados do mapa.

## Classe `Camera`

Gerencia a posição da "câmera" no mundo, controlando qual parte do mapa é visível.

### Visão Geral da Câmera

-   **Smooth Follow**: Segue um `GameObject` alvo com suavidade.
-   **Shake**: Efeito de tremor.
-   **Zoom**: Permite aplicar zoom na renderização.
-   **Clamping**: Garante que a câmera não mostre áreas fora do mapa.

## Classe `Tile`

Representa um único bloco no grid do mapa. Pode ser visual e/ou sólido.

### Exemplo de Uso (`World` e `IMapLoaderListener`)

Para carregar um mapa, você precisa de uma classe que implemente `IMapLoaderListener`.

```java
// Em seu estado de jogo (ex: Level1State)

public class Level1State extends EnginePlayingState implements IMapLoaderListener {
    
    private World world;
    private Player player;

    public Level1State() {
        super();
        assets = new AssetManager();
        assets.loadSprite("wall_tile", "/tiles/wall.png");
        assets.loadSprite("floor_tile", "/tiles/floor.png");
        
        // O World precisa do listener para construir os objetos
        world = new World("/maps/level1.json", this);
    }
    
    // O World vai chamar este método para cada TILE que encontrar no JSON
    @Override
    public Tile onTileFound(String layerName, int tileId, int x, int y) {
        if (layerName.equals("Walls")) {
            // ID 1 no Tiled corresponde à nossa parede
            if (tileId == 1) { 
                Tile wall = new Tile(x, y, assets.getSprite("wall_tile"));
                wall.isSolid = true;
                return wall;
            }
        }
        if (layerName.equals("Floors")) {
            // ID 2 no Tiled corresponde ao nosso chão
            if (tileId == 2) {
                return new Tile(x, y, assets.getSprite("floor_tile"));
            }
        }
        return null; // Tile vazio
    }

    // O World vai chamar este método para cada OBJETO que encontrar no JSON
    @Override
    public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
        if (type.equals("PlayerStart")) {
            player = new Player(x, y);
            addGameObject(player);
        } else if (type.equals("EnemySpawner")) {
            Enemy enemy = new Enemy(x, y);
            addGameObject(enemy);
        }
    }

    @Override
    public void render(Graphics g) {
        // Renderiza o mapa primeiro
        world.render(g);
        // Depois renderiza os objetos
        super.render(g);
    }
}
```