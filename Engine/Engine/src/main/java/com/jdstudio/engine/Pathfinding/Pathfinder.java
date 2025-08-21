package com.jdstudio.engine.Pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.jdstudio.engine.Utils.Node;
import com.jdstudio.engine.World.Tile.TileType;
import com.jdstudio.engine.World.World;

public class Pathfinder {

    private static final double MOVE_STRAIGHT_COST = 10.0;
    private static final double MOVE_DIAGONAL_COST = 14.14;
    
    // --- NOVO PARÂMETRO AJUSTÁVEL ---
    /**
     * Penalidade adicionada ao custo de um nó se ele for adjacente a uma parede.
     * Aumentar este valor fará com que a IA evite paredes de forma mais agressiva.
     */
    private static final double WALL_PROXIMITY_PENALTY = 10.0;

    public static List<Point> findPath(World world, Point start, Point end) {
        int startX = start.x / world.tileWidth;
        int startY = start.y / world.tileHeight;
        int endX = end.x / world.tileWidth;
        int endY = end.y / world.tileHeight;

        Node startNode = new Node(startX, startY);
        Node endNode = new Node(endX, endY);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startNode, endNode);
        startNode.calculateFCost();
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            
            if (currentNode.equals(endNode)) {
                return reconstructPath(currentNode, world);
            }
            
            closedList.add(currentNode);

            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    if (xOffset == 0 && yOffset == 0) continue;

                    Node neighbourNode = new Node(currentNode.x + xOffset, currentNode.y + yOffset);

                    if (closedList.contains(neighbourNode) || world.getTile(neighbourNode.x, neighbourNode.y).getTileType() == TileType.SOLID) {
                        continue;
                    }
                    
                    double tentativeGCost = currentNode.gCost + ((xOffset != 0 && yOffset != 0) ? MOVE_DIAGONAL_COST : MOVE_STRAIGHT_COST);

                    // --- INÍCIO DA LÓGICA DE PENALIDADE ---
                    boolean isNearWall = false;
                    for (int nx = -1; nx <= 1 && !isNearWall; nx++) {
                        for (int ny = -1; ny <= 1; ny++) {
                            if (nx == 0 && ny == 0) continue;
                            if (world.getTile(neighbourNode.x + nx, neighbourNode.y + ny).getTileType() == TileType.SOLID) {
                                isNearWall = true;
                                break; 
                            }
                        }
                    }
                    if (isNearWall) {
                        tentativeGCost += WALL_PROXIMITY_PENALTY;
                    }
                    // --- FIM DA LÓGICA DE PENALIDADE ---

                    if (tentativeGCost < neighbourNode.gCost || !openList.contains(neighbourNode)) {
                        neighbourNode.parent = currentNode;
                        neighbourNode.gCost = tentativeGCost;
                        neighbourNode.hCost = calculateHeuristic(neighbourNode, endNode);
                        neighbourNode.calculateFCost();
                        
                        openList.add(neighbourNode);
                    }
                }
            }
        }
        
        return new ArrayList<>(); // Nenhum caminho encontrado
    }
    
    private static List<Point> reconstructPath(Node endNode, World world) {
        List<Point> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode != null) {
            int pixelX = currentNode.x * world.tileWidth + world.tileWidth / 2;
            int pixelY = currentNode.y * world.tileHeight + world.tileHeight / 2;
            path.add(new Point(pixelX, pixelY));
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static double calculateHeuristic(Node a, Node b) {
        int dstX = Math.abs(a.x - b.x);
        int dstY = Math.abs(a.y - b.y);
        return MOVE_STRAIGHT_COST * (Math.max(dstX, dstY) - Math.min(dstX, dstY)) + MOVE_DIAGONAL_COST * Math.min(dstX, dstY);
    }
}