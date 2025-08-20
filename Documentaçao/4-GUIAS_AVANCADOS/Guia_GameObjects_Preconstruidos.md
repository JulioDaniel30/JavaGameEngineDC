# Guia Avançado: Usando os GameObjects Pré-Construídos

A JDStudio Engine fornece uma biblioteca de classes de `GameObject`s "pré-fabricadas" para funcionalidades comuns, como portas, baús, interruptores, itens coletáveis, barreiras destrutíveis e portais. Esta abordagem de framework acelera o desenvolvimento, fornecendo uma base de lógica testada que você pode usar diretamente ou estender para criar as suas próprias variações.

## O Padrão: Herança e Métodos Abstratos

O sistema funciona com um padrão de herança:
1.  **Engine-Side**: Fornece uma classe `abstract` (ex: `EngineDoor`) com toda a lógica de funcionamento (`tick`, `interact`, `saveState`, etc.). Esta classe tem "lacunas" na forma de métodos abstratos (ex: `setupAnimations`).
2.  **Game-Side**: Você cria uma classe no seu jogo (ex: `MagicDoor`) que herda da classe da engine. A sua única responsabilidade é preencher essas lacunas, fornecendo os assets (sprites, sons) e a lógica específica do seu jogo.

## PreBuildObjects Disponíveis

### 1. EngineDoor - Portas Interativas
**Funcionalidades:**
- Estados aberto/fechado com animações
- Sistema de save/load
- Mudança automática de colisão (SOLID ↔ TRIGGER)

**Propriedades Tiled:**
- `startsOpen` (boolean): Se a porta começa aberta
- `name` (string): Nome único da porta

### 2. EngineChest - Baús de Tesouro
**Funcionalidades:**
- Sistema de loot configurável
- Estados: fechado, aberto com loot, aberto vazio
- Suporte a chaves obrigatórias
- Sistema de save/load

**Propriedades Tiled:**
- `startsOpen` (boolean): Se o baú começa aberto
- `hasBeenLooted` (boolean): Se já foi saqueado
- `lootTable` (string): ID da tabela de loot
- `requiresKey` (boolean): Se requer chave
- `requiredKeyId` (string): ID da chave necessária

**Animações Esperadas:**
- `idleClosed`, `opening`, `idleOpenFull`, `idleOpenEmpty`

### 3. EngineSwitch - Interruptores e Alavancas
**Funcionalidades:**
- Estados on/off com sistema de eventos
- Cooldown configurável
- Modo toggleável ou temporário
- Suporte a chaves

**Propriedades Tiled:**
- `startsOn` (boolean): Se começa ligado
- `switchId` (string): ID único do interruptor
- `isToggleable` (boolean): Se pode ser alternado
- `requiresKey` (boolean): Se requer chave
- `requiredKeyId` (string): ID da chave necessária
- `cooldownTime` (int): Tempo de cooldown em ticks

**Animações Esperadas:**
- `idleOff`, `turningOn`, `idleOn`, `turningOff`

### 4. EnginePickup - Itens Coletáveis
**Funcionalidades:**
- Coleta manual ou automática (por colisão)
- Sistema de respawn configurável
- Integração com inventário
- Suporte a chaves

**Propriedades Tiled:**
- `itemId` (string): ID do item a ser coletado
- `quantity` (int): Quantidade do item
- `autoPickup` (boolean): Se é coletado automaticamente
- `respawns` (boolean): Se o item respawna
- `respawnTime` (int): Tempo para respawn em ticks
- `requiresKey` (boolean): Se requer chave
- `requiredKeyId` (string): ID da chave necessária

**Animações Esperadas:**
- `idleAvailable`, `collecting`, `collected`, `respawning`

### 5. EngineBarrier - Barreiras Destrutíveis
**Funcionalidades:**
- Sistema de vida e dano
- Regeneração configurável
- Armas especiais obrigatórias
- Drop de loot ao ser destruída
- Estados visuais baseados na vida

**Propriedades Tiled:**
- `maxHealth` (double): Vida máxima
- `health` (double): Vida atual
- `requiresSpecialWeapon` (boolean): Se requer arma especial
- `requiredWeaponType` (string): Tipo de arma necessária
- `canRegenerate` (boolean): Se pode regenerar
- `regenerationRate` (double): Taxa de regeneração por tick
- `dropsLoot` (boolean): Se dropa loot
- `lootTable` (string): Tabela de loot

**Animações Esperadas:**
- `idleIntact`, `idleDamaged`, `idleHeavilyDamaged`, `idleCritical`
- `damaged`, `destroying`, `destroyed`

### 6. EnginePortal - Portais de Teletransporte
**Funcionalidades:**
- Teletransporte manual ou automático
- Condições de uso (chaves, quests)
- Sistema de cooldown
- Uso único opcional
- Estados ativo/inativo

**Propriedades Tiled:**
- `portalId` (string): ID único do portal
- `destinationLevel` (string): Nível de destino
- `destinationX` (int): Coordenada X de destino
- `destinationY` (int): Coordenada Y de destino
- `destinationPortalId` (string): ID do portal de destino
- `requiresKey` (boolean): Se requer chave
- `requiredKeyId` (string): ID da chave necessária
- `requiresQuest` (boolean): Se requer quest
- `requiredQuestId` (string): ID da quest necessária
- `autoTeleport` (boolean): Se teleporta automaticamente
- `cooldownTime` (int): Tempo de cooldown em ticks
- `oneTimeUse` (boolean): Se pode ser usado apenas uma vez

**Animações Esperadas:**
- `idleActive`, `idleInactive`, `idleCooldown`, `activating`

---

## Receita: Criando um Baú Customizado

Vamos ver o fluxo completo para criar um baú de tesouro.

### 1. A Classe Base na Engine (`EngineChest.java`)
A engine já fornece a classe `EngineChest`, que contém toda a lógica para:
* Gerir o estado de "aberto" ou "fechado" (`isOpen`).
* Sistema de loot com verificação se já foi saqueado.
* Suporte a chaves obrigatórias.
* Animações automáticas baseadas no estado.
* Salvar e carregar o seu estado.

### 2. A Implementação no Jogo (`TreasureChest.java`)
No seu projeto de jogo, crie uma classe que herda de `EngineChest`.

```java
public class TreasureChest extends EngineChest {
    public TreasureChest(JSONObject properties) {
        super(properties);
    }

    @Override
    protected void setupAnimations(Animator animator) {
        Animation idleClosed = new Animation(1, PlayingState.assets.getSprite("chest_closed"));
        Animation opening = new Animation(20, false, 
            PlayingState.assets.getSprite("chest_frame_1"),
            PlayingState.assets.getSprite("chest_frame_2"),
            PlayingState.assets.getSprite("chest_frame_3"));
        Animation idleOpenFull = new Animation(1, PlayingState.assets.getSprite("chest_open_full"));
        Animation idleOpenEmpty = new Animation(1, PlayingState.assets.getSprite("chest_open_empty"));

        animator.addAnimation("idleClosed", idleClosed);
        animator.addAnimation("opening", opening);
        animator.addAnimation("idleOpenFull", idleOpenFull);
        animator.addAnimation("idleOpenEmpty", idleOpenEmpty);
    }

    @Override
    protected boolean hasRequiredKey() {
        if (!requiresKey()) return true;
        // Verifica se o jogador tem a chave no inventário
        return PlayerInventory.hasItem(getRequiredKeyId());
    }

    @Override
    protected void giveLoot(String lootTable) {
        // Implementa o sistema de loot do seu jogo
        LootSystem.giveLootToPlayer(lootTable);
    }

    @Override
    protected void onChestOpened() {
        // Som de abertura do baú
        SoundManager.play("chest_open", getX(), getY());
    }

    @Override
    protected void onKeyRequired() {
        // Mostra mensagem que precisa de chave
        MessageSystem.show("Este baú está trancado!");
    }
}
```

### 3. A Configuração no `Tiled`
Crie o objeto do baú no **Tiled**.

Defina a sua Classe como `TreasureChest`.

Adicione as propriedades:
- `name`: "treasure_chest_01"
- `lootTable`: "basic_treasure"
- `requiresKey`: true
- `requiredKeyId`: "golden_key"

### 4. A Instanciação no `PlayingState`
No `onObjectFound`, adicione o case para o seu novo baú.

```java
// Em PlayingState.java -> onObjectFound
case "TreasureChest":
    this.addGameObject(new TreasureChest(properties));
    break;
```

Este padrão pode ser aplicado a todos os outros PreBuildObjects, criando uma base sólida e reutilizável para o seu projeto.

---
[⬅️ Voltar para o Guias Avançados](./README.md)
