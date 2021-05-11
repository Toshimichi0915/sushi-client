package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.BlockFace;
import net.toshimichi.sushi.utils.RotationUtils;

public class RaytraceBlockRotateTask extends TaskAdapter<BlockFace, Vec3d> {

    private final double distance;

    public RaytraceBlockRotateTask(double distance) {
        this.distance = distance;
    }

    @Override
    public void tick() throws Exception {
        BlockFace face = getInput();
        double[][] points = RotationUtils.points(face.getBlockPos());
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        stop(RotationUtils.raytrace(player.getPositionVector(), distance, points, player.world, face));
    }
}
