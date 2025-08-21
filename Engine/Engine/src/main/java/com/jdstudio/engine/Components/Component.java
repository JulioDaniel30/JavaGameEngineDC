package com.jdstudio.engine.Components;

import java.awt.Graphics;
import com.jdstudio.engine.Object.GameObject;

public abstract class Component {

    protected GameObject owner;

    /**
     * Chamado quando o componente é adicionado a um GameObject.
     * @param owner O GameObject ao qual este componente foi adicionado.
     */
    public void initialize(GameObject owner) {this.owner = owner;}

    public void update() {}

    public void render(Graphics g) {}

    public final void setOwner(GameObject owner) {
        this.owner = owner;
    }
}