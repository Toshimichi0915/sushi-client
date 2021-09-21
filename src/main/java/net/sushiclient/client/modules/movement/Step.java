package net.sushiclient.client.modules.movement;

import net.minecraft.client.Minecraft;

public interface Step {
    boolean step(double dX, double dY, double dZ, double toY, boolean phase);

    boolean reverse(double dX, double dY, double dZ, double toY, boolean phase);

    default boolean step(double dX, double dY, double dZ, boolean phase) {
        return step(dX, dY, dZ, Minecraft.getMinecraft().player.posY + dY, phase);
    }

    default boolean reverse(double dX, double dY, double dZ, boolean phase) {
        return reverse(dX, dY, dZ, Minecraft.getMinecraft().player.posY + dY, phase);
    }
}
