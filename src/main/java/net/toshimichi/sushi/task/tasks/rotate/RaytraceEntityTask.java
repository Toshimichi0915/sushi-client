package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.RotationUtils;

public class RaytraceEntityTask extends TaskAdapter<Entity, Vec3d> {

    private final double distance;

    public RaytraceEntityTask(double distance) {
        this.distance = distance;
    }

    @Override
    public void tick() throws Exception {
        Entity entity = getInput();
        double[][] points = RotationUtils.points(entity);
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        stop(RotationUtils.raytrace(player.getPositionVector(), distance, points, player.world, entity));
    }
}
