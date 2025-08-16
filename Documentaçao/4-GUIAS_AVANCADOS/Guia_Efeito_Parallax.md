# Guia Avançado: Scrolling com Efeito Parallax

Este guia descreve como adicionar um efeito de scrolling parallax para criar uma ilusão de profundidade, movendo camadas de fundo em velocidades diferentes.

## O Conceito

1.  O efeito usa múltiplas imagens de fundo (camadas).
2.  Camadas distantes (nuvens) se movem mais devagar que a câmera.
3.  Camadas próximas (árvores) se movem mais rápido.
4.  A engine já fornece a classe `ParallaxLayer` para facilitar este processo.

### Passo 1: Preparar as Classes

A engine já possui a classe `ParallaxLayer(Sprite sprite, double scrollFactor)`, que armazena a imagem e seu fator de rolagem (velocidade relativa à câmera).

### Passo 2: Implementar a Lógica no Jogo (`PlayingState.java`)

#### 2.1. Declarar a Lista de Camadas
Adicione uma lista para guardar suas camadas de parallax no `PlayingState`.
```java
// Em PlayingState.java
private List<ParallaxLayer> parallaxLayers;
```

#### 2.2. Carregar e Inicializar as Camadas
No construtor (ou em um método de inicialização) do `PlayingState`, carregue os sprites e crie as instâncias de `ParallaxLayer`. A ordem de adição (da mais distante para a mais próxima) é recomendada para organização.
```java
// No construtor de PlayingState.java
this.parallaxLayers = new ArrayList<>();
parallaxLayers.add(new ParallaxLayer(assets.getSprite("bg_sky"), 0.1));       // Céu (10% da velocidade da câmera)
parallaxLayers.add(new ParallaxLayer(assets.getSprite("bg_mountains"), 0.3)); // Montanhas (30%)
parallaxLayers.add(new ParallaxLayer(assets.getSprite("bg_forest"), 0.6));    // Floresta (60%)
```
#### 2.3. Criar e Chamar o Método de Renderização
Adicione um método para renderizar o fundo e chame-o no início do método `render()` do seu `PlayingState`, antes de qualquer outra renderização.
```java
// Em PlayingState.java
@Override
public void render(Graphics g) {
    // PASSO 1: DESENHA O FUNDO PARALLAX PRIMEIRO
    renderParallaxBackground(g);

    // PASSO 2: DEIXA O RENDERMANAGER CUIDAR DO RESTO
    RenderManager.getInstance().render(g);
}

private void renderParallaxBackground(Graphics g) {
    if (parallaxLayers == null) return;

    for (ParallaxLayer layer : parallaxLayers) {
        Sprite sprite = layer.getSprite();
        if (sprite == null) continue;

        int spriteWidth = sprite.getWidth();
        if (spriteWidth <= 0) continue;
        
        // Calcula o deslocamento da camada baseado na câmera e no fator de rolagem
        int offsetX = (int)(Engine.camera.getX() * layer.getScrollFactor());
        
        // Calcula o ponto de partida para criar um loop infinito
        int startX = -(offsetX % spriteWidth);

        // Desenha cópias do sprite lado a lado para preencher a tela
        for (int i = 0; (i * spriteWidth) + startX < Engine.WIDTH; i++) {
            g.drawImage(sprite.getImage(), startX + (i * spriteWidth), 0, null);
        }
    }
}
```
Com isso, seu jogo terá um fundo **parallax** funcional. Você pode adicionar quantas camadas quiser e ajustar os valores de `scrollFactor` para obter o efeito desejado.
---
[⬅️ Voltar para o Guias Avançados](./README.md)