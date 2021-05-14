package net.toshimichi.sushi.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockUtils {
    public static BlockFace findFace(World world, BlockPos input) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = input.offset(facing);
            IBlockState blockState = world.getBlockState(pos);
            AxisAlignedBB box = blockState.getBoundingBox(world, pos);
            box.offset(new Vec3d(facing.getDirectionVec()).scale(0.5));
            return new BlockFace(box.getCenter().add(input.getX(), input.getY(), input.getZ()), facing);
        }
        return null;
    }
}
