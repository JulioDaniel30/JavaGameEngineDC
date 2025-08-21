package com.jdstudio.engine.Components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.InteractionEventData;
import com.jdstudio.engine.Object.GameObject;

/**
 * Gerencia múltiplas zonas de interação para um GameObject.
 * Dispara eventos quando um alvo entra ou sai de uma de suas zonas.
 */
public class InteractionComponent extends Component {

    private List<InteractionZone> zones = new ArrayList<>();
    // Mapeia um alvo para as zonas em que ele está atualmente
    private Map<GameObject, List<InteractionZone>> trackedTargets = new HashMap<>();

    public InteractionComponent() {
        // Construtor vazio
    }

    public void addZone(InteractionZone zone) {
        this.zones.add(zone);
    }

    /**
     * O método principal que verifica as interações.
     * @param targets Uma lista de GameObjects a serem verificados (geralmente, apenas o jogador).
     */
    public void checkInteractions(List<GameObject> targets) {
        if (owner == null) return;

        // Atualiza a posição de todas as zonas para seguir o dono
        for (InteractionZone zone : zones) {
            zone.updatePosition(); // A zona sabe como se atualizar
        }

        for (GameObject target : targets) {
            if (target == owner) continue;

            List<InteractionZone> zonesTargetIsIn = trackedTargets.computeIfAbsent(target, k -> new ArrayList<>());
            double targetCenterX = target.getX() + target.getWidth() / 2.0;
            double targetCenterY = target.getY() + target.getHeight() / 2.0;

            for (InteractionZone zone : zones) {
                boolean isInside = zone.contains(targetCenterX, targetCenterY);
                boolean wasInside = zonesTargetIsIn.contains(zone);

                if (isInside && !wasInside) {
                    // Alvo acabou de entrar
                    zonesTargetIsIn.add(zone);
                    // Dispara um evento!
                    EventManager.getInstance().trigger(EngineEvent.TARGET_ENTERED_ZONE, new InteractionEventData(owner, target, zone));
                } else if (!isInside && wasInside) {
                    // Alvo acabou de sair
                    zonesTargetIsIn.remove(zone);
                    // Dispara outro evento!
                    EventManager.getInstance().trigger(EngineEvent.TARGET_EXITED_ZONE, new InteractionEventData(owner, target, zone));
                }
            }
        }
    }

    /**
     * **MÉTODO DE RENDERIZAÇÃO DE DEBUG ATUALIZADO**
     * Este método agora desenha cada zona com uma cor diferente.
     */
    @Override
    public void render(Graphics g) {
        if (!Engine.isDebug || owner == null) return;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        for (InteractionZone zone : zones) {
            Color debugColor;
            
            // --- A LÓGICA DE SELEÇÃO DE COR ESTÁ AQUI ---
            switch (zone.type) {
                case InteractionZone.TYPE_AGGRO:
                    debugColor = new Color(255, 255, 0, 70); // Amarelo para perseguição
                    break;
                case InteractionZone.TYPE_ATTACK:
                    debugColor = new Color(255, 0, 0, 70);   // Vermelho para ataque
                    break;
                case InteractionZone.TYPE_DIALOGUE:
                    debugColor = new Color(0, 255, 255, 70); // Ciano para diálogo
                    break;
                default:
                    debugColor = new Color(128, 128, 128, 70); // Cinza para qualquer outro tipo
                    break;
            }
            
            g2d.setColor(debugColor);
            
            Shape s = zone.getShape();
            
            g2d.translate(-Engine.camera.getX(), -Engine.camera.getY());
            g2d.fill(s);
            g2d.setTransform(originalTransform);
        }
    }
}