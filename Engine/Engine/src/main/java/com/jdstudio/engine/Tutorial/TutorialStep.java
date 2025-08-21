package com.jdstudio.engine.Tutorial;

import org.json.JSONObject;

/**
 * Representa um único passo de um tutorial, lido de um arquivo JSON.
 */
public class TutorialStep {

    public final String id;
    public final String text;
    public final JSONObject triggerCondition;
    public final JSONObject completionCondition;
    public final JSONObject uiConfig;

    public TutorialStep(JSONObject json) {
        this.id = json.getString("id");
        this.text = json.getString("text");
        this.triggerCondition = json.getJSONObject("trigger");
        this.completionCondition = json.getJSONObject("completion");
        this.uiConfig = json.optJSONObject("ui"); // A UI é opcional
    }
}