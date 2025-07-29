package com.JDStudio.Engine.World;

import org.json.JSONObject;

public interface IMapLoaderListener {

    /**
     * Chamado pela engine sempre que um tile é encontrado no mapa.
     * @param layerName O nome da camada onde o tile foi encontrado.
     * @param tileId O ID do tile lido do arquivo JSON.
     * @param x A posição X (em pixels) onde o tile deve ser colocado.
     * @param y A posição Y (em pixels) onde o tile deve ser colocado.
     * @return O objeto Tile específico do jogo a ser criado.
     */
    Tile onTileFound(String layerName, int tileId, int x, int y);

    /**
     * @param type O "type" ou "class" do objeto lido do JSON.
     * @param x A posição X do objeto.
     * @param y A posição Y do objeto.
     * @param width A largura do objeto.
     * @param height A altura do objeto.
     * @param properties O objeto JSON contendo todas as propriedades.
     */
    void onObjectFound(String type, int x, int y, int width, int height, JSONObject properties);
}