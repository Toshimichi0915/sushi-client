package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.data.Named;

public enum RotateMode implements Named, Rotate {

    VANILLA("Vanilla") {
        @Override
        public void rotate(float yaw, float pitch, boolean desync, PositionOperator operator, Runnable success, Runnable fail) {
            if (success != null) success.run();
        }
    },

    NCP_OLD("NCP") {
        @Override
        public void rotate(float yaw, float pitch, boolean desync, PositionOperator operator, Runnable success, Runnable fail) {
            PositionUtils.move(0, 0, 0, yaw, pitch, false, PositionMask.LOOK, operator);
            if (success != null) PositionUtils.on(success);
        }
    },

    NCP("2B2T") {
        @Override
        public void rotate(float yaw, float pitch, boolean desync, PositionOperator operator, Runnable success, Runnable fail) {
            PositionUtils.move(0, 0, 0, yaw, pitch, false, PositionMask.LOOK, operator);
            if (success != null) PositionUtils.on(() -> PositionUtils.on(success));
        }
    },

    NCP_FAST("2B2T Instant") {
        @Override
        public void rotate(float yaw, float pitch, boolean desync, PositionOperator operator, Runnable success, Runnable fail) {
            PositionUtils.move(0, 0, 0, yaw, pitch, false, PositionMask.LOOK, operator);
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.getConnection().sendPacket(new CPacketPlayer.Rotation(yaw, pitch, minecraft.player.onGround));
            if (success != null) {
                PositionUtils.on(() -> PositionUtils.on(success));
            }
        }
    };

    private final String name;

    RotateMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
