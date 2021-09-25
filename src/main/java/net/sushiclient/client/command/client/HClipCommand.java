package net.sushiclient.client.command.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;

@CommandAlias(value = "hclip", description = "Clip horizontally")
public class HClipCommand {

    @Default
    public void onDefault(Integer h) {
        Minecraft mc = Minecraft.getMinecraft();
        double yaw = mc.player.rotationYaw * Math.PI / 180;
        double x = -MathHelper.sin((float) yaw) * h;
        double z = MathHelper.cos((float) yaw) * h;
        PositionUtils.move(mc.player.getPositionVector().add(x, 0, z), 0, 0, false, DesyncMode.POSITION);
    }
}
