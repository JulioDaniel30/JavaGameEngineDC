# API: Outros Sistemas

Esta página cobre os sistemas de gerenciamento essenciais restantes da engine: Eventos, Salvamento/Carregamento e Áudio.

## Sistema de Eventos (`EventManager`)

A engine utiliza um padrão de **Publicação/Inscrição (Publish/Subscribe)** para desacoplar diferentes partes do código. Em vez de um sistema chamar o outro diretamente, ele "publica" um evento. Outros sistemas podem se "inscrever" para ouvir esse evento e reagir a ele, sem que o publicador precise conhecê-los.

### Como se Inscrever em um Evento

Você pode se inscrever em qualquer evento definido no `EngineEvent` ou em um `enum` de eventos criado pelo seu próprio jogo.

```java
// Em um componente ou GameState que precisa saber quando o mundo foi carregado.
public void someInitMethod() {
    EventManager.getInstance().subscribe(EngineEvent.WORLD_LOADED, (data) -> {
        // O 'data' é um Object, então fazemos o cast para o tipo de dado correto.
        if (data instanceof WorldLoadedEventData) {
            WorldLoadedEventData eventData = (WorldLoadedEventData) data;
            
            // Agora temos acesso ao mundo e aos objetos sem acoplamento direto!
            this.world = eventData.world();
            this.allGameObjects = eventData.gameObjects();
            System.out.println("Mundo recebido via evento!");
        }
    });
}
```
### Como Disparar um Evento
Você pode disparar tanto os eventos da engine quanto os seus próprios.
```java
// Exemplo: Disparando um evento customizado "QUEST_COMPLETED"
// Primeiro, crie seu próprio enum no jogo:
// public enum GameEvent { QUEST_COMPLETED }

// Crie uma classe de dados para o evento (um record é ideal)
// public record QuestCompletedData(String questId, int xpReward) {}

// No código, quando a quest for completada:
QuestCompletedData eventData = new QuestCompletedData("slay_the_dragon", 500);
EventManager.getInstance().trigger(GameEvent.QUEST_COMPLETED, eventData);
```
### Sistema de Salvamento e Carregamento (`SaveManager`)
A engine fornece um sistema simples para salvar e carregar o estado do jogo em arquivos JSON.

#### A Interface `ISavable`
Qualquer classe cujo estado você deseja salvar (ex: `PlayerStats`, `WorldState`) deve implementar a interface `ISavable`.
```java
// Exemplo: Uma classe que guarda as estatísticas do jogador
public class PlayerStats implements ISavable {
    public int level;
    public int xp;

    // ... construtor ...

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("level", this.level);
        state.put("xp", this.xp);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.level = state.getInt("level");
        this.xp = state.getInt("xp");
    }
}
```

#### Usando o `SaveManager`
O `SaveManager` possui métodos estáticos para lidar com os arquivos.

#### Para Salvar:
```java
// Supondo que você tenha objetos 'playerStats' e 'gameQuests' que implementam ISavable
JSONObject fullSaveState = new JSONObject();
fullSaveState.put("playerStats", playerStats.saveState());
fullSaveState.put("activeQuests", gameQuests.saveState());

// Salva o estado completo no arquivo "savegame1.json"
SaveManager.saveToFile(fullSaveState, "savegame1.json");
```

#### Para Carregar:
```java
JSONObject loadedState = SaveManager.loadFromFile("savegame1.json");

if (loadedState != null) {
    playerStats.loadState(loadedState.getJSONObject("playerStats"));
    gameQuests.loadState(loadedState.getJSONObject("activeQuests"));
    System.out.println("Jogo carregado com sucesso!");
}
```

### Sistema de Áudio (`Sound`)
A classe Sound é uma classe utilitária estática para todo o gerenciamento de áudio.

#### Canais de Áudio
O áudio é separado em três canais, permitindo controle de volume independente:

- `Sound.SoundChannel.MUSIC`: Para a trilha sonora.

- `Sound.SoundChannel.SFX`: Para efeitos sonoros do jogo (passos, colisões).

- `Sound.SoundChannel.UI`: Para sons de interface (cliques de botão).
```java
// Define o volume da música para 50% e dos efeitos para 100%
Sound.setChannelVolume(Sound.SoundChannel.MUSIC, 0.5f);
Sound.setChannelVolume(Sound.SoundChannel.SFX, 1.0f);
```
#### Tocando Sons e Músicas
```java
// Tocar um efeito sonoro uma vez
Sound.play("/sfx/player_hit.wav", Sound.SoundChannel.SFX);

// Tocar uma música em loop contínuo
Sound.loop("/music/level1_theme.mp3");

// Parar a música atual
Sound.stopMusic();
```
#### Áudio Espacial (2D)
A engine pode simular a posição de um som no mundo 2D, ajustando seu volume e balanço (pan esquerda/direita) com base na distância da câmera.
```java
// Um inimigo na posição (x, y) emite um som
int enemyX = 500;
int enemyY = 250;
Sound.play("/sfx/enemy_alert.wav", Sound.SoundChannel.SFX, enemyX, enemyY);

// Se o jogador estiver longe do inimigo, o som será mais baixo.
// Se o inimigo estiver à direita do centro da câmera, o som tocará mais no alto-falante direito.
```
