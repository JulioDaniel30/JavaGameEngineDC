package com.JDStudio.Engine.World;

import org.json.JSONObject;

/**
 * Uma interface (contrato) que permite que a engine delegue a criação de
 * tiles e objetos específicos do jogo para a classe que está carregando o mapa.
 */
public interface IMapLoaderListener {

    /**
     * Chamado pela engine sempre que um tile é encontrado no mapa.
     * @param tileId O ID do tile lido do arquivo JSON.
     * @param x A posição X (em pixels) onde o tile deve ser colocado.
     * @param y A posição Y (em pixels) onde o tile deve ser colocado.
     * @return O objeto Tile específico do jogo a ser criado.
     */
    Tile onTileFound(int tileId, int x, int y);

    /**
     * Chamado pela engine sempre que um objeto é encontrado em uma camada de objetos.
     * @param type O "Tipo" do objeto definido no Tiled (ex: "player_start").
     * @param x A posição X do objeto.
     * @param y A posição Y do objeto.
     * @param properties O objeto JSON contendo todas as propriedades personalizadas do objeto.
     */
    void onObjectFound(String type, int x, int y, JSONObject properties);
}