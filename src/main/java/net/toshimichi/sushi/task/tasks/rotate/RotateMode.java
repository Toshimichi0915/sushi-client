package net.toshimichi.sushi.task.tasks.rotate;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.data.Named;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.utils.BlockFace;

import java.util.function.Function;

public enum RotateMode implements Named {
    NONE("None", NoBlockRotateTask::new, NoEntityRotateTask::new),
    CLOSEST("Closest", ClosestBlockRotateTask::new, ClosestEntityRotateTask::new),
    RAYTRACE("Raytrace", RaytraceBlockRotateTask::new, RaytraceEntityTask::new);

    private final String name;
    private final Function<Double, TaskAdapter<BlockFace, Vec3d>> blockRotor;
    private final Function<Double, TaskAdapter<Entity, Vec3d>> entityRotor;

    RotateMode(String name, Function<Double, TaskAdapter<BlockFace, Vec3d>> blockRotor,
               Function<Double, TaskAdapter<Entity, Vec3d>> entityRotor) {
        this.name = name;
        this.blockRotor = blockRotor;
        this.entityRotor = entityRotor;
    }

    public TaskAdapter<BlockFace, Vec3d> newBlockRotor(double distance) {
        return blockRotor.apply(distance);
    }

    public TaskAdapter<Entity, Vec3d> newEntityRotor(double distance) {
        return entityRotor.apply(distance);
    }

    @Override
    public String getName() {
        return name;
    }
}
