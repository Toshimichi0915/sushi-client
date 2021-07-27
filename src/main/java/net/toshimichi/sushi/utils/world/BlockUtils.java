package net.toshimichi.sushi.utils.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.toshimichi.sushi.utils.player.InventoryType;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;

public class BlockUtils {

    private static BlockPos breakingBlockPos;
    private static int breakingTime;

    public static BlockPos getBreakingBlockPos() {
        return breakingBlockPos;
    }

    public static int getBreakingTime() {
        return breakingTime;
    }

    public static void setBreakingBlockPos(BlockPos pos, int time) {
        breakingBlockPos = pos;
        breakingTime = time;
    }

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

    public static boolean canInteract(BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return false;
        Vec3d checkPos = BlockUtils.toVec3d(pos).add(0.5, 0.5, 0.5);
        return player.getDistanceSq(checkPos.x, checkPos.y, checkPos.z) < 64;
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

    public static void place(BlockPlaceInfo info, boolean packet) {
        Minecraft minecraft = Minecraft.getMinecraft();
        PlayerControllerMP controller = minecraft.playerController;
        EntityPlayerSP player = minecraft.player;
        WorldClient world = minecraft.world;
        if (controller == null || player == null || world == null) return;
        BlockPos pos = info.getBlockPos();
        Vec3d vec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        BlockFace face = info.getBlockFace();
        NetHandlerPlayClient connection = minecraft.getConnection();
        if (!packet || connection == null) {
            controller.processRightClickBlock(player, world, pos.offset(face.getFacing().getOpposite()), face.getFacing(), face.getPos().add(vec), EnumHand.MAIN_HAND);
        } else {
            BlockPos placePos = pos.offset(face.getFacing().getOpposite());
            Vec3d placeVec = face.getPos().add(vec);
            float f = (float) (placeVec.x - (double) placePos.getX());
            float f1 = (float) (placeVec.y - (double) placePos.getY());
            float f2 = (float) (placeVec.z - (double) placePos.getZ());
            connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, face.getFacing(), EnumHand.MAIN_HAND, f, f1, f2));
        }
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

    public static void checkGhostBlock(BlockPos... arr) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (player == null || connection == null) return;
        boolean swap = false;
        ItemSlot current = ItemSlot.current();
        if (current.getItemStack().getItem() instanceof ItemBlock) {
            swap = true;
            for (ItemSlot itemSlot : InventoryType.HOTBAR) {
                if (itemSlot.getItemStack().getItem() instanceof ItemBlock) continue;
                InventoryUtils.moveHotbar(itemSlot.getIndex());
                break;
            }
        }
        for (BlockPos pos : arr) {
            connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
        }
        if (swap) InventoryUtils.moveHotbar(current.getIndex());
    }

    public static boolean equals(BlockPos pos1, BlockPos pos2) {
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ();
    }
}
