// game
package com.game.manegers;

/**
 * Define todos os tipos de eventos possíveis no jogo de forma segura.
 * Usar um enum previne erros de digitação e centraliza os eventos.
 */
public enum GameEvent {
	PLAYER_DIED,
    ENEMY_DIED,
    PLAYER_TOOK_DAMAGE,
    PLAYER_HEALED,
    PLAYER_FIRE,
    PLAYER_ADD_AMMO,
    DOOR_OPENED
    // Adicione qualquer outro evento que seu jogo precise aqui...
}