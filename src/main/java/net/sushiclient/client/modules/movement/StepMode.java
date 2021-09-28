package net.sushiclient.client.modules.movement;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;

import java.util.ArrayList;

public enum StepMode implements Named, Step {
    @SerializedName("VANILLA")
    VANILLA("Vanilla") {
        @Override
        public boolean step(double dX, double dY, double dZ, double toY, boolean phase) {
            move(dX, toY, dZ);
            return true;
        }

        @Override
        public boolean reverse(double dX, double dY, double dZ, double toY, boolean phase) {
            move(dX, toY, dZ);
            return true;
        }
    },
    @SerializedName("JP2B")
    JP2B("2b2t.jp") {
        @Override
        public boolean step(double dX, double dY, double dZ, double toY, boolean phase) {
            ArrayList<Packet<?>> packets = new ArrayList<>();
            for (double y = 0; y < dY; y++) {
                if (collides(0, y, 0)) {
                    if (phase) continue;
                    else return false;
                }
                packets.add(newPacket(0, y, 0));
            }
            packets.forEach(mc().getConnection()::sendPacket);
            move(dX, toY, dZ);
            return true;
        }

        @Override
        public boolean reverse(double dX, double dY, double dZ, double toY, boolean phase) {
            ArrayList<Packet<?>> packets = new ArrayList<>();
            for (double y = dY; y < 0; y++) {
                if (collides(dX, y, dZ)) {
                    if (phase) continue;
                    else return false;
                }
                packets.add(newPacket(dX, y, dY));
            }
            packets.forEach(mc().getConnection()::sendPacket);
            move(dX, toY, dZ);
            return true;
        }
    },
    @SerializedName("NCP")
    NCP("2B2T") {
        @Override
        public boolean step(double dX, double dY, double dZ, double toY, boolean phase) {
            ArrayList<Packet<?>> packets = new ArrayList<>();
            double[] arr;
            if (dY > 2.5) {
                return false;
            } else if (dY > 2.019) {
                arr = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.919};
            } else if (dY > 1.869) {
                arr = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
            } else if (dY > 1.5) {
                arr = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652};
            } else if (dY > 1.015) {
                arr = new double[]{0.425, 0.753, 1.010, 1.093, 1.015};
            } else if (dY > 0.875) {
                arr = new double[]{0.420, 0.753};
            } else if (dY > mc().player.stepHeight) {
                arr = new double[]{0.39, 0.6938};
            } else {
                return false;
            }
            for (double d : arr) {
                if (collides(0, d, 0)) {
                    if (phase) continue;
                    else return false;
                }
                packets.add(newPacket(0, d, 0));
            }
            packets.forEach(mc().getConnection()::sendPacket);
            move(dX, toY, dZ);
            return true;
        }

        @Override
        public boolean reverse(double dX, double dY, double dZ, double toY, boolean phase) {
            ArrayList<Packet<?>> packets = new ArrayList<>();
            for (double v : REVERSE_TIMER) {
                if (v < dY) break;
                if (collides(dX, v, dZ)) {
                    if (phase) continue;
                    else return false;
                }
                packets.add(newPacket(dX, v, dZ));
            }
            packets.forEach(mc().getConnection()::sendPacket);
            move(dX, toY, dZ);
            return true;
        }
    },
    @SerializedName("NCP_OLD")
    NCP_OLD("NCP") {
        @Override
        public boolean step(double dX, double dY, double dZ, double toY, boolean phase) {
            return NCP.step(dX, dY, dZ, toY, phase);
        }

        @Override
        public boolean reverse(double dX, double dY, double dZ, double toY, boolean phase) {
            if (dY > -2) {
                return VANILLA.reverse(dX, dY, dZ, toY, phase);
            } else {
                return NCP.reverse(dX, dY, dZ, toY, phase);
            }
        }
    };

    public static final double[] REVERSE_TIMER = {-0.0785, -0.2337, -0.4642, -0.7685, -1.1452, -1.5927, -2.1096, -2.6946, -3.3463, -4.0634, -4.8445, -5.6884, -6.5939, -7.5596, -8.5844};

    private static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    private static boolean collides(double dX, double dY, double dZ) {
        return mc().world.collidesWithAnyBlock(mc().player.getEntityBoundingBox().offset(dX, dY, dZ));
    }

    private static CPacketPlayer newPacket(double dX, double dY, double dZ) {
        return new CPacketPlayer.Position(mc().player.posX + dX, mc().player.posY + dY, mc().player.posZ + dZ, true);
    }

    private static void move(double dX, double toY, double dZ) {
        PositionUtils.move(mc().player.posX + dX, toY, mc().player.posZ + dZ, 0, 0, false, DesyncMode.POSITION);
    }

    private final String name;

    StepMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
