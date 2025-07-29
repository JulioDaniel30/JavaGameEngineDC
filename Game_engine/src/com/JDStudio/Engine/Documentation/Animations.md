# Pacote: com.JdStudio.Engine.Graphics.Sprite.Animations

Fornece um sistema de animação baseado em frames.

## Classe `Animation`

Contém uma sequência de `Sprite` (os frames) e a lógica para alternar entre eles com base em uma velocidade. Pode ser configurada para executar em loop ou apenas uma vez.

## Classe `Animator`

Atua como uma máquina de estados para múltiplas `Animation`. Um `GameObject` possui um `Animator`, e você pode dizer a ele qual animação (`"walk_right"`, `"idle"`, etc.) deve ser executada.

### Exemplo de Uso

Normalmente, você configura o `Animator` de um objeto no seu construtor.

```java
// Na classe Player, que herda de GameObject

public Player(double x, double y) {
    super(x, y, 16, 16);
    
    // O campo 'animator' já existe em GameObject
    setupAnimations();
}

private void setupAnimations() {
    // Busca os sprites previamente carregados no AssetManager
    Sprite idleFrame = Game.assets.getSprite("player_idle");
    Sprite walk1 = Game.assets.getSprite("player_walk_1");
    Sprite walk2 = Game.assets.getSprite("player_walk_2");
    
    // Cria as animações
    // Animação "idle": 1 frame, velocidade não importa, em loop
    Animation idleAnim = new Animation(10, true, idleFrame);
    
    // Animação "walk": 2 frames, troca a cada 20 ticks, em loop
    Animation walkAnim = new Animation(20, true, walk1, walk2);

    // Adiciona as animações ao animator com chaves
    this.animator.addAnimation("idle", idleAnim);
    this.animator.addAnimation("walk", walkAnim);
}

@Override
public void tick() {
    // ... (lógica de input para mover o personagem)
    
    // Lógica para trocar de animação
    if (isMoving) { // supondo que você tenha uma variável 'isMoving'
        this.animator.play("walk");
    } else {
        this.animator.play("idle");
    }

    super.tick(); // Chama o tick do GameObject, que chama animator.tick()
}
```