package net.sushiclient.client.events.render;

import net.minecraft.entity.Entity;
import net.sushiclient.client.events.CancellableEvent;

public class LivingLabelRenderEvent extends CancellableEvent {
    private final Entity entityIn;
    private final String str;
    private final double x;
    private final double y;
    private final double z;
    private final int maxDistance;

    public LivingLabelRenderEvent(Entity entityIn, String str, double x, double y, double z, int maxDistance) {
        this.entityIn = entityIn;
        this.str = str;
        this.x = x;
        this.y = y;
        this.z = z;
        this.maxDistance = maxDistance;
    }

    public Entity getEntity() {
        return entityIn;
    }

    public String getStr() {
        return str;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getMaxDistance() {
        return maxDistance;
    }
}
