# Guia de Implementação: Scrolling com Efeito Parallax

Este documento descreve como adicionar um efeito de scrolling parallax à sua game engine. O efeito parallax cria uma ilusão de profundidade ao mover camadas de fundo em velocidades diferentes em relação à câmera, resultando em um visual mais rico e profissional.

## O Conceito

A ideia é simples:
1.  Teremos múltiplas imagens de fundo (camadas).
2.  Camadas que representam objetos distantes (como nuvens ou montanhas) se moverão mais devagar que a câmera.
3.  Camadas mais próximas (como árvores ou colinas) se moverão um pouco mais rápido.
4.  O resultado é uma simulação de profundidade convincente.

A implementação será feita em duas partes principais: uma nova classe na engine para representar uma camada parallax e a lógica de renderização no `PlayingState` do jogo.

---



## Passo 1: Implementar a Lógica no Jogo (`PlayingState.java`)

Agora, vamos usar a nova classe no `PlayingState` para carregar e desenhar as camadas de fundo.

### 1.1 - Declarar a Lista de Camadas

Adicione uma lista para guardar suas camadas de parallax no topo da classe `PlayingState.java`.

```java
// Em PlayingState.java
import com.JDStudio.Engine.Graphics.Effects.ParallaxLayer; // Adicione esta importação
import java.util.List; // Adicione esta importação
import java.util.ArrayList; // Adicione esta importação

public class PlayingState extends EnginePlayingState implements IMapLoaderListener {
    // ... (suas outras variáveis de instância)
    private List<ParallaxLayer> parallaxLayers;
    // ...
}
```

### 1.2 - Carregar as Imagens de Fundo

No método `loadAssets()`, carregue as imagens que você usará para o fundo. É importante que estas imagens tenham a mesma altura que a sua engine (`160px`) e uma largura que permita a repetição.

```java
// Em PlayingState.java -> loadAssets()
private void loadAssets() {
    // ... (carregamento de outros assets)
    
    // Carrega as imagens que serão usadas no fundo parallax
    assets.loadSprite("bg_sky", "/backgrounds/sky.png");
    assets.loadSprite("bg_mountains", "/backgrounds/mountains.png");
    assets.loadSprite("bg_forest", "/backgrounds/forest.png");
}
```

### 1.3 - Inicializar as Camadas

No construtor do `PlayingState`, crie as instâncias de `ParallaxLayer` e adicione-as à lista. A ordem é importante: adicione da camada mais distante para a mais próxima.

```java
// Em PlayingState.java -> Construtor PlayingState()
public PlayingState() {
    // ... (inicialização de assets e managers)

    // Inicializa as camadas de parallax
    this.parallaxLayers = new ArrayList<>();
    // As camadas são adicionadas da mais distante para a mais próxima
    parallaxLayers.add(new ParallaxLayer(assets.getSprite("bg_sky"), 0.1));       // Céu se move muito lentamente (10%)
    parallaxLayers.add(new ParallaxLayer(assets.getSprite("bg_mountains"), 0.3)); // Montanhas se movem a 30% da velocidade da câmera
    parallaxLayers.add(new ParallaxLayer(assets.getSprite("bg_forest"), 0.6));    // Floresta se move a 60%

    // ... (resto do construtor, como a criação do 'world')
}
```

### 1.4 - Criar o Método de Renderização Parallax

Adicione este novo método à sua classe `PlayingState`. Ele contém toda a matemática para calcular as posições e desenhar as camadas, garantindo a repetição para criar um fundo infinito.

```java
// Em PlayingState.java, adicione este novo método
/**
 * Renderiza todas as camadas de fundo com efeito parallax.
 * Este método deve ser chamado ANTES de renderizar o mundo do jogo.
 */
private void renderParallaxBackground(Graphics g) {
    if (parallaxLayers == null) return;

    for (ParallaxLayer layer : parallaxLayers) {
        Sprite sprite = layer.getSprite();
        if (sprite == null) continue;

        int spriteWidth = sprite.getWidth();
        if (spriteWidth <= 0) continue;
        
        // Calcula o deslocamento da camada baseado na posição da câmera e no fator de rolagem
        // A câmera já é uma variável estática acessível via Engine.camera
        int offsetX = (int)(Engine.camera.getX() * layer.getScrollFactor());
        
        // Calcula o ponto de partida do primeiro sprite, usando o operador de módulo (%)
        // para garantir o "loop infinito" do fundo.
        int startX = -(offsetX % spriteWidth);

        // Desenha cópias do sprite lado a lado para preencher a tela inteira
        for (int i = 0; (i * spriteWidth) + startX < Engine.WIDTH; i++) {
            g.drawImage(sprite.getImage(), startX + (i * spriteWidth), 0, null);
        }
    }
}
```

### 1.5 - Chamar a Renderização Parallax

Finalmente, chame o novo método `renderParallaxBackground(g)` no início do seu método `render()` principal. É crucial que ele seja chamado **antes** de `world.render(g)` para que o fundo fique, de fato, atrás do jogo.

```java
// Em PlayingState.java -> render(Graphics g)
@Override
public void render(Graphics g) {
    // --- PASSO 1: DESENHA O FUNDO PARALLAX PRIMEIRO ---
    renderParallaxBackground(g);

    // --- PASSO 2: DESENHA O RESTO DO JOGO ---
    if (world != null) world.render(g);
    super.render(g); // Desenha todos os GameObjects
    projectileManager.render(g);
    particleManager.render(g);
    lightingManager.render(g);
    if (uiManager != null) uiManager.render(g);
}
```

-----

## Conclusão

Com estas alterações, seu jogo agora tem um sistema de parallax funcional. Ao mover o personagem, você verá as diferentes camadas de fundo se movendo em velocidades distintas, criando uma rica sensação de profundidade. Você pode adicionar quantas camadas quiser e ajustar os valores de `scrollFactor` para obter o efeito exato que deseja.
