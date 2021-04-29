package net.toshimichi.sushi.events.player;

import net.minecraft.entity.MoverType;
import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PlayerMoveEvent extends CancellableEvent {
    private final MoverType type;
    private final double x;
    private final double y;
    private final double z;

    public PlayerMoveEvent(MoverType type, double x, double y, double z) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlayerMoveEvent(EventTiming timing, MoverType type, double x, double y, double z) {
        super(timing);
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MoverType getType() {
        return type;
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
}
