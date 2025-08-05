# Roadmap da Game Engine

Aqui está uma lista de sugestões de funcionalidades e melhorias para  engine

## Para Jogabilidade e Interação


- [x] **Sistema de Save/Load Game** Criar um sistema para salvar e carregar o jogo
- [x] **Sistema de Attachment:** Criar um `Attachment System` para um objeto poder ser filho/pai do outro
- [x] **Sistema de Projéteis:** Criar um `ProjectileManager` para gerenciar eficientemente o disparo, movimento e colisão de projéteis (balas, feitiços, etc.).
- [x] **Sistema de Eventos Genérico:** Implementar um `EventManager` global para permitir a comunicação entre objetos do jogo de forma desacoplada, melhorando a arquitetura do código.
- [x] **Sistema de Componentes Genérico:** Refatorar o `GameObject` para ser um contêiner mais flexível, permitindo adicionar/remover componentes (`addComponent`, `getComponent`) dinamicamente, em vez de ter campos fixos como `movement` e `animator`.

- [ ] **Finalizar e Refinar o A\* Pathfinding:** Reintegrar o `Pathfinder` ao `AIMovementComponent`, resolvendo os bugs de movimento para criar uma IA de navegação verdadeiramente inteligente que desvia de labirintos.
- [ ] **Física Simples:** Adicionar conceitos como gravidade e força de pulo ao `MovementComponent` para habilitar a criação de jogos de plataforma.
---
## Para Gráficos e Efeitos Visuais

- [x] **Sistema de Partículas:** Desenvolver um sistema para criar efeitos visuais como fumaça, explosões, fogo e magias, adicionando muito polimento visual.
- [x] **Iluminação 2D Simples:** Implementar um sistema de luzes 2D (fontes de luz coloridas e circulares) para criar uma atmosfera mais dinâmica no ambiente.
- [x] **Scrolling com Efeito Parallax:** Adicionar suporte a múltiplas camadas de background que se movem em velocidades diferentes em relação à câmera para criar um efeito de profundidade.
- [x] **Transições de Tela (Fades):** Implementar efeitos de fade-in e fade-out para transições suaves entre diferentes `GameStates` (ex: do menu para o jogo).
---
## Para Melhorias de UI e Input

- [x] **Suporte a Mouse:** Criar um `MouseManager` para rastrear a posição do cursor e os estados dos botões (pressionado, solto, clicado).
- [x] **Elemento de UI: Botão (`UIButton`):** Desenvolver um componente de botão clicável, com diferentes estados visuais (normal, hover, pressionado), que pode ser adicionado ao `UIManager`.
----
## Áudio e Imersão

- [ ] **Áudio Espacial (2D):** Fazer com que o volume e o balanço estéreo (esquerda/direita) dos efeitos sonoros mudem com base na sua posição em relação à câmera.
- [ ] **Mixer de Áudio:** Criar um sistema para gerenciar diferentes canais de áudio (Música, Efeitos Sonoros, UI) com controles de volume separados.


----
## Para Jogos De Plataforma

- [ ] **Sistema de Física**

   - [ ] Adicionar velocity e acceleration ao componente de movimento.

   - [ ] Mudar o loop de tick para usar posicao += velocidade.

   - [ ] Implementar uma resposta à colisão que diferencia chão, parede e teto.

- [ ] **Sistema de Tiles Avançado**

   - [ ] Criar um enum para tipos de tile (SOLID, ONE_WAY, etc.).

   - [ ] Implementar a lógica para plataformas "pula-através" (ONE_WAY).

- [ ] **Máquina de Estados do Player**

   - [ ] Criar um enum para os estados do jogador (JUMPING, FALLING, etc.).

   - [ ] Estruturar o tick() do Player em torno dessa máquina de estados.