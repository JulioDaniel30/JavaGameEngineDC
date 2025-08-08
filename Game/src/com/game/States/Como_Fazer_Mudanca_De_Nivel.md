# Guia de Implementação: Mudança de Nível

Este documento descreve como implementar um sistema de mudança de nível (transição entre mapas) na sua game engine. O sistema é **data-driven**, o que significa que a conexão entre os níveis é definida diretamente no editor de mapas Tiled, tornando o fluxo de trabalho rápido e flexível.

## O Conceito

A lógica funciona da seguinte maneira:
1.  No Tiled, criamos uma área retangular invisível chamada **Zona de Saída**.
2.  Nesta zona, adicionamos uma propriedade customizada que informa à engine qual o **caminho para o próximo mapa** (ex: `/map2.json`).
3.  A engine carrega essa zona como um `GameObject` especial.
4.  Quando o jogador colide com esta zona, a engine inicia uma transição de tela e carrega um novo `PlayingState` com o mapa especificado.

---

## Passo 1: Preparar a Engine ()

Para que o sistema funcione, a sua engine já foi preparada com as seguintes classes e modificações:

* **`PlayingState(String mapPath)`:** O construtor do `PlayingState` foi modificado para aceitar o caminho do arquivo de mapa a ser carregado. Isso o torna reutilizável para qualquer nível.
* **`LevelExitZone.java`:** Uma nova classe foi criada no jogo. Ela herda de `TriggerZone` e tem a capacidade de ler e armazenar a propriedade `nextMap` a partir do Tiled.
* **`Engine.transitionToState(...)`:** A engine possui um método para realizar transições de tela suaves (fade) entre os estados de jogo.

---

## Passo 2: Configurar o Nível no Tiled Editor

Esta é a parte prática, onde você conecta seus mapas.

### 2.1 - Criar a Zona de Saída

1.  Abra o seu mapa (ex: `map1.json`) no Tiled.
2.  Selecione a **Camada de Objetos** onde você coloca entidades como o jogador e inimigos.
3.  Selecione a ferramenta **"Inserir Retângulo"** na barra de ferramentas.
4.  Desenhe um retângulo na área do mapa que servirá como a "porta" ou saída para o próximo nível.

### 2.2 - Definir as Propriedades do Objeto

1.  Com o retângulo selecionado, vá para o painel de **Propriedades** à esquerda.
2.  No campo **Classe** (ou "Type"), digite exatamente: `LevelExit`
    * Este nome deve corresponder ao `case` que criaremos no código.
3.  Abaixo, na seção **Propriedades Customizadas**, clique no ícone de **`+`** para adicionar uma nova propriedade.
    * **Tipo:** `string`
    * **Nome:** `nextMap`
    * **Valor:** O caminho para o próximo mapa (ex: `/map2.json`)

![Configuração do Objeto de Saída no Tiled](https://i.imgur.com/B9iEw3c.png)
*(Exemplo visual de como as propriedades devem ficar no Tiled)*

4.  Salve o seu mapa.

---

## Passo 3: Implementar a Lógica no Jogo (`PlayingState.java`)

Agora, vamos garantir que o `PlayingState` saiba como criar e reagir à nossa nova `LevelExitZone`.

### 3.1 - Ensinar o Jogo a Criar a Zona

No seu método `onObjectFound`, adicione um novo `case` para o tipo `LevelExit`.

```java
// Em PlayingState.java -> onObjectFound

@Override
public void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties) {
    GameObject newObject = null;
    
    switch (type) {
        // ... (seus outros cases como "player_start", "enemy", etc.)

        case "LevelExit":
            LevelExitZone exitZone = new LevelExitZone(properties);
            newObject = exitZone;
            // Adicionamos à lista geral de trigger zones para a verificação de colisão
            this.triggerZones.add(exitZone);
            break;

        // ...
    }
    
    if (newObject != null) {
        this.addGameObject(newObject);
    }
}