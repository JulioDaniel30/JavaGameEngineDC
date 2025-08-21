package com.jdstudio.engine.Dialogue;

/**
 * Representa um ponto de entrada condicional para um diálogo.
 */
public record DialogueEntryPoint(String condition, int nodeId) {}