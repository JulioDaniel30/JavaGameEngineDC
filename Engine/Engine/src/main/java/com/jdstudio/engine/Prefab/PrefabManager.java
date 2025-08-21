package com.jdstudio.engine.Prefab;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.jdstudio.engine.Components.Component;
import com.jdstudio.engine.Object.GameObject;

/**
 * Um Singleton que gere o carregamento de definições de Prefab e a
 * instanciação de GameObjects a partir delas.
 */
public class PrefabManager {
    private static final PrefabManager instance = new PrefabManager();
    private final Map<String, Prefab> prefabs = new HashMap<>();

    private PrefabManager() {}
    public static PrefabManager getInstance() { return instance; }

    /**
     * Carrega uma única definição de prefab de um ficheiro JSON.
     * @param jsonPath O caminho para o recurso do ficheiro .json.
     */
    public void loadPrefab(String jsonPath) {
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Ficheiro de Prefab não encontrado: " + jsonPath);
                return;
            }
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject prefabJson = new JSONObject(jsonText);

            String name = prefabJson.getString("name");
            String baseClass = prefabJson.getString("baseClass");
            JSONArray components = prefabJson.getJSONArray("components");
            
            prefabs.put(name, new Prefab(name, baseClass, components));
            System.out.println("Prefab '" + name + "' carregado com sucesso.");

        } catch (Exception e) {
            System.err.println("Falha ao carregar o prefab de: " + jsonPath);
            e.printStackTrace();
        }
    }

    /**
     * Instancia um novo GameObject a partir de um prefab, passando as propriedades do Tiled.
     * @param prefabName O nome do prefab a ser instanciado.
     * @param tiledProperties As propriedades do objeto lidas do Tiled (x, y, name, etc.).
     * @return O GameObject totalmente montado, ou null se falhar.
     */
    public GameObject instantiate(String prefabName, JSONObject tiledProperties) {
        Prefab prefab = prefabs.get(prefabName);
        if (prefab == null) {
            System.err.println("Prefab '" + prefabName + "' não encontrado!");
            return null;
        }

        try {
            // 1. Cria a instância da classe base usando Reflection
            Class<?> clazz = Class.forName(prefab.baseClassName);
            Constructor<?> ctor = clazz.getConstructor(JSONObject.class);
            GameObject gameObject = (GameObject) ctor.newInstance(tiledProperties);

            // 2. Itera sobre as definições de componentes do prefab
            for (int i = 0; i < prefab.componentsJson.length(); i++) {
                JSONObject componentJson = prefab.componentsJson.getJSONObject(i);
                String componentClassName = componentJson.getString("class");
                
                // 3. Cria a instância do componente usando Reflection
                Class<?> componentClass = Class.forName(componentClassName);
                Component component = (Component) componentClass.getConstructor().newInstance();

                // 4. (Futuro) Configura as propriedades do componente a partir do JSON...
                
                // 5. Adiciona o componente ao GameObject
                gameObject.addComponent(component);
            }
            
            return gameObject;
        } catch (Exception e) {
            System.err.println("Falha ao instanciar o prefab '" + prefabName + "'. Verifique os nomes das classes e construtores.");
            e.printStackTrace();
            return null;
        }
    }
}