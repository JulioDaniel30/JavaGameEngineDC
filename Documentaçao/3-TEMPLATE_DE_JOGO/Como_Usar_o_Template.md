# Como Usar o Template de Jogo

O template de jogo é um projeto mínimo e funcional que serve como o ponto de partida ideal para criar seu próprio jogo com a JDStudio Engine. Ele já contém a estrutura de classes e a inicialização básica, permitindo que você se concentre diretamente na criação da sua jogabilidade.

## Estrutura do Projeto

O template está organizado no pacote `com.game`. Ele contém:
-   `Main.java`: O ponto de entrada que inicializa e roda a engine.
-   `Player.java`: Uma classe de jogador básica com movimento.
-   `GameEvent.java`: Um enum para os eventos do seu jogo.
-   `MenuState.java`: Um estado de menu principal simples.
-   `PlayingState.java`: O estado de jogo principal, pronto para ser preenchido com a lógica do seu nível.

## Passo a Passo para Começar seu Jogo

### 1. Renomeie o Pacote
Antes de tudo, é uma boa prática renomear o pacote de `com.game` para algo único para o seu projeto (ex: `com.meuestudio.meujogo`). A maioria das IDEs (Eclipse, IntelliJ) possui uma ferramenta de "Refactor -> Rename" que faz isso automaticamente em todos os arquivos.

### 2. Configure seu Jogo em `Main.java`
Abra `Main.java`. Aqui você pode configurar as propriedades básicas da janela do seu jogo:
```java
// Em Main.java
Engine engine = new Engine(
    320, // Altere a largura da resolução
    240, // Altere a altura da resolução
    3,   // Altere o fator de escala da janela
    true, // Permite redimensionar a janela?
    "O Título do Meu Jogo", // Altere o título
    60.0 // FPS
);
```
Você também pode adicionar mais vínculos de input no método setupInputBindings().

### 3. Carregue seus Assets em `PlayingState.java`
Abra `PlayingState.java` e vá para o método `loadAssets()`. Este é o lugar para carregar todos os recursos visuais do seu jogo.
```java
// Em PlayingState.java -> loadAssets()
private void loadAssets() {
    // Apague o exemplo e carregue sua própria spritesheet
    Spritesheet minhaSheet = new Spritesheet("/minha_spritesheet.png");
    
    // Registre seus sprites com chaves únicas
    assets.registerSprite("player_idle", minhaSheet.getSprite(0, 0, 16, 16));
    assets.registerSprite("inimigo_slime", minhaSheet.getSprite(16, 0, 16, 16));
    assets.registerSprite("tile_parede", minhaSheet.getSprite(0, 16, 16, 16));
}
```

### 4. Crie seu Mapa e Conecte-o
1. Crie seu primeiro mapa no **Tiled** e exporte-o como `.json` para sua pasta de recursos (ex: `res/maps/`).

2. Em `PlayingState.java`, no construtor, mude o caminho do mapa para o seu:
```java
// Em PlayingState.java -> Construtor
world = new World("/maps/meu_primeiro_mapa.json", this);
```

### 5. Crie seus `GameObject`s
No `PlayingState.java`, vá para o método `onObjectFound`. É aqui que você dará vida aos objetos que colocou no **Tiled**.
```java
// Em PlayingState.java -> onObjectFound
@Override
public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
    if ("player_start".equals(type)) {
        player = new Player(properties);
        this.addGameObject(player);
    } 
    // Adicione um case para seus próprios objetos
    else if ("slime".equals(type)) {
        // Supondo que você criou uma classe Slime.java
        Slime slime = new Slime(properties);
        this.addGameObject(slime);
    }
}
```
### 6. Customize o Menu Principal
Abra MenuState.java e edite o método buildUI() para alterar o texto do título e adicionar mais botões conforme necessário.

## Próximos Passos
Com a base funcionando, você está pronto para expandir. Os próximos passos típicos são:

- Criar classes para seus inimigos (ex: `Slime.java`).

- Adicionar uma HUD (vida, pontuação) no `setupUI()` do `PlayingState`.

- Criar novos estados de jogo, como `GameOverState` ou `LevelCompleteState`.

- Implementar a lógica de colisão e interação no `tick()` do `PlayingState` ou nos próprios `GameObject`s.

Bom desenvolvimento!