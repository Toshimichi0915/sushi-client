package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.RotationUtils;

public class ClosestEntityRotateTask extends TaskAdapter<Entity, Vec3d> {

    private final double distance;

    public ClosestEntityRotateTask(double distance) {
        this.distance = distance;
    }

    @Override
    public void tick() throws Exception {
        Entity input = getInput();
        double[][] points = RotationUtils.points(input);
        stop(RotationUtils.closest(Minecraft.getMinecraft().player.getPositionVector(), distance, points));
    }
}
