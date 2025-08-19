package com.JDStudio.Engine.Prefab;

import org.json.JSONArray;

/**
 * Representa a definição de um Prefab carregada de um ficheiro JSON.
 * Contém a classe base e a lista de componentes a serem adicionados.
 */
public class Prefab {
    public final String name;
    public final String baseClassName;
    public final JSONArray componentsJson;

    public Prefab(String name, String baseClassName, JSONArray componentsJson) {
        this.name = name;
        this.baseClassName = baseClassName;
        this.componentsJson = componentsJson;
    }
}