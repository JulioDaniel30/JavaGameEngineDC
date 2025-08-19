# Guia Avançado: Criando Objetos Interativos Comuns (Receitas Completas)

Este guia serve como um livro de receitas detalhado para criar `GameObject`s interativos comuns. Cada exemplo inclui a configuração no Tiled, a implementação completa da classe em Java e a lógica de integração com o `PlayingState`, mostrando como os sistemas da JDStudio Engine trabalham em conjunto.

---

## 1. Portais de Mudança de Nível

**Conceito**: Uma área que, ao ser tocada pelo jogador, o transporta para um novo mapa. A interação é automática.

* **Sistemas da Engine Utilizados**: `InteractionComponent`, `Engine.transitionToState()`.

### A. Configuração no Tiled
1.  Crie um objeto retangular que cubra a área do portal.
2.  Defina a sua **Classe** (ou "Type") como `LevelExit`.
3.  Adicione as seguintes **Propriedades Customizadas**:
    * `nextMap` (`string`): O caminho para o próximo mapa (ex: `/maps/level2.json`).

### B. Implementação em Java (`LevelExit.java`)
Esta classe é bastante simples. A sua principal função é ler os dados do Tiled e criar uma zona de interação.

```java
// Em com.game.gameObjects.LevelExit.java
package com.game.gameObjects;

import org.json.JSONObject;
import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Utils.PropertiesReader;

public class LevelExit extends GameObject {
    public final String nextMapPath;

    public LevelExit(JSONObject properties) {
        super(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.nextMapPath = reader.getString("nextMap", null);
        
        // Cria uma zona de interação retangular com o mesmo tamanho do objeto no Tiled.
        // Usa o tipo TRIGGER para interações automáticas baseadas em proximidade.
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_TRIGGER, getWidth(), getHeight(), 0, 0));
        this.addComponent(interaction);
    }
}
```

### C. Lógica de Jogo (`PlayingState.java`)

A transição é automática, gerida por um `EventListener` que "ouve" a entrada do jogador na zona do portal.

```java
// Em PlayingState.java -> setupEventListeners()
EventManager.getInstance().subscribe(GameEvent.TARGET_ENTERED_ZONE, (data) -> {
    InteractionEventData event = (InteractionEventData) data;
    // Verifica se quem entrou foi o jogador e se o dono da zona é um LevelExit
    if (event.target() instanceof Player && event.zoneOwner() instanceof LevelExit) {
        LevelExit portal = (LevelExit) event.zoneOwner();
        if (portal.nextMapPath != null) {
            System.out.println("Jogador entrou no portal. A transitar para: " + portal.nextMapPath);
            // Inicia a transição de tela para um novo PlayingState, carregando o novo mapa.
            Engine.transitionToState(new PlayingState(portal.nextMapPath));
        }
    }
});
```

-----

## 2\. Portas Animadas e com Colisão

**Conceito**: Um obstáculo que pode alternar entre os estados "aberto" e "fechado". Quando fechada, a porta é sólida e bloqueia o jogador. Quando aberta, ela se torna "atravessável" (um trigger) e o jogador pode passar. A porta guarda o seu estado (aberta/fechada) e pode ser salva e carregada.

* **Sistemas da Engine Utilizados**: `Animator`, `ISavable`, `InteractionComponent`, `GameStateManager` (para guardar o estado).

### A. Configuração no Tiled

1.  Crie um objeto na sua camada de objetos no local da porta.
2.  Defina a sua **Classe** (ou "Type") como `Door`.
3.  Adicione as seguintes **Propriedades Customizadas**:
    * `name` (`string`): Um ID único para esta porta (ex: `porta_masmorra_1`). É **essencial** para o sistema de save/load.
    * `startsOpen` (`bool`): (Opcional) Se `true`, a porta começa o jogo já aberta. O padrão é `false`.

### B. Implementação em Java (`Door.java`)

A classe `Door` gere o seu próprio estado (`isOpen`), as suas animações e a sua colisão. Ela implementa `ISavable` para que o seu estado de "aberta" ou "fechada" possa ser guardado.

```java
// Em com.game.gameObjects.Door.java
import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
// ... outras importações

public class Door extends GameObject implements ISavable {

    private boolean isOpen = false;
    private Animator animator;

    public Door(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.isOpen = reader.getBoolean("startsOpen", false);

        this.animator = new Animator();
        this.addComponent(animator);
        setupAnimations(); // Método que carrega e adiciona as animações "opening", "closing", etc.

        // Adiciona a zona para interação manual
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);

        // Define o estado inicial com base no Tiled ou no estado salvo
        updateStateVisuals();
    }
    
    // Método para ser chamado pela interação no PlayingState
    public void interact() {
        // Lógica para não interagir durante a animação
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        if (isOpen) {
            animator.play("closing");
            setCollisionType(CollisionType.SOLID); // Torna-se sólida IMEDIATAMENTE
            isOpen = false;
        } else {
            animator.play("opening");
            isOpen = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        // Lógica para mudar a colisão para TRIGGER DEPOIS de a animação "opening" terminar
        if (animator.getCurrentAnimationKey().equals("opening") && animator.getCurrentAnimation().hasFinished()) {
            setCollisionType(CollisionType.TRIGGER);
            animator.play("idleOpen");
        } else if (animator.getCurrentAnimationKey().equals("closing") && animator.getCurrentAnimation().hasFinished()){
            animator.play("idleClosed");
        }
    }

    private void updateStateVisuals() {
        if (isOpen) {
            animator.play("idleOpen");
            setCollisionType(CollisionType.TRIGGER);
        } else {
            animator.play("idleClosed");
            setCollisionType(CollisionType.SOLID);
        }
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOpen", this.isOpen);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.isOpen = state.getBoolean("isOpen");
        updateStateVisuals(); // Atualiza a aparência e colisão da porta com base no estado carregado
    }
}
```
### C. Lógica de Jogo (`PlayingState.java`)
A interação com a porta é gerida pelo sistema de interação manual que já criamos em **[Interações avançadas](./Guia_Interacoes_Avancadas.md)**, através da tecla de ação quando o jogador está próximo.

```java

// Em PlayingState.java -> tick(), dentro do seu bloco handleInput()
if (InputManager.isActionJustPressed("INTERACT") && interactableObjectInRange != null) {
    if (interactableObjectInRange instanceof Door) {
        // Chama o método público da porta para que ela trate da sua própria lógica
        ((Door) interactableObjectInRange).interact();
    }
}
```
Com esta estrutura, a `Door` é um objeto completamente autocontido. O `PlayingState` não precisa de saber se a porta está aberta ou fechada; ele apenas diz à porta "interage", e a própria porta gere os seus estados, animações e colisões.

----

## 3\. Baús de Loot (Uso Único)

**Conceito**: Um objeto de uso único que, ao ser interagido com a tecla de ação, dá itens ao jogador e permanece aberto.

  * **Sistemas da Engine Utilizados**: `Animator`, `InteractionComponent`, `GameStateManager` (para o estado "aberto").

### A. Configuração no Tiled

1.  Crie o objeto do baú.
2.  Defina a sua **Classe** como `LootChest`.
3.  Adicione as seguintes **Propriedades Customizadas**:
      * `name` (`string`): Um ID único para este baú (ex: `level1_chest_sword`). Essencial para o save/load.
      * `lootItems` (`string`): Uma lista de itens e quantidades, separados por vírgulas (ex: `"health_potion:1,gold:50"`).

### B. Implementação em Java (`LootChest.java`)

```java
// Em com.game.gameObjects.LootChest.java
public class LootChest extends GameObject {
    private boolean isOpen = false;
    private String lootItems;
    private Animator animator;

    public LootChest(JSONObject properties) { super(properties); }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.lootItems = reader.getString("lootItems", "");

        this.animator = new Animator();
        this.addComponent(animator);
        // Supondo que você tem um método para carregar as animações "idle_closed", "opening", "idle_opened"
        setupAnimations(); 

        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 16.0)); // Zona para interação manual
        this.addComponent(interaction);

        // Verifica no GameStateManager se este baú já foi aberto numa sessão anterior
        if (GameStateManager.getInstance().hasFlag("CHEST_OPENED_" + this.name)) {
            this.isOpen = true;
            animator.play("idle_opened");
        } else {
            animator.play("idle_closed");
        }
    }

    public void open(Player player) {
        if (isOpen) return;

        this.isOpen = true;
        animator.play("opening");
        Sound.play("/sfx/chest_open.wav", Sound.SoundChannel.SFX, getX(), getY());
        
        // 1. Analisa a string lootItems
        String[] items = lootItems.split(",");
        for (String itemString : items) {
            String[] parts = itemString.split(":");
            String itemId = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            
            // 2. Adiciona os itens ao inventário do jogador
            // (Isto requer uma "fábrica" de itens para criar o item a partir do seu ID)
            player.getComponent(InventoryComponent.class).inventory.addItem(ItemFactory.createItem(itemId), quantity);
        }

        // 3. Marca no GameStateManager que este baú foi aberto (para persistência entre saves)
        GameStateManager.getInstance().setFlag("CHEST_OPENED_" + this.name);
    }
}
```

### C. Lógica de Jogo (`PlayingState.java`)

```java
// Em PlayingState.java -> tick(), dentro do seu bloco handleInput()
if (InputManager.isActionJustPressed("INTERACT") && interactableObjectInRange != null) {
    if (interactableObjectInRange instanceof LootChest) {
        ((LootChest) interactableObjectInRange).open(player);
    }
}
```

-----

## 4\. Baús de Armazenamento (Inventário Extra)

**Conceito**: Um objeto que funciona como um inventário persistente, permitindo que o jogador guarde e recupere itens.

  * **Sistemas da Engine Utilizados**: `InteractionComponent`, `InventoryComponent`, `ISavable`.

### A. Configuração no Tiled

1.  Crie o objeto do baú.
2.  Defina a sua **Classe** como `StorageChest`.
3.  Adicione as seguintes **Propriedades Customizadas**:
      * `name` (`string`): Um ID único para o baú (ex: `casa_player_bau_1`).
      * `capacity` (`int`): O número de slots que este baú terá.

### B. Implementação em Java (`StorageChest.java`)

```java
// Em com.game.gameObjects.StorageChest.java
public class StorageChest extends GameObject implements ISavable {
    private InventoryComponent inventoryComponent;

    public StorageChest(JSONObject properties) {
        super(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        int capacity = reader.getInt("capacity", 12);
        
        this.inventoryComponent = new InventoryComponent(capacity);
        this.addComponent(inventoryComponent);

        // Configura a zona de interação manual
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);
    }
    
    public Inventory getInventory() {
        return this.inventoryComponent.inventory;
    }
    
    public void openStorage() {
        System.out.println("Abrindo o baú de armazenamento: " + this.name);
        // A lógica real para abrir a UI de armazenamento
        Engine.pushState(new StorageState(player, this));
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        // Lógica para guardar o conteúdo do inventário em um JSONArray de itens
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        // Lógica para carregar o conteúdo do inventário a partir do JSON
    }
}
```

### C. Lógica de Jogo

A interação com este baú abriria um novo `GameState` (`StorageState`) que mostraria a UI de transferência.

```java
// Em PlayingState.java -> tick(), dentro do seu bloco handleInput()
if (InputManager.isActionJustPressed("INTERACT") && interactableObjectInRange != null) {
    if (interactableObjectInRange instanceof StorageChest) {
        ((StorageChest) interactableObjectInRange).openStorage();
    }
}
```

-----

## 5\. Checkpoints de Save

**Conceito**: Uma área ou objeto que, ao ser tocado, salva o progresso do jogo.

  * **Sistemas da Engine Utilizados**: `InteractionComponent`, `SaveManager`, `GameStateManager`.

### A. Configuração no Tiled

1.  Crie o objeto do checkpoint.
2.  Defina a sua **Classe** como `Checkpoint`.
3.  Adicione a seguinte **Propriedade Customizada**:
      * `name` (`string`): Um ID único para o checkpoint (ex: `checkpoint_vila_inicio`).

### B. Implementação em Java (`Checkpoint.java`)

```java
// Em com.game.gameObjects.Checkpoint.java
public class Checkpoint extends GameObject {
    private Animator animator;

    public Checkpoint(JSONObject properties) {
        super(properties);
        
        this.animator = new Animator();
        this.addComponent(animator);
        // Carrega animações "inactive", "activating", "active"

        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_TRIGGER, 32.0));
        this.addComponent(interaction);

        // Verifica se este checkpoint já foi o último a ser ativado
        if (GameStateManager.getInstance().hasFlag("LAST_CHECKPOINT_" + this.name)) {
            animator.play("active");
        } else {
            animator.play("inactive");
        }
    }

    public void activate() {
        if (animator.getCurrentAnimationKey().equals("active")) return;
        
        animator.play("activating");
        Sound.play("/sfx/checkpoint.wav", Sound.SoundChannel.SFX, getX(), getY());
        
        // Limpa a flag de checkpoint antigo e define a nova
        // (Isto requer uma lógica para encontrar e limpar as flags com o prefixo "LAST_CHECKPOINT_")
        GameStateManager.getInstance().setFlag("LAST_CHECKPOINT_" + this.name);
    }
}
```

### C. Lógica de Jogo (`PlayingState.java`)

A lógica é automática, baseada na entrada do jogador na zona.

```java
// Em PlayingState.java -> setupEventListeners()
EventManager.getInstance().subscribe(GameEvent.TARGET_ENTERED_ZONE, (data) -> {
    InteractionEventData event = (InteractionEventData) data;
    if (event.target() instanceof Player && event.zoneOwner() instanceof Checkpoint) {
        Checkpoint checkpoint = (Checkpoint) event.zoneOwner();
        
        checkpoint.activate(); // Ativa o feedback visual
        
        // O PlayingState é responsável por chamar o save
        this.saveGame(); 
        
        // Mostra uma mensagem de "Jogo Salvo" na UI
        PopupManager.getInstance().createPopup(player, "Jogo Salvo!", new Font("Arial", Font.BOLD, 12), Color.CYAN, 120);
    }
});
```

---

## 6. Lojas (NPCs ou Balcões)

**Conceito**: Um `GameObject` que, ao ser interagido, abre uma interface de compra e venda de itens. A interação é iniciada através de um diálogo, e a loja em si é um novo `GameState` que se sobrepõe ao jogo.

* **Sistemas da Engine Utilizados**: `InteractionComponent`, `DialogueManager`, `ActionManager`, `InventoryComponent`, `EngineMenuState` (para a UI da loja).

### A. Configuração no Tiled

1.  Crie o objeto que representará a sua loja (pode ser um NPC ou um balcão).
2.  Defina a sua **Classe** (ou "Type") como `Shopkeeper`.
3.  Adicione as seguintes **Propriedades Customizadas**:
    * `name` (`string`): Um ID único para a loja (ex: `loja_armas_vila`).
    * `dialogueFile` (`string`): O caminho para o diálogo de saudação do lojista (ex: `/dialogues/lojista.json`).
    * `shopInventory` (`string`): **(Importante)** Uma lista dos itens que a loja vende e os seus preços. O formato é `id_do_item:preço`, com os itens separados por vírgulas. Ex: `"health_potion:15,sword_wood:50,shield_basic:30"`.

### B. Implementação em Java

A implementação requer duas novas classes principais no seu jogo: `Shopkeeper.java` e `ShopState.java`.

#### **1. A Classe `Shopkeeper.java`**

Esta classe representa o NPC ou o objeto da loja. A sua principal responsabilidade é carregar o seu inventário de venda a partir dos dados do Tiled.

```java
// Em com.game.gameObjects.Shopkeeper.java
import com.JDStudio.Engine.Items.Item; // E outras classes de Item

// Uma pequena classe auxiliar para guardar o item e o seu preço
record ShopItem(Item item, int price) {}

public class Shopkeeper extends EngineNPC {
    private List<ShopItem> itemsForSale = new ArrayList<>();

    public Shopkeeper(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties); // Carrega o diálogo, etc.
        PropertiesReader reader = new PropertiesReader(properties);
        String shopItemsString = reader.getString("shopInventory", "");

        // Analisa a string de inventário para preencher a lista de venda
        parseShopInventory(shopItemsString);
    }

    private void parseShopInventory(String inventoryData) {
        String[] items = inventoryData.split(",");
        for (String itemString : items) {
            String[] parts = itemString.split(":");
            if (parts.length == 2) {
                String itemId = parts[0];
                int price = Integer.parseInt(parts[1]);
                Item item = ItemFactory.createItem(itemId); // Usa a sua "fábrica" de itens
                if (item != null) {
                    itemsForSale.add(new ShopItem(item, price));
                }
            }
        }
    }
    
    public List<ShopItem> getItemsForSale() {
        return this.itemsForSale;
    }
}
```
### 2. O Diálogo do Lojista (`lojista.json`)
O diálogo é o ponto de entrada para a loja. A escolha principal do jogador terá uma action para abrir a interface.

```json

{
  "defaultEntryPoint": 0,
  "nodes": [
    {
      "id": 0,
      "speakerName": "Lojista",
      "text": "Bem-vindo à minha loja! Queres dar uma vista de olhos nas minhas mercadorias?",
      "choices": [
        {
          "text": "Sim, mostrar o que tens.",
          "nextNodeId": -1,
          "action": "ABRIR_LOJA"
        },
        {
          "text": "Não, obrigado.",
          "nextNodeId": -1
        }
      ]
    }
  ]
}
```
### C. Lógica de Jogo (Orquestração)

#### 1. Registar a Ação
No seu `PlayingState`, registe a ação `"ABRIR_LOJA"`. A sua única responsabilidade é "empurrar" o novo `ShopState` para a pilha de estados.

```java
// Em PlayingState.java -> setupDialogueActions()
ActionManager.getInstance().registerAction("ABRIR_LOJA", (player, npc) -> {
    if (npc instanceof Shopkeeper) {
        Engine.pushState(new ShopState((Player) player, (Shopkeeper) npc));
    }
});
```
#### 2. Criar o `ShopState.java` (com `UIShopView`)**
Este GameState agora fica incrivelmente simples. Ele apenas precisa de criar a UIShopView, passando a lista de itens e a lógica de compra.

```java

// Em com.game.states.ShopState.java
import com.JDStudio.Engine.Graphics.UI.Elements.IShopItem;
import com.JDStudio.Engine.Graphics.UI.Elements.UIShopView;
import java.util.function.Consumer;

public class ShopState extends EngineMenuState {
    private Player player;
    private Shopkeeper shopkeeper;

    public ShopState(Player player, Shopkeeper shopkeeper) {
        super();
        this.player = player;
        this.shopkeeper = shopkeeper;
    }

    @Override
    protected void buildUI() {
        // --- LÓGICA DE COMPRA ---
        // Define o que acontece quando o botão "Comprar" de um item é clicado.
        Consumer<IShopItem> onBuyAction = (shopItem) -> {
            ShopItem itemData = (ShopItem) shopItem;
            if (player.getGold() >= itemData.price()) {
                player.removeGold(itemData.price());
                player.getComponent(InventoryComponent.class).inventory.addItem(itemData.item(), 1);
                System.out.println("Comprou: " + itemData.item().name);
            } else {
                System.out.println("Ouro insuficiente!");
                // (Aqui você poderia mostrar um popup de erro)
            }
        };

        // --- CRIA A UI DA LOJA ---
        // Cria a janela da loja, passando a lista de itens e a lógica de compra.
        UIShopView shopView = new UIShopView(
            20, 20, 200, // Posição (x,y) e largura
            null, // Sprite de fundo (opcional)
            shopkeeper.getItemsForSale(),
            onBuyAction
        );
        uiManager.addElement(shopView);
        
        // --- BOTÃO DE SAIR ---
        UIButton exitButton = new UIButton(Engine.WIDTH / 2 - 40, 130, "Sair", font, () -> Engine.popState());
        uiManager.addElement(exitButton);
    }
}
```
Com esta estrutura, você tem um sistema de loja completo e data-driven. Você pode criar dezenas de lojas diferentes no seu jogo apenas criando novos objetos `Shopkeeper` no Tiled e definindo os seus inventários na propriedade `shopInventory`.

---
[⬅️ Voltar para o Guias Avançados](./README.md)