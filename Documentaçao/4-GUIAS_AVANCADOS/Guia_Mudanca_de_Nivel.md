# Guia Avançado: Mudança de Nível Data-Driven

Este guia descreve como implementar um sistema de transição entre mapas, definido diretamente no editor Tiled para um fluxo de trabalho rápido e flexível.

## O Conceito

1.  No **Tiled**, cria-se uma área retangular invisível (a "Zona de Saída").
2.  Adiciona-se a esta zona uma propriedade customizada que informa à engine o caminho para o **próximo mapa** (ex: `/maps/level2.json`).
3.  A engine carrega essa zona como um `GameObject` especial (`LevelExitZone`).
4.  Quando o jogador colide com esta zona, a engine inicia uma transição de tela e carrega um novo `PlayingState` com o mapa especificado.

### Passo 1: Preparar o `PlayingState`

Para que a engine possa carregar diferentes níveis, o `PlayingState` deve ser reutilizável, aceitando o caminho do mapa como um parâmetro em seu construtor.

```java
// Em PlayingState.java
public class PlayingState extends EnginePlayingState {
    // Construtor que aceita o caminho do mapa
    public PlayingState(String mapPath) {
        // ...
        world = new World(mapPath, this);
        // ...
    }
    
    // Construtor de conveniência para o primeiro nível
    public PlayingState() {
        this("/maps/level1.json");
    }
}
```
### Passo 2: Criar a `LevelExitZone.java`
Esta classe de jogo herda de `TriggerZone` da engine e sua única responsabilidade é ler e armazenar o caminho para o próximo mapa a partir das propriedades do Tiled.
```java
// Em com.meujogo.objects
public class LevelExitZone extends TriggerZone {
    public final String nextMapPath;

    public LevelExitZone(JSONObject properties) {
        super(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.nextMapPath = reader.getString("nextMap", null);
    }
}
```
### Passo 3: Configurar o Objeto no **Tiled**
No **Tiled**, em uma camada de objetos, desenhe um retângulo onde será a saída.

1. Com o retângulo selecionado, defina sua Classe (ou **"Type"**) como `LevelExit`.

2. Adicione uma Propriedade Customizada do tipo `string` com o Nome `nextMap` e o Valor sendo o caminho para o mapa de destino (ex: `/maps/level2.json`).

### Passo 4: Implementar a Lógica no Jogo
1. **Criação**: No `onObjectFound` do `PlayingState`, adicione um case para criar a `LevelExitZone`.
    ```java
    // Em PlayingState -> onObjectFound
    case "LevelExit":
        LevelExitZone exit = new LevelExitZone(properties);
        addGameObject(exit); // Adiciona ao mundo
        // Adicione 'exit' a uma lista de zonas de saída para facilitar a verificação de colisão
        break;
    ```
2. Verificação: No `tick()` do `PlayingState` (ou em um método de verificação de colisões), verifique se o jogador está colidindo com alguma `LevelExitZone`.
    ```java
    // Em PlayingState -> tick()
    for (LevelExitZone exit : levelExits) { // Itera sobre as zonas de saída
        if (GameObject.isColliding(player, exit)) {
            // Inicia a transição para o novo nível
            Engine.transitionToState(new PlayingState(exit.nextMapPath));
            break; // Sai do loop para não acionar múltiplas transições
        }
    }
    ```
---
[⬅️ Voltar para o Guias Avançados](./README.md)