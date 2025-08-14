package com.JDStudio.Engine.Graphics.Layers;

import java.util.Objects;

/**
 * Representa uma camada de renderização com um nome e um valor de profundidade.
 * Objetos nesta classe são imutáveis.
 */
public final class RenderLayer implements Comparable<RenderLayer> {

    private final String name;
    private final int depth;

    public RenderLayer(String name, int depth) {
        this.name = name;
        this.depth = depth;
    }

    public String getName() { return name; }
    public int getDepth() { return depth; }

    @Override
    public int compareTo(RenderLayer other) {
        return Integer.compare(this.depth, other.depth);
    }
    
    // Métodos equals e hashCode são cruciais para o uso em Mapas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderLayer that = (RenderLayer) o;
        return depth == that.depth && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, depth);
    }
}