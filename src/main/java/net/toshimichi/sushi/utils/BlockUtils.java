package net.toshimichi.sushi.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockUtils {

    public static boolean isAir(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos);
    }

    public static boolean canPlace(World world, BlockPlaceInfo face) {
        BlockPos pos = face.getBlockPos();
        EnumFacing facing = face.getBlockFace().getFacing();
        if (facing == null) return world.getBlockState(pos).getBlock().canPlaceBlockAt(world, pos);
        else if (isAir(world, pos.offset(facing))) return false;
        else return world.getBlockState(pos).getBlock().canPlaceBlockOnSide(world, pos, facing);
    }

    public static BlockFace findFace(World world, BlockPos input) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = input.offset(facing);
            EnumFacing opposite = facing.getOpposite();
            IBlockState blockState = world.getBlockState(pos);
            AxisAlignedBB box = blockState.getBoundingBox(world, pos);
            box = box.offset(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            Vec3d center = box.getCenter();
            if (!canPlace(world, new BlockPlaceInfo(input, new BlockFace(center, facing)))) continue;
            return new BlockFace(center, facing);
        }
        return null;
    }
}
