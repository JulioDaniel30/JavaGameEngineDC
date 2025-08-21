// game
package com.game.manegers;

import com.jdstudio.engine.Object.BaseProjectile; // Importa a classe base da ENGINE

/**
 * A implementação específica do nosso jogo para um projétil.
 * Herda toda a lógica da classe BaseProjectile da engine.
 */
public class Projectile extends BaseProjectile {

    public Projectile() {
        super(); // Chama o construtor da classe pai
        // Você pode adicionar lógicas específicas do seu jogo aqui se precisar,
        // como um som especial ao ser criado, etc.
    }
    
    // Toda a lógica de init(), tick(), deactivate() já está na classe pai!
}