# Guia Avançado: Usando os GameObjects Pré-Construídos

A JDStudio Engine fornece uma biblioteca de classes de `GameObject`s "pré-fabricadas" para funcionalidades comuns, como portas, inimigos e baús. Esta abordagem de framework acelera o desenvolvimento, fornecendo uma base de lógica testada que você pode usar diretamente ou estender para criar as suas próprias variações.

## O Padrão: Herança e Métodos Abstratos

O sistema funciona com um padrão de herança:
1.  **Engine-Side**: Fornece uma classe `abstract` (ex: `EngineDoor`) com toda a lógica de funcionamento (`tick`, `interact`, `saveState`, etc.). Esta classe tem "lacunas" na forma de métodos abstratos (ex: `setupAnimations`).
2.  **Game-Side**: Você cria uma classe no seu jogo (ex: `MagicDoor`) que herda da classe da engine. A sua única responsabilidade é preencher essas lacunas, fornecendo os assets (sprites, sons) e a lógica específica do seu jogo.

---
### Receita: Criando uma Porta Customizada

Vamos ver o fluxo completo para criar uma porta.

#### 1. A Classe Base na Engine (`EngineDoor.java`)
A engine já fornece a classe `EngineDoor`, que contém toda a lógica para:
* Gerir o estado de "aberta" ou "fechada" (`isOpen`).
* Lidar com a interação do jogador.
* Trocar entre as animações de "abrir", "fechar" e "parada".
* Mudar o seu tipo de colisão de `SOLID` para `TRIGGER`.
* Salvar e carregar o seu estado.

Ela obriga a classe do jogo a implementar um método: `protected abstract void setupAnimations(Animator animator);`

#### 2. A Implementação no Jogo (`MagicDoor.java`)
No seu projeto de jogo, crie uma classe que herda de `EngineDoor`.

```java
public class MagicDoor extends EngineDoor {
    public MagicDoor(JSONObject properties) {
        super(properties);
    }

    @Override
    protected void setupAnimations(Animator animator) {
        // Pega os sprites do AssetManager do JOGO
        Animation idleClosed = new Animation(1, PlayingState.assets.getSprite("magic_door_closed"));
        Animation opening = new Animation(30, false, /* ... frames da porta mágica a abrir ... */);
        // ... outras animações

        // Configura o animator que a classe base nos deu
        animator.addAnimation("idleClosed", idleClosed);
        animator.addAnimation("opening", opening);
        // ...
    }
    
    // Opcional: Sobrescrever o comportamento
    @Override
    public void interact() {
        super.interact(); // Chama a lógica original de abrir/fechar
        // Adiciona um comportamento extra do JOGO
        Sound.play("/sfx/magic_door_sound.wav", SoundChannel.SFX, getX(), getY());
    }
}
```
#### 3. A Configuração no `Tiled`
Crie o objeto da porta no **Tiled**.

Defina a sua Classe como `MagicDoor` (o nome da sua classe de jogo).

Adicione as propriedades que `EngineDoor` espera, como `name` e `startsOpen`.

#### 4. A Instanciação no `PlayingState`
No `onObjectFound`, adicione o case para a sua nova porta.

```java

// Em PlayingState.java -> onObjectFound
case "MagicDoor":
    this.addGameObject(new MagicDoor(properties));
    break;
```
Este padrão pode ser aplicado a todos os outros objetos, como `AbstractEnemy`, `AbstractLootChest`, etc., criando uma base sólida e reutilizável para o seu projeto.


---
[⬅️ Voltar para o Guias Avançados](./README.md)