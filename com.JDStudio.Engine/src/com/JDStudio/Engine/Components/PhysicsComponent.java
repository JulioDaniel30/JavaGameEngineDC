package com.JDStudio.Engine.Components;

import com.JDStudio.Engine.Events.EngineEvent;
import com.JDStudio.Engine.Events.EventManager;
import com.JDStudio.Engine.Events.WorldLoadedEventData;
import com.JDStudio.Engine.World.World;


public class PhysicsComponent extends Component {

    // --- Propriedades da Física ---
    public double velocityX, velocityY;
    public double accelerationX, accelerationY;

    public double gravity = 0.5;
    public boolean onGround = false;

    private World world; // A referência ao mundo, agora obtida via evento

    /**
     * O construtor agora é responsável por se inscrever no evento WORLD_LOADED.
     */
    public PhysicsComponent() {
        // O componente inscreve-se para ouvir quando o mundo estiver pronto.
        EventManager.getInstance().subscribe(EngineEvent.WORLD_LOADED, (data) -> {
            // Apenas configura se ainda não tiver sido configurado
            if (this.world == null && data instanceof WorldLoadedEventData) {
                WorldLoadedEventData eventData = (WorldLoadedEventData) data;
                this.world = eventData.world();
                System.out.println("PhysicsComponent para '" + owner.name + "' configurado via evento!");
            }
        });
    }

    @Override
    public void update() {
        // Se o mundo ainda não foi carregado, não faz nada para evitar erros.
        if (world == null) return;
        owner.onGround = false;

        // 1. Aplica a aceleração à velocidade
        this.velocityX += this.accelerationX;
        this.velocityY += this.accelerationY;

        // 2. Aplica a gravidade à velocidade vertical
        this.velocityY += this.gravity;

        // 3. Move o objeto no eixo X e verifica colisões
        moveAndCollide(velocityX, 0);

        // 4. Move o objeto no eixo Y e verifica colisões
        moveAndCollide(0, velocityY);

        owner.velocityY = this.velocityY;
        // Reseta a aceleração a cada quadro
        this.accelerationX = 0;
        this.accelerationY = 0;
    }

    /**
     * Move o objeto e responde a colisões, um eixo de cada vez.
     */
    private void moveAndCollide(double moveX, double moveY) {
        this.onGround = false; // Assume que não está no chão até provar o contrário

        // Move pixel por pixel para uma colisão precisa
        for (int i = 0; i < Math.abs(moveX); i++) {
            if (!isPathClearForPhysics(owner.getX() + (int)Math.signum(moveX), owner.getY())) {
                this.velocityX = 0; // Bateu numa parede, para a velocidade horizontal
                break;
            }
            owner.setX(owner.getX() + (int)Math.signum(moveX));
        }

        for (int i = 0; i < Math.abs(moveY); i++) {
            if (!isPathClearForPhysics(owner.getX(), owner.getY() + (int)Math.signum(moveY))) {
                // Se estava a cair, significa que aterrou
                if (moveY > 0) {
                    this.onGround = true;
                }
                this.velocityY = 0; // Bateu no chão ou no teto, para a velocidade vertical
                break;
            }
            owner.setY(owner.getY() + (int)Math.signum(moveY));
        }
    }

    /**
     * Verifica se o caminho está livre para um objeto com física.
     * Usa o método isFree do World.
     */
    private boolean isPathClearForPhysics(int nextX, int nextY) {
        // A verificação 'world == null' já acontece no início do update()
        return world.isFree(nextX, nextY, owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getMaskHeight());
    }
    
    /**
     * Aplica uma força vertical instantânea ao objeto (ex: pulo).
     * @param force A força do pulo (um valor negativo para ir para cima).
     */
    public void addVerticalForce(double force) {
        this.velocityY = force;
    }
}