package com.jdstudio.engine.Pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Utils.Node;
import com.jdstudio.engine.World.World;

/**
 * Represents the world as a grid of nodes for pathfinding purposes.
 * Handles conversions between world coordinates (pixels) and grid coordinates (tiles).
 */
public class Grid {

    private final World world;
    private final Node[][] nodes;
    private final int gridWidth;
    private final int gridHeight;
    private final int tileWidth;
    private final int tileHeight;

    /**
     * Creates a new Grid based on the provided World object.
     * It initializes a 2D array of Nodes representing the tiles in the world.
     * @param world The world to create the grid from.
     */
    public Grid(World world) {
        this.world = world;
        this.tileWidth = world.tileWidth;
        this.tileHeight = world.tileHeight;
        this.gridWidth = world.WIDTH;
        this.gridHeight = world.HEIGHT;
        this.nodes = new Node[gridWidth][gridHeight];

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                nodes[x][y] = new Node(x, y);
            }
        }
    }

    /**
     * Gets the node at the specified grid coordinates.
     * @param x The x-coordinate of the grid.
     * @param y The y-coordinate of the grid.
     * @return The Node at the given coordinates, or null if out of bounds.
     */
    public Node getNode(int x, int y) {
        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
            return nodes[x][y];
        }
        return null;
    }

    /**
     * Converts a point in world coordinates (pixels) to grid coordinates (tiles).
     * @param worldPoint The point in world coordinates.
     * @return The corresponding point in grid coordinates.
     */
    public Point worldToGrid(Point worldPoint) {
        int x = worldPoint.x / tileWidth;
        int y = worldPoint.y / tileHeight;
        return new Point(x, y);
    }

    /**
     * Converts a point in grid coordinates (tiles) to world coordinates (pixels).
     * The world coordinate will be the center of the tile.
     * @param gridPoint The point in grid coordinates.
     * @return The corresponding point in world coordinates.
     */
    public Point gridToWorld(Point gridPoint) {
        int x = gridPoint.x * tileWidth + tileWidth / 2;
        int y = gridPoint.y * tileHeight + tileHeight / 2;
        return new Point(x, y);
    }

    /**
     * Gets a list of all valid neighbours for a given node.
     * It checks all 8 directions (horizontal, vertical, and diagonal).
     * @param node The node to get the neighbours for.
     * @return A List of neighbouring Nodes.
     */
    public List<Node> getNeighbours(Node node) {
        List<Node> neighbours = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }

                int checkX = node.x + x;
                int checkY = node.y + y;

                if (checkX >= 0 && checkX < gridWidth && checkY >= 0 && checkY < gridHeight) {
                    neighbours.add(nodes[checkX][checkY]);
                }
            }
        }
        return neighbours;
    }

    /**
     * Gets the movement penalty for a given node.
     * This can be used to make certain tiles more "expensive" to traverse.
     * @param node The node to check.
     * @return The movement penalty cost.
     */
    public double getMovementPenalty(Node node) {
        // For now, we don't have different penalties for different tiles.
        // This could be extended to, for example, add a higher cost for "water" or "mud" tiles.
        return 0;
    }
}
