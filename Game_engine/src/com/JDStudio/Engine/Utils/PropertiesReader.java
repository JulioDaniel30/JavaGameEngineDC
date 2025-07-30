package com.JDStudio.Engine.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class PropertiesReader {

    private final JSONObject mergedProperties;

    public PropertiesReader(JSONObject tiledObjectProperties) {
        // Começa com uma cópia das propriedades raiz (x, y, name, etc.)
        this.mergedProperties = new JSONObject(tiledObjectProperties.toString());

        // Extrai e funde as propriedades customizadas
        if (tiledObjectProperties.has("properties")) {
            JSONArray propsArray = tiledObjectProperties.getJSONArray("properties");
            for (int i = 0; i < propsArray.length(); i++) {
                JSONObject prop = propsArray.getJSONObject(i);
                String key = prop.getString("name");
                Object value = prop.get("value");
                this.mergedProperties.put(key, value);
            }
        }
    }

    // Agora, todos os métodos 'get' funcionam tanto para propriedades normais como para as customizadas
    public String getString(String key, String defaultValue) {
        return mergedProperties.has(key) ? mergedProperties.getString(key) : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        return mergedProperties.has(key) ? mergedProperties.getInt(key) : defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        return mergedProperties.has(key) ? mergedProperties.getDouble(key) : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mergedProperties.has(key) ? mergedProperties.getBoolean(key) : defaultValue;
    }
}