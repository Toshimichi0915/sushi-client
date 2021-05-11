package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.BlockFace;

public class NoBlockRotateTask extends TaskAdapter<BlockFace, Vec3d> {

    public NoBlockRotateTask(double distance) {
    }

    @Override
    public void tick() throws Exception {
        stop(Minecraft.getMinecraft().player.getLookVec());
    }
}
