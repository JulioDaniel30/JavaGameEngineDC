package com.jdstudio.engine.Pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.jdstudio.engine.Utils.Node;
import com.jdstudio.engine.World.World;
import com.jdstudio.engine.World.Tile.TileType;

/**
 * A static utility class that finds the shortest path between two points in a given world
 * using the A* (A-star) pathfinding algorithm. It navigates a grid representation
 * of the world, avoiding solid obstacles.
 */
public class Pathfinder {

    private static final double MOVE_STRAIGHT_COST = 10.0;
    private static final double MOVE_DIAGONAL_COST = 14.14;

    /**
     * Finds a path between a start and end point in the world using the A* algorithm.
     *
     * @param world The game world, containing the tile map information.
     * @param start The starting point of the path in world coordinates (pixels).
     * @param end   The target point of the path in world coordinates (pixels).
     * @return A List of Points representing the path from start to end in world coordinates.
     *         The list will be empty if no path is found.
     */
    public static List<Point> findPath(World world, Point start, Point end) {
        Grid grid = new Grid(world);
        Point startPoint = grid.worldToGrid(start);
        Point endPoint = grid.worldToGrid(end);

        Node startNode = grid.getNode(startPoint.x, startPoint.y);
        Node endNode = grid.getNode(endPoint.x, endPoint.y);

        // The set of nodes to be evaluated
        PriorityQueue<Node> openList = new PriorityQueue<>();
        // The set of nodes already evaluated
        Set<Node> closedList = new HashSet<>();

        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startNode, endNode);
        startNode.calculateFCost();
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (currentNode.equals(endNode)) {
                return reconstructPath(currentNode, grid);
            }

            closedList.add(currentNode);

            for (Node neighbourNode : grid.getNeighbours(currentNode)) {
                if (closedList.contains(neighbourNode) || world.getTile(neighbourNode.x, neighbourNode.y).getTileType() == TileType.SOLID) {
                    continue;
                }

                double tentativeGCost = currentNode.gCost + calculateDistanceCost(currentNode, neighbourNode) + grid.getMovementPenalty(neighbourNode);

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

        return new ArrayList<>(); // No path found
    }

    /**
     * Reconstructs the path from the end node back to the start node.
     *
     * @param endNode The final node in the path.
     * @param grid    The grid used for pathfinding, to convert grid coordinates back to world coordinates.
     * @return The complete path as a list of points in world coordinates.
     */
    private static List<Point> reconstructPath(Node endNode, Grid grid) {
        List<Point> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode != null) {
            path.add(grid.gridToWorld(new Point(currentNode.x, currentNode.y)));
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Calculates the heuristic (estimated cost) between two nodes using the Diagonal Distance method.
     *
     * @param a The starting node.
     * @param b The ending node.
     * @return The estimated heuristic cost.
     */
    private static double calculateHeuristic(Node a, Node b) {
        int dstX = Math.abs(a.x - b.x);
        int dstY = Math.abs(a.y - b.y);
        return MOVE_STRAIGHT_COST * (Math.max(dstX, dstY) - Math.min(dstX, dstY)) + MOVE_DIAGONAL_COST * Math.min(dstX, dstY);
    }

    /**
     * Calculates the actual cost of moving from one node to an adjacent one.
     *
     * @param a The first node.
     * @param b The second, adjacent node.
     * @return The cost for moving (10 for straight, 14.14 for diagonal).
     */
    private static double calculateDistanceCost(Node a, Node b) {
        int dstX = Math.abs(a.x - b.x);
        int dstY = Math.abs(a.y - b.y);

        if (dstX > 0 && dstY > 0) {
            return MOVE_DIAGONAL_COST;
        }
        return MOVE_STRAIGHT_COST;
    }
}