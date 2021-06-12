package net.toshimichi.sushi.utils;

import net.minecraft.entity.Entity;

public class EntityInfo<T extends Entity> implements Comparable<EntityInfo<T>> {
    private final T entity;
    private final double distanceSq;

    public EntityInfo(T entity, double distanceSq) {
        this.entity = entity;
        this.distanceSq = distanceSq;
    }

    public T getEntity() {
        return entity;
    }

    public double getDistanceSq() {
        return distanceSq;
    }

    @Override
    public int compareTo(EntityInfo<T> o) {
        return Double.compare(distanceSq, o.distanceSq);
    }
}
