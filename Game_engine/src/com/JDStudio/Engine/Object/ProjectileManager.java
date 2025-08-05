// engine
package com.JDStudio.Engine.Object;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier; // IMPORTANTE

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Object.GameObject.CollisionType;
import com.JDStudio.Engine.World.World;

// Note que não importamos mais nada do pacote "com.game"
// import com.game.Projectile; // REMOVIDO!

public class ProjectileManager {

    private static final ProjectileManager instance = new ProjectileManager();
    // A piscina agora é do tipo abstrato BaseProjectile
    private final List<BaseProjectile> projectilePool = new ArrayList<>();
    
    // A "fábrica" que o jogo nos dará para criar projéteis
    private Supplier<BaseProjectile> projectileFactory;

    private ProjectileManager() {}

    public static ProjectileManager getInstance() {
        return instance;
    }

    /**
     * O jogo DEVE chamar este método uma vez para ensinar o manager a criar seus projéteis.
     * @param factory Uma função que sabe como criar um novo projétil do jogo (ex: () -> new Projectile())
     */
    public void init(Supplier<BaseProjectile> factory) {
        this.projectileFactory = factory;
    }

    public void spawnProjectile(GameObject owner, double startX, double startY, double dirX, double dirY,
                                double speed, double damage, int lifeTime, Sprite sprite) {
        if (projectileFactory == null) {
            System.err.println("ERRO: ProjectileManager não foi inicializado! Chame init() primeiro.");
            return;
        }

        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        if (length != 0) {
            dirX /= length;
            dirY /= length;
        }

        for (BaseProjectile p : projectilePool) {
            if (!p.isActive) {
                p.init(owner, startX, startY, dirX, dirY, speed, damage, lifeTime, sprite);
                return;
            }
        }
        
        // Usa a fábrica para criar um novo projétil sem que a engine saiba o tipo concreto
        BaseProjectile newProjectile = projectileFactory.get();
        newProjectile.init(owner, startX, startY, dirX, dirY, speed, damage, lifeTime, sprite);
        projectilePool.add(newProjectile);
    }

    /**
     * Atualiza todos os projéteis ativos. Agora também verifica colisão com o mundo.
     * @param allGameObjects A lista de todos os GameObjects para checar colisão.
     * @param world O mundo do jogo para checar colisão com os tiles.
     */
    public void update(List<GameObject> allGameObjects, World world) {
        for (BaseProjectile p : projectilePool) {
            if (p.isActive) {
                p.tick(); // Move o projétil

                // --- NOVA VERIFICAÇÃO: COLISÃO COM TILES DO MUNDO ---
                // Se o mundo existe e a posição atual do projétil NÃO está livre...
                if (world != null && !world.isFree(p.getX(), p.getY(), p.getMaskX(), p.getMaskY(), p.getMaskWidth(), p.getMaskHeight())) {
                    p.deactivate(); // ...desativa o projétil.
                    continue; // Pula para o próximo projétil, pois este já foi desativado.
                }

                // Verificação de colisão com GameObjects (lógica existente)
                for (GameObject other : allGameObjects) {
                    if (other == p || other == p.getOwner()) continue;

                    if (other.getCollisionType() == CollisionType.SOLID && GameObject.isColliding(p, other)) {
                        p.deactivate();
                        break;
                    }
                    else if (other instanceof Character && GameObject.isColliding(p, other)) {
                        // A conversão para int garante compatibilidade com o takeDamage
                        ((Character) other).takeDamage((int)p.getDamage());
                        p.deactivate();
                        break; 
                    }
                }
            }
        }
    }

    public void render(Graphics g) {
        for (BaseProjectile p : projectilePool) {
            if (p.isActive) {
                p.render(g);
            }
        }
    }
    
    public void reset() {
        projectilePool.clear();
        System.out.println("ProjectileManager resetado.");
    }
    
}