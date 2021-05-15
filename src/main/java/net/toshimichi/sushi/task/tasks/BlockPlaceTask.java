package net.toshimichi.sushi.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.task.ConsumerTaskAdapter;
import net.toshimichi.sushi.utils.*;

import java.util.List;

public class BlockPlaceTask extends ConsumerTaskAdapter<List<BlockPlaceInfo>> {

    private int index = -1;

    private final SyncMode mode;

    private final PlayerControllerMP controller;
    private final EntityPlayerSP player;
    private final WorldClient world;

    public BlockPlaceTask(SyncMode mode) {
        this.mode = mode;
        Minecraft minecraft = Minecraft.getMinecraft();
        controller = minecraft.playerController;
        player = minecraft.player;
        world = minecraft.world;
    }

    @Override
    public void tick() throws Exception {
        if (++index >= getInput().size()) {
            stop(null);
            return;
        }
        BlockPlaceInfo info = getInput().get(index);
        BlockPos pos = info.getBlockPos();
        BlockFace face = info.getBlockFace();
        if (!BlockUtils.canPlace(world, info)) {
            tick();
            return;
        }
        PositionUtils.setSyncMode(mode);
        PositionUtils.lookAt(face.getPos());
        PositionUtils.setSyncMode(SyncMode.BOTH);
        controller.processRightClickBlock(player, world, pos, face.getFacing(), face.getPos(), EnumHand.MAIN_HAND);
    }
}
