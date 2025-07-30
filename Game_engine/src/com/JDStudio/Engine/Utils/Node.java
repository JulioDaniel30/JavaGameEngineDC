// engine
package com.JDStudio.Engine.Utils; // Ou com.JDStudio.Engine.Pathfinding

import java.util.Objects;

public class Node {
    public int x, y; // Posição do nó no grid de tiles
    
    // Custos usados pelo algoritmo A*
    public double gCost; // Custo do início até este nó
    public double hCost; // Custo estimado (heurística) deste nó até o fim
    public double fCost; // Custo total (gCost + hCost)

    public Node parent; // O nó anterior no caminho

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void calculateFCost() {
        this.fCost = this.gCost + this.hCost;
    }

    // Métodos equals e hashCode são importantes para
    // verificar se um nó já está em uma lista de forma eficiente.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}