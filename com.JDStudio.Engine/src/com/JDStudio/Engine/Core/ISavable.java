package com.JDStudio.Engine.Core;

import org.json.JSONObject;

/**
 * Define o contrato para objetos que podem ter seu estado salvo e carregado.
 */
public interface ISavable {

    /**
     * Gera um JSONObject contendo o estado atual do objeto.
     * @return Um JSONObject com os dados a serem salvos.
     */
    JSONObject saveState();

    /**
     * Restaura o estado do objeto a partir de um JSONObject.
     * @param state O JSONObject contendo os dados salvos.
     */
    void loadState(JSONObject state);
}