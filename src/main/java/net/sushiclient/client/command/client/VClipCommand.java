package net.sushiclient.client.command.client;

import net.minecraft.client.Minecraft;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;

@CommandAlias(value = "vclip", description = "Clip vertically")
public class VClipCommand {

    @Default
    public void onDefault(Integer y) {
        Minecraft mc = Minecraft.getMinecraft();
        PositionUtils.move(mc.player.getPositionVector().add(0, y, 0), 0, 0, false, DesyncMode.POSITION);
    }
}
