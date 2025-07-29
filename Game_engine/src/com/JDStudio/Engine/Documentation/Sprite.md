# Pacote: com.JdStudio.Engine.Graphics

Contém o gerenciador de recursos visuais.

## Classe `AssetManager`

Centraliza o carregamento e o acesso a todos os `Sprite` do jogo. Usa um cache para evitar carregar a mesma imagem múltiplas vezes.

### Métodos Principais

-   `loadSprite(key, path)`: Carrega uma imagem de um arquivo e a armazena no cache com uma chave.
-   `registerSprite(key, sprite)`: Armazena um objeto `Sprite` já existente (útil para sprites de uma `Spritesheet`).
-   `getSprite(key)`: Recupera um `Sprite` do cache usando sua chave.

### Exemplo de Uso

```java
public class Game {
    public static AssetManager assets;

    public void initialize() {
        assets = new AssetManager();
        
        // Carregando sprites individuais
        assets.loadSprite("player_idle", "/sprites/player.png");
        assets.loadSprite("enemy_type_A", "/sprites/enemyA.png");

        // Carregando uma spritesheet e registrando seus recortes
        Spritesheet uiSheet = new Spritesheet("/ui/ui_sheet.png");
        assets.registerSprite("heart_full", uiSheet.getSprite(0, 0, 16, 16));
        assets.registerSprite("heart_empty", uiSheet.getSprite(16, 0, 16, 16));
    }
    
    public void createPlayer() {
        // Usando o sprite carregado
        Sprite playerSprite = assets.getSprite("player_idle");
        Player player = new Player(100, 100, 16, 16, playerSprite);
    }
}
```