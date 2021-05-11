package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.task.TaskAdapter;

public class NoEntityRotateTask extends TaskAdapter<Entity, Vec3d> {

    public NoEntityRotateTask(double distance) {
    }

    @Override
    public void tick() throws Exception {
        stop(Minecraft.getMinecraft().player.getLookVec());
    }
}
