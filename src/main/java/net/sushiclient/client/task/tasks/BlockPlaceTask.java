package net.sushiclient.client.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.sushiclient.client.task.TaskAdapter;
import net.sushiclient.client.utils.player.DesyncCloseable;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;
import net.sushiclient.client.utils.world.PlaceOptions;

import java.util.List;

public class BlockPlaceTask extends TaskAdapter<List<BlockPlaceInfo>, Object> {

    private int index = -1;

    private final boolean rotate;
    private final boolean desync;
    private final boolean swing;
    private final boolean packet;
    private final PlaceOptions[] option;
    private final WorldClient world;

    public BlockPlaceTask(boolean rotate, boolean desync, PlaceOptions... option) {
        this(rotate, desync, false, true, option);
    }

    public BlockPlaceTask(boolean rotate, boolean desync, boolean packet, boolean swing, PlaceOptions... option) {
        this.rotate = rotate;
        this.desync = desync;
        this.packet = packet;
        this.swing = swing;
        this.option = option;
        Minecraft minecraft = Minecraft.getMinecraft();
        world = minecraft.world;
    }

    @Override
    public void tick() throws Exception {
        if (getInput() == null) {
            stop(null);
            return;
        }
        if (++index >= getInput().size()) {
            stop(null);
            return;
        }
        BlockPlaceInfo info = getInput().get(index);
        if (!BlockUtils.canPlace(world, info, option)) {
            tick();
            return;
        }
        if (rotate) {
            try (DesyncCloseable closable = PositionUtils.desync(DesyncMode.LOOK)) {
                PositionUtils.lookAt(info, desync ? DesyncMode.LOOK : DesyncMode.NONE);
            }
        }
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (swing && connection != null) {
            connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        BlockUtils.place(info, packet);
        if (index >= getInput().size()) stop(null);
    }
}
