package net.toshimichi.sushi.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.player.DesyncCloseable;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockPlaceOption;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.List;

public class BlockPlaceTask extends TaskAdapter<List<BlockPlaceInfo>, Object> {

    private int index = -1;

    private final boolean rotate;
    private final boolean desync;
    private final BlockPlaceOption option;
    private final WorldClient world;

    public BlockPlaceTask(boolean rotate, boolean desync) {
        this(rotate, desync, new BlockPlaceOption());
    }

    public BlockPlaceTask(boolean rotate, boolean desync, BlockPlaceOption option) {
        this.rotate = rotate;
        this.desync = desync;
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
            try (DesyncCloseable closeable = PositionUtils.desync(DesyncMode.LOOK)) {
                PositionUtils.lookAt(info, desync ? DesyncMode.LOOK : DesyncMode.NONE);
            }
        }
        BlockUtils.place(info);
        if (index >= getInput().size()) stop(null);
    }
}
