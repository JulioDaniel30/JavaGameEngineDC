package com.JDStudio.Engine.Tutorial;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Events.EventListener;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Graphics.UI.Elements.TutorialBox;
import com.JDStudio.Engine.Input.InputManager;

/**
 * Gerencia o carregamento e a execução de tutoriais definidos em JSON.
 */
public class TutorialManager {

    private static final TutorialManager instance = new TutorialManager();
    private Map<String, TutorialStep> availableTutorials = new HashMap<>();
    private TutorialStep activeTutorial = null;
    private TutorialBox tutorialBox;
    private int durationTimer = 0;

    private TutorialManager() {}

    public static TutorialManager getInstance() {
        return instance;
    }

    public void loadTutorials(String jsonPath, TutorialBox box) {
        this.tutorialBox = box;
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Arquivo de tutorial não encontrado: " + jsonPath);
                return;
            }
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(jsonText);
            JSONArray tutorialsArray = config.getJSONArray("tutorials");
            
            for (int i = 0; i < tutorialsArray.length(); i++) {
                TutorialStep step = new TutorialStep(tutorialsArray.getJSONObject(i));
                availableTutorials.put(step.id, step);
            }
            System.out.println(availableTutorials.size() + " tutoriais carregados.");
            
            // Inscreve-se nos eventos para verificar os gatilhos
            setupEventListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupEventListeners() {
        EventListener globalListener = (eventEnum) -> {
            if (activeTutorial != null) return; // Só verifica gatilhos se nenhum tutorial estiver ativo
            
            // O nome do evento é o nome do valor do enum (ex: "GAME_STARTED")
            String eventName = ((Enum<?>) eventEnum).name();
            
            for (TutorialStep step : availableTutorials.values()) {
                // **LÓGICA DE VERIFICAÇÃO CORRIGIDA**
                if ("EVENT".equals(step.triggerCondition.getString("type")) && 
                    eventName.equals(step.triggerCondition.getString("eventName"))) {
                    activateTutorial(step);
                    break;
                }
            }
        };

        // **A CORREÇÃO PRINCIPAL ESTÁ AQUI**
        // Agora, o manager inscreve-se para ouvir todos os eventos.
        EventManager.getInstance().subscribeToAll(globalListener);
    }

    private void activateTutorial(TutorialStep step) {
        if (activeTutorial != null || tutorialBox == null) return;
        
        activeTutorial = step;
        String position = (step.uiConfig != null) ? step.uiConfig.optString("position", "TOP_CENTER") : "TOP_CENTER";
        tutorialBox.show(step.text, position);
        
        if ("DURATION".equals(step.completionCondition.getString("type"))) {
            durationTimer = step.completionCondition.getInt("ticks");
        }
    }

    private void completeTutorial() {
        if (activeTutorial == null || tutorialBox == null) return;
        
        availableTutorials.remove(activeTutorial.id); // Garante que o tutorial não apareça novamente
        activeTutorial = null;
        tutorialBox.hide();
    }

    public void update() {
        if (activeTutorial == null) return;

        // Verifica as condições de conclusão
        JSONObject completion = activeTutorial.completionCondition;
        String type = completion.getString("type");

        if ("KEY_PRESS".equals(type)) {
            JSONArray actions = completion.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++) {
                if (InputManager.isActionJustPressed(actions.getString(i))) {
                    completeTutorial();
                    break;
                }
            }
        } else if ("DURATION".equals(type)) {
            durationTimer--;
            if (durationTimer <= 0) {
                completeTutorial();
            }
        }
    }
}