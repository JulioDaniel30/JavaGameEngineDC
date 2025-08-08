# Documentação da Engine: Sistema de Mudança de Nível

Esta documentação detalha os componentes chave responsáveis pela funcionalidade de transição entre diferentes mapas (níveis) no jogo.

### Como a Engine a Utiliza 

1.  **No Tiled:** O level designer cria um objeto retangular, define a sua **Classe** como `LevelExit` e adiciona uma **Propriedade Customizada** do tipo `string` chamada `nextMap` com o valor `/map2.json`.
2.  **No `PlayingState.onObjectFound`:** O código deteta um objeto com a classe `LevelExit` e cria uma nova instância de `LevelExitZone`, passando as propriedades.
3.  **No `PlayingState.collisionsUpdate`:** O código verifica continuamente se o `player` está a colidir com algum objeto. Se o objeto for uma `instanceof LevelExitZone`, ele lê a propriedade `nextMapPath` e chama `Engine.transitionToState(new PlayingState(nextMapPath));` para iniciar a transição para o novo nível.

## 1. O Construtor Parametrizado de `PlayingState`

Para que a engine possa carregar diferentes níveis, a classe `PlayingState` foi desenhada para não depender de um único arquivo de mapa fixo (como `"map1.json"`). Em vez disso, o seu construtor foi modificado para aceitar o caminho do mapa como um parâmetro.

**Propósito:** Tornar a classe `PlayingState` reutilizável. A mesma classe pode agora ser usada para carregar e gerir qualquer nível do jogo, bastando para isso que lhe seja fornecido um ficheiro de mapa diferente no momento da sua criação.

### Implementação em `PlayingState.java`

No construtor, o parâmetro `mapPath` é passado diretamente para o construtor da classe `World`, que é responsável por carregar o nível.

```java
// Em PlayingState.java
public class PlayingState extends EnginePlayingState implements IMapLoaderListener {

    // ... (outras variáveis de instância)

    /**
     * Construtor principal que aceita o caminho do mapa a ser carregado.
     * @param mapPath O caminho para o recurso do arquivo de mapa .json (ex: "/map2.json").
     */
    public PlayingState(String mapPath) {
        // ... (inicialização de managers, etc.)
      
        // A engine agora lida com a ordem de carregamento internamente.
        // Carrega o mapa que foi passado como parâmetro.
        world = new World(mapPath, this);
        
        // ... (resto do construtor)
    }
    
    /**
     * Construtor de conveniência sem argumentos.
     * Chama o construtor principal com um mapa padrão (o primeiro nível).
     * É útil para iniciar o jogo a partir do Main.java ou do MenuState.
     */
    public PlayingState() {
        this("/map1.json");
    }

    // ... (resto da classe)
}
````

## 2\. A Classe `LevelExitZone` (Não fornecida, mas reconstruída)

A classe `LevelExitZone` é o "gatilho" data-driven que inicia a mudança de nível. É uma especialização da classe `TriggerZone` da engine, com a responsabilidade adicional de saber para qual mapa o jogador deve ser transportado.

**Propósito:** Desacoplar a lógica de transição do código. Em vez de escrever no código "se o jogador estiver na posição X, carregue o mapa Y", nós definimos esta ligação diretamente no editor de mapas Tiled, tornando o design de níveis mais fácil e flexível.

### Implementação de `LevelExitZone.java`

Esta classe lê uma propriedade customizada do objeto no Tiled para determinar o destino da saída.

```java
// package com.game; (no projeto do Jogo)

import org.json.JSONObject;
import com.JDStudio.Engine.Object.TriggerZone;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Uma zona de gatilho especial que, quando tocada pelo jogador,
 * carrega o próximo nível do jogo.
 */
public class LevelExitZone extends TriggerZone {

    /** O caminho para o arquivo do próximo mapa, lido do Tiled. */
    public final String nextMapPath;

    /**
     * Construtor que recebe as propriedades do objeto a partir do Tiled.
     */
    public LevelExitZone(JSONObject properties) {
        super(properties);
        
        PropertiesReader reader = new PropertiesReader(properties);
        
        // Lê a propriedade customizada "nextMap" do objeto no Tiled.
        // Se a propriedade não existir, retorna null.
        this.nextMapPath = reader.getString("nextMap", null);
    }
}
```