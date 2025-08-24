package com.jdstudio.engine.Graphics.Layers;

import java.util.Objects;

/**
 * Represents a rendering layer with a name and a depth value.
 * Objects on layers with lower depth values are rendered before objects on layers with higher depth values.
 * This class is immutable.
 * 
 * @author JDStudio
 */
public final class RenderLayer implements Comparable<RenderLayer> {

    /** The name of the render layer. */
    private final String name;
    
    /** The depth of the render layer. Lower values are rendered first. */
    private final int depth;

    /**
     * Constructs a new RenderLayer.
     *
     * @param name  The name of the layer.
     * @param depth The depth value of the layer.
     */
    public RenderLayer(String name, int depth) {
        this.name = name;
        this.depth = depth;
    }

    /**
     * Gets the name of the render layer.
     * @return The name of the layer.
     */
    public String getName() { 
        return name; 
    }

    /**
     * Gets the depth value of the render layer.
     * @return The depth value.
     */
    public int getDepth() { 
        return depth; 
    }

    /**
     * Compares this RenderLayer to another based on their depth values.
     * This allows RenderLayers to be sorted by their rendering order.
     * 
     * @param other The other RenderLayer to compare to.
     * @return A negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(RenderLayer other) {
        return Integer.compare(this.depth, other.depth);
    }
    
    /**
     * Indicates whether some other object is "equal to" this one.
     * Two RenderLayer objects are considered equal if they have the same name and depth.
     * 
     * @param o The reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderLayer that = (RenderLayer) o;
        return depth == that.depth && Objects.equals(name, that.name);
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hash tables such as those provided by HashMap.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, depth);
    }
}
