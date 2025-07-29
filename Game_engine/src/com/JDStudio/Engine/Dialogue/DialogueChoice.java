package com.JDStudio.Engine.Dialogue;

//Em Engine/Dialogue/DialogueChoice.java
public class DialogueChoice {
 public final String text;
 public final int nextNodeId;
 public final String action; // <-- NOVO CAMPO

 public DialogueChoice(String text, int nextNodeId, String action) {
     this.text = text;
     this.nextNodeId = nextNodeId;
     this.action = action;
 }
}