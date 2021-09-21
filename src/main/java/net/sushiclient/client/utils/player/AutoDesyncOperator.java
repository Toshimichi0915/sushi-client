package net.sushiclient.client.utils.player;

import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.world.BlockPlaceInfo;

public class AutoDesyncOperator {

    private DesyncMode desyncMode = DesyncMode.NONE;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public AutoDesyncOperator desyncMode(DesyncMode desyncMode) {
        this.desyncMode = desyncMode;
        return this;
    }

    public AutoDesyncOperator pos(double x, double y, double z) {
        setPos(x, y, z);
        return this;
    }

    public AutoDesyncOperator rotation(float yaw, float pitch) {
        setRotation(yaw, pitch);
        return this;
    }

    public AutoDesyncOperator move(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        PositionUtils.move(x, y, z, yaw, pitch, onGround, desyncMode, this);
        return this;
    }

    public AutoDesyncOperator lookAt(Vec3d loc) {
        PositionUtils.lookAt(loc, desyncMode, this);
        return this;
    }

    public AutoDesyncOperator lookAt(BlockPlaceInfo info) {
        PositionUtils.lookAt(info, this);
        return this;
    }

    public DesyncMode getDesyncMode() {
        return desyncMode;
    }

    public void setDesyncMode(DesyncMode desyncMode) {
        this.desyncMode = desyncMode;
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
