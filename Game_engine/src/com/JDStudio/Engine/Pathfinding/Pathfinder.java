package com.JDStudio.Engine.Pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.JDStudio.Engine.Utils.Node;
import com.JDStudio.Engine.World.World;

/**
 * Uma classe utilitária que implementa o algoritmo A* para encontrar
 * o caminho mais curto entre dois pontos em um mapa de tiles.
 */
public class Pathfinder {

    // Custo de movimento para se mover para um tile adjacente (reto)
    private static final double MOVE_STRAIGHT_COST = 10.0;
    // Custo de movimento para se mover para um tile diagonal
    private static final double MOVE_DIAGONAL_COST = 14.0; // Aproximadamente 10 * sqrt(2)

    /**
     * Encontra o caminho mais curto de um ponto a outro.
     * @param world O mapa no qual a busca será feita.
     * @param start A coordenada inicial em pixels.
     * @param end   A coordenada final em pixels.
     * @return Uma lista de Points representando o caminho em pixels, ou uma lista vazia se nenhum caminho for encontrado.
     */
    public static List<Point> findPath(World world, Point start, Point end) {
        // Converte as coordenadas de pixels para coordenadas de tiles
        int startX = start.x / world.tileWidth;
        int startY = start.y / world.tileHeight;
        int endX = end.x / world.tileWidth;
        int endY = end.y / world.tileHeight;

        Node startNode = new Node(startX, startY);
        Node endNode = new Node(endX, endY);

        List<Node> openList = new ArrayList<>();   // Nós a serem avaliados
        Set<Node> closedList = new HashSet<>(); // Nós já avaliados

        openList.add(startNode);
        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startNode, endNode);
        startNode.calculateFCost();

        while (!openList.isEmpty()) {
            Node currentNode = getLowestFCostNode(openList);
            
            // Se chegamos ao nó final, o caminho foi encontrado
            if (currentNode.equals(endNode)) {
                return reconstructPath(currentNode, world);
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            // Avalia os 8 vizinhos do nó atual
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    if (xOffset == 0 && yOffset == 0) continue; // Pula o próprio nó

                    Node neighbourNode = new Node(currentNode.x + xOffset, currentNode.y + yOffset);

                    // Pula se o vizinho já foi avaliado ou se é um obstáculo
                    if (closedList.contains(neighbourNode) || world.getTile(neighbourNode.x, neighbourNode.y).isSolid) {
                        continue;
                    }

                    // Custo para chegar a este vizinho a partir do nó atual
                    double tentativeGCost = currentNode.gCost + ((xOffset != 0 && yOffset != 0) ? MOVE_DIAGONAL_COST : MOVE_STRAIGHT_COST);

                    if (tentativeGCost < neighbourNode.gCost || !openList.contains(neighbourNode)) {
                        neighbourNode.parent = currentNode;
                        neighbourNode.gCost = tentativeGCost;
                        neighbourNode.hCost = calculateHeuristic(neighbourNode, endNode);
                        neighbourNode.calculateFCost();
                        
                        if (!openList.contains(neighbourNode)) {
                            openList.add(neighbourNode);
                        }
                    }
                }
            }
        }
        
        // Nenhum caminho foi encontrado
        return new ArrayList<>();
    }
    
    /**
     * Reconstrói o caminho final, voltando do nó final até o inicial.
     */
    private static List<Point> reconstructPath(Node endNode, World world) {
        List<Point> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode != null) {
            // Converte a coordenada do tile de volta para o centro em pixels
            int pixelX = currentNode.x * world.tileWidth + world.tileWidth / 2;
            int pixelY = currentNode.y * world.tileHeight + world.tileHeight / 2;
            path.add(new Point(pixelX, pixelY));
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Calcula a heurística (distância estimada) entre dois nós.
     */
    private static double calculateHeuristic(Node a, Node b) {
        int dstX = Math.abs(a.x - b.x);
        int dstY = Math.abs(a.y - b.y);
        if (dstX > dstY) {
            return MOVE_DIAGONAL_COST * dstY + MOVE_STRAIGHT_COST * (dstX - dstY);
        }
        return MOVE_DIAGONAL_COST * dstX + MOVE_STRAIGHT_COST * (dstY - dstX);
    }
    
    /**
     * Encontra o nó com o menor F-Cost na lista aberta.
     */
    private static Node getLowestFCostNode(List<Node> list) {
        Node lowestFCostNode = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).fCost < lowestFCostNode.fCost) {
                lowestFCostNode = list.get(i);
            }
        }
        return lowestFCostNode;
    }
}