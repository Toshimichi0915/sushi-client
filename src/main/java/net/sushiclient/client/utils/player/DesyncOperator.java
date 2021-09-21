package net.sushiclient.client.utils.player;

import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.world.BlockPlaceInfo;

import java.io.Closeable;

public class DesyncOperator extends AutoDesyncOperator implements Closeable {

    @Override
    public void close() {
        PositionUtils.pop(this);
    }

    @Override
    public DesyncOperator desyncMode(DesyncMode desyncMode) {
        super.desyncMode(desyncMode);
        return this;
    }

    @Override
    public DesyncOperator pos(double x, double y, double z) {
        super.pos(x, y, z);
        return this;
    }

    @Override
    public DesyncOperator rotation(float yaw, float pitch) {
        super.rotation(yaw, pitch);
        return this;
    }

    @Override
    public DesyncOperator move(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        super.move(x, y, z, yaw, pitch, onGround);
        return this;
    }

    @Override
    public DesyncOperator lookAt(Vec3d loc) {
        super.lookAt(loc);
        return this;
    }

    @Override
    public DesyncOperator lookAt(BlockPlaceInfo info) {
        super.lookAt(info);
        return this;
    }
}
