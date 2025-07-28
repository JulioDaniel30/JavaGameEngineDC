# Pacote `com.JDStudio.Engine.Graphics`

Este pacote contém classes de alto nível para o gerenciamento de recursos gráficos.

## Resumo das Classes

### `AssetManager.java`

Uma classe de gerenciamento central para todos os recursos visuais (assets) do jogo. O seu principal objetivo é carregar e armazenar sprites para que eles possam ser facilmente acessados de qualquer parte do jogo sem a necessidade de recarregá-los do disco.

- **Funcionalidades:**
    - **Cache de Sprites:** Utiliza um `Map` para armazenar `Sprite`s associados a uma chave de texto única (ex: "player_idle").
    - **Carregamento:** O método `loadSprite(key, path)` carrega uma imagem de um arquivo e a armazena no cache.
    - **Registro:** O método `registerSprite(key, sprite)` permite adicionar ao cache um `Sprite` já existente, o que é ideal para sprites extraídos de uma `Spritesheet`.
    - **Acesso:** O método `getSprite(key)` recupera um sprite do cache de forma rápida.
    - **Prevenção de Duplicidade:** Garante que cada recurso seja carregado da memória apenas uma vez, economizando recursos e otimizando a performance.