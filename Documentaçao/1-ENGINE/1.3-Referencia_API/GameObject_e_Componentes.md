# API: GameObject e o Sistema de Componentes

O núcleo de qualquer jogo feito com a JDStudio Engine é a interação entre `GameObject`s e `Component`s. Esta página detalha como eles funcionam.

## GameObject: A Entidade Base

Um `GameObject` é a classe fundamental para qualquer "coisa" que existe no seu mundo de jogo: o jogador, inimigos, itens, NPCs, projéteis, etc. Sozinho, um `GameObject` é apenas um contêiner com posição, tamanho e um nome. Seu verdadeiro poder vem da sua capacidade de ter **Componentes** adicionados a ele.

### Instanciação a partir do Tiled

A forma mais comum de criar um `GameObject` é através do editor de mapas Tiled. Ao criar uma camada de objetos, cada objeto que você desenha pode ter uma "Classe" (ou "Type") e propriedades customizadas. A engine usa isso para instanciar a classe de `GameObject` correta do seu jogo.

```java
// Dentro do seu IMapLoaderListener
@Override
public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
    if (type.equals("Player")) {
        // 'properties' contém todas as informações do objeto no Tiled
        Player player = new Player(properties);
        // Adicione o jogador à sua lista de objetos no GameState
        addGameObject(player);
    }
}
```
### Sistema de Anexos (`Attachments`)

`GameObjects` podem ser organizados em hierarquias de pai/filho. Quando um objeto "filho" é anexado a um "pai", sua posição no mundo é atualizada automaticamente em relação à posição do pai. Isso é perfeito para criar coisas como uma espada que segue a mão do jogador, ou um escudo nas costas de um inimigo.

- `void attach(GameObject child, int localX, int localY)`: Anexa um objeto filho a este, com um deslocamento (posição local).

- `void detach()`: Desanexa este objeto do seu pai.

- `GameObject getChildByName(String name)`: Procura por um filho anexado pelo seu nome

#### Exemplo
```java
// Supondo que 'player' e 'sword' são GameObjects
// A espada ficará 10 pixels à direita e 5 pixels abaixo da origem do jogador.
player.attach(sword, 10, 5); 
```

### Sistema de Colisão

- `CollisionType`: Um enum que define como um objeto se comporta em colisões (`NO_COLLISION`, `SOLID`, `TRIGGER`, `CHARACTER_SOLID`).

- `setCollisionMask(x, y, w, h)`: Define a área retangular da máscara de colisão, relativa à posição do objeto.

- `static boolean isColliding(GameObject obj1, GameObject obj2)`: Método estático que verifica se as máscaras de colisão de dois objetos se sobrepõem.

## Componentes: Os Blocos de Funcionalidade

Componentes são classes que contêm uma lógica específica e podem ser adicionadas a qualquer `GameObject` para lhe dar novas habilidades.

- `addComponent(Component c)`: Adiciona um componente ao `GameObject`.

- `getComponent(Class<T> componentClass)`: Recupera um componente do `GameObject` pelo seu tipo.

´`hasComponent(Class<T> componentClass)`: Verifica se o `GameObject` possui um certo tipo de componente.

---

### Componentes da Engine
A seguir, a lista de componentes padrão fornecidos pela engine.

#### Animator
Gerencia as animações de um `GameObject`. Atua como uma máquina de estados de animação.

##### Como Usar:
O fluxo ideal é carregar animações de um arquivo JSON exportado do Aseprite.

```java
// 1. Em um estado de carregamento, carregue a spritesheet e as animações
Spritesheet playerSheet = new Spritesheet("/sprites/player.png");
Map<String, Animation> playerAnims = AnimationLoader.loadFromAsepriteJson("/anims/player.json", playerSheet, true);

// 2. No construtor do seu GameObject (ex: Player), crie e configure o Animator
public Player(JSONObject properties) {
    super(properties);
    Animator animator = new Animator();
    
    // 3. Adicione todas as animações carregadas ao componente
    for (Map.Entry<String, Animation> entry : playerAnims.entrySet()) {
        animator.addAnimation(entry.getKey(), entry.getValue());
    }

    // 4. Adicione o componente ao GameObject
    this.addComponent(animator);
}

// 5. Na lógica do jogo, troque as animações
public void tick() {
    super.tick(); // Atualiza todos os componentes, incluindo o Animator
    Animator animator = getComponent(Animator.class);
    
    if (isMoving) {
        animator.play("walk_right");
    } else {
        animator.play("idle");
    }
}
```
##### Métodos Chave:

- `play(String key)`: Inicia uma animação pelo seu nome.

- `getCurrentAnimation()`: Retorna a instância da `Animation` atual, útil para verificar `hasFinished()`.

#### HealthComponent
Um componente simples que adiciona o conceito de vida a um `GameObject`

##### Como Usar:
```java
public Enemy(JSONObject properties) {
    super(properties);
    // Este inimigo terá 100 de vida máxima.
    this.addComponent(new HealthComponent(100));
}

// Em outro lugar, para causar dano:
public void onCollision(GameObject other) {
    if (other instanceof PlayerProjectile) {
        HealthComponent health = getComponent(HealthComponent.class);
        if (health != null) {
            health.takeDamage(10);
        }
    }
}
```
##### Métodos Chave:

- `takeDamage(int amount)`: Reduz a vida.

- `heal(int amount)`: Aumenta a vida.

- `getHealthPercentage()`: Retorna a vida como uma porcentagem (0.0 a 1.0), útil para barras de vida na UI.

#### InventoryComponent
Um componente que anexa um sistema de inventário a um `GameObject`

##### Como Usar:
```java
public Player(JSONObject properties) {
    super(properties);
    // Anexa um inventário com 20 slots de capacidade.
    this.addComponent(new InventoryComponent(20));
}

// Para acessar o inventário:
InventoryComponent invComponent = player.getComponent(InventoryComponent.class);
if (invComponent != null) {
    // Adiciona um item ao inventário do jogador
    invComponent.inventory.addItem(new HealthPotion(), 1);
}
```

#### PhysicsComponent
Adiciona física de plataforma a um `GameObject`, incluindo gravidade e pulo.

##### Como Usar:
Basta adicionar o componente a um `GameObject`. Ele se autoconfigura ouvindo o evento `WORLD_LOADED` para obter a referência do mundo para as colisões.
```java
public Player(JSONObject properties) {
    super(properties);
    // Adiciona o componente de física. A gravidade padrão é 0.5.
    this.addComponent(new PhysicsComponent());
}

// Para fazer o jogador pular:
public void tick() {
    super.tick();
    PhysicsComponent physics = getComponent(PhysicsComponent.class);

    if (InputManager.isActionJustPressed("JUMP") && physics.onGround) {
        // Aplica uma força vertical para cima.
        physics.addVerticalForce(-12.0);
    }
}
```

##### Propriedades Chave:

- `gravity`: A força da gravidade aplicada a cada `tick`.

- `onGround`: Um booleano que indica se o objeto está no chão.

- `addVerticalForce(double force)`: Método para aplicar um impulso vertical (pulo).

#### MovementComponent e AIMovementComponent
Estes componentes lidam com o movimento top-down (8 direções), gerenciando a velocidade e a colisão com o cenário (`World`) e outros `GameObject`s.

- `MovementComponent`: Para movimento direto controlado por input (jogador).

- `AIMovementComponent`: Para movimento de IA, com capacidade de seguir alvos e usar pathfinding A*(o A* não funciona por enquanto).

##### Exemplo com `MovementComponent` (Jogador):
```java
// No construtor do Player:
this.addComponent(new MovementComponent(2.5)); // Velocidade de 2.5 pixels/tick

// No tick() do Player:
MovementComponent movement = getComponent(MovementComponent.class);
double dx = 0, dy = 0;
if (InputManager.isActionPressed("MOVE_UP")) dy = -1;
if (InputManager.isActionPressed("MOVE_DOWN")) dy = 1;
// ...etc para esquerda e direita...
movement.setDirection(dx, dy);
```

##### Exemplo com `AIMovementComponent` (Inimigo):

```java
// No construtor do Inimigo:
this.addComponent(new AIMovementComponent(1.5)); // Velocidade de 1.5

// Para fazer o inimigo seguir o jogador:
public void someLogicMethod(GameObject player) {
    AIMovementComponent aiMovement = getComponent(AIMovementComponent.class);
    aiMovement.setTarget(player);
}
```

##### Recursos Avançados do `AIMovementComponent`:

- `useAStarPathfinding = true`: Habilita o pathfinding A* para desviar de obstáculos (requer a classe `Pathfinder`) ``ESTA OBSOLETO``.

- `avoidOtherActors = true`: Tenta desviar de outros Characters.

- `targetAnchor`: Enum (`CENTER`, `BOTTOM_CENTER`, `TOP_LEFT`) para definir qual ponto do alvo a IA deve perseguir.

#### ShadowComponent

Este componente adiciona uma sombra dinâmica a um `GameObject`, que o segue automaticamente. A sombra é desenhada na camada `GAMEPLAY_BELOW`, garantindo que apareça sob o personagem, mas acima do chão. Isso ajuda a "ancorar" as entidades ao cenário, dando uma melhor sensação de profundidade.

Existem duas formas de criar uma sombra:

### 1. Sombra Procedural (Oval)

Esta abordagem desenha uma elipse suave e com gradiente, ideal para a maioria dos personagens e objetos. É flexível, pois o tamanho e a opacidade são definidos por código.

**Como Usar:**

```java
// No construtor ou método initialize de um GameObject

// Adiciona uma sombra procedural oval.
// Parâmetros: (largura, altura, opacidade, deslocamento Y da base)
this.addComponent(new ShadowComponent(12, 6, 0.4f, 0));
```

- **largura, altura:** Tamanho da elipse da sombra.
- **opacidade:** Valor entre 0.0f (invisível) e 1.0f (preto total).
- **deslocamento Y:** Quantos pixels abaixo da base do personagem a sombra aparece.

### 2. Sombra Baseada em Sprite

Permite usar uma imagem customizada para a sombra, dando total controle artístico.

**Como Usar:**

1. **Carregue o Sprite:** Primeiro, carregue a imagem da sombra no `AssetManager`.

```java
// Em um método de carregamento de assets
assets.loadSprite("sombra_circular", "/sprites/efeitos/sombra.png");
```

2. **Crie o Componente:** Passe o Sprite carregado para o construtor do componente.

```java
// No construtor do GameObject
Sprite sombraSprite = PlayingState.assets.getSprite("sombra_circular");

// Adiciona uma sombra baseada em sprite.
// Parâmetros: (sprite, deslocamento Y da base)
this.addComponent(new ShadowComponent(sombraSprite, 0));
```

A opacidade pode ser ajustada no próprio arquivo de imagem ou via código, alterando o `alpha` do `Sprite` da sombra.

#### Como desativar a sombra sem remover o componente

Se você quiser desativar temporariamente a sombra de um `GameObject` sem remover o `ShadowComponent`, basta usar o método `setActive(false)`:

```java
// Desativa a sombra do GameObject
this.getComponent(ShadowComponent.class).setActive(false);
```

Para reativar, utilize `setActive(true)`. Isso é útil para situações em que a sombra não deve ser exibida, como durante animações específicas

#### Como verificar se a sombra está ativa

Para saber se a sombra está ativa, utilize o método `isActive()` do componente:

```java
// Verifica se a sombra está ativa
boolean sombraAtiva = this.getComponent(ShadowComponent.class).isActive();
```

Se retornar `true`, a sombra está sendo exibida; se `false`, está

---
[⬅️ Voltar para o Referencia API](./README.md)