package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.BlockFace;
import net.toshimichi.sushi.utils.RotationUtils;

public class ClosestBlockRotateTask extends TaskAdapter<BlockFace, Vec3d> {

    private final double distance;

    public ClosestBlockRotateTask(double distance) {
        this.distance = distance;
    }

    @Override
    public void tick() throws Exception {
        BlockPos pos = getInput().getBlockPos();
        double[][] points = RotationUtils.points(pos);
        stop(RotationUtils.closest(Minecraft.getMinecraft().player.getPositionVector(), distance, points));
    }
}
