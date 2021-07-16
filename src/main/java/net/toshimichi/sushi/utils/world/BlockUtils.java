package net.toshimichi.sushi.utils.world;

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

    public static boolean isColliding(World world, AxisAlignedBB box) {
        return world.collidesWithAnyBlock(box) || !world.checkNoEntityCollision(box);
    }

    public static boolean canPlace(World world, BlockPlaceInfo face, BlockPlaceOption option) {
        BlockPos pos = face.getBlockPos();
        EnumFacing facing = face.getBlockFace() == null ? null : face.getBlockFace().getFacing();
        AxisAlignedBB box = world.getBlockState(pos).getBoundingBox(world, pos).offset(pos);
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null && BlockUtils.toVec3d(pos).add(0.5, 0.5, 0.5).squareDistanceTo(player.getPositionVector()) > 64)
            return false;
        if (world.collidesWithAnyBlock(box) ||
                !world.checkNoEntityCollision(box) && !option.isEntityCollisionIgnored()) return false;
        if (facing == null) return world.getBlockState(pos).getBlock().canPlaceBlockAt(world, pos);
        else if (isAir(world, pos.offset(facing.getOpposite())) && !option.isAirPlaceIgnored()) return false;
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

    public static BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input, BlockPlaceOption option) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPlaceInfo info = new BlockFace(input.offset(facing), facing.getOpposite()).toBlockPlaceInfo(world);
            if (!canPlace(world, info, option)) continue;
            return info;
        }
        return null;
    }

    public static boolean canPlace(World world, BlockPlaceInfo face) {
        return canPlace(world, face, new BlockPlaceOption());
    }

    public static BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input) {
        return findBlockPlaceInfo(world, input, new BlockPlaceOption());
    }
}
