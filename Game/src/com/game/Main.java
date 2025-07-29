package com.game;

import com.JDStudio.Engine.Engine;

public class Main {
	public static void main(String[] args) {
		Engine engine = new Engine();
		Engine.setGameState(new PlayingState()); // Define a cena inicial
		engine.start();
	}
}