package net.toshimichi.sushi.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockUtils {

    public static BlockPos toBlockPos(Vec3d vec) {
        return new BlockPos(vec.x, vec.y, vec.z);
    }

    public static Vec3d toVec3d(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isAir(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos);
    }

    public static boolean canPlace(World world, BlockPlaceInfo face) {
        BlockPos pos = face.getBlockPos();
        EnumFacing facing = face.getBlockFace().getFacing();
        if (facing == null) return world.getBlockState(pos).getBlock().canPlaceBlockAt(world, pos);
        else if (isAir(world, pos.offset(facing.getOpposite()))) return false;
        else return world.getBlockState(pos).getBlock().canPlaceBlockOnSide(world, pos, facing.getOpposite());
    }

    public static void place(BlockPlaceInfo info) {
        Minecraft minecraft = Minecraft.getMinecraft();
        PlayerControllerMP controller = minecraft.playerController;
        EntityPlayerSP player = minecraft.player;
        WorldClient world = minecraft.world;
        if (controller == null || player == null || world == null) return;
        BlockPos pos = info.getBlockPos();
        Vec3d vec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        BlockFace face = info.getBlockFace();
        controller.processRightClickBlock(player, world, pos.offset(face.getFacing().getOpposite()), face.getFacing(), face.getPos().add(vec), EnumHand.MAIN_HAND);
    }

    public static BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos = input.offset(facing);
            EnumFacing opposite = facing.getOpposite();
            IBlockState blockState = world.getBlockState(pos);
            AxisAlignedBB box = blockState.getBoundingBox(world, pos);
            box = box.offset(-box.minX, -box.minY, -box.minZ).offset(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            Vec3d center = box.getCenter();
            if (!canPlace(world, new BlockPlaceInfo(input, new BlockFace(center, opposite)))) continue;
            return new BlockPlaceInfo(input, new BlockFace(center, opposite));
        }
        return null;
    }
}
