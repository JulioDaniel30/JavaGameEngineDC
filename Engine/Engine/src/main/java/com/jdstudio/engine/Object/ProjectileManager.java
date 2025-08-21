// engine
package com.jdstudio.engine.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Object.GameObject.CollisionType;
import com.jdstudio.engine.World.World;

public class ProjectileManager {

    private static final ProjectileManager instance = new ProjectileManager();
    private final List<BaseProjectile> projectilePool = new ArrayList<>();
    
    private Supplier<BaseProjectile> projectileFactory;
    private World world;
    private List<GameObject> allGameObjects; // Referência para a lista principal do jogo

    private ProjectileManager() {}

    public static ProjectileManager getInstance() {
        return instance;
    }

    public void init(Supplier<BaseProjectile> factory, World world, List<GameObject> gameObjects) {
        this.projectileFactory = factory;
        this.world = world;
        this.allGameObjects = gameObjects;
    }

    public void spawnProjectile(GameObject owner, double startX, double startY, double dirX, double dirY,
                                double speed, int damage, int lifeTime, Sprite sprite) {
        if (projectileFactory == null) {
            System.err.println("ERRO: ProjectileManager não foi inicializado!");
            return;
        }

        // ... (lógica de normalização de direção)

        BaseProjectile projectileToSpawn = null;
        for (BaseProjectile p : projectilePool) {
            if (!p.isActive) {
                projectileToSpawn = p;
                break;
            }
        }
        
        if (projectileToSpawn == null) {
            projectileToSpawn = projectileFactory.get();
            projectilePool.add(projectileToSpawn);
        }
        
        projectileToSpawn.init(owner, startX, startY, dirX, dirY, speed, damage, lifeTime, sprite);
        
        // --- A CORREÇÃO CRÍTICA ESTÁ AQUI ---
        // Adiciona o projétil à lista principal de GameObjects do jogo,
        // garantindo que ele seja visto pelo sistema de colisão imediatamente.
        if (!allGameObjects.contains(projectileToSpawn)) {
            allGameObjects.add(projectileToSpawn);
        }
    }

    public void update() {
        if (world == null || allGameObjects == null) return;
        
        // Usamos um iterador para poder remover itens com segurança durante o loop
        for (BaseProjectile p : projectilePool) {
            if (p.isActive) {
                p.tick();

                if (!world.isFree(p.getX(), p.getY(), p.getMaskX(), p.getMaskY(), p.getMaskWidth(), p.getMaskHeight())) {
                    p.deactivate();
                }

                for (GameObject other : allGameObjects) {
                    if (other == p || other == p.getOwner() || other.isDestroyed) continue;
                    if ((other.getCollisionType() == CollisionType.SOLID
                    		|| other.getCollisionType() == CollisionType.CHARACTER_TRIGGER
                    		|| other.getCollisionType() == CollisionType.CHARACTER_SOLID) && GameObject.isColliding(p, other)) {
                        if (other instanceof Character) {
                            ((Character) other).takeDamage(p.getDamage());
                        }
                        p.deactivate();
                        break; 
                    }
                }
            }
        }
    }

    // A renderização agora é feita pelo RenderManager, pois os projéteis são GameObjects
    // Este método pode ser removido se você registar os projéteis no RenderManager.
    // public void render(Graphics g) { ... }
    
    public void reset() {
        // Ao reiniciar, remove todos os projéteis da lista do jogo também
        for(BaseProjectile p : projectilePool) {
            allGameObjects.remove(p);
        }
        projectilePool.clear();
    }
}