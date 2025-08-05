package com.JDStudio.Engine.Object;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

public abstract class BaseProjectile extends GameObject {

    public boolean isActive = false;
    protected double directionX, directionY;
    protected double speed;
    protected double damage;
    protected int lifeTime;
    protected GameObject owner;

    // Construtor protegido, pois não pode ser instanciado diretamente
    protected BaseProjectile() {
        super(new org.json.JSONObject()); // Passa um JSON vazio para o pai
        setCollisionType(CollisionType.TRIGGER);
    }

    public void init(GameObject owner, double startX, double startY, double dirX, double dirY,
                     double speed, double damage, int lifeTime, Sprite sprite) {
        this.owner = owner;
        this.x = startX;
        this.y = startY;
        this.directionX = dirX;
        this.directionY = dirY;
        this.speed = speed;
        this.damage = damage;
        this.lifeTime = lifeTime;
        this.sprite = sprite;
        this.isActive = true;

        if (sprite != null) {
            setCollisionMask(0, 0, sprite.getWidth(), sprite.getHeight());
        }
    }

    @Override
    public void tick() {
        if (!isActive) return;

        x += directionX * speed;
        y += directionY * speed;

        lifeTime--;
        if (lifeTime <= 0) {
            deactivate();
        }
    }

    public void deactivate() {
        this.isActive = false;
    }
    
    public GameObject getOwner() { return this.owner; }
    public double getDamage() { return this.damage; }
}