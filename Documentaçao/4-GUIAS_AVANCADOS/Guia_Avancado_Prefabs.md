
# Guia Avançado: Criando com Prefabs

O sistema de Prefabs é uma das funcionalidades mais poderosas da JDStudio Engine. Ele permite que você crie "plantas" de `GameObject`s em ficheiros JSON, que podem ser reutilizadas e modificadas sem precisar de alterar o código do jogo. Isto acelera drasticamente o desenvolvimento e a criação de conteúdo.

## 1. O Conceito: A Planta de um `GameObject`

Um Prefab é um ficheiro `.json` que descreve como montar um `GameObject`. Ele define a sua classe base (o "corpo") e a lista de componentes (as "peças") que lhe dão funcionalidade.

### O Formato do Ficheiro JSON de Prefab

Crie uma pasta `res/prefabs/` no seu projeto para organizar estes ficheiros.

**Exemplo (`goblin_patrulheiro.json`):**
```json
{
  "name": "goblin_patrulheiro",
  "baseClass": "com.game.gameObjects.Enemy",
  "components": [
    {
      "class": "com.JDStudio.Engine.Graphics.Sprite.Animations.Animator"
    },
    {
      "class": "com.JDStudio.Engine.Components.HealthComponent"
    },
    {
      "class": "com.JDStudio.Engine.Components.Moviments.AIMovementComponent"
    },
    {
      "class": "com.JDStudio.Engine.Components.InteractionComponent"
    },
    {
      "class": "com.JDStudio.Engine.Components.ShadowComponent"
    }
  ]
}
```

- **name**: O nome único do prefab, que você usará no Tiled.
- **baseClass**: O nome completo (incluindo o pacote) da classe Java que serve de base.
- **components**: Um array de objetos, onde cada objeto define o nome completo da classe do componente a ser adicionado.

## 2. O Fluxo de Trabalho (Passo a Passo)

### Passo 1: Carregar os Prefabs no Início do Jogo

No seu `LoadingState` ou no construtor do `PlayingState`, diga ao `PrefabManager` para carregar as suas "plantas".

```java
// Em PlayingState.java, no construtor

// ...
PrefabManager.getInstance().loadPrefab("/prefabs/goblin_patrulheiro.json");
PrefabManager.getInstance().loadPrefab("/prefabs/porta_madeira.json");
// Carregue todos os seus outros prefabs...
```

### Passo 2: Usar o Prefab no Tiled Editor

Agora, em vez de usar o nome da classe Java no Tiled, você usará o nome do prefab.

1. Crie um objeto na sua camada de objetos.
2. No campo Classe (ou "Type"), digite o nome do prefab (ex: `goblin_patrulheiro`).
3. Adicione as propriedades customizadas que a sua classe base (Enemy) espera (ex: `name`, `speed`, `visionRadius`, etc.).

### Passo 3: Instanciar o Prefab no Jogo

Finalmente, no seu `onObjectFound`, em vez de ter um switch com `new Enemy(...)`, `new Door(...)`, etc., você simplesmente pede ao `PrefabManager` para instanciar o objeto.

```java
// Em PlayingState.java -> onObjectFound()

@Override
public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
    // 'type' agora é o nome do seu prefab (ex: "goblin_patrulheiro")
    
    // Se o tipo for 'player_start', cria o jogador como antes.
    if ("player_start".equals(type)) {
        player = new Player(properties);
        this.addGameObject(player);
        return;
    }
    
    // Para todos os outros objetos, pede ao PrefabManager para os construir.
    GameObject newObject = PrefabManager.getInstance().instantiate(type, properties);
    
    if (newObject != null) {
        this.addGameObject(newObject);
        
        // Se precisar de fazer alguma configuração extra que não está no prefab,
        // pode fazer aqui. Por exemplo, ligar um inimigo a um caminho de patrulha.
        if (newObject instanceof Enemy) {
            // ... (lógica para encontrar o Path e chamar enemy.setPath(...))
        }
    }
}
```

Com este sistema, o seu `onObjectFound` fica extremamente simples, e a lógica de como um "Goblin Patrulheiro" é construído vive num ficheiro JSON, onde pode ser facilmente modificada.

---
[⬅️ Voltar para o Guias Avançados](./README.md)