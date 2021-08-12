package net.sushiclient.client.modules.combat;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;

public class EnderCrystalInfo {
    private final int entityId;
    private final Vec3d pos;
    private final AxisAlignedBB box;

    public EnderCrystalInfo(int entityId, Vec3d pos, AxisAlignedBB box) {
        this.entityId = entityId;
        this.pos = pos;
        this.box = box;
    }

    public int getEntityId() {
        return entityId;
    }

    public Vec3d getPos() {
        return pos;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public CPacketUseEntity newAttackPacket() {
        CPacketUseEntity packet = new CPacketUseEntity();
        PacketBuffer write = new PacketBuffer(Unpooled.buffer());
        write.writeVarInt(getEntityId());
        write.writeEnumValue(CPacketUseEntity.Action.ATTACK);
        try {
            packet.readPacketData(write);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }
}
