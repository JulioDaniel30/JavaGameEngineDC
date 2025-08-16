# Guia Avançado: Sombras Dinâmicas de Personagens

Este guia detalha como usar o `ShadowComponent` para adicionar sombras dinâmicas aos seus `GameObject`s, como jogadores e inimigos. Adicionar sombras é uma forma simples e eficaz de dar profundidade e "ancorar" os personagens ao cenário.

A engine oferece duas formas de criar sombras, ambas gerenciadas pelo `ShadowComponent`.

## O Conceito

O `ShadowComponent` é um componente que, quando adicionado a um `GameObject`, se encarrega de desenhar uma sombra na camada correta (`GAMEPLAY_BELOW`) e com uma profundidade (`Z-Order`) que garante que ela sempre apareça por baixo do seu "dono". A sombra segue a posição do `GameObject` automaticamente.

---

### Método 1: Sombra Procedural (Oval e Suave)

Esta é a abordagem mais fácil e flexível. O componente desenha uma sombra em formato de elipse com um efeito de gradiente suave, ideal para a maioria dos personagens.

**Como Usar:**

No construtor ou método `initialize` do seu `GameObject`, simplesmente crie e adicione uma instância do `ShadowComponent`.

```java
// Dentro da sua classe Player.java (ou Enemy.java)

public Player(JSONObject properties) {
    super(properties);

    // ... (outros componentes como Animator, MovementComponent)

    // Adiciona uma sombra procedural
    // Parâmetros: (largura, altura, opacidade, deslocamento Y)
    this.addComponent(new ShadowComponent(12, 6, 0.4f, 0)); 
}
```

- **largura e altura:** Definem o tamanho da elipse da sombra.
- **opacidade:** Um valor de 0.0f (invisível) a 1.0f (totalmente preto). 0.4f (40%) é um bom valor inicial.
- **deslocamento Y:** Quantos pixels a sombra deve aparecer abaixo da base do personagem. 0 significa que o centro da sombra estará alinhado com os "pés" do personagem.

---

### Método 2: Sombra Baseada em Sprite

Esta abordagem lhe dá total controle artístico sobre o formato da sombra. É útil para personagens com formatos não convencionais ou para criar efeitos de sombra mais estilizados.

**Como Usar:**

1. **Carregue o Sprite da Sombra:** Primeiro, carregue a imagem da sua sombra no AssetManager, como faria com qualquer outro sprite. É recomendado que a imagem da sombra seja em tons de cinza ou preto.

```java
// Em um método de carregamento (ex: PlayingState.loadAssets())
assets.loadSprite("sombra_personagem", "/sprites/efeitos/sombra_circular.png");
```

2. **Crie o Componente com o Sprite:** No construtor do seu GameObject, crie o ShadowComponent passando o Sprite carregado.

```java
// Dentro da sua classe Player.java

public Player(JSONObject properties) {
    super(properties);

    // ... (outros componentes)

    // Pega o sprite da sombra que já foi carregado
    Sprite sombraSprite = PlayingState.assets.getSprite("sombra_personagem");

    // Adiciona uma sombra baseada em sprite
    // Parâmetros: (sprite, deslocamento Y)
    this.addComponent(new ShadowComponent(sombraSprite, 0));
}
```
A opacidade do sprite pode ser ajustada no próprio arquivo de imagem. O componente também aplica uma opacidade base, que pode ser customizada no código se necessário.

#### Conclusão
Com o `ShadowComponent`, você pode facilmente adicionar um nível extra de polimento visual aos seus jogos. Experimente diferentes tamanhos, opacidades e sprites para encontrar o estilo que melhor se adapta à sua direção de arte.

---
[⬅️ Voltar para o Guias Avançados](./README.md)