package net.toshimichi.sushi.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.*;

import java.util.List;

public class BlockPlaceTask extends TaskAdapter<List<BlockPlaceInfo>, Object> {

    private int index = -1;

    private final DesyncMode mode;
    private final WorldClient world;

    public BlockPlaceTask(DesyncMode mode) {
        this.mode = mode;
        Minecraft minecraft = Minecraft.getMinecraft();
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
        PositionUtils.desync(mode);
        PositionUtils.lookAt(face.getPos().add(pos.getX(), pos.getY(), pos.getZ()), mode);
        PositionUtils.pop();
        BlockUtils.place(info);
    }
}
