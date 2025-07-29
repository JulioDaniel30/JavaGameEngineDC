# Pacote: com.JdStudio.Engine.Sound

Uma classe utilitária estática para controle de áudio.

## Classe `Sound`

Gerencia o carregamento, cache e reprodução de efeitos sonoros (SFX) e músicas.

### Visão Geral

-   **Métodos Estáticos**: Não precisa ser instanciada.
-   **Cache de Áudio**: Efeitos sonoros são cacheados em memória para reprodução rápida. Músicas são cacheadas como `Clip` para controle de loop.
-   **Separação de Volume**: Controles de volume separados para música e SFX.

### Métodos Principais

-   `play(path)`: Toca um efeito sonoro uma vez.
-   `loop(path)`: Toca uma música em loop contínuo. Para a música anterior antes de tocar a nova.
-   `stopMusic()`: Para a música que estiver tocando.
-   `setMusicVolume(float)` e `setSfxVolume(float)`: Ajustam o volume (0.0f a 1.0f).

### Exemplo de Uso

```java
public class MyGame {

    public void init() {
        // Toca a música de fundo da fase
        Sound.loop("/music/level1_theme.wav");
        Sound.setMusicVolume(0.8f);
    }
    
    public void playerShoot() {
        // Toca o som do tiro
        Sound.play("/sfx/laser_shot.wav");
    }

    public void openSettingsMenu() {
        // Exemplo de como diminuir o volume da música ao entrar em um menu
        Sound.setMusicVolume(0.3f);
    }
}
```