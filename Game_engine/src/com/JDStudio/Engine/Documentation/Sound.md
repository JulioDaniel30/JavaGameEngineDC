# Pacote `com.JDStudio.Engine.Sound`

Um utilitário simples e estático para carregar e reproduzir áudio no jogo.

## Resumo das Classes

### `Sound.java`

Uma classe `final` com métodos estáticos, projetada para ser facilmente acessível de qualquer parte do código sem a necessidade de instanciar um objeto.

- **Funcionalidades:**
    - `play(path)`: Reproduz um efeito sonoro uma única vez.
    - `loop(path)`: Inicia a reprodução de uma música em loop contínuo. Se uma música já estiver tocando, ela é interrompida e a nova começa.
    - **Controle de Volume:** Permite ajustar o volume dos efeitos sonoros (`setSfxVolume`) e da música (`setMusicVolume`) de forma independente.
    - **Cache:** Armazena os dados dos arquivos de áudio em memória após o primeiro carregamento para evitar leituras repetidas do disco, melhorando a performance.

## Como Usar

Para tocar um som ou música, basta chamar os métodos estáticos da classe:

```java
// Tocar um efeito sonoro de pulo
Sound.play("/sounds/jump.wav");

// Iniciar a música da fase em loop
Sound.loop("/music/level1_theme.wav");

// Ajustar o volume
Sound.setMusicVolume(0.5f); // 50% do volume