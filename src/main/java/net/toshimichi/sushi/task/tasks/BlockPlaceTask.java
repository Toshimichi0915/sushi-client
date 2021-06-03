package net.toshimichi.sushi.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

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
        if (!BlockUtils.canPlace(world, info)) {
            tick();
            return;
        }
        PositionUtils.desync(mode);
        PositionUtils.lookAt(info, mode);
        PositionUtils.pop();
        BlockUtils.place(info);
    }
}
