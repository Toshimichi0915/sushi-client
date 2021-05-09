package net.toshimichi.sushi.utils;

import net.minecraft.util.math.MathHelper;

public class TpsUtils {
    private static final double MAX_TPS = 40;
    private static final double MIN_TPS = 0.1;

    private static double tps;

    public static double getTps() {
        return tps;
    }

    public static void setTps(double tps) {
        TpsUtils.tps = MathHelper.clamp(tps, 0.1, 40);
    }
}
